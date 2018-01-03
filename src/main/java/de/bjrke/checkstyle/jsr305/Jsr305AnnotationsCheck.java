/*
 * JSR305CheckstylePlugin - a checkstyle plugin to ensure nullness anotations
 *
 * Copyright (C) 2008 Marcus Thiesen (initial version)
 * Copyright (C) 2008-2011 Jan Burkhardt (maintainer)
 *
 * thanks to Mattias Nissler, Thorsten Ehlers, Fabian Loewner for contributions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.bjrke.checkstyle.jsr305;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class Jsr305AnnotationsCheck extends AbstractCheck {

    // TODO check correct imports
    // TODO I18n

    private enum NullnessAnnotation {

        OVERRIDE( "Override", "java.lang" ),
        CHECK_FOR_NULL( "CheckForNull", "javax.annotation" ),
        NULLABLE( "Nullable", "javax.annotation" ),
        NONNULL( "Nonnull", "javax.annotation" ),
        CHECK_RETURN_VALUE( "CheckReturnValue", "javax.annotation" ),
        PARAMETERS_ARE_NONNULL_BY_DEFAULT( "ParametersAreNonnullByDefault", "javax.annotation" ),
        PARAMETERS_ARE_NULLABLE_BY_DEFAULT( "ParametersAreNullableByDefault", "javax.annotation" ),
        RETURN_VALUES_ARE_NONNULL_BY_DEFAULT( "ReturnValuesAreNonnullByDefault", "edu.umd.cs.findbugs.annotations" ),

        ;

        private final String _annotationName;

        private final String _fqcn;

        private NullnessAnnotation( final String annotationName, final String packageName ) {
            _annotationName = annotationName;
            _fqcn = packageName + "." + annotationName;
        }

    }

    // global constants
    private static final Map<String, NullnessAnnotation> STRING2ANNOTATION = createString2AnnotationMap();

    private static int[] DEFAULT_MODIFIERS = { TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF, TokenTypes.PACKAGE_DEF,
            TokenTypes.CTOR_DEF, TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF };

    // parameters
    private String[] _packages = new String[0];
    private String[] _excludePackages = new String[0];
    private boolean _allowOverridingReturnValue = false;
    private boolean _allowOverridingParameter = false;

    // state
    private boolean _packageExcluded = false;

    public void setPackages( final String[] packageNames ) {
        _packages = transformToUnique( packageNames );
    }

    private static Map<String, NullnessAnnotation> createString2AnnotationMap() {
        final Map<String, NullnessAnnotation> result = new HashMap<String, NullnessAnnotation>();

        for ( final NullnessAnnotation annotation : NullnessAnnotation.values() ) {
            result.put( annotation._annotationName, annotation );
            result.put( annotation._fqcn, annotation );
        }

        return Collections.unmodifiableMap( result );
    }

    public void setExcludePackages( final String[] packageNames ) {
        _excludePackages = transformToUnique( packageNames );
    }

    private static String[] transformToUnique( final String[] input ) {
        final Set<String> inputSet = new HashSet<String>( Arrays.asList( input ) );
        return inputSet.toArray( new String[inputSet.size()] );
    }

    @Override
    public int[] getDefaultTokens() {
        return DEFAULT_MODIFIERS.clone();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public int[] getAcceptableTokens() {
        return DEFAULT_MODIFIERS.clone();
    }

    @Override
    public void visitToken( final DetailAST ast ) {
        try {
            if ( ast.getType() == TokenTypes.PACKAGE_DEF ) {
                final DetailAST nameAST = ast.getLastChild().getPreviousSibling();
                _packageExcluded = isPackageExcluded( FullIdent.createFullIdent( nameAST ) );
            } else if ( _packageExcluded ) {
                // skip
                return;
            } else {
                handleDefinition( ast );
            }
        } catch ( final RuntimeException e ) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isPackageExcluded( final FullIdent fullIdent ) {
        if ( fullIdent == null ) {
            return true;
        }
        final String packageName = fullIdent.getText();
        if ( packageName == null ) {
            return true;
        }
        for ( final String excludesPackageName : _excludePackages ) {
            if ( packageName.startsWith( excludesPackageName ) ) {
                return true;
            }
        }
        for ( final String includePackageName : _packages ) {
            if ( packageName.startsWith( includePackageName ) ) {
                return false;
            }
        }
        return true;
    }

    private AbstractJsr305Check handleDefinition( final DetailAST ast ) {

        // no definition in catch clause
        final DetailAST parent = ast.getParent();
        if ( parent != null && parent.getType() == TokenTypes.LITERAL_CATCH ) {
            return null;
        }

        // search modifiers
        final int type = ast.getType();
        switch ( type ) {
            case TokenTypes.METHOD_DEF:
                return new MethodJsr305Check( ast );
            case TokenTypes.CTOR_DEF:
                return new ConstructorJsr305Check( ast );
            case TokenTypes.PARAMETER_DEF:
                return new ParameterJsr305Check( ast );
            case TokenTypes.CLASS_DEF:
            case TokenTypes.INTERFACE_DEF:
            case TokenTypes.ENUM_DEF:
                return new ClassJsr305Check( ast );
            default:
                throw new UnsupportedOperationException( "no implementation for " + type );
        }
    }

    public void setAllowOverridingReturnValue( final boolean allowOverridingReturnValue ) {
        _allowOverridingReturnValue = allowOverridingReturnValue;
    }

    public void setAllowOverridingParameter( final boolean allowOverridingParameter ) {
        _allowOverridingParameter = allowOverridingParameter;
    }

    private final class ClassJsr305Check extends AbstractJsr305Check {

        public ClassJsr305Check( final DetailAST ast ) {
            super( ast );
        }

        @Override
        protected void runcheck() {
            checkContainsAny(
                    "@CheckForNull, @Nullable, @Nonnull and @CheckReturnValue are not allowed on class level. Use @ParametersAreNonnullByDefault, @ParametersAreNullableByDefault and @ReturnValuesAreNonnullByDefault.",
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE, NullnessAnnotation.NONNULL,
                    NullnessAnnotation.NULLABLE );
            checkContainsAll( "@ParametersAreNullableByDefault and @ParametersAreNonnullByDefault are not allowed together!",
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT, NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT );
        }

    }

    private final class ParameterJsr305Check extends AbstractJsr305Check {

        ParameterJsr305Check( final DetailAST ast ) {
            super( ast );
        }

        @Override
        protected void runcheck() {
            checkContainsAny( "Parameter defintion don't need checking, use @Nullable or @Nonnull.", NullnessAnnotation.CHECK_FOR_NULL,
                    NullnessAnnotation.CHECK_RETURN_VALUE );
            checkContainsAny( "@Override is not allowed on parameter definition!", NullnessAnnotation.OVERRIDE );
            checkContainsAny( "@ParametersAreNonnullByDefault is not allowed on parameter definition!",
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT );
            checkContainsAny( "@ParametersAreNullableByDefault is not allowed on parameter definition!",
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT );
            checkContainsAny( "@ReturnValuesAreNonnullByDefault is not allowed on parameter definition!",
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT );
            checkContainsAll( "@Nonnull and @Nullable are not allowed together!", NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE );

            final NullnessAnnotation firstAncestorAnnotation = getParentMethodOrClassAnnotation(
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT, NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT );
            final boolean parametersAreNonnullByDefault = firstAncestorAnnotation == NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT;
            final boolean parametersAreNullableByDefault = firstAncestorAnnotation == NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT;
            final boolean isMethodOverridden = isMethodOverridden();

            if ( isPrimitiveType() ) {
                checkContainsAny( "Primitives must not have any nullness annotations!", NullnessAnnotation.CHECK_FOR_NULL,
                        NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE );
                return;
            }
            if ( isMethodOverridden && !_allowOverridingParameter ) {
                checkContainsAny( "It is not allowed to increase nullness constraint for overriden method parameter definitions!",
                        NullnessAnnotation.NONNULL );
            }
            if ( parametersAreNonnullByDefault ) {
                checkContainsAny(
                        "It is not necessary to annotate @Nonnull if you annotated the method or class with @ParametersAreNonnullByDefault.",
                        NullnessAnnotation.NONNULL );
            }
            if ( parametersAreNullableByDefault ) {
                checkContainsAny(
                        "It is not necessary to annotate @Nullable if you annoted the method or class with @ParametersAreNullableByDefault.",
                        NullnessAnnotation.NULLABLE );
            }

            if ( !isMethodOverridden && !parametersAreNonnullByDefault && !parametersAreNullableByDefault ) {
                checkContainsNone( "No nullness Annotation for parameter definition found!", NullnessAnnotation.NONNULL,
                        NullnessAnnotation.NULLABLE );
            }

        }
    }

    private abstract class AbstractMethodJsr305Check extends AbstractJsr305Check {

        AbstractMethodJsr305Check( final DetailAST ast ) {
            super( ast );
        }

        @Override
        protected final void runcheck() {
            checkContainsAll( "@ParametersAreNonnullByDefault and @ParametersAreNullableByDefault are not allowed together!",
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT, NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT );
            runReturnAnnotationCheck();
        }

        protected abstract void runReturnAnnotationCheck();

    }

    private final class MethodJsr305Check extends AbstractMethodJsr305Check {

        MethodJsr305Check( final DetailAST ast ) {
            super( ast );
        }

        @Override
        protected void runReturnAnnotationCheck() {
            checkContainsAny( "@ReturnValuesAreNonnullByDefault is not allowed on method return values!",
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT );
            checkContainsAny( "@Nullable is not allowed on method return values!", NullnessAnnotation.NULLABLE );
            checkContainsAll( "@Nonnull and @CheckForNull are not allowed together!", NullnessAnnotation.NONNULL,
                    NullnessAnnotation.CHECK_FOR_NULL );
            checkContainsAll( "@CheckReturnValue is not allowed on overriden methods, annotate the interface or superclass!",
                    NullnessAnnotation.CHECK_RETURN_VALUE, NullnessAnnotation.OVERRIDE );
            checkRedundancyDueToClassLevelAnnotation(
                    "Redundant @ParametersAreNonnullByDefault, the class is annotated with the same annotation",
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT );
            checkRedundancyDueToClassLevelAnnotation(
                    "Redundant @ParametersAreNullableByDefault, the class is annotated with the same annotation",
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT );

            if ( isVoid() ) {
                checkContainsAny( "There is nothing to check on void return methods, remove @CheckReturnValue!",
                        NullnessAnnotation.CHECK_RETURN_VALUE );
            }
            if ( isPrimitiveType() ) {
                checkContainsAny( "Primitives must not have any nullness annotations!", NullnessAnnotation.CHECK_FOR_NULL,
                        NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE );
                return;
            }

            final boolean returnValuesAreNonnullByDefault = getParentMethodOrClassAnnotation( NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT ) == NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT;
            final boolean isMethodOverridden = isMethodOverridden();

            if (returnValuesAreNonnullByDefault) {
                if (!isMethodOverridden) {
                    checkContainsAny(
                            "It is not necessary to annotate @Nonnull if you annoted the class with @ReturnValuesAreNonnullByDefault.",
                            NullnessAnnotation.NONNULL);
                }
            } else {
                checkContainsNone( "Returnvalue must have nullness Annotation (@Nonnull or @CheckForNull)!",
                        NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NONNULL, NullnessAnnotation.OVERRIDE );
            }

            if ( isMethodOverridden && !_allowOverridingReturnValue ) {
                checkContainsAny( "Overriden methods allow only @Nonnull.", NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NULLABLE );
            }

            if ( isMethodOverridden ) {
                checkContainsAny( "You have to inherit parameter annotations!", NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT );
            }
        }

    }

    private final class ConstructorJsr305Check extends AbstractMethodJsr305Check {

        ConstructorJsr305Check( final DetailAST ast ) {
            super( ast );
        }

        @Override
        protected void runReturnAnnotationCheck() {
            checkContainsAny( "Constructors have no return Value and must not be annotated!", NullnessAnnotation.CHECK_FOR_NULL,
                    NullnessAnnotation.CHECK_RETURN_VALUE, NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE,
                    NullnessAnnotation.OVERRIDE );
        }

    }

    public abstract class AbstractJsr305Check {

        private boolean _errorFound = false;
        private final Set<NullnessAnnotation> _annotations;
        private final DetailAST _ast;

        AbstractJsr305Check( final DetailAST ast ) {
            _ast = ast;
            if ( ast == null ) {
                _annotations = Collections.emptySet();
                return;
            }
            _annotations = findAnnotation();
            runcheck();
        }

        protected abstract void runcheck();

        protected void checkContainsAny( final String msg, final NullnessAnnotation... annotations ) {
            if ( !_errorFound && containsAny( annotations ) ) {
                error( msg );
            }
        }

        protected boolean containsAny( final NullnessAnnotation... annotations ) {
            if ( _annotations.isEmpty() ) {
                return false;
            }
            for ( final NullnessAnnotation obj : annotations ) {
                if ( _annotations.contains( obj ) ) {
                    return true;
                }
            }
            return false;
        }

        protected void checkContainsAll( final String msg, final NullnessAnnotation... annotations ) {
            if ( !_errorFound && containsAll( annotations ) ) {
                error( msg );
            }
        }

        protected void checkRedundancyDueToClassLevelAnnotation( final String msg, final NullnessAnnotation... annotations ) {
            if ( _errorFound ) {
                return;
            }
            for ( final NullnessAnnotation nullnessAnnotation : annotations ) {
                final boolean thisIsAnnotated = _annotations.contains( nullnessAnnotation );
                final boolean parentIsAnnotated = getParentMethodOrClassAnnotation( nullnessAnnotation ) != null;
                if ( thisIsAnnotated && parentIsAnnotated ) {
                    error( msg );
                    return;
                }
            }
        }

        protected boolean containsAll( final NullnessAnnotation... annotations ) {
            if ( _annotations.isEmpty() ) {
                return annotations.length == 0;
            }
            for ( final NullnessAnnotation obj : annotations ) {
                if ( !_annotations.contains( obj ) ) {
                    return false;
                }
            }
            return true;
        }

        protected void checkContainsNone( final String msg, final NullnessAnnotation... annotations ) {
            if ( !_errorFound && !containsAny( annotations ) ) {
                error( msg );
            }
        }

        private void error( final String msg ) {
            if ( !_errorFound ) {
                log( _ast, msg );
            }
            _errorFound = true;
        }

        protected boolean isPrimitiveType() {
            final DetailAST parameterType = _ast.findFirstToken( TokenTypes.TYPE );
            if ( parameterType == null ) {
                return false;
            }
            final DetailAST identToken = parameterType.getFirstChild();

            if ( identToken == null ) {
                return false;
            }
            switch ( identToken.getType() ) {
                case TokenTypes.LITERAL_BOOLEAN:
                case TokenTypes.LITERAL_INT:
                case TokenTypes.LITERAL_LONG:
                case TokenTypes.LITERAL_SHORT:
                case TokenTypes.LITERAL_BYTE:
                case TokenTypes.LITERAL_CHAR:
                case TokenTypes.LITERAL_VOID:
                case TokenTypes.LITERAL_DOUBLE:
                case TokenTypes.LITERAL_FLOAT:
                    return !isArrayOrElipsis( parameterType );
            }

            return false;
        }

        private boolean isArrayOrElipsis( final DetailAST identToken ) {
            final DetailAST next = identToken.getNextSibling();
            if ( next == null ) {
                return false;
            }
            switch ( next.getType() ) {
                case TokenTypes.ARRAY_DECLARATOR:
                case TokenTypes.ELLIPSIS:
                    return true;
            }
            return false;
        }

        protected boolean isVoid() {
            final DetailAST parameterType = _ast.findFirstToken( TokenTypes.TYPE );
            if ( parameterType == null ) {
                return false;
            }
            final DetailAST identToken = parameterType.getFirstChild();
            return identToken != null && identToken.getType() == TokenTypes.LITERAL_VOID;
        }

        private Set<NullnessAnnotation> findAnnotation() {
            return findAnnotation( _ast );
        }

        private Set<NullnessAnnotation> findAnnotation( final DetailAST ast ) {
            final Set<NullnessAnnotation> result = new HashSet<NullnessAnnotation>();

            final DetailAST modifiers = ast.findFirstToken( TokenTypes.MODIFIERS );
            if ( modifiers == null ) {
                return result;
            }
            AST child = modifiers.getFirstChild();
            while ( child != null ) {
                if ( child.getType() == TokenTypes.ANNOTATION ) {
                    final DetailAST identifier = ((DetailAST) child).findFirstToken( TokenTypes.IDENT );
                    if ( identifier != null ) {
                        final String annotationName = identifier.getText();
                        if ( annotationName != null ) {
                            final NullnessAnnotation annotation = STRING2ANNOTATION.get( annotationName );
                            if ( annotation != null && !result.add( annotation ) ) {
                                error( "Double Annotation (" + annotation._annotationName + ") found!" );
                            }
                        }
                    }
                }

                child = child.getNextSibling();
            }

            return result;
        }

        protected NullnessAnnotation getParentMethodOrClassAnnotation( final NullnessAnnotation... annotationsToLookFor ) {
            DetailAST current = _ast.getParent();
            while ( current != null ) {
                final int tokenType = current.getType();
                if ( tokenType == TokenTypes.CLASS_DEF || tokenType == TokenTypes.INTERFACE_DEF || tokenType == TokenTypes.METHOD_DEF
                        || tokenType == TokenTypes.CTOR_DEF || tokenType == TokenTypes.ENUM_DEF ) {
                    final Set<NullnessAnnotation> foundAnnotations = findAnnotation( current );
                    final Set<NullnessAnnotation> foundAndLookedFor = new HashSet<NullnessAnnotation>();
                    for ( final NullnessAnnotation nullnessAnnotation : annotationsToLookFor ) {
                        if ( foundAnnotations.contains( nullnessAnnotation ) ) {
                            foundAndLookedFor.add( nullnessAnnotation );
                        }
                    }
                    if ( foundAndLookedFor.size() == 1 ) {
                        return foundAndLookedFor.iterator().next();
                    } else if ( foundAndLookedFor.size() > 0 ) {
                        return null;
                    }
                }
                // break on inner and anonymous classes/interfaces, we can't
                // handle inheritance correctly
                if ( tokenType == TokenTypes.LITERAL_NEW || tokenType == TokenTypes.CLASS_DEF || tokenType == TokenTypes.INTERFACE_DEF
                        || tokenType == TokenTypes.ENUM_DEF ) {
                    break;
                }
                current = current.getParent();
            }
            return null;
        }

        protected boolean isMethodOverridden() {
            DetailAST current = _ast;
            while ( current != null ) {
                switch ( current.getType() ) {
                    case TokenTypes.METHOD_DEF:
                        return findAnnotation( current ).contains( NullnessAnnotation.OVERRIDE );
                    case TokenTypes.LAMBDA:
                        return true;
                    default:
                        current = current.getParent();
                }
            }
            return false;
        }

    }

}
