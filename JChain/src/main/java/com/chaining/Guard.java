package com.chaining;


import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
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
public class Guard<S extends Internal<S, T>, T> implements Internal<Guard<S, T>, T>
{

    private final Proxy<S, T> proxy;
    private final Exception error;

    private Guard(Proxy<S, T> proxy, Exception error) {
        this.proxy = proxy;
        this.error = error;
    }

    Guard(Proxy<S, T> proxy, Callable<T> callable) {

        T callResult = null;
        Exception callError = null;

        try {
            callResult = callable.call();
        } catch (Exception e) {
            callError = e;
        }

        this.proxy = proxy.copy(callResult).access();
        this.error = callError;

    }

    /**
     * execute the passed {@link Callable} safely, which will handle any thrown {@link Exception}
     * internally, you will need to call {@link Guard#onErrorReturnItem(Object)} or
     * {@link Guard#onErrorReturn(Function)} to continue chaining the invoke calls,
     * or you can call {@link #onError(Consumer)} to finish the chain
     *
     * @param callable a {@link Callable} that may crash
     * @param <T>      the type of the returned item
     * @return a {@link Guard} to handle fallback scenarios
     */
    public static <T> Guard<Chain<T>, T> call(@NonNull Callable<T> callable) {
        return new Guard<>(new Chain<T>(null, InternalConfiguration.getInstance(null)).access(),
                callable);
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
            return proxy.copy(item);
        } else {
            return proxy.owner();
        }
    }

    /**
     * invoke another risky action if the action before this did not crash
     *
     * @param action a {@link Function} that may crash
     * @return a {@link Guard} that will either hold the result of the {@link Callable}, or an error
     * that needs to be handled
     */
    public Guard<S, T> guard(final @NonNull Function<T, T> action) {
        if (error == null) {
            return new Guard<>(proxy, guardedFunctionCallable(action));
        } else {
            return new Guard<>(proxy, error);
        }
    }

    private Callable<T> guardedFunctionCallable(@NonNull final Function<T, T> action) {
        return new Callable<T>()
        {
            @Override
            public T call() throws Exception {
                return action.apply(proxy.getItem());
            }
        };
    }

    /**
     * apply an action to the stored item if no error occurred
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    public Guard<S, T> apply(Consumer<T> action) {
        if (error == null) {
            Invoker.invoke(action, proxy.getItem());
        }
        return new Guard<>(proxy, error);
    }

    /**
     * provide a {@link Function} that will return a fallback item if the {@link Callable} that was
     * passed to this {@link Guard} crashed
     *
     * @param function the invoke that will provide the fallback item
     * @return a {@link Chain} to continue the flow
     */
    public S onErrorReturn(@NonNull Function<Throwable, T> function) {
        if (error != null) {
            return proxy.copy(Invoker.invoke(function, error));
        } else {
            return proxy.owner();
        }
    }

    /**
     * provide a {@link Consumer} to be invoked when an {@link Exception} is thrown, this
     * invoke will end the current functions chain, if you need to continue the chain with
     * another operations, you can use {@link #onErrorReturn(Function)} or
     * {@link #onErrorReturnItem(Object)}
     *
     * @param consumer a {@link Consumer} to handle the {@link Exception}
     */
    public void onError(Consumer<Exception> consumer) {
        if (error != null) {
            Invoker.invoke(consumer, error);
        }
    }

    /**
     * start logging operation with the passed tag, to see the logs active, you should
     * set {@link ChainConfiguration#setLogging(boolean)} to {@code true}, and you should
     * set the logger invoke corresponding to the logger method that you will use, for instance
     * {@link ChainConfiguration#setInfoLogger(BiConsumer)} or
     * {@link ChainConfiguration#setErrorLogger(BiConsumer)}
     *
     * @param tag the tag of the logs
     * @return a {@link Logger} to handle logging operations
     */
    public Logger<Guard<S, T>, T> log(Object tag) {
        return new Logger<>(this, proxy.getConfiguration(), tag);
    }

    /**
     * map the item in the stream to another item if an error occurred
     *
     * @param mappedItem the new mapped item
     * @param <R>        the new item type
     * @return an {@link Optional} that holds the mapped item on error, or empty {@link Optional}
     * if no error occurred
     */
    public <R> Optional<R> onErrorMapItem(R mappedItem) {
        if (error != null) {
            return new Optional<>(mappedItem, proxy.getConfiguration());
        } else {
            return new Optional<>(null, proxy.getConfiguration());
        }
    }

    @Override
    public Proxy<Guard<S, T>, T> access() {
        return new Proxy<Guard<S, T>, T>()
        {
            @Override
            Guard<S, T> copy(T item, InternalConfiguration configuration) {
                return new Guard<>(proxy.copy(item).access(), error);
            }

            @Override
            InternalConfiguration getConfiguration() {
                return proxy.getConfiguration();
            }

            @Override
            T getItem() {
                return proxy.getItem();
            }

            @Override
            Guard<S, T> owner() {
                return Guard.this;
            }
        };
    }


    public <R> Optional<R> onErrorMap(Function<Throwable, R> mapperFunction) {
        if (error != null) {
            return new Optional<>(Invoker.invoke(mapperFunction, error), proxy.getConfiguration());
        } else {
            return new Optional<>(null, proxy.getConfiguration());
        }
    }
}
