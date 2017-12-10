package com.chaining;


import com.chaining.annotations.SideEffect;

import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a invoke that will not execute the {@link Consumer} passed to the {@link #then(Consumer)}
 * until the {@link Predicate} passed to it returned {@code true}, else it will change nothing
 * <p>
 * Created by Ahmed Adel Ismail on 10/29/2017.
 */
public class Condition<S extends Conditional<S, T>, T> implements Internal<Condition<S, T>, T> {

    private final boolean negateExpression;
    private final Predicate<T> predicate;
    private final Proxy<S, T> sourceProxy;

    private Condition(S source, Predicate<T> predicate, boolean negateExpression) {
        this.sourceProxy = source.access();
        this.predicate = predicate;
        this.negateExpression = negateExpression;

    }

    static <S extends Conditional<S, T>, T> Condition<S, T>
    createNormal(S source, Predicate<T> predicate) {
        return new Condition<>(source, predicate, false);
    }

    static <S extends Conditional<S, T>, T> Condition<S, T>
    createNegated(S source, Predicate<T> predicate) {
        return new Condition<>(source, predicate, true);
    }

    /**
     * invoke the passed action if the {@link Predicate} returned {@code true}
     *
     * @param action the action to update the current Object
     * @return the {@link Chain} with the updated state (if the {@link Predicate} returned
     * {@code true}, or will return it with no updates
     */
    public S then(Consumer<T> action) {
        if (isSourceChainUpdateAccepted())
            return Invoker.invoke(sourceProxy.owner(), action);
        else {
            return sourceProxy.owner();
        }
    }

    private boolean isSourceChainUpdateAccepted() {

        T item = sourceProxy.getItem();
        if (item == null) {
            return false;
        }

        boolean expression = Invoker.invoke(predicate, item);
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
    public S invoke(Action action) {
        if (isSourceChainUpdateAccepted()) Invoker.invoke(action);
        return sourceProxy.owner();

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
        if (isSourceChainUpdateAccepted()) {
            return new Optional<>(Invoker.invoke(mapper, sourceProxy.getItem()),
                    sourceProxy.getConfiguration());
        } else {
            return new Optional<>(null, sourceProxy.getConfiguration());
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
        if (isSourceChainUpdateAccepted()) {
            return new Optional<>(item, sourceProxy.getConfiguration());
        } else {
            return new Optional<>(null, sourceProxy.getConfiguration());
        }
    }

    /**
     * convert to an {@link Optional} containing the result of the passed {@link java.util.concurrent.Callable}
     * based on the previous {@link Predicate} result
     *
     * @param itemCallable a {@link java.util.concurrent.Callable} that will return an item to be the root for the new
     *                     {@link Optional}
     * @return a new {@link Optional}
     */
    public <R> Optional<R> thenTo(@NonNull Callable<R> itemCallable) {
        if (isSourceChainUpdateAccepted()) {
            return new Optional<>(Invoker.invoke(itemCallable), sourceProxy.getConfiguration());
        } else {
            return new Optional<>(null, sourceProxy.getConfiguration());
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
    public Logger<Condition<S, T>, T> log(Object tag) {
        return new Logger<>(this, sourceProxy.getConfiguration(), tag);
    }


    @Override
    public Proxy<Condition<S, T>, T> access() {
        return new Proxy<Condition<S, T>, T>() {
            @Override
            T getItem() {
                return sourceProxy.getItem();
            }

            @Override
            InternalConfiguration getConfiguration() {
                return sourceProxy.getConfiguration();
            }

            @Override
            Condition<S, T> copy(T item, InternalConfiguration configuration) {
                return new Condition<>(sourceProxy.copy(item, configuration), predicate, negateExpression);
            }

            @Override
            Condition<S, T> owner() {
                return Condition.this;
            }
        };
    }
}
