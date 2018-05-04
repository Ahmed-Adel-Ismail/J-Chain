package com.chaining.interfaces;

import io.reactivex.functions.Function;

/**
 * an interface that represents the Functor abstraction, where any functor can hold an operator that
 * can convert the type it holds
 *
 * @param <T> the original type
 */
public interface Functor<T> {

    /**
     * convert the current stored item into another item
     *
     * @param mapper the converter {@link Function}
     * @param <R>    the type of the new item
     * @return another {@link Functor} that holds the new item
     */
    <R> Functor<R> map(Function<T, R> mapper);

}
