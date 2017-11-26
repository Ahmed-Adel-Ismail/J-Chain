package com.chaining;


import com.chaining.annotations.SideEffect;
import com.chaining.exceptions.RuntimeExceptionConverter;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a function that will not execute the {@link Consumer} passed to the {@link #then(Consumer)}
 * until the {@link Predicate} passed to it returned {@code true}, else it will change nothing
 * <p>
 * Created by Ahmed Adel Ismail on 10/29/2017.
 */
public class Condition<T> {

    private final boolean negateExpression;
    private final Predicate<T> predicate;
    private final Chain<T> sourceChain;

    private Condition(Chain<T> sourceChain, Predicate<T> predicate, boolean negateExpression) {
        this.predicate = predicate;
        this.sourceChain = sourceChain;
        this.negateExpression = negateExpression;
    }

    static <T> Condition<T> createNormal(Chain<T> sourceChain, Predicate<T> predicate) {
        return new Condition<>(sourceChain, predicate, false);
    }

    static <T> Condition<T> createNegated(Chain<T> sourceChain, Predicate<T> predicate) {
        return new Condition<>(sourceChain, predicate, true);
    }

    /**
     * invoke the passed action if the {@link Predicate} returned {@code true}
     *
     * @param action the action to update the current Object
     * @return the {@link Chain} with the updated state (if the {@link Predicate} returned
     * {@code true}, or will return it with no updates
     */
    public Chain<T> then(Consumer<T> action) {
        try {
            return invokeThenImplementation(action);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }

    }

    private Chain<T> invokeThenImplementation(Consumer<T> action) throws Exception {
        if (isSourceChainUpdateAccepted())
            return sourceChain.apply(action);
        else {
            return sourceChain;
        }
    }

    private boolean isSourceChainUpdateAccepted() throws Exception {

        if (sourceChain.item == null) {
            return false;
        }

        boolean expression = predicate.test(sourceChain.item);
        if (negateExpression) {
            expression = !expression;
        }
        return expression;
    }

    /**
     * invoke the passed action if the {@link Predicate} returned {@code true}, usually this
     * is done for side-effects
     *
     * @param action the action to execute
     * @return the {@link Chain} with the updated state (if the {@link Predicate} returned
     * {@code true}, or will return it with no updates
     */
    @SideEffect("usually this operation is done for side-effects")
    public Chain<T> invoke(Action action) {
        try {
            return invokeThenImplementation(action);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }

    }

    private Chain<T> invokeThenImplementation(Action action) throws Exception {
        if (isSourceChainUpdateAccepted()) action.run();
        return sourceChain;
    }

    /**
     * invoke the passed action based on the previous {@link Predicate} result, this action will
     * cause the item stored to be changed to the expected type (it will return a new
     * {@link Optional} holding the mapping result)
     *
     * @param mapper the action to convert the current item into a new item
     * @return the {@link Optional} with the updated state based on the previous {@link Predicate}
     */
    public <R> Optional<R> thenMap(Function<T, R> mapper) {
        try {
            return new Optional<>(mappedChain(mapper));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    private <R> Chain<R> mappedChain(Function<T, R> mapper) throws Exception {
        if (isSourceChainUpdateAccepted()) {
            return new Chain<>(mapper.apply(sourceChain.item), sourceChain.configuration);
        } else {
            return new Chain<>(null, sourceChain.configuration);
        }
    }
}
