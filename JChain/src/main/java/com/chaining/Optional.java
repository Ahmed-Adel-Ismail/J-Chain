package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;

import io.reactivex.Maybe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that acts as an RxJava {@link Maybe}, but it returns back to the Chain if possible
 * <p>
 * Created by Ahmed Adel Ismail on 11/6/2017.
 */
public class Optional<T, S extends ChainBlock<T, S>> extends ChainBlock<T, Optional<T, S>> {

    private final S chainBlock;

    Optional(S chainBlock) {
        super(chainBlock.item, chainBlock.configuration);
        this.chainBlock = chainBlock;
    }

    @Override
    Optional<T, S> copy(T item, ChainConfigurationImpl configuration) {
        return new Optional<>(chainBlock.copy(item, configuration));
    }

    /**
     * apply an action to the stored item if not null
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    @Override
    public Optional<T, S> apply(Consumer<T> action) {

        NullChecker.crashIfNull(action);

        if (chainBlock.item != null) {
            try {
                action.accept(chainBlock.item);
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
    public <R> Optional<R, Chain<R>> map(Function<T, R> mapper) {

        NullChecker.crashIfNull(mapper);

        try {
            return new Optional<>(mappedChain(mapper));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    private <R> Chain<R> mappedChain(Function<T, R> mapper) throws Exception {
        if (chainBlock.item != null) {
            return new Chain<>(mapper.apply(chainBlock.item), chainBlock.configuration);
        } else {
            return new Chain<>(null, chainBlock.configuration);
        }
    }





}
