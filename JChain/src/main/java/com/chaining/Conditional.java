package com.chaining;


import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * an interface implemented by the Classes that can start a {@link Condition} state
 * <p>
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
interface Conditional<S, T> extends
        Internal<S, T>,
        Function<Consumer<T>, S> {
}
