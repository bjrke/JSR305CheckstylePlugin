package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

@ReturnValuesAreNonnullByDefault
public class DefaultParameterTestObject {

    // ok
    @Override
    @Nonnull
    public String toString() {
        return super.toString();
    }

    // error
    @Nonnull
    public Object bar() {
        return new Object();
    }

}
