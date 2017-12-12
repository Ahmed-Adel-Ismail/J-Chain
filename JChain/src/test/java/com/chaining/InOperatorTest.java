package com.chaining;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static com.chaining.InternalConfiguration.getInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InOperatorTest {

    @Test
    public void testWithValidCollectionAndValidItemThenReturnTrue() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        InternalConfiguration config = getInstance("testWithValidCollectionAndValidItemThenReturnTrue");
        assertTrue(new InOperator<>(collection, new IsEqualComparator<Integer>(), config).test(2));
    }

    @Test
    public void testWithValidCollectionAndInvalidItemThenReturnFalse() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        InternalConfiguration config = getInstance("testWithValidCollectionAndInvalidItemThenReturnFalse");
        assertFalse(new InOperator<>(collection, new IsEqualComparator<Integer>(), config).test(5));
    }

    @Test
    public void testWithValidCollectionAndNullItemThenReturnFalse() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        InternalConfiguration config = getInstance("testWithValidCollectionAndNullItemThenReturnFalse");
        assertFalse(new InOperator<>(collection, new IsEqualComparator<Integer>(), config).test(null));
    }

    @Test
    public void testWithNullCollectionAndValidItemThenReturnFalse() {
        InternalConfiguration config = getInstance("testWithNullCollectionAndValidItemThenReturnFalse");
        assertFalse(new InOperator<>(null, new IsEqualComparator<Integer>(), config).test(2));
    }
}