/**
 *
 * JSR305CheckstylePlugin - a checkstyle plugin to ensure nullness anotations
 *
 * Copyright (C) 2008 Marcus Thiesen (initial version)
 * Copyright (C) 2008-2009 Jan Burkhardt (maintainer)
 *
 * thanks to Mattias Nissler, Thorsten Ehlers, Fabian Löwner for contributions
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
 *
 */

package de.bjrke.checkstyle.jsr305;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class Jsr305Annotations extends Check {

    private static int[] DEFAULT_MODIFIERS = { TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF, TokenTypes.PACKAGE_DEF };

    private String[] _packages = new String[0];
    private String[] _excludePackages = new String[0];
    private String[] _allowedAnnotations = { "Nullable", "CheckForNull", "Nonnull" };
    private String[] _allowedMethodAnnotations = { "CheckForNull", "Nonnull", "CheckReturnValue" };

    private boolean _checkAnnotation;

    public void setPackages( final String[] packageNames ) {
        _packages = new HashSet<String>( Arrays.asList( packageNames ) ).toArray( _packages );
    }

    public void setExcludePackages( final String[] packageNames ) {
        _excludePackages = new HashSet<String>( Arrays.asList( packageNames ) ).toArray( _excludePackages );
    }

    public void setAllowedAnnotations( final String[] allowedAnnotations ) {
        _allowedAnnotations = new HashSet<String>( Arrays.asList( allowedAnnotations ) ).toArray( _allowedAnnotations );
    }

    public void setAllowedMethodAnnotations( final String[] allowedAnnotations ) {
        _allowedMethodAnnotations = new HashSet<String>( Arrays.asList( allowedAnnotations ) ).toArray( _allowedMethodAnnotations );
    }

    @Override
    public int[] getDefaultTokens() {
        return  DEFAULT_MODIFIERS;
    }

    @Override
    public void visitToken( final DetailAST aast ) {
        if ( aast.getType() == TokenTypes.PACKAGE_DEF ) {
            final DetailAST nameAST = aast.getLastChild().getPreviousSibling();
            final FullIdent full = FullIdent.createFullIdent( nameAST );
            _checkAnnotation = isConfiguredPackage( full.getText() );
        } else if ( _checkAnnotation ) {
            handleDefinition( aast );
        }
    }

    private boolean isConfiguredPackage( final String givenPackageName ) {
        for ( final String excludesPackageName : _excludePackages ) {
            if ( givenPackageName.startsWith( excludesPackageName ) ) {
                return false;
            }
        }
        for ( final String includePackageName : _packages ) {
            if ( givenPackageName.startsWith( includePackageName ) ) {
                return true;
            }
        }
        return false;
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
        final String[] allowed = type == TokenTypes.METHOD_DEF ? _allowedMethodAnnotations : _allowedAnnotations;
        final DetailAST modifiers = aast.findFirstToken( TokenTypes.MODIFIERS );
        if ( modifiers != null ) {
            for ( final DetailAST annotation : findAnnotations( modifiers ) ) {
                if ( checkAnnotation( allowed, annotation.findFirstToken( TokenTypes.IDENT ) ) ) {
                    return;
                }
            }
        }

        final StringBuilder sb = new StringBuilder( "No Annotations for " )
            .append( type == TokenTypes.PARAMETER_DEF ? "parameter definition" : "method definition (return value)" )
            .append( " found, expected one of @" );

        String comma = "";
        for ( final String annotation : allowed ) {
            sb.append( comma ).append( annotation );
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
            default:
                return false;
        }
    }

    private List<DetailAST> findAnnotations( final DetailAST modifiers ) {
        final List<DetailAST> result = new ArrayList<DetailAST>();

        AST child = modifiers.getFirstChild();
        while ( child != null ) {
            if ( child.getType() == TokenTypes.ANNOTATION ) {
                result.add( (DetailAST) child );
            }

            child = child.getNextSibling();
        }
        return result;
    }

    private boolean checkAnnotation( final String[] allowedAnnotations, final DetailAST identifier ) {
        if ( identifier == null ) {
            return false;
        }
        final String annotationName = identifier.getText();
        if ( annotationName == null ) {
            return false;
        }
        for ( final String allowed : allowedAnnotations ) {
            if ( allowed.equals( annotationName ) ) {
                return true;
            }
        }
        return false;
    }

}
