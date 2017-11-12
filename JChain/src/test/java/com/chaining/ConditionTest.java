package com.chaining;


import org.junit.Test;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionTest {

    @Test
    public void whenWithTruePredicateThenInvokeApply() {
        final boolean[] result = {false};
        Chain.let(new TestClass())
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
    public void whenWithFalsePredicateThenDoNotInvokeApply() {
        final boolean[] result = {false};
        TestClass testClass = Chain.let(new TestClass())
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

    @Test(expected = UnsupportedOperationException.class)
    public void whenWithExceptionThenThrowException() {
        Chain.let(new TestClass())
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
    public void applyWithExceptionThenThrowException() {
        Chain.let(new TestClass())
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


}