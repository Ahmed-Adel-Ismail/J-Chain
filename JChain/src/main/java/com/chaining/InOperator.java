package com.chaining;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a function that checks weather an item is in a certain collection or not
 * <p>
 * Created by Ahmed Adel Ismail on 12/11/2017.
 */
class InOperator<T> implements Predicate<T> {

    private final Collection<T> collection;
    private final BiPredicate<T, T> comparator;
    private final InternalConfiguration configuration;

    InOperator(Collection<T> collection,
               BiPredicate<T, T> comparator,
               InternalConfiguration configuration) {

        this.collection = new ArrayList<>();
        if (collection != null && !collection.isEmpty()) {
            this.collection.addAll(collection);
        }

        this.comparator = comparator;
        this.configuration = configuration;
    }

    @Override
    public boolean test(T item) {
        return item != null &&
                !collection.isEmpty() &&
                new Chain<>(collection, configuration)
                        .apply(removeNulls())
                        .flatMap(toObservableFromIterable())
                        .any(hasComparatorTestPassed(item))
                        .blockingGet();
    }

    private Consumer<Collection<T>> removeNulls() {
        return new Consumer<Collection<T>>() {
            @Override
            public void accept(Collection<T> items) throws Exception {
                items.remove(null);
            }
        };
    }

    private Function<Collection<T>, Observable<T>> toObservableFromIterable() {
        return new Function<Collection<T>, Observable<T>>() {
            @Override
            public Observable<T> apply(Collection<T> source) {
                return Observable.fromIterable(source);
            }
        };
    }

    private Predicate<T> hasComparatorTestPassed(final T originalItem) {
        return new Predicate<T>() {
            @Override
            public boolean test(T item) throws Exception {
                return comparator.test(originalItem, item);
            }
        };
    }
}
