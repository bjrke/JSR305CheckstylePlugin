package de.bjrke.checkstyle.jsr305.test;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class LambdaTest {

    //ok
    public static final Function<Object, String> TO_STRING = ( o ) -> o.toString();

    //error
    public static final ToIntFunction<Object> HASH_CODE = (@Nonnull Object o) -> o.hashCode();

}
