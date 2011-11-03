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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class Jsr305Annotations extends Check {

    private enum NullnessAnnotation {

            OVERRIDE( "Override", "java.lang" ),
            CHECK_FOR_NULL( "CheckForNull", "javax.annotation" ),
            NULLABLE( "Nullable", "javax.annotation" ),
            NONNULL( "Nonnull", "javax.annotation" ),
            CHECK_RETURN_VALUE( "CheckReturnValue", "javax.annotation" ),

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

    private static final Set<NullnessAnnotation> ALLOWED_PARAMETER_ANNOTATIONS = Collections.unmodifiableSet( EnumSet.of(
            NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE ) );
    private static final Set<NullnessAnnotation> ALLOWED_METHOD_ANNOTATIONS = Collections.unmodifiableSet( EnumSet.of(
            NullnessAnnotation.NONNULL, NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.OVERRIDE ) );

    private static int[] DEFAULT_MODIFIERS = { TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF, TokenTypes.PACKAGE_DEF };

    // paramters
    private String[] _packages = new String[0];
    private String[] _excludePackages = new String[0];

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
        return DEFAULT_MODIFIERS;
    }

    @Override
    public void visitToken( final DetailAST aast ) {
        try {
            if ( aast.getType() == TokenTypes.PACKAGE_DEF ) {
                final DetailAST nameAST = aast.getLastChild().getPreviousSibling();
                _packageExcluded = isPackageExcluded( FullIdent.createFullIdent( nameAST ) );
            } else if ( _packageExcluded ) {
                // skip
                return;
            } else {
                handleDefinition( aast );
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

    private void handleDefinition( final DetailAST aast ) {
        // do not check primitives
        final int type = aast.getType();
        if ( isPrimitiveType( aast.findFirstToken( TokenTypes.TYPE ) ) ) {
            return;
        }

        // no definition in catch clause
        if ( aast.getParent().getType() == TokenTypes.LITERAL_CATCH ) {
            return;
        }

        // search modifiers
        final Set<NullnessAnnotation> allowed = type == TokenTypes.METHOD_DEF
            ? ALLOWED_METHOD_ANNOTATIONS
            : ALLOWED_PARAMETER_ANNOTATIONS;
        final DetailAST modifiers = aast.findFirstToken( TokenTypes.MODIFIERS );
        if ( modifiers != null ) {
            for ( final NullnessAnnotation annotationName : findAnnotationNames( modifiers ) ) {
                if ( checkAnnotation( allowed, annotationName ) ) {
                    return;
                }
            }
        }

        final StringBuilder sb = new StringBuilder( "No Annotations for " ).append( type == TokenTypes.PARAMETER_DEF
            ? "parameter definition"
            : "method definition (return value)" ).append( " found, expected one of @" );

        String comma = "";
        for ( final NullnessAnnotation annotation : allowed ) {
            sb.append( comma ).append( annotation._annotationName );
            comma = ", @";
        }

        log( aast.getLineNo(), sb.append( "." ).toString() );
    }

    private boolean isPrimitiveType( final DetailAST parameterType ) {
        if ( parameterType == null ) {
            return false;
        }
        final AST identToken = parameterType.getFirstChild();
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
                return true;
        }
        return false;
    }

    private List<NullnessAnnotation> findAnnotationNames( final DetailAST modifiers ) {
        final List<NullnessAnnotation> result = new ArrayList<NullnessAnnotation>();

        AST child = modifiers.getFirstChild();
        while ( child != null ) {
            if ( child.getType() == TokenTypes.ANNOTATION ) {
                final DetailAST identifier = ( (DetailAST) child ).findFirstToken( TokenTypes.IDENT );
                if ( identifier != null ) {
                    final String annotationName = identifier.getText();
                    if ( annotationName != null ) {
                        final NullnessAnnotation annotation = STRING2ANNOTATION.get( annotationName );
                        if ( annotation != null ) {
                            result.add( annotation );
                        }
                    }
                }
            }

            child = child.getNextSibling();
        }
        return result;
    }

    private boolean checkAnnotation( final Set<NullnessAnnotation> allowed, final NullnessAnnotation annotation ) {
        return allowed.contains( annotation );
    }

}
