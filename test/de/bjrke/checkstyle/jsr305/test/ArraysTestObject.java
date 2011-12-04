/*
 * $Id$
 * (c) Copyright 2011 freiheit.com technologies GmbH
 *
 * Created on 02.12.2011 by Jan Burkhardt (jan.burkhardt@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package de.bjrke.checkstyle.jsr305.test;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link ArraysTestObject}.
 * 
 * @author <a href="mailto:Jan Burkhardt<jan.burkhardt@freiheit.com">Jan
 *         Burkhardt</a> (initial creation)
 */
public class ArraysTestObject {

    private final Object _obj;

    // ok
    ArraysTestObject( @Nonnull final int[] nonnull ) {
        _obj = nonnull;
    }

    // ok
    ArraysTestObject( @Nullable final long[] nullable ) {
        _obj = nullable;
    }

    // error
    ArraysTestObject( final short[] array ) {
        _obj = array;
    }

    // ok
    ArraysTestObject( @Nonnull final byte... varargs ) {
        _obj = varargs;
    }

    // ok
    ArraysTestObject( @Nullable final float... varargs ) {
        _obj = varargs;
    }

    // error
    ArraysTestObject( final double... varargs ) {
        _obj = varargs;
    }

    // ok
    ArraysTestObject( @Nonnull final String... varargs ) {
        _obj = varargs;
    }

    // ok
    ArraysTestObject( @Nullable final Object... varargs ) {
        _obj = varargs;
    }

    // error
    ArraysTestObject( final Serializable... varargs ) {
        _obj = varargs;
    }

    // ok
    ArraysTestObject( @Nonnull final Class<?>[] x ) {
        _obj = x;
    }

    // ok
    ArraysTestObject( @Nullable final Iterable<?>[] x ) {
        _obj = x;
    }

    // error
    ArraysTestObject( final Comparable<?> x ) {
        _obj = x;
    }

    @Nonnull
    int[] retNonnul() {
        return new int[] { };
    }

    @CheckForNull
    int[] retCheckForNull() {
        return null;
    }

    int[] retNoAnnotation() {
        return new int[] { };
    }

    // ok
    void testNonnullArray( @Nonnull final int[] array ) {
        array[1] = 0;
    }

    // ok
    void testNullableArray( @Nullable final int[] array ) {
        array[1] = 0;
    }

    // error
    void testNoAnnotationArray( final int[] array ) {
        array[1] = 0;
    }

    // ok
    void testNonnullVarargs( @Nonnull final int... varargs ) {
        varargs[1] = 0;
    }

    // ok
    void testNullableVarargs( @Nullable final int... varargs ) {
        varargs[1] = 0;
    }

    // error
    void testNoAnnotationVarargs( final int... varargs ) {
        varargs[1] = 0;
    }

    @Override
    public String toString() {
        return String.valueOf( _obj );
    }
}
