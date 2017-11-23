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
public class Guard<T, S extends ChainBlock<T, S>> extends ChainBlock<T, Guard<T, S>> {

    private final Exception error;
    private final S chainBlock;

    Guard(Callable<T> callable, S chainBlock) throws Exception {
        super(callable.call(), chainBlock.configuration);
        this.error = null;
        this.chainBlock = chainBlock;
    }

    Guard(Exception error, S chainBlock) {
        super(null, chainBlock.configuration);
        this.error = error;
        this.chainBlock = chainBlock;
    }

    private Guard(T item, S chainBlock) {
        super(item, chainBlock.configuration);
        this.chainBlock = chainBlock;
        this.error = null;
    }

    @Override
    Guard<T, S> copy(T item, ChainConfigurationImpl configuration) {
        if (error != null) {
            return new Guard<>(error, chainBlock.copy(null, configuration));
        } else {
            return new Guard<>(item, chainBlock.copy(item, configuration));
        }
    }

    /**
     * provide a fallback item if the {@link Callable} that was passed to this {@link Guard}
     * crashed
     *
     * @param item the fallback item
     * @return a {@link Chain} to continue the flow
     */
    public S onErrorReturnItem(@NonNull T item) {
        if (error != null) {
            return chainBlock.copy(item);
        } else {
            return chainBlock.copy(this.item);
        }
    }

    /**
     * provide a {@link Function} that will return a fallback item if the {@link Callable} that was
     * passed to this {@link Guard} crashed
     *
     * @param function the function that will provide the fallback item
     * @return a {@link Chain} to continue the flow
     */
    public S onErrorReturn(@NonNull Function<Throwable, T> function) {
        if (error != null) {
            return chainFromFunction(function);
        } else {
            return chainBlock.copy(item);
        }
    }

    @NonNull
    private S chainFromFunction(Function<Throwable, T> function) {
        try {
            return chainBlock.copy(function.apply(error));
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
