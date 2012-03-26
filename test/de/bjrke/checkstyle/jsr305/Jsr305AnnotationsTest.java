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
        Jsr305AnnationsTestUtil.check(

        new ExpectedWarning(ParameterTestObject.class, 15, 45), //
                new ExpectedWarning(ParameterTestObject.class, 20, 46), //
                new ExpectedWarning(ParameterTestObject.class, 20, 63), //
                new ExpectedWarning(ParameterTestObject.class, 50, 42), //
                new ExpectedWarning(ParameterTestObject.class, 56, 28), //
                new ExpectedWarning(ParameterTestObject.class, 74, 36), //

                new ExpectedWarning(PrimitivesTestObject.class, 10, 38), //
                new ExpectedWarning(PrimitivesTestObject.class, 15, 39), //
                new ExpectedWarning(PrimitivesTestObject.class, 20, 43), //
                new ExpectedWarning(PrimitivesTestObject.class, 25, 5), //
                new ExpectedWarning(PrimitivesTestObject.class, 31, 5), //
                new ExpectedWarning(PrimitivesTestObject.class, 37, 5), //
                new ExpectedWarning(PrimitivesTestObject.class, 43, 5), //
                new ExpectedWarning(PrimitivesTestObject.class, 90, 35), //

                new ExpectedWarning(ReturnValueTestObject.class, 29, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 35, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 56, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 63, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 83, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 86, 27), //
                new ExpectedWarning(ReturnValueTestObject.class, 86, 44), //
                new ExpectedWarning(ReturnValueTestObject.class, 91, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 98, 5), //
                new ExpectedWarning(ReturnValueTestObject.class, 104, 5), //

                new ExpectedWarning(ConstructorTestObject.class, 19, 35), //
                new ExpectedWarning(ConstructorTestObject.class, 25, 35), //
                new ExpectedWarning(ConstructorTestObject.class, 39, 35), //
                new ExpectedWarning(ConstructorTestObject.class, 53, 53), //

                new ExpectedWarning(DefectConstructorTest.class), //

                new ExpectedWarning(ArraysTestObject.class, 41, 23), //
                new ExpectedWarning(ArraysTestObject.class, 56, 23), //

                new ExpectedWarning(ArraysTestObject.class, 71, 23), //
                new ExpectedWarning(ArraysTestObject.class, 86, 23), //
                new ExpectedWarning(ArraysTestObject.class, 100, 8), //
                new ExpectedWarning(ArraysTestObject.class, 115, 33), //
                new ExpectedWarning(ArraysTestObject.class, 130, 35), //

                new ExpectedWarning(ClassTestObject.class, 14, 9), //
                new ExpectedWarning(ClassTestObject.class, 28, 41), //
                new ExpectedWarning(ClassTestObject.class, 38, 44), //
                new ExpectedWarning(ClassTestObject.class, 58, 40), //
                new ExpectedWarning(ClassTestObject.class, 65, 44), //
                new ExpectedWarning(ClassTestObject.class, 109, 51), //
                new ExpectedWarning(ClassTestObject.class, 115, 17), //
                new ExpectedWarning(ClassTestObject.class, 119, 17), //
                new ExpectedWarning(ClassTestObject.class, 130, 17), //
                new ExpectedWarning(ClassTestObject.class, 136, 17), //
                new ExpectedWarning(ClassTestObject.class, 154, 17), //
                new ExpectedWarning(ClassTestObject.class, 189, 51), //
                new ExpectedWarning(ClassTestObject.class, 224, 59), //
                new ExpectedWarning(ClassTestObject.class, 244, 40), //
                new ExpectedWarning(ClassTestObject.class, 252, 44), //
                new ExpectedWarning(ClassTestObject.class, 266, 17), //
                new ExpectedWarning(ClassTestObject.class, 272, 17)
        );
    }
}
