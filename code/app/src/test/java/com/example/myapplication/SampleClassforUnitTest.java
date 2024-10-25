package com.example.myapplication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleClassforUnitTest {
    @Test
    public void testAddNums() {
        SampleTest sampleTest = new SampleTest();
        int result = sampleTest.addNums();
        assertEquals(3, result, "The addNums method should return 3");
    }

}
