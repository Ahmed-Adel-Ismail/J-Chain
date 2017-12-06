package com.chaining;

import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.And;
import com.chaining.interfaces.Monad;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * a class that keeps collecting new items into a list then returns the new List on request
 * <p>
 * Created by Ahmed Adel Ismail on 11/13/2017.
 */
public class Collector<T> implements
        Internal<Collector<T>, List<T>>,
        And<T>,
        Monad<List<T>> {

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
        return this;
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
        try {
            return flatMapper.apply(items);
        } catch (Throwable e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a mapper function on every item in this {@link Collector}
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
        collector.items.addAll(mappedItems(mapper));
        return collector;
    }

    private <R> List<R> mappedItems(Function<T, R> mapper) {
        return Observable.fromIterable(items)
                .map(mapper)
                .toList()
                .blockingGet();
    }

    /**
     * reduce all the items in this {@link Collector}
     *
     * @param reducer the reducer function
     * @return the result of the reducer function
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
     * set the logger function corresponding to the logger method that you will use, for instance
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
