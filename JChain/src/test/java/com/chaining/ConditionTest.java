package com.chaining;

import org.junit.Test;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionTest {

    private final ChainConfigurationImpl chainConfiguration = ChainConfigurationImpl
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
    public void whenWithTruePredicateThenInvokeThenWithAction() {
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
                .then(new Action() {

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
    public void whenWithFalsePredicateThenDoNotInvokeThenWithAction() {
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
                .then(new Action() {

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
    public void whenNotWithTruePredicateThenDoNotInvokeThenWithAction() {
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
                .then(new Action() {

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
    public void whenNotWithFalsePredicateThenInvokeThenWithAction() {
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
                .then(new Action() {

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
    public void thenActionWithExceptionThenThrowException() {
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
                .then(new Action() {

                    @Override
                    public void run() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .call();

    }

}