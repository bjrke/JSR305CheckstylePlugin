package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.CheckForNull;

public interface TestInterface {

    @CheckForNull
    Object foo();
}
