package com.chaining;

import com.chaining.interfaces.And;
import com.chaining.interfaces.Functor;
import com.chaining.interfaces.Monad;
import com.functional.curry.Invoker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that keeps collecting new items into a list then returns the new List on request
 * <p>
 * Created by Ahmed Adel Ismail on 11/13/2017.
 */
public class Collector<T> implements
        Internal<Collector<T>, List<T>>,
        And<T>,
        Monad<List<T>>,
        Functor<T> {

    final List<T> items = new LinkedList<>();
    private final InternalConfiguration configuration;

    Collector(InternalConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    public Collector<T> and(T item) {
        if (item != null) {
            items.add(item);
        }
        return copy();
    }

    private Collector<T> copy() {
        Collector<T> collector = new Collector<>(configuration);
        collector.items.addAll(items);
        return collector;
    }

    /**
     * collect the added items into a {@link List} that holds no {@code null} values
     *
     * @return a {@link Chain} holding a List of items
     */
    public Chain<List<T>> toList() {
        return new Chain<>(items, configuration);
    }

    @Override
    public <R> R flatMap(@NonNull Function<List<T>, R> flatMapper) {
        return Invoker.invoke(flatMapper, items);
    }

    /**
     * iterate over the items and invoke a certain action
     *
     * @param action a {@link Consumer} that will be invoked over all items in the {@link Collector}
     * @return {@code this} {@link Collector} after iteration
     */
    public Collector<T> forEach(@NonNull Consumer<T> action) {
        Observable.fromIterable(items).blockingForEach(action);
        return this;
    }

    /**
     * invoke a mapper invoke on every item in this {@link Collector}, if the mapper function
     * returned {@code null}, this item will be removed from the list
     *
     * @param mapper the mapper {@link Function}
     * @return the new {@link Collector} with mapped items
     */
    public <R> Collector<R> map(Function<T, R> mapper) {
        if (items.isEmpty()) {
            return new Collector<>(configuration);
        } else {
            return invokeMap(mapper);
        }
    }

    @NonNull
    private <R> Collector<R> invokeMap(Function<T, R> mapper) {
        Collector<R> collector = new Collector<>(configuration);
        collector.items.addAll(nonNullMappedItems(mapper));
        return collector;
    }

    private <R> List<R> nonNullMappedItems(Function<T, R> mapper) {
        List<R> mappedItems = new ArrayList<>(items.size());
        for (T item : items) {
            addMappedItemIfNonNull(Invoker.invoke(mapper, item), mappedItems);
        }
        return mappedItems;

    }

    private <R> void addMappedItemIfNonNull(R mappedItem, List<R> mappedItems) {
        if (mappedItem != null) mappedItems.add(mappedItem);
    }


    /**
     * reduce all the items in this {@link Collector}
     *
     * @param reducer the reducer invoke
     * @return the result of the reducer invoke
     */
    public Chain<T> reduce(BiFunction<T, T, T> reducer) {

        if (items.isEmpty()) {
            return new Chain<>(null, configuration);
        }

        return Observable.fromIterable(items)
                .reduce(reducer)
                .map(toChain())
                .blockingGet();
    }

    private Function<T, Chain<T>> toChain() {
        return new Function<T, Chain<T>>() {
            @Override
            public Chain<T> apply(T item) throws Exception {
                return new Chain<>(item, configuration);
            }
        };
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
    public Logger<Collector<T>, List<T>> log(Object tag) {
        return new Logger<>(this, configuration, tag);
    }

    @Override
    public Proxy<Collector<T>, List<T>> access() {
        return new Proxy<Collector<T>, List<T>>() {
            @Override
            List<T> getItem() {
                return items;
            }

            @Override
            InternalConfiguration getConfiguration() {
                return configuration;
            }

            @Override
            Collector<T> copy(List<T> items, InternalConfiguration configuration) {
                Collector<T> collector = new Collector<>(configuration);
                collector.items.addAll(items);
                return collector;
            }

            @Override
            Collector<T> owner() {
                return Collector.this;
            }
        };
    }
}
