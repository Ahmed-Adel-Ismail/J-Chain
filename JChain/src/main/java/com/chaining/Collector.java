package com.chaining;

import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.And;
import com.chaining.interfaces.Monad;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * a class that keeps collecting new items into a list then returns the new List on request
 * <p>
 * Created by Ahmed Adel Ismail on 11/13/2017.
 */
public class Collector<T> implements And<T>, Monad<List<T>> {

    final List<T> items = new LinkedList<>();
    private final ChainConfigurationImpl chainConfiguration;

    Collector(ChainConfigurationImpl chainConfiguration) {
        this.chainConfiguration = chainConfiguration;
    }


    @Override
    public Collector<T> and(T item) {
        NullChecker.crashIfNull(item);
        items.add(item);
        return this;
    }

    /**
     * collect the added items into a {@link List} that holds no {@code null} values
     *
     * @return a {@link Chain} holding a List of items
     */
    public Chain<List<T>> collect() {
        return new Chain<>(items, chainConfiguration);
    }

    /**
     * collect the added items into an Object created from the passed {@link Function}
     *
     * @param collectorFunction {@link Function} that takes the items {@link List} as it's
     *                          parameter, and returns an Object
     * @return a {@link Chain} holding the Object created from the passed {@link Function}
     */
    public <R> Chain<R> collect(Function<List<T>, R> collectorFunction) {

        NullChecker.crashIfNull(collectorFunction);

        try {
            return new Chain<>(collectorFunction.apply(items), chainConfiguration);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    @Override
    public <R> R flatMap(@NonNull Function<List<T>, R> flatMapper) {

        NullChecker.crashIfNull(flatMapper);

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

        NullChecker.crashIfNull(mapper);

        if (items.isEmpty()) {
            return new Collector<>(chainConfiguration);
        } else {
            return invokeMap(mapper);
        }
    }

    @NonNull
    private <R> Collector<R> invokeMap(Function<T, R> mapper) {
        Collector<R> collector = new Collector<>(chainConfiguration);
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

        NullChecker.crashIfNull(reducer);

        if (items.isEmpty()) {
            return new Chain<>(null, chainConfiguration);
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
                return new Chain<>(item, chainConfiguration);
            }
        };
    }
}
