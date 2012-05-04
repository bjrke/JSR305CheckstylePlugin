package de.bjrke.checkstyle.jsr305.test;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class ReturnValueTestObject implements Comparable<ReturnValueTestObject>, Serializable, Runnable {

    private static final long serialVersionUID = 1L;

    // ok
    @Nonnull
    Object returnNonnull() {
        return new Object();
    }

    // ok
    @CheckForNull
    Object returnCheckForNull() {
        return null;
    }

    // error
    @Nullable
    Object returnNullable() {
        return null;
    }

    // error
    @Nonnull
    @CheckForNull
    Object returnNonnullCheckForNull() {
        return new Object();
    }

    // ok
    @CheckReturnValue
    @CheckForNull
    Object returnCheckReturnValueCheckForNull() {
        return new Object();
    }

    // ok
    @CheckReturnValue
    @Nonnull
    Object returnCheckReturnValueNonnull() {
        return new Object();
    }

    // error
    @Override
    @Nonnull
    public boolean equals( final Object obj ) {
        return super.equals( obj );
    }

    // error
    @Override
    @CheckForNull
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    @Override
    public void run() {
        hashCode();
    }

    // ok
    @Override
    @Nonnull
    public String toString() {
        return super.toString();
    }

    // error + 2x parameter error
    @ParametersAreNonnullByDefault
    @ParametersAreNullableByDefault
    @Nonnull
    public String concat( final String s1, final String s2 ) {
        return s1 + s2;
    }

    // error
    @Override
    @CheckReturnValue
    public int compareTo( @Nullable final ReturnValueTestObject o ) {
        return 0;
    }

    // error
    @CheckReturnValue
    public void voidNoCheckReturnValue() {
        hashCode();
    }

    // error
    @Nonnull
    public void voidNoNonnull() {
        hashCode();
    }

}
