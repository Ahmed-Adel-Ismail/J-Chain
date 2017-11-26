package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * start a {@link Chain} that executes crashing code safely, you should
 * provide {@link #onErrorReturn(Function)} or {@link #onErrorReturnItem(Object)} to provide a
 * fallback item if a crash occurred, or you can call {@link #onError(Consumer)} to handle the
 * {@link Exception} (if thrown) and finish the functions chain
 * <p>
 * Created by Ahmed Adel Ismail on 11/1/2017.
 */
public class Guard<T> {

    private final T item;
    private final Exception error;
    private final ChainConfigurationImpl configuration;

    Guard(Callable<T> callable, ChainConfigurationImpl configuration) {

        T callResult = null;
        Exception callError = null;

        try {
            callResult = callable.call();
        } catch (Exception e) {
            callError = e;
        }

        this.item = callResult;
        this.error = callError;
        this.configuration = configuration;

    }

    /**
     * provide a fallback item if the {@link Callable} that was passed to this {@link Guard}
     * crashed
     *
     * @param item the fallback item
     * @return a {@link Chain} to continue the flow
     */
    public Chain<T> onErrorReturnItem(@NonNull T item) {
        if (error != null) {
            return new Chain<>(item, configuration);
        } else {
            return new Chain<>(this.item, configuration);
        }
    }

    /**
     * provide a {@link Function} that will return a fallback item if the {@link Callable} that was
     * passed to this {@link Guard} crashed
     *
     * @param function the function that will provide the fallback item
     * @return a {@link Chain} to continue the flow
     */
    public Chain<T> onErrorReturn(@NonNull Function<Throwable, T> function) {
        if (error != null) {
            return chainFromFunction(function);
        } else {
            return new Chain<>(item, configuration);
        }
    }

    @NonNull
    private Chain<T> chainFromFunction(Function<Throwable, T> function) {
        try {
            return new Chain<>(function.apply(error), configuration);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * provide a {@link Consumer} to be invoked when an {@link Exception} is thrown, this
     * function will end the current functions chain, if you need to continue the chain with
     * another operations, you can use {@link #onErrorReturn(Function)} or
     * {@link #onErrorReturnItem(Object)}
     *
     * @param consumer a {@link Consumer} to handle the {@link Exception}
     */
    public void onError(Consumer<Exception> consumer) {
        if (error != null) {
            try {
                consumer.accept(error);
            } catch (Exception e) {
                throw new RuntimeExceptionConverter().apply(e);
            }
        }
    }
}
