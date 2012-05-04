package de.bjrke.checkstyle.jsr305;

import org.testng.annotations.Test;

import de.bjrke.checkstyle.jsr305.Jsr305AnnationsTestUtil.ExpectedWarning;
import de.bjrke.checkstyle.jsr305.test.ArraysTestObject;
import de.bjrke.checkstyle.jsr305.test.ClassTestObject;
import de.bjrke.checkstyle.jsr305.test.ConstructorTestObject;
import de.bjrke.checkstyle.jsr305.test.DefectConstructorTest;
import de.bjrke.checkstyle.jsr305.test.ParameterTestObject;
import de.bjrke.checkstyle.jsr305.test.PrimitivesTestObject;
import de.bjrke.checkstyle.jsr305.test.ReturnValueTestObject;

public class Jsr305AnnotationsTest {

    @Test
    public void test() {
        Jsr305AnnationsTestUtil.check( //
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
                new ExpectedWarning( ReturnValueTestObject.class, 56, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 63, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 83, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 86, 27 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 86, 44 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 91, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 98, 5 ), //
                new ExpectedWarning( ReturnValueTestObject.class, 104, 5 ), //

                new ExpectedWarning( ConstructorTestObject.class, 19, 35 ), //
                new ExpectedWarning( ConstructorTestObject.class, 25, 35 ), //
                new ExpectedWarning( ConstructorTestObject.class, 39, 35 ), //
                new ExpectedWarning( ConstructorTestObject.class, 53, 53 ), //

                new ExpectedWarning( DefectConstructorTest.class ), //

                new ExpectedWarning( ArraysTestObject.class, 35, 23 ), //
                new ExpectedWarning( ArraysTestObject.class, 50, 23 ), //
                new ExpectedWarning( ArraysTestObject.class, 65, 23 ), //
                new ExpectedWarning( ArraysTestObject.class, 80, 23 ), //
                new ExpectedWarning( ArraysTestObject.class, 94, 8 ), //
                new ExpectedWarning( ArraysTestObject.class, 109, 33 ), //
                new ExpectedWarning( ArraysTestObject.class, 124, 35 ), //

                new ExpectedWarning( ClassTestObject.class, 14, 5 ), //
                new ExpectedWarning( ClassTestObject.class, 28, 34 ), //
                new ExpectedWarning( ClassTestObject.class, 38, 37 ), //
                new ExpectedWarning( ClassTestObject.class, 60, 33 ), //
                new ExpectedWarning( ClassTestObject.class, 68, 37 ), //
                new ExpectedWarning( ClassTestObject.class, 122, 44 ), //
                new ExpectedWarning( ClassTestObject.class, 129, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 133, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 144, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 150, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 168, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 205, 44 ), //
                new ExpectedWarning( ClassTestObject.class, 248, 48 ), //
                new ExpectedWarning( ClassTestObject.class, 270, 33 ), //
                new ExpectedWarning( ClassTestObject.class, 279, 37 ), //
                new ExpectedWarning( ClassTestObject.class, 296, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 302, 9 ), //
                new ExpectedWarning( ClassTestObject.class, 315, 5 ) //
                );
    }
}
