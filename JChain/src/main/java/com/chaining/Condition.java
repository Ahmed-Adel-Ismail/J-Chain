package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a function that will not execute the {@link Consumer} passed to the {@link #apply(Consumer)}
 * until the {@link Predicate} passed to it returned {@code true}, else it will change nothing
 * <p>
 * Created by Ahmed Adel Ismail on 10/29/2017.
 */
public class Condition<T> implements Function<Consumer<T>, Chain<T>> {

    private final Predicate<T> predicate;
    private final Chain<T> sourceChain;

    Condition(Chain<T> sourceChain, Predicate<T> predicate) {
        this.predicate = predicate;
        this.sourceChain = sourceChain;
    }

    /**
     * invoke the passed action if the {@link Predicate} returned {@code true}
     *
     * @param action the action to execute
     * @return the {@link Chain} with the updated state (if the {@link Predicate} returned
     * {@code true}, or will return it with no updates
     */
    public Chain<T> apply(Consumer<T> action) {
        try {
            return invokeImplementation(action);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }

    }

    private Chain<T> invokeImplementation(Consumer<T> action) throws Exception {
        if (predicate.test(sourceChain.item))
            return sourceChain.apply(action);
        else {
            return sourceChain;
        }
    }
}
