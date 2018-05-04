package com.chaining;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void deferWithFunctionThenCreateLazyInstanceAtCall() {
        String result = Lazy.defer(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return String.valueOf(integer);
            }
        }, 10).call();

        assertEquals("10", result);
    }

    @Test
    public void mapAndDoNotInvokeCallThenDoNotInitializeThem() {

        final boolean[] itemOneInitialized = {false};
        final boolean[] itemTwoInitialized = {false};
        final boolean[] itemThreeInitialized = {false};

        Lazy.defer(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                itemOneInitialized[0] = true;
                return 1;
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                itemTwoInitialized[0] = true;
                return "";
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                itemThreeInitialized[0] = true;
                return 2;
            }
        });

        assertFalse(itemOneInitialized[0]
                && itemTwoInitialized[0]
                && itemThreeInitialized[0]);
    }

    @Test
    public void mapAndInvokeCallThenDoInitializeThemOnCallInvocation() {

        final boolean[] itemOneInitialized = {false};
        final boolean[] itemTwoInitialized = {false};
        final boolean[] itemThreeInitialized = {false};

        int result = Lazy.defer(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                itemOneInitialized[0] = true;
                return 1;
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                itemTwoInitialized[0] = true;
                return String.valueOf(integer + 1);
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                itemThreeInitialized[0] = true;
                return Integer.parseInt(s) + 1;
            }
        }).call();

        assertTrue(itemOneInitialized[0]
                && itemTwoInitialized[0]
                && itemThreeInitialized[0]
                && result == 3);
    }

    @Test
    public void applyAndDoNotInvokeCallThenDoNotInitializeThem() {

        final boolean[] itemOneInitialized = {false};
        final boolean[] itemTwoInitialized = {false};
        final boolean[] itemThreeInitialized = {false};

        Lazy.defer(new Callable<Integer[]>() {
            @Override
            public Integer[] call() throws Exception {
                itemOneInitialized[0] = true;
                return new Integer[]{1};
            }
        }).apply(new Consumer<Integer[]>() {
            @Override
            public void accept(Integer[] integers) throws Exception {
                itemTwoInitialized[0] = true;
                integers[0] = 2;
            }
        }).apply(new Consumer<Integer[]>() {
            @Override
            public void accept(Integer[] integers) throws Exception {
                itemThreeInitialized[0] = true;
                integers[0] = 3;
            }
        });

        assertFalse(itemOneInitialized[0]
                && itemTwoInitialized[0]
                && itemThreeInitialized[0]);
    }

    @Test
    public void applyAndInvokeCallThenDoNotInitializeThem() {

        final boolean[] itemOneInitialized = {false};
        final boolean[] itemTwoInitialized = {false};
        final boolean[] itemThreeInitialized = {false};

        Integer[] result = Lazy.defer(new Callable<Integer[]>() {
            @Override
            public Integer[] call() throws Exception {
                itemOneInitialized[0] = true;
                return new Integer[]{1};
            }
        }).apply(new Consumer<Integer[]>() {
            @Override
            public void accept(Integer[] integers) throws Exception {
                itemTwoInitialized[0] = true;
                integers[0] = 2;
            }
        }).apply(new Consumer<Integer[]>() {
            @Override
            public void accept(Integer[] integers) throws Exception {
                itemThreeInitialized[0] = true;
                integers[0] = 3;
            }
        }).call();

        assertTrue(itemOneInitialized[0]
                && itemTwoInitialized[0]
                && itemThreeInitialized[0]
                && result[0] == 3);
    }
}