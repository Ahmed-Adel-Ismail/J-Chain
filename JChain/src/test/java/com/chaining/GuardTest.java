package com.chaining;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GuardTest
{


    @Test
    public void callWithSetTextOnTestClassThenFindTextValueUpdated() {

        final TestClass[] result = {new TestClass()};
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                result[0].text = "!";
                return result[0];
            }
        });

        assertEquals("!", result[0].text);
    }

    @Test
    public void callWithExceptionThenDoNotThrowException() {
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test
    public void guardWithNonCrashingFunctionForNonCrashingGuardThenReturnTheValue() {

        final TestClass[] result = {new TestClass()};
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                result[0].text = "!";
                return result[0];
            }
        }).guard(new Function<TestClass, TestClass>()
        {
            @Override
            public TestClass apply(TestClass testClass) throws Exception {
                result[0].text = "!!";
                return result[0];
            }
        });

        assertEquals("!!", result[0].text);
    }

    @Test
    public void guardWithCrashingFunctionForNonCrashingGuardThenReturnError() {
        TestClass result = Guard
                .call(new Callable<TestClass>()
                {
                    @Override
                    public TestClass call() throws Exception {
                        return new TestClass("!");
                    }
                })
                .guard(new Function<TestClass, TestClass>()
                {

                    @Override
                    public TestClass apply(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorReturnItem(new TestClass("!!"))
                .call();

        assertEquals("!!", result.text);

    }

    @Test
    public void onErrorReturnForNonCrashingGuardThenDoNotChangeAnyThing() {
        TestClass testClass = Guard
                .call(new Callable<TestClass>()
                {
                    @Override
                    public TestClass call() throws Exception {
                        return new TestClass("!");
                    }
                })
                .onErrorReturn(new Function<Throwable, TestClass>()
                {
                    @Override
                    public TestClass apply(@NonNull Throwable throwable) throws Exception {
                        return new TestClass("!!");
                    }
                })
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void onErrorReturnForCrashingGuardThenReturnTheValue() {
        TestClass testClass = Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new UnsupportedOperationException();
            }
        })
                .onErrorReturn(new Function<Throwable, TestClass>()
                {
                    @Override
                    public TestClass apply(@NonNull Throwable throwable) throws Exception {
                        return new TestClass("!");
                    }
                })
                .call();

        assertEquals("!", testClass.text);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onErrorReturnWithCrashingFunctionThenThrowTheException() {
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new UnsupportedOperationException();
            }
        }).onErrorReturn(new Function<Throwable, TestClass>()
        {
            @Override
            public TestClass apply(@NonNull Throwable throwable) throws Exception {
                throw new UnsupportedOperationException();
            }
        }).call();
    }

    @Test
    public void onErrorReturnItemForNonCrashingGuardThenDoNotChangeAnyThing() {
        TestClass testClass = Guard.call(new Callable<TestClass>()
        {
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
    public void onErrorReturnItemForCrashingGuardThenReturnTheValue() {
        TestClass testClass = Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new UnsupportedOperationException();
            }
        })
                .onErrorReturnItem(new TestClass("!"))
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void onErrorAcceptForNonCrashingGuardThenChangeNothing() {

        final Exception[] result = {null};
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                return new TestClass("!");
            }
        }).onError(new Consumer<Exception>()
        {

            @Override
            public void accept(@NonNull Exception e) throws Exception {
                result[0] = e;
            }
        });

        assertNull(result[0]);
    }

    @Test
    public void onErrorAcceptForCrashingGuardThenInvokeTheFunction() {

        final Exception[] result = {null};
        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new UnsupportedOperationException();
            }
        }).onError(new Consumer<Exception>()
        {

            @Override
            public void accept(@NonNull Exception e) throws Exception {
                result[0] = e;
            }
        });

        assertEquals(UnsupportedOperationException.class, result[0].getClass());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onErrorAcceptWithCrashingFunctionThenThrowException() {

        Guard.call(new Callable<TestClass>()
        {
            @Override
            public TestClass call() throws Exception {
                throw new NullPointerException();
            }
        }).onError(new Consumer<Exception>()
        {

            @Override
            public void accept(@NonNull Exception e) throws Exception {
                throw new UnsupportedOperationException();
            }
        });

    }

    @Test
    public void logWithSelfAsSourceThenReturnSelfAsSource() {
        Guard<?, ?> source = Chain.let(0).guard(new Consumer<Integer>()
        {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });
        Logger<?, ?> logger = source.log("1");
        assertEquals(source, logger.source);
    }

    @Test
    public void logWithStringTagThenReturnLoggerWithThatTag() {
        Guard<?, ?> source = Chain.let(0).guard(new Consumer<Integer>()
        {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });
        Logger<?, ?> logger = source.log("1");
        assertEquals("1", logger.tag);
    }

    @Test
    public void onErrorMapWithErrorThenReturnAnOptionalWithNewItem() {
        String result = Chain.let(0)
                .guard(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorMap(new Function<Throwable, String>()
                {
                    @Override
                    public String apply(Throwable throwable) throws Exception {
                        return "!";
                    }
                })
                .defaultIfEmpty("")
                .call();
        assertEquals("!", result);
    }

    @Test
    public void onErrorMapWithNoErrorThenReturnEmptyOptional() {
        String result = Chain.let(0)
                .guard(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        // do nothing
                    }
                })
                .onErrorMap(new Function<Throwable, String>()
                {
                    @Override
                    public String apply(Throwable throwable) throws Exception {
                        return "!";
                    }
                })
                .defaultIfEmpty("")
                .call();

        assertEquals("", result);
    }

    @Test
    public void onErrorMapItemWithErrorThenReturnAnOptionalWithNewItem() {
        String result = Chain.let(0)
                .guard(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorMapItem("!")
                .defaultIfEmpty("")
                .call();

        assertEquals("!", result);
    }


    @Test
    public void onErrorMapItemWithNoErrorThenReturnEmptyOptional() {
        String result = Chain.let(0)
                .guard(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        // do nothing
                    }
                })
                .onErrorMapItem("!")
                .defaultIfEmpty("")
                .call();

        assertEquals("", result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onErrorMapWithErrorAndCrashInMapperFunctionThenThrowException() {
        Chain.let(0)
                .guard(new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorMap(new Function<Throwable, String>()
                {
                    @Override
                    public String apply(Throwable throwable) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });

    }

    @Test
    public void applyWithNoErrorInGuardThenInvokeApplyFunction() {
        Boolean[] result = Chain.let(new Boolean[]{false})
                .guard(new Consumer<Boolean[]>()
                {
                    @Override
                    public void accept(Boolean[] booleans) throws Exception {
                        // do nothing
                    }
                })
                .apply(new Consumer<Boolean[]>()
                {
                    @Override
                    public void accept(Boolean[] booleans) throws Exception {
                        booleans[0] = true;
                    }
                })
                .onErrorReturnItem(new Boolean[]{false})
                .call();

        assertTrue(result[0]);
    }

    @Test
    public void applyWithErrorInGuardThenDoNotInvokeApplyFunction() {
        Boolean[] result = Chain.let(new Boolean[]{false})
                .guard(new Consumer<Boolean[]>()
                {
                    @Override
                    public void accept(Boolean[] booleans) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .apply(new Consumer<Boolean[]>()
                {
                    @Override
                    public void accept(Boolean[] booleans) throws Exception {
                        booleans[0] = true;
                    }
                })
                .onErrorReturnItem(new Boolean[]{false})
                .call();

        assertFalse(result[0]);
    }

    @Test
    public void runGuardProxyTester() {
        Guard<?, Integer> guard = Guard.call(new Callable<Integer>()
        {
            @Override
            public Integer call() throws Exception {
                return 0;
            }
        });

        new ProxyTester<>(guard, 1).run();
    }
}