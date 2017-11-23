package com.chaining;

import org.junit.Test;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;

public class OptionalTest {

    @Test
    public void applyWithTestClassValueForNonNullChainThenInvokeApply() {

        TestClass testClass = Chain.optional(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) {
                        testClass.text = "1";
                    }
                })
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("1", testClass.text);
    }


    @Test
    public void applyWithNullChainThenDoNothing() {

        TestClass testClass = new Optional<>(Chain.<TestClass>let(null))
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) {
                        testClass.text = "1";
                    }
                })
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("2", testClass.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void applyWithCrashThenThrowException() {

        new Optional<>(Chain.let(new TestClass()))
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void defaultIfEmptyWithTestClassValueForNotNullChainThenReturnOriginalTestClass() {

        TestClass testClass = new Optional<>(Chain.let(new TestClass("1")))
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("1", testClass.text);
    }

    @Test
    public void defaultIfEmptyWithTestClassValueForNullChainThenReturnTestClass() {

        TestClass testClass = new Optional<>(Chain.<TestClass>let(null))
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("2", testClass.text);
    }

    @Test
    public void mapWithTestClassTwoForTestClassChainThenReturnTestClassTwo() {

        TestClassTwo testClassTwo = new Optional<>(Chain.let(new TestClass()))
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", testClassTwo.text);
    }


    @Test
    public void mapWithNullChainThenDoNothing() {

        TestClassTwo testClassTwo = new Optional<>(Chain.<TestClass>let(null))
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", testClassTwo.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void mapWithCrashThenThrowException() {

        new Optional<>(Chain.let(new TestClass()))
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) {
                        throw new UnsupportedOperationException();
                    }
                });
    }

}