package com.chaining;


import com.chaining.annotations.SideEffect;
import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.ItemHolder;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a function that will not execute the {@link Consumer} passed to the {@link #then(Consumer)}
 * until the {@link Predicate} passed to it returned {@code true}, else it will change nothing
 * <p>
 * Created by Ahmed Adel Ismail on 10/29/2017.
 */
public class Condition<T> implements ItemHolder<T>{

    private final boolean negateExpression;
    private final Predicate<T> predicate;
    private final Chain<T> chain;

    private Condition(Chain<T> chain, Predicate<T> predicate, boolean negateExpression) {
        this.predicate = predicate;
        this.chain = chain;
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
            return chain.apply(action);
        else {
            return chain;
        }
    }

    private boolean isSourceChainUpdateAccepted() throws Exception {

        if (chain.item == null) {
            return false;
        }

        boolean expression = predicate.test(chain.item);
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
        return chain;
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
            return new Chain<>(mapper.apply(chain.item), chain.configuration);
        } else {
            return new Chain<>(null, chain.configuration);
        }
    }

    /**
     * convert to an {@link Optional} containing the passed item based on the previous
     * {@link Predicate} result
     *
     * @param item an item to be the root for the new {@link Optional}
     * @return a new {@link Optional}
     */
    public <R> Optional<R> thenTo(@NonNull R item) {
        try {
            return new Optional<>(toChainFromItem(item));
        } catch (Throwable e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    private <R> Chain<R> toChainFromItem(R item) throws Exception {
        if (isSourceChainUpdateAccepted()) {
            return new Chain<>(item, chain.configuration);
        } else {
            return new Chain<>(null, chain.configuration);
        }
    }

    /**
     * convert to an {@link Optional} containing the result of the passed {@link Callable}
     * based on the previous {@link Predicate} result
     *
     * @param itemCallable a {@link Callable} that will return an item to be the root for the new
     *                     {@link Optional}
     * @return a new {@link Optional}
     */
    public <R> Optional<R> thenTo(@NonNull Callable<R> itemCallable) {
        try {
            return new Optional<>(toChainFromCallable(itemCallable));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    private <R> Chain<R> toChainFromCallable(Callable<R> callable) throws Exception {
        if (isSourceChainUpdateAccepted()) {
            return new Chain<>(callable.call(), chain.configuration);
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
    public Logger<Condition<T>,T> log(Object tag) {
        return new Logger<>(this, chain.configuration, tag);
    }

    @Override
    public T getItem() {
        return chain.item;
    }
}
