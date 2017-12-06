package com.chaining;

import org.junit.Test;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        TestClass testClass = Chain.<TestClass>optional(null)
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

        Chain.optional(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void defaultIfEmptyWithTestClassValueForNotNullChainThenReturnOriginalTestClass() {

        TestClass testClass = Chain.optional(new TestClass("1"))
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("1", testClass.text);
    }

    @Test
    public void defaultIfEmptyWithTestClassValueForNullChainThenReturnTestClass() {

        TestClass testClass = Chain.<TestClass>optional(null)
                .defaultIfEmpty(new TestClass("2"))
                .call();

        assertEquals("2", testClass.text);
    }

    @Test
    public void mapWithTestClassTwoForTestClassChainThenReturnTestClassTwo() {

        TestClassTwo testClassTwo = Chain.optional(new TestClass())
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

        TestClassTwo testClassTwo = Chain.<TestClass>optional(null)
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

        Chain.optional(new TestClass())
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void logWithSelfAsSourceThenReturnSelfAsSource() {
        Optional<?> source = Chain.optional(0);
        Logger<?, ?> logger = source.log("1");
        assertEquals(source, logger.source);
    }

    @Test
    public void logWithStringTagThenReturnLoggerWithThatTag() {
        Optional<?> source = Chain.optional(0);
        Logger<?, ?> logger = source.log("1");
        assertEquals("1", logger.tag);
    }

    @Test
    public void debugWhileChainConfigIsDebuggingThenInvokeDebug() {

        InternalConfiguration config = InternalConfiguration
                .getInstance("debugWhileChainConfigIsDebuggingThenInvokeDebug");
        config.setDebugging(true);

        final boolean[] result = {false};

        new Optional<>(new TestClass(), config)
                .debug(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                });

        assertTrue(result[0]);
    }

    @Test
    public void debugWhileChainConfigIsNotDebuggingThenDoNotInvokeDebug() {

        InternalConfiguration config = InternalConfiguration
                .getInstance("debugWhileChainConfigIsNotDebuggingThenDoNotInvokeDebug");
        config.setDebugging(false);

        final boolean[] result = {false};

        new Optional<>(new TestClass(), config)
                .debug(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                });

        assertFalse(result[0]);
    }

    @Test
    public void runOptionalProxyTesterWithNonNullValue() {
        Optional<Integer> optional = new Optional<>(0,InternalConfiguration
                        .getInstance("runOptionalProxyTesterWithNonNullValue"));

        new ProxyTester<>(optional, 1).run();
    }

    @Test
    public void runOptionalProxyTesterWithNullValue() {
        Optional<Integer> optional = new Optional<>(null,InternalConfiguration
                .getInstance("runOptionalProxyTesterWithNullValue"));

        new ProxyTester<>(optional, 1).run();
    }

}