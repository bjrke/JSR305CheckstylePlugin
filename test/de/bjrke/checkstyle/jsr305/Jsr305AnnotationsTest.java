package de.bjrke.checkstyle.jsr305;

import org.testng.annotations.Test;

import de.bjrke.checkstyle.jsr305.Jsr305AnnationsTestUtil.ExpectedWarning;
import de.bjrke.checkstyle.jsr305.test.ConstrutorTestObject;
import de.bjrke.checkstyle.jsr305.test.ParameterTestObject;
import de.bjrke.checkstyle.jsr305.test.PrimitivesTestObject;
import de.bjrke.checkstyle.jsr305.test.ReturnValueTestObject;

public class Jsr305AnnotationsTest {

    @Test
    public void test() {
        Jsr305AnnationsTestUtil.check(
        //
                new ExpectedWarning( ParameterTestObject.class, 15, 45 ), //
                new ExpectedWarning( ParameterTestObject.class, 20, 46 ), //
                new ExpectedWarning( ParameterTestObject.class, 20, 63 ), //
                new ExpectedWarning( ParameterTestObject.class, 50, 42 ), //
                new ExpectedWarning( ParameterTestObject.class, 56, 28 ), // 
                new ExpectedWarning( ParameterTestObject.class, 74, 36 ), //

                new ExpectedWarning( PrimitivesTestObject.class, 10, 38 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 15, 39 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 20, 43 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 25, 5 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 31, 5 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 37, 5 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 43, 5 ), //
                new ExpectedWarning( PrimitivesTestObject.class, 90, 35 ), //

                new ExpectedWarning( ReturnValueTestObject.class, 29, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 35, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 42, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 56, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 63, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 83, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 86, 27 ), //  <- fixen
                new ExpectedWarning( ReturnValueTestObject.class, 86, 44 ), //  <- fixen

                new ExpectedWarning( ConstrutorTestObject.class, 19, 34 ), //
                new ExpectedWarning( ConstrutorTestObject.class, 25, 34 ), //
                new ExpectedWarning( ConstrutorTestObject.class, 32, 34 ), //
                new ExpectedWarning( ConstrutorTestObject.class, 39, 60 ), //
                new ExpectedWarning( ConstrutorTestObject.class, 46, 61 ), //
                new ExpectedWarning( ConstrutorTestObject.class, 53, 34 )

        );
    }
}
