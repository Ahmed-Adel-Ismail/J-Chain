package com.chaining;


import com.chaining.interfaces.DefaultIfEmpty;

import java.util.Collection;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a class that acts as a {@link Chain} that may hold a {@code null} value,
 * if the stored item is {@code null}, all operations will be skipped until the
 * {@link #defaultIfEmpty(Object)} is called, so the default item is returned ... if the item was
 * not {@code null}, the {@link #defaultIfEmpty(Object)} will be skipped
 * <p>
 * Created by Ahmed Adel Ismail on 11/6/2017.
 */
public class Optional<T> implements
        Conditional<Optional<T>, T>,
        Internal<Optional<T>, T>,
        Function<Consumer<T>, Optional<T>>,
        DefaultIfEmpty<T> {

    private final Chain<T> chain;

    Optional(T item, InternalConfiguration configuration) {
        this.chain = new Chain<>(item, configuration);
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
            Invoker.invoke(action, chain.item);
        }
        return new Optional<>(chain.item, chain.configuration);
    }

    /**
     * invoke an action to the stored item if not null
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    public Optional<T> invoke(Action action) {
        if (chain.item != null) {
            Invoker.invoke(action);
        }
        return new Optional<>(chain.item, chain.configuration);
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
        if (chain.item != null) {
            return new Optional<>(Invoker.invoke(mapper, chain.item), chain.configuration);
        } else {
            return new Optional<>(null, chain.configuration);
        }
    }

    /**
     * apply an action to the stored item if not null, this action will cause this {@link Optional}
     * to be changed to a {@link Maybe}, if the stored item is {@code null} then an empty
     * {@link Maybe} will be returned
     *
     * @param flatMapper the flat-mapped {@link Function}
     * @param <R>        the new expected type
     * @return a {@link Maybe} that either hold a value, or is empty
     */
    public <R> Maybe<R> flatMapMaybe(Function<T, R> flatMapper) {
        if (chain.item != null) {
            return maybeFromNonNullItem(flatMapper);
        } else {
            return Maybe.empty();
        }
    }

    private <R> Maybe<R> maybeFromNonNullItem(Function<T, R> flatMapper) {
        R newItem = Invoker.invoke(flatMapper, chain.item);
        if (newItem != null) {
            return Maybe.just(newItem);
        } else {
            return Maybe.empty();
        }
    }

    /**
     * convert the current {@link Optional} to another {@link Optional} if the current item is
     * not {@code null}
     *
     * @param item an item to be the root for the new {@link Optional}
     * @return a new {@link Optional}
     */
    public <R> Optional<R> to(@NonNull R item) {
        if (chain.item != null) {
            return new Optional<>(item, chain.configuration);
        } else {
            return new Optional<>(null, chain.configuration);
        }
    }

    /**
     * convert the current {@link Optional} to another {@link Optional} if the current item is
     * not {@code null}
     *
     * @param itemCallable a {@link Callable} that will return an item to be the root for the new
     *                     {@link Optional}
     * @return a new {@link Optional}
     */
    public <R> Optional<R> to(@NonNull Callable<R> itemCallable) {
        if (chain.item != null) {
            return new Optional<>(Invoker.invoke(itemCallable), chain.configuration);
        } else {
            return new Optional<>(null, chain.configuration);
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
            Invoker.invoke(action, chain.item);
        }
        return new Optional<>(chain.item, chain.configuration);
    }

    /**
     * pass a {@link Predicate} that if it returned {@code true}, it's
     * {@link Condition#then(Consumer)} will update the current Object, else nothing
     * will happen
     *
     * @param predicate the {@link Predicate} that will decide weather the
     *                  {@link Condition#then(Consumer)} will update the current Object or not
     * @return a {@link Condition} to supply it's {@link Condition#then(Consumer)}
     * {@link Consumer}
     */
    public Condition<Optional<T>, T> when(Predicate<T> predicate) {
        return Condition.createNormal(this, predicate);
    }


    /**
     * pass a {@link Predicate} that if it returned {@code false}, it's
     * {@link Condition#then(Consumer)} will update the current Object, else nothing
     * will happen
     *
     * @param predicate the {@link Predicate} that will decide weather the
     *                  {@link Condition#then(Consumer)} will update the current Object or not
     * @return a {@link Condition} to supply it's {@link Condition#then(Consumer)}
     * {@link Consumer}
     */
    public Condition<Optional<T>, T> whenNot(Predicate<T> predicate) {
        return Condition.createNegated(this, predicate);
    }

    /**
     * check if the current Object in the {@link Optional} is available in the passed {@link Collection}
     *
     * @param collection the {@link Collection} that holds the items
     * @return a {@link Condition} that will execute it's {@link Condition#then(Consumer)} or
     * similar methods if the item is available in the passed {@link Collection}
     */
    public Condition<Optional<T>, T> whenIn(Collection<T> collection) {
        return whenIn(collection, new IsEqualComparator<T>());
    }

    /**
     * check if the current Object in the {@link Optional} is available in the passed {@link Collection}
     *
     * @param collection the {@link Collection} that holds the items
     * @param comparator the {@link BiPredicate} that will be invoked over every item, the stored
     *                   Object will be passed as it's first parameter, and the item in the
     *                   {@link Collection} will be passed as the second parameter, if the returned
     *                   value is {@code true}, this means that both items are equal, if
     *                   the returned item is {@code false}, they do not match
     * @return a {@link Condition} that will execute it's {@link Condition#then(Consumer)} or
     * similar methods if the item is available in the passed {@link Collection}
     */
    public Condition<Optional<T>, T> whenIn(Collection<T> collection, BiPredicate<T, T> comparator) {
        return Condition.createNormal(this,
                new InOperator<>(collection, comparator, chain.configuration));
    }

    /**
     * check if the current Object in the {@link Optional} is NOT available in the
     * passed {@link Collection}
     *
     * @param collection the {@link Collection} that holds the items
     * @return a {@link Condition} that will execute it's {@link Condition#then(Consumer)} or
     * similar methods if the item is NOT available in the passed {@link Collection}
     */
    public Condition<Optional<T>, T> whenNotIn(Collection<T> collection) {
        return whenNotIn(collection, new IsEqualComparator<T>());
    }

    /**
     * check if the current Object in the {@link Optional} is NOT available in the
     * passed {@link Collection}
     *
     * @param collection the {@link Collection} that holds the items
     * @param comparator the {@link BiPredicate} that will be invoked over every item, the stored
     *                   Object will be passed as it's first parameter, and the item in the
     *                   {@link Collection} will be passed as the second parameter, if the returned
     *                   value is {@code true}, this means that both items are equal, if
     *                   the returned item is {@code false}, they do not match
     * @return a {@link Condition} that will execute it's {@link Condition#then(Consumer)} or
     * similar methods if the item is NOT available in the passed {@link Collection}
     */
    public Condition<Optional<T>, T> whenNotIn(Collection<T> collection, BiPredicate<T, T> comparator) {
        return Condition.createNegated(this,
                new InOperator<>(collection, comparator, chain.configuration));
    }


    @Override
    public Proxy<Optional<T>, T> access() {
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

            @Override
            Optional<T> owner() {
                return Optional.this;
            }
        };
    }
}
