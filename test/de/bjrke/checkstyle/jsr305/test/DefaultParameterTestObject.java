package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

@ReturnValuesAreNonnullByDefault
public class DefaultParameterTestObject implements TestInterface {

    // ok
    @Override
    @Nonnull
    public Object foo() {
        return new Object();
    }

    // error
    @Nonnull
    public Object bar() {
        return new Object();
    }

}
