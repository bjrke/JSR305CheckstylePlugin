/*
 * $Id$
 * (c) Copyright 2011 freiheit.com technologies GmbH
 *
 * Created on 30.11.2011 by Jan Burkhardt (jan.burkhardt@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.Nonnull;

/**
 * {@link DefectConstructorTest}.
 * 
 * @author <a href="mailto:Jan Burkhardt<jan.burkhardt@freiheit.com">Jan
 *         Burkhardt</a> (initial creation)
 */
public class DefectConstructorTest {

    private final String _s;

    @Override
    public String toString() {
        return _s;
    }

    public DefectConstructorTest( @Nonnull final String s ) {
        _s = s;
    }

}
