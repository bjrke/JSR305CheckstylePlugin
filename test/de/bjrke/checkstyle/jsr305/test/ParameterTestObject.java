package de.bjrke.checkstyle.jsr305.test;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParameterTestObject implements Comparable<ParameterTestObject>, Serializable {

    private static final long serialVersionUID = 1L;

    // error
    @Nonnull
    public String missingNullnessParameter( final Object obj ) {
        return obj.toString();
    }

    // 2x error
    public boolean missingNullnessParameter( final Object o1, final Object o2 ) {
        return o1.equals( o2 );
    }

    // ok
    @Nonnull
    public String nonnullParameter( @Nonnull final Object obj ) {
        return obj.toString();
    }

    // ok
    public boolean nonnullParameter( @Nonnull final Object o1, @Nonnull final Object o2 ) {
        return o1.equals( o2 );
    }

    // ok
    @Nonnull
    public String nullableParameter( @Nullable final Object obj ) {
        return String.valueOf( obj );
    }

    // ok
    public boolean nullableParameter( @Nullable final Object o1, @Nullable final Object o2 ) {
        return o1 != null
            ? o1.equals( o2 )
            : o2 == null;
    }

    // error
    @Nonnull
    public String checkForNullParameter( @CheckForNull final Object obj ) {
        return String.valueOf( obj );
    }

    // error
    @Override
    public boolean equals( @Nonnull final Object obj ) {
        return super.equals( obj );
    }

    // ok
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    @Override
    public int compareTo( @Nullable final ParameterTestObject o ) {
        return 0;
    }

    //error
    @Nonnull
    public String nonnullNullable( @Nonnull @Nullable final Object o ) {
        return String.valueOf( o );
    }

}
