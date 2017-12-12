package com.chaining;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsEqualComparatorTest {

    @Test
    public void testWithNullFirstParameterAndNonNullSecondParameterThenReturnFalse() {
        assertFalse(new IsEqualComparator<Integer>().test(null, 0));
    }

    @Test
    public void testWithNonNullFirstParameterAndNullSecondParameterThenReturnFalse() {
        assertFalse(new IsEqualComparator<Integer>().test(0, null));
    }

    @Test
    public void testWithNullFirstParameterAndNullSecondParameterThenReturnTrue() {
        assertTrue(new IsEqualComparator<Integer>().test(null, null));
    }

    @Test
    public void testWithNonEqualParametersThenReturnFalse() {
        assertFalse(new IsEqualComparator<Integer>().test(1, 0));
    }

    @Test
    public void testWithEqualParametersThenReturnTrue() {
        assertTrue(new IsEqualComparator<Integer>().test(0, 0));
    }

}