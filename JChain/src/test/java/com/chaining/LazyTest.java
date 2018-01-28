package com.chaining;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.functions.Function;

import static org.junit.Assert.assertTrue;

public class LazyTest {

    @Test
    public void deferWithCallableThenAssignCallableVariableOnly() {
        Lazy<Boolean> lazyBoolean = Lazy.defer(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });

        assertTrue(lazyBoolean.delayedAction != null && lazyBoolean.item == null);

    }

    @Test
    public void callWithValidCallableThenAssignCallableVariableAndItemVariable() {
        Lazy<Boolean> lazyBoolean = Lazy.defer(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });

        lazyBoolean.call();

        assertTrue(lazyBoolean.delayedAction != null && lazyBoolean.item != null);

    }

    @Test
    public void callWithValidCallableThenReturnCallableResult() {
        Lazy<Boolean> lazyBoolean = Lazy.defer(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });

        assertTrue(lazyBoolean.call());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void callWithCrashingCallableThenCrash() {
        Lazy<Boolean> lazyBoolean = Lazy.defer(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                throw new UnsupportedOperationException();
            }
        });

        lazyBoolean.call();

    }

    @Test
    public void flatMapWithValidMapperThenReturnValidResult() {
        Lazy<Integer> lazyBoolean = Lazy.defer(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });

        boolean result = lazyBoolean.flatMap(new Function<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer integer) throws Exception {
                return integer.equals(1);
            }
        });

        assertTrue(result);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void flatMapWithCrashingMapperThenCrash() {
        Lazy<Integer> lazyBoolean = Lazy.defer(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });

        lazyBoolean.flatMap(new Function<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer integer) throws Exception {
                throw new UnsupportedOperationException();
            }
        });
    }

}