package com.chaining;



import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ChainTest {

    @Test
    public void guardWithSetTextOnTestClassThenFindTextValueUpdated() {
        TestClass testClass = Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                }).onErrorReturnItem(new TestClass("!!"))
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void debugWhileChainConfigIsDebuggingThenInvokeDebug() {

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("debugWhileChainConfigIsDebuggingThenInvokeDebug");
        config.setDebugging(true);

        final boolean[] result = {false};

        new Chain<>(new TestClass(), config)
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

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("debugWhileChainConfigIsNotDebuggingThenDoNotInvokeDebug");
        config.setDebugging(false);

        final boolean[] result = {false};

        new Chain<>(new TestClass(), config)
                .debug(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        result[0] = true;
                    }
                });

        assertFalse(result[0]);
    }


    @Test
    public void applyWithSetTextOnTestClassThenFindTextValueUpdated() {
        TestClass testClass = Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .call();

        assertEquals("!", testClass.text);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void applyWithExceptionThenThrowException() {
        Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .call();
    }


    @Test
    public void mapWithTestClassToTestClassTwoThenReturnTestClassTwo() {
        TestClassTwo testClassTwo = Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                }).map(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        TestClassTwo testClassTwo = new TestClassTwo();
                        testClassTwo.setText(testClass.text);
                        return testClassTwo;
                    }
                }).call();

        assertEquals("!", testClassTwo.text);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void mapWithExceptionThenThrowException() {
        Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .map(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .call();


    }

    @Test
    public void flatMapWithTestClassToTestClassTwoThenReturnTestClassTwo() {
        TestClassTwo testClassTwo = Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .flatMap(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        TestClassTwo testClassTwo = new TestClassTwo();
                        testClassTwo.setText(testClass.text);
                        return testClassTwo;
                    }
                });

        assertEquals("!", testClassTwo.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatMapWithExceptionThenThrowException() {
        Chain.let(new TestClass())
                .apply(new Consumer<TestClass>() {
                    @Override
                    public void accept(@NonNull TestClass testClass) throws Exception {
                        testClass.setText("!");
                    }
                })
                .flatMap(new Function<TestClass, TestClassTwo>() {

                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });


    }


    @Test
    public void inWithValidCollectionThenReturnTrue() {
        Collection<TestClass> testClasses = new ArrayList<>(2);
        TestClass testClass = new TestClass();
        testClasses.add(testClass);
        testClasses.add(new TestClass());
        testClasses.add(null);

        boolean result = Chain.let(testClass)
                .in(testClasses)
                .call()
                .getValue1();

        assertTrue(result);

    }

    @Test
    public void inWithInvalidCollectionThenReturnFalse() {
        Collection<TestClass> testClasses = new ArrayList<>(2);
        TestClass testClass = new TestClass();
        testClasses.add(new TestClass());

        boolean result = Chain.let(testClass)
                .in(testClasses)
                .call()
                .getValue1();

        assertFalse(result);

    }

    @Test
    public void inWithTrueBiPredicateThenReturnTrue() {
        Collection<TestClass> testClasses = new ArrayList<>(2);
        TestClass testClass = new TestClass();
        testClasses.add(testClass);
        testClasses.add(new TestClass());
        testClasses.add(null);

        boolean result = Chain.let(testClass)
                .in(testClasses, new BiPredicate<TestClass, TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass original, @NonNull TestClass collectionItem) {
                        return original.equals(collectionItem);
                    }
                })
                .call()
                .getValue1();

        assertTrue(result);

    }

    @Test
    public void inWithFalseBiPredicateThenReturnTrue() {
        Collection<TestClass> testClasses = new ArrayList<>(2);
        TestClass testClass = new TestClass();
        testClasses.add(testClass);
        testClasses.add(new TestClass());
        testClasses.add(null);

        boolean result = Chain.let(testClass)
                .in(testClasses, new BiPredicate<TestClass, TestClass>() {

                    @Override
                    public boolean test(@NonNull TestClass original, @NonNull TestClass collectionItem) {
                        return false;
                    }
                })
                .call()
                .getValue1();

        assertFalse(result);

    }

    @Test
    public void defaultIfEmptyWithTestClassTwoValueForNullChainThenReturnTestClassTwo() {
        TestClassTwo testClassTwo = Chain.let(new TestClass())
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        return null;
                    }
                })
                .defaultIfEmpty(new TestClassTwo())
                .call();

        assertNotNull(testClassTwo);
    }

    @Test
    public void defaultIfEmptyWithTestClassTwoValueForNotNullChainThenReturnOriginalTestClassTwo() {
        TestClassTwo testClassTwo = Chain.let(new TestClass())
                .map(new Function<TestClass, TestClassTwo>() {
                    @Override
                    public TestClassTwo apply(@NonNull TestClass testClass) throws Exception {
                        return new TestClassTwo("1");
                    }
                })
                .defaultIfEmpty(new TestClassTwo("2"))
                .call();

        assertEquals("1", testClassTwo.text);
    }

}


class TestClass {

    String text;

    TestClass() {

    }

    TestClass(String text) {
        this.text = text;
    }

    void setText(String text) {
        this.text = text;
    }
}


class TestClassTwo {
    String text;

    TestClassTwo() {
    }

    TestClassTwo(String text) {
        this.text = text;
    }

    void setText(String text) {
        this.text = text;
    }
}