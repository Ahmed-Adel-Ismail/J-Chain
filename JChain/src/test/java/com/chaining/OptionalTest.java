package com.chaining;

import org.junit.Test;

import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;

public class OptionalTest {

    @Test
    public void applyWithTestClassValueForNonNullChainThenInvokeApply() {

        TestClass testClass = new Optional<TestClass>(Chain.let(new TestClass()))
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
    public void applyWithTestClassValueForNullChainThenDoNothing() {

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

}