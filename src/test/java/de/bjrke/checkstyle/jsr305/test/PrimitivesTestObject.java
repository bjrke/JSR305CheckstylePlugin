package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrimitivesTestObject {

    // error
    public boolean nonnullParameter( @Nonnull final int i ) {
        return i == 0;
    }

    // error
    public boolean nullableParameter( @Nullable final int i ) {
        return i == 0;
    }

    // error
    public boolean checkForNullParameter( @CheckForNull final int i ) {
        return i == 0;
    }

    // error
    @Nonnull
    public int returnNonnull() {
        return 0;
    }

    // error
    @CheckForNull
    public int returnCheckForNull() {
        return 0;
    }

    // error
    @Nullable
    public int returnNullable() {
        return 0;
    }

    // error
    @Override
    @Nonnull
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    public int primitivesInt( final int i ) {
        return i + 1;
    }

    // ok
    public byte primitivesByte( final byte i ) {
        return i;
    }

    // ok
    public boolean primitivesBoolean( final boolean i ) {
        return !i;
    }

    // ok
    public float primitivesFloat( final float i ) {
        return i + 1.0F;
    }

    // ok
    public double primitivesDouble( final double i ) {
        return i + 1.0D;
    }

    // ok
    public long primitivesLong( final long i ) {
        return i + 1L;
    }

    // ok
    public short primitivesShort( final short i ) {
        return i;
    }

    // ok
    public char primitivesChar( final char i ) {
        return i;
    }

    // error
    public int primitivesNonnull( @Nonnull final int i ) {
        return i + 1;
    }

}
