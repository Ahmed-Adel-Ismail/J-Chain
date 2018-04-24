package com.chaining;

import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
    public void callOrCrashWithNotEmptyOptionalThenReturnStoredItem() {

        TestClass testClass = Chain.optional(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) {
                        testClass.text = "1";
                    }
                })
                .callOrCrash();

        assertEquals("1", testClass.text);
    }


    @Test(expected = NoSuchElementException.class)
    public void callOrCrashWithNotEmptyOptionalThenThrowNoSuchElement() {

        TestClass testClass = Chain.<TestClass>optional(null)
                .callOrCrash();
    }

    @Test
    public void flatMapMaybeForNonNullChainThenReturnNonEmptyMaybe() {

        TestClassTwo testClass = Chain.optional(new TestClass("1"))
                .flatMapMaybe(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) throws Exception {
                        return new TestClassTwo(testClass.text);
                    }
                })
                .blockingGet();

        assertEquals("1", testClass.text);
    }


    @Test
    public void flatMapMaybeForNullChainThenReturnEmptyMaybe() {

        TestClassTwo testClass = Chain.optional((TestClass) null)
                .flatMapMaybe(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(TestClass testClass) throws Exception {
                        return new TestClassTwo(testClass.text);
                    }
                })
                .blockingGet();

        assertNull(testClass);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatMapMaybeWithCrashThenThrowException() {

        Chain.optional(new TestClass())
                .flatMapMaybe(new Function<TestClass, Object>() {
                    @Override
                    public Object apply(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void invokeForNonNullChainThenExecuteInvoke() {
        final boolean[] result = {false};
        Chain.optional(new TestClass())
                .invoke(new Action() {
                    @Override
                    public void run() {
                        result[0] = true;
                    }
                });

        assertTrue(result[0]);
    }


    @Test
    public void invokeWithNullChainThenDoNothing() {

        final boolean[] result = {false};
        Chain.optional(null)
                .invoke(new Action() {
                    @Override
                    public void run() {
                        result[0] = true;
                    }
                });

        assertFalse(result[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeWithCrashThenThrowException() {

        Chain.optional(new TestClass())
                .invoke(new Action() {
                    @Override
                    public void run() throws Exception {
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
    public void toWithTestClassTwoForTestClassChainThenReturnTestClassTwo() {

        TestClassTwo testClassTwo = Chain.optional(new TestClass())
                .to(new TestClassTwo("1"))
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", testClassTwo.text);
    }


    @Test
    public void toWithNullChainThenDoNothing() {

        TestClassTwo testClassTwo = Chain.<TestClass>optional(null)
                .to(new TestClassTwo("1"))
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", testClassTwo.text);
    }

    @Test
    public void toCallableWithTestClassTwoForTestClassChainThenReturnTestClassTwo() {

        TestClassTwo testClassTwo = Chain.optional(new TestClass())
                .to(new Callable<TestClassTwo>() {
                    @Override
                    public TestClassTwo call() {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", testClassTwo.text);
    }


    @Test
    public void toCallableWithNullChainThenDoNothing() {

        TestClassTwo testClassTwo = Chain.<TestClass>optional(null)
                .to(new Callable<TestClassTwo>() {
                    @Override
                    public TestClassTwo call() {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", testClassTwo.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void toCallableWithCrashThenThrowException() {

        Chain.optional(new TestClass())
                .to(new Callable<TestClassTwo>() {
                    @Override
                    public TestClassTwo call() {
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
    public void whenWithNonNullValueThenReturnOperatingCondition() {

        boolean result = Chain.optional(1)
                .when(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                })
                .thenMap(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return true;
                    }
                })
                .defaultIfEmpty(false)
                .call();

        assertTrue(result);
    }

    @Test
    public void whenWithNullValueThenReturnNonOperatingCondition() {

        boolean result = Chain.optional((Integer) null)
                .when(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                })
                .thenMap(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return false;
                    }
                })
                .defaultIfEmpty(true)
                .call();

        assertTrue(result);
    }

    @Test
    public void whenNotWithNonNullValueThenReturnOperatingCondition() {

        boolean result = Chain.optional(1)
                .whenNot(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return false;
                    }
                })
                .thenMap(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return true;
                    }
                })
                .defaultIfEmpty(false)
                .call();

        assertTrue(result);
    }

    @Test
    public void whenNotWithNullValueThenReturnNonOperatingCondition() {

        boolean result = Chain.optional((Integer) null)
                .whenNot(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return false;
                    }
                })
                .thenMap(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return false;
                    }
                })
                .defaultIfEmpty(true)
                .call();

        assertTrue(result);
    }

    @Test
    public void whenInWithValidCollectionThenReturnValidCondition() {

        boolean result = Chain.optional(0)
                .whenIn(Arrays.asList(0, 1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertTrue(result);
    }

    @Test
    public void whenInWithNullItemAndValidCollectionThenReturnInValidCondition() {
        boolean result = Chain.optional((Integer) null)
                .whenIn(Arrays.asList(0, null, 2, null, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertFalse(result);
    }

    @Test
    public void whenInWithNullItemAndInValidCollectionThenReturnInValidCondition() {
        boolean result = Chain.optional((Integer) null)
                .whenIn(Arrays.asList(0, 1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertFalse(result);
    }


    @Test
    public void whenInWithInValidCollectionThenReturnInValidCondition() {
        boolean result = Chain.optional(0)
                .whenIn(Arrays.asList(1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertFalse(result);
    }

    @Test
    public void whenNotInWithValidCollectionThenReturnInValidCondition() {

        boolean result = Chain.optional(0)
                .whenNotIn(Arrays.asList(0, 1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertFalse(result);
    }

    @Test
    public void whenNotInWithNullItemThenReturnInValidCondition() {

        boolean result = Chain.optional((Integer)null)
                .whenNotIn(Arrays.asList(0, 1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertFalse(result);
    }

    @Test
    public void whenNotInWithInValidCollectionThenReturnValidCondition() {

        boolean result = Chain.optional(0)
                .whenNotIn(Arrays.asList(1, 2, 3, 4))
                .thenTo(true)
                .defaultIfEmpty(false)
                .call();

        assertTrue(result);
    }

    @Test
    public void toMaybeWithNullItemThenReturnEmptyMaybe(){
        boolean result = Chain.optional((Integer)null)
                .toMaybe()
                .isEmpty()
                .blockingGet();

        assertTrue(result);
    }

    @Test
    public void toMaybeWithNonNullItemThenReturnNonEmptyMaybe(){
        boolean result = Chain.optional(10)
                .toMaybe()
                .isEmpty()
                .blockingGet();

        assertFalse(result);
    }

    @Test
    public void runOptionalProxyTesterWithNonNullValue() {
        Optional<Integer> optional = new Optional<>(0, InternalConfiguration
                .getInstance("runOptionalProxyTesterWithNonNullValue"));

        new ProxyTester<>(optional, 1).run();
    }

    @Test
    public void runOptionalProxyTesterWithNullValue() {
        Optional<Integer> optional = new Optional<>(null, InternalConfiguration
                .getInstance("runOptionalProxyTesterWithNullValue"));

        new ProxyTester<>(optional, 1).run();
    }

}