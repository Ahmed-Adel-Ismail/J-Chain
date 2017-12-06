package com.chaining;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionTest {

    private final InternalConfiguration chainConfiguration = InternalConfiguration
            .getInstance("ConditionTest");

    @Test
    public void whenWithTruePredicateThenInvokeThenWithConsumer() {
        final boolean[] result = {false};
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertTrue(result[0]);
    }

    @Test
    public void whenWithTruePredicateThenInvokeAction() {
        final boolean[] result = {false};
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");
                    }
                })
                .invoke(new Action() {

                    @Override
                    public void run() throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertTrue(result[0]);
    }

    @Test
    public void whenWithFalsePredicateThenDoNotInvokeThenWithConsumer() {
        final boolean[] result = {false};
        TestClass testClass = new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertFalse(result[0]);
    }

    @Test
    public void whenWithFalsePredicateThenDoNotInvokeAction() {
        final boolean[] result = {false};
        TestClass testClass = new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .invoke(new Action() {

                    @Override
                    public void run() throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertFalse(result[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                    }
                })
                .call();

    }

    @Test(expected = UnsupportedOperationException.class)
    public void thenConsumerWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");

                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .call();

    }

    @Test
    public void whenNotWithTruePredicateThenDoNotInvokeThenWithConsumer() {
        final boolean[] result = {false};
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .whenNot(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertFalse(result[0]);
    }

    @Test
    public void whenNotWithTruePredicateThenDoNotInvokeAction() {
        final boolean[] result = {false};
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .whenNot(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");
                    }
                })
                .invoke(new Action() {

                    @Override
                    public void run() throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertFalse(result[0]);
    }

    @Test
    public void whenNotWithFalsePredicateThenInvokeThenWithConsumer() {
        final boolean[] result = {false};
        TestClass testClass = new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .whenNot(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertTrue(result[0]);
    }

    @Test
    public void whenNotWithFalsePredicateThenInvokeAction() {
        final boolean[] result = {false};
        TestClass testClass = new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .whenNot(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .invoke(new Action() {

                    @Override
                    public void run() throws Exception {
                        result[0] = true;
                    }
                })
                .call();

        assertTrue(result[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenNotWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .whenNot(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .then(new Consumer<TestClass>() {

                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                    }
                })
                .call();

    }

    @Test(expected = UnsupportedOperationException.class)
    public void invokeActionWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .when(new Predicate<TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass testClass) throws Exception {
                        return testClass.text.equals("!");

                    }
                })
                .invoke(new Action() {

                    @Override
                    public void run() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .call();

    }


    @Test
    public void thenMapWithValidConditionThenReturnAnOptionalContainingMappedValue() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return true;
                    }
                })
                .thenMap(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(TestClass testClass) throws Exception {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", result.text);


    }

    @Test
    public void thenMapWithInvalidConditionThenReturnAnOptionalContainingNull() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .thenMap(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(TestClass testClass) throws Exception {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", result.text);


    }

    @Test(expected = UnsupportedOperationException.class)
    public void thenMapWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return true;
                    }
                })
                .thenMap(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });

    }

    /////////////////////

    @Test
    public void thenToItemWithValidConditionThenReturnAnOptionalContainingItem() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return true;
                    }
                })
                .thenTo(new TestClassTwo("1"))
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", result.text);


    }

    @Test
    public void thenToItemWithInvalidConditionThenReturnAnOptionalContainingNull() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .thenTo(new TestClassTwo("1"))
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", result.text);


    }

    @Test
    public void thenToCallableWithValidConditionThenReturnAnOptionalContainingMappedValue() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return true;
                    }
                })
                .thenTo(new Callable<TestClassTwo>() {

                    @Override
                    public TestClassTwo call() throws Exception {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", result.text);


    }

    @Test
    public void thenToCallableWithInvalidConditionThenReturnAnOptionalContainingNull() {

        TestClassTwo result = new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return false;
                    }
                })
                .thenTo(new Callable<TestClassTwo>() {

                    @Override
                    public TestClassTwo call() throws Exception {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("2", result.text);


    }

    @Test(expected = UnsupportedOperationException.class)
    public void thenToCallableWithExceptionThenThrowException() {
        new Chain<>(new TestClass(), chainConfiguration)
                .when(new Predicate<TestClass>() {
                    @Override
                    public boolean test(TestClass testClass) throws Exception {
                        return true;
                    }
                })
                .thenTo(new Callable<TestClassTwo>() {

                    @Override
                    public TestClassTwo call() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });

    }

    @Test
    public void logWithSelfAsSourceThenReturnSelfAsSource() {
        Condition<?> source = Chain.let(0).when(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return false;
            }
        });
        Logger<?, ?> logger = source.log("1");
        assertEquals(source, logger.source);
    }

    @Test
    public void logWithStringTagThenReturnLoggerWithThatTag() {
        Condition<?> source = Chain.let(0).when(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return false;
            }
        });
        Logger<?, ?> logger = source.log("1");
        assertEquals("1", logger.tag);
    }

    @Test
    public void runNormalConditionProxyTester() {
        Condition<Integer> condition = Chain.let(0)
                .when(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                });

        new ProxyTester<>(condition.proxy(), 1).run();
    }

    @Test
    public void runNegatedConditionProxyTester() {
        Condition<Integer> condition = Chain.let(0)
                .whenNot(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                });

        new ProxyTester<>(condition.proxy(), 1).run();
    }
}