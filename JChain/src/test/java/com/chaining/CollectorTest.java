package com.chaining;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectorTest {

    private final InternalConfiguration configuration = InternalConfiguration
            .getInstance("CollectorTest");


    @Test
    public void andWithMultipleItemsThenAppendAllToTheList() {
        Collector<Boolean> result = new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true);

        assertTrue(result.items.get(0) && !result.items.get(1) && result.items.get(2));
    }

    @Test
    public void collectThenReturnAllItemsInListChain() {
        List<Boolean> result = new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true)
                .toList()
                .call();

        assertTrue(result.get(0) && !result.get(1) && result.get(2));
    }

    @Test
    public void flatMapThenReturnFunctionResult() {
        boolean result = new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true)
                .flatMap(new Function<List<Boolean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<Boolean> list) throws Exception {
                        return list.get(0) && list.get(1) && list.get(2);
                    }
                });

        assertFalse(result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatMapWithCrashingFunctionThenThrowException() {
        new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true)
                .flatMap(new Function<List<Boolean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<Boolean> list) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void reduceWithMultipleItemsThenReturnFunctionResult() {
        boolean result = new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true)
                .reduce(new BiFunction<Boolean, Boolean, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Boolean itemOne, @NonNull Boolean itemTwo) {
                        return itemOne.equals(itemTwo);
                    }
                })
                .call();

        assertFalse(result);
    }

    @Test
    public void reduceWithOneItemThenReturnThisItemInResult() {
        boolean result = new Collector<Boolean>(configuration)
                .and(true)
                .reduce(new BiFunction<Boolean, Boolean, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Boolean itemOne, @NonNull Boolean itemTwo) {
                        return itemOne.equals(itemTwo);
                    }
                })
                .call();

        assertTrue(result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reduceWithCrashingFunctionThenThrowException() {
        new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(true)
                .reduce(new BiFunction<Boolean, Boolean, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Boolean itemOne, @NonNull Boolean itemTwo) {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void mapMultipleItemsThenInvokeOnAllItems() {
        List<Integer> result = new Collector<Boolean>(configuration)
                .and(true)
                .and(false)
                .and(false)
                .map(new Function<Boolean, Integer>() {

                    @Override
                    public Integer apply(@NonNull Boolean trueValue) throws Exception {
                        return trueValue ? 1 : 0;
                    }
                })
                .toList()
                .call();

        assertTrue(result.get(0) == 1 && result.get(1) == 0 && result.get(2) == 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void mapWithCrashingFunctionThenThrowException() {
        new Collector<Boolean>(configuration)
                .and(true)
                .map(new Function<Boolean, Integer>() {

                    @Override
                    public Integer apply(@NonNull Boolean trueValue) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void logWithSelfAsSourceThenReturnSelfAsSource() {
        Collector<?> source = Chain.let(0).collect(Integer.class);
        Logger<?, ?> logger = source.log("1");
        assertEquals(source, logger.source);
    }

    @Test
    public void logWithStringTagThenReturnLoggerWithThatTag() {
        Collector<?> source = Chain.let(0).collect(Integer.class);
        Logger<?, ?> logger = source.log("1");
        assertEquals("1", logger.tag);
    }

    @Test
    public void runCollectorProxyTester(){
        Collector<Integer> collector =
                new Collector<>(InternalConfiguration.getInstance("runCollectorProxyTester"));

        collector.and(1).and(2).and(3);

        new ProxyTester<>(collector, Arrays.asList(4,5,6)).run();
    }
}