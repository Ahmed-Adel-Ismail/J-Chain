package com.chaining;

import org.javatuples.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ChainTest {


    @Test
    public void callWithCallableThenReturnTheCallableResult() {
        TestClass testClass = Chain.call(new Callable<TestClass>() {
            @Override
            public TestClass call() throws Exception {
                return new TestClass("!");
            }
        }).call();

        assertEquals("!", testClass.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void callWithCrashingCallableThenThrowException() {
        Chain.call(new Callable<TestClassTwo>() {
            @Override
            public TestClassTwo call() throws Exception {
                throw new UnsupportedOperationException();
            }
        });
    }

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
    public void guardWithCallableThenReturnTheValueOfCall() {
        TestClass testClass = Guard
                .call(new Callable<TestClass>() {
                    @Override
                    public TestClass call() throws Exception {
                        return new TestClass("!");
                    }
                })
                .onErrorReturnItem(new TestClass("!!"))
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void guardWithCrashingCallableThenReturnTheValueOfOnErrorReturnItem() {
        TestClass testClass = Guard
                .call(new Callable<TestClass>() {
                    @Override
                    public TestClass call() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorReturnItem(new TestClass("!!"))
                .call();

        assertEquals("!!", testClass.text);
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
    public void applyConsumerWithSetTextOnTestClassThenFindTextValueUpdated() {
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
    public void applyConsumerWithExceptionThenThrowException() {
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
    public void invokeWithSetTextOnTestClassThenFindTextValueUpdated() {
        final boolean[] result = {false};
        Chain.let(new TestClass())
                .invoke(new Action() {
                    @Override
                    public void run() throws Exception {
                        result[0] = true;
                    }
                });

        assertTrue(result[0]);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void invokeWithExceptionThenThrowException() {
        Chain.let(new TestClass())
                .invoke(new Action() {
                    @Override
                    public void run() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
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


    @Test
    public void toWithSameTypeThenReturnNewChain() {
        TestClass result = Chain.let(new TestClass("1"))
                .to(new TestClass("2"))
                .call();

        assertEquals("2", result.text);
    }

    @Test
    public void toWithSameTypeCallableThenReturnNewChain() {

        TestClass result = Chain.let(new TestClass("1"))
                .to(new Callable<TestClass>() {
                    @Override
                    public TestClass call() throws Exception {
                        return new TestClass("2");
                    }
                })
                .call();

        assertEquals("2", result.text);

    }

    @Test
    public void toWithDifferentTypeThenReturnNewChain() {
        TestClassTwo result = Chain.let(new TestClass("1"))
                .to(new TestClassTwo("2"))
                .call();

        assertEquals("2", result.text);
    }

    @Test
    public void toWithDifferentTypeCallableThenReturnNewChain() {

        TestClassTwo result = Chain.let(new TestClass("1"))
                .to(new Callable<TestClassTwo>() {
                    @Override
                    public TestClassTwo call() throws Exception {
                        return new TestClassTwo("2");
                    }
                })
                .call();

        assertEquals("2", result.text);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void toWithCrashingTestClassCallableThenThrowException() {
        Chain.let(new TestClass())
                .to(new Callable<TestClass>() {
                    @Override
                    public TestClass call() throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }


    @Test
    public void andWithAnotherItemThenReturnACollectorWithTwoItems() {
        Collector<Boolean> result = Chain.let(true).and(false);
        assertTrue(result.items.get(0) && !result.items.get(1));
    }

    @Test
    public void pairWithAnotherItemThenReturnAPairOfTwoItemsInChain() {
        Chain<Pair<Boolean, Integer>> result = Chain.let(true).pair(0);
        assertTrue(result.call().getValue0() && result.call().getValue1() == 0);
    }

    @Test
    public void pairFunctionWithAnotherItemThenReturnAPairOfTwoItemsInChain() {
        Chain<Pair<Boolean, Integer>> result = Chain.let(true)
                .pair(new Function<Boolean, Integer>() {
                    @Override
                    public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                        return 0;
                    }
                });
        assertTrue(result.call().getValue0() && result.call().getValue1() == 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pairFunctionWithCrashThenThrowException() {
        Chain.let(true)
                .pair(new Function<Boolean, Integer>() {
                    @Override
                    public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }


    @Test
    public void collectWithOneItemThenReturnACollectorWithThisItemInList() {
        List<Integer> result = Chain.let(10)
                .collect(Integer.class)
                .toList()
                .call();

        assertEquals(10, (int) result.get(0));

    }


    @Test
    public void collectWithIterableThenReturnACollectorWithThisListItems() {

        List<Integer> result = Chain.let(Arrays.asList(1, 2, 3, 4, 5))
                .collect(Integer.class)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer * 10;
                    }
                })
                .toList()
                .call();

        assertTrue(result.size() == 5 && result.get(0) == 10 && result.get(4) == 50);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void collectWithWrongTypeThenThrowException() {
        Map map = new HashMap();
        Chain.let(map).collect(Integer.class);
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