package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.DefaultIfEmpty;

import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that acts as an RxJava {@link Maybe}, but it returns back to the Chain if possible
 * <p>
 * Created by Ahmed Adel Ismail on 11/6/2017.
 */
public class Optional<T> implements
        Internal<Optional<T>, T>,
        Function<Consumer<T>, Optional<T>>,
        DefaultIfEmpty<T> {

    private final Chain<T> chain;

    Optional(T item, InternalConfiguration configuration) {
        this.chain = new Chain<>(item, configuration);
    }

    Optional(Chain<T> chain) {
        this.chain = chain;
    }


    @Override
    public Chain<T> defaultIfEmpty(@NonNull T defaultValue) {
        if (chain.item == null) {
            return new Chain<>(defaultValue, chain.configuration);
        }
        return chain;
    }

    /**
     * apply an action to the stored item if not null
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    public Optional<T> apply(Consumer<T> action) {
        if (chain.item != null) {
            try {
                action.accept(chain.item);
            } catch (Exception e) {
                throw new RuntimeExceptionConverter().apply(e);
            }
        }
        return this;
    }

    /**
     * apply an action to the stored item if not null, this action will cause the stored item
     * to be changed to the new type (it will return a new {@link Optional} with the new type)
     *
     * @param mapper the action to be applied
     * @param <R>    the expected type to be mapped for
     * @return {@code this} instance for chaining
     */
    public <R> Optional<R> map(Function<T, R> mapper) {
        try {
            return new Optional<>(mappedChain(mapper));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    private <R> Chain<R> mappedChain(Function<T, R> mapper) throws Exception {
        if (chain.item != null) {
            return new Chain<>(mapper.apply(chain.item), chain.configuration);
        } else {
            return new Chain<>(null, chain.configuration);
        }
    }

    /**
     * start logging operation with the passed tag, to see the logs active, you should
     * set {@link ChainConfiguration#setLogging(boolean)} to {@code true}, and you should
     * set the logger function corresponding to the logger method that you will use, for instance
     * {@link ChainConfiguration#setInfoLogger(BiConsumer)} or
     * {@link ChainConfiguration#setErrorLogger(BiConsumer)}
     *
     * @param tag the tag of the logs
     * @return a {@link Logger} to handle logging operations
     */
    public Logger<Optional<T>, T> log(Object tag) {
        return new Logger<>(this, chain.configuration, tag);
    }

    /**
     * invoke the passed {@link Consumer} if the Application is in the debug mode, you can set the
     * debugging mode in {@link ChainConfiguration} in the Application's {@code onCreate()}
     *
     * @param action a {@link Consumer} to be invoked in debugging only
     */
    public Optional<T> debug(Consumer<T> action) {
        if (chain.configuration.isDebugging() && chain.item != null) {
            try {
                action.accept(chain.item);
            } catch (Exception e) {
                throw new RuntimeExceptionConverter().apply(e);
            }
        }
        return this;
    }

    @Override
    public Proxy<Optional<T>, T> proxy() {
        return new Proxy<Optional<T>, T>() {
            @Override
            Optional<T> copy(T item, InternalConfiguration configuration) {
                return new Optional<>(item, configuration);
            }

            @Override
            InternalConfiguration getConfiguration() {
                return chain.configuration;
            }

            @Override
            T getItem() {
                return chain.item;
            }
        };
    }
}
