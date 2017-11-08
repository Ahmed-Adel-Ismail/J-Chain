package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.DefaultIfEmpty;

import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that acts as an RxJava {@link Maybe}, but it returns back to the Chain if possible
 * <p>
 * Created by Ahmed Adel Ismail on 11/6/2017.
 */
public class Optional<T> implements Function<Consumer<T>, Optional<T>>, DefaultIfEmpty<T> {

    private final Chain<T> chain;

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
}
