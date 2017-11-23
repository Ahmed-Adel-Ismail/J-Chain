package com.chaining;

import org.junit.Test;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GuardTest {


    @Test
    public void guardWithSetTextOnTestClassThenFindTextValueUpdated() throws Exception {

        final TestClass[] result = {new TestClass()};

        Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        result[0].text = "!";
                    }
                });

        assertEquals("!", result[0].text);
    }

    @Test
    public void guardWithExceptionThenDoNotThrowException() {

        Chain.let(0)
                .guard(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    @Test
    public void onErrorReturnForNonCrashingGuardThenDoNotChangeAnyThing() {

        TestClass testClass = Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        testClass.text = "!";
                    }
                })
                .onErrorReturn(new Function<Throwable, TestClass>() {
                    @Override
                    public TestClass apply(Throwable throwable) throws Exception {
                        return new TestClass("!!");
                    }
                })
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void onErrorReturnForCrashingGuardThenReturnTheValue() {
        TestClass testClass = Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                })
                .onErrorReturn(new Function<Throwable, TestClass>() {
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
        Chain.let(0)
                .guard(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test
    public void onErrorReturnItemForNonCrashingGuardThenDoNotChangeAnyThing() {
        TestClass testClass = Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                           @Override
                           public void accept(TestClass testClass) throws Exception {
                               testClass.text = "!";
                           }
                       }
                )
                .onErrorReturnItem(new TestClass("!!"))
                .call();

        assertEquals("!", testClass.text);
    }

    @Test
    public void onErrorReturnItemForCrashingGuardThenReturnTheValue() {
        TestClass testClass = Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
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
        Chain.let(new TestClass())
                .guard(new Consumer<TestClass>() {
                    @Override
                    public void accept(TestClass testClass) throws Exception {
                        testClass.text = "!";
                    }
                })
                .onError(new Consumer<Exception>() {

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
        Chain.let(0)
                .guard(new Consumer<Integer>() {
                           @Override
                           public void accept(Integer integer) throws Exception {
                               throw new UnsupportedOperationException();
                           }
                       }
                )
                .onError(new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) throws Exception {
                        result[0] = e;
                    }
                });


        assertEquals(UnsupportedOperationException.class, result[0].getClass());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onErrorAcceptWithCrashingFunctionThenThrowException() {

        Chain.let(0)
                .guard(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        throw new NullPointerException();
                    }
                })
                .onError(new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) throws Exception {
                        throw new UnsupportedOperationException();
                    }
                });
    }


}