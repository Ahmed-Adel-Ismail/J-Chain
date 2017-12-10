package com.chaining;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a class that handles invoking crashing functions and convert it's {@link Exception} thrown to
 * a {@link RuntimeException}
 * <p>
 * Created by Ahmed Adel Ismail on 12/10/2017.
 */
class Invoker {

    /**
     * invoke a {@link Function} or throw a {@link RuntimeException}
     *
     * @param function  the {@link Function} to invoke
     * @param parameter the parameter passed
     * @param <T>       the type of the parameter
     * @param <R>       the type of the return
     * @return the result of the invocation
     */
    static <T, R> R invoke(Function<T, R> function, T parameter) {
        try {
            return function.apply(parameter);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a {@link Consumer} or throw a {@link RuntimeException}
     *
     * @param consumer  the {@link Consumer} to invoke
     * @param parameter the parameter passed
     * @param <T>       the type of the parameter
     */
    static <T> void invoke(Consumer<T> consumer, T parameter) {
        try {
            consumer.accept(parameter);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a {@link BiConsumer} or throw a {@link RuntimeException}
     *
     * @param biConsumer   the {@link BiConsumer} to invoke
     * @param parameterOne the first parameter passed
     * @param parameterTwo the second parameter passed
     * @param <T1>         the type of the first parameter
     * @param <T2>         the type of the second parameter
     */
    static <T1, T2> void invoke(BiConsumer<T1, T2> biConsumer, T1 parameterOne, T2 parameterTwo) {
        try {
            biConsumer.accept(parameterOne, parameterTwo);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a {@link Predicate} or throw a {@link RuntimeException}
     *
     * @param predicate the {@link Predicate} to invoke
     * @param parameter the parameter passed
     * @param <T>       the type of the parameter
     * @return the result of the invocation
     */
    static <T> boolean invoke(Predicate<T> predicate, T parameter) {
        try {
            return predicate.test(parameter);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a {@link Callable} or throw a {@link RuntimeException}
     *
     * @param callable the {@link Callable} to invoke
     * @param <R>      the type of the return
     * @return the result of the invocation
     */
    static <R> R invoke(Callable<R> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke an {@link Action} or throw a {@link RuntimeException}
     *
     * @param action the {@link Action} to invoke
     */
    static void invoke(Action action) {
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * a class that wraps any Throwable and turns it into a
     * {@link RuntimeException}
     * <p>
     * Created by Ahmed Adel Ismail on 5/3/2017.
     */
    static class RuntimeExceptionConverter implements Function<Throwable, RuntimeException> {


        @Override
        public RuntimeException apply(@NonNull Throwable e) {
            return (e instanceof RuntimeException)
                    ? (RuntimeException) e
                    : new RuntimeException(e);
        }

    }

}
