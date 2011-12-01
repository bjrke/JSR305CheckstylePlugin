package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class ConstructorTestObject {

    @Nonnull
    private final Object _nonnull;

    @Nullable
    private final Object _nullable;

    // error
    public ConstructorTestObject( @Nonnull @Nullable final Enum<?> value ) {
        _nullable = value;
        _nonnull = new Object();
    }

    // error
    public ConstructorTestObject( final Class<?> clz ) {
        _nonnull = new Object();
        _nullable = clz;
    }

    // ok
    @ParametersAreNonnullByDefault
    public ConstructorTestObject( final String s1, @Nullable final String s2 ) {
        _nonnull = s1;
        _nullable = s2;
    }

    // error
    @ParametersAreNonnullByDefault
    public ConstructorTestObject( @Nonnull final String s1, final Integer s2 ) {
        _nonnull = s1;
        _nullable = s2;
    }

    // ok
    @ParametersAreNullableByDefault
    public ConstructorTestObject( @Nonnull final Integer i1, final Integer i2 ) {
        _nonnull = i1;
        _nullable = i2;
    }

    // error
    @ParametersAreNullableByDefault
    public ConstructorTestObject( final Integer i1, @Nullable final String i2 ) {
        _nonnull = i1 != null
            ? i1
            : Integer.valueOf( 1 );
        _nullable = i2;
    }

    // ok
    public ConstructorTestObject( @Nullable final String obj ) {
        _nonnull = new Object();
        _nullable = obj;
    }

    // ok
    public ConstructorTestObject( @Nonnull final Integer obj ) {
        _nonnull = obj;
        _nullable = null;
    }

    // ok
    @CheckForNull
    public Object getNullable() {
        return _nullable;
    }

    // ok
    @CheckReturnValue
    @Nonnull
    public Object getNonnull() {
        return _nonnull;
    }

}
