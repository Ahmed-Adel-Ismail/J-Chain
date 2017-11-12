package com.chaining;


import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.DefaultIfEmpty;

import org.javatuples.Pair;

import java.util.Collection;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.functional.curry.Curry.toCallable;

/**
 * a class that encapsulates chaining multiple operations to be invoked on an Object through
 * chained sequence of operations, then you can get the Object finally through calling
 * {@link #call()}
 * <p>
 * Created by Ahmed Adel Ismail on 10/29/2017.
 */
public class Chain<T> implements
        Function<Consumer<T>, Chain<T>>,
        Callable<T>,
        DefaultIfEmpty<T> {

    final T item;
    final ChainConfigurationImpl configuration;

    Chain(T item, ChainConfigurationImpl configuration) {
        this.item = item;
        this.configuration = configuration;
    }

    /**
     * create an {@link Optional} Object that may contain a value, or may not ... similar to
     * {@link Maybe} in RxJava
     *
     * @param item the item that maybe {@code null}
     * @param <T>  the expected item type
     * @return an {@link Optional} to handle the value
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> Optional<T> optional(@Nullable T item) {
        return new Optional<>(new Chain<>(item, ChainConfigurationImpl.getInstance(null)));
    }

    /**
     * start a chain of Functions
     *
     * @param item the item that will be the root of this chain, should not be null
     * @param <T>  the type of this root Object
     * @return a new {@link Chain}
     */
    public static <T> Chain<T> let(@NonNull T item) {
        return new Chain<>(item, ChainConfigurationImpl.getInstance(null));
    }

    /**
     * invoke an action on the root item that may throw an {@link Exception}
     *
     * @param action the {@link Consumer} to be invoked
     * @return a {@link Guard} to handle safe execution
     */
    public Guard<T> guard(Consumer<T> action) {
        return new Guard<>(toCallable(invokeGuardFunction(), action), configuration);
    }

    private Function<Consumer<T>, T> invokeGuardFunction() {
        return new Function<Consumer<T>, T>() {
            @Override
            public T apply(@NonNull Consumer<T> action1) throws Exception {
                return invokeGuard(action1);
            }
        };
    }

    private T invokeGuard(Consumer<T> action) throws Exception {
        action.accept(item);
        return item;
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
    public Condition<T> when(Predicate<T> predicate) {
        return new Condition<>(this, predicate);
    }

    /**
     * check if the current Object in the chain is available in the passed {@link Collection},
     * the comparison will be through {@link Object#equals(Object)}, if you need custom
     * comparison, you can use {@link #in(Collection, BiPredicate)} instead
     *
     * @param collection the {@link Collection} that holds the items
     * @return a new {@link Chain} holding a {@link Pair}, where {@link Pair#getValue0()} will
     * return the original Object, and {@link Pair#getValue1()} will return a boolean indicating
     * weather the the Object was found in the passed {@link Collection} or not
     */
    public Chain<Pair<T, Boolean>> in(Collection<T> collection) {
        return in(collection, new BiPredicate<T, T>() {
            @Override
            public boolean test(@NonNull T t, @NonNull T o) throws Exception {
                return t.equals(o);
            }
        });
    }

    /**
     * check if the current Object in the chain is available in the passed {@link Collection}
     *
     * @param collection the {@link Collection} that holds the items
     * @param comparator the {@link BiPredicate} that will be invoked over every item, the stored
     *                   Object will be passed as it's first parameter, and the item in the
     *                   {@link Collection} will be passed as the second parameter, if the returned
     *                   value is {@code true}, this means that both items are equal, if
     *                   the returned item is {@code false}, they do not match
     * @return a new {@link Chain} holding a {@link Pair}, where {@link Pair#getValue0()} will
     * return the original Object, and {@link Pair#getValue1()}  will return a boolean indicating
     * weather the the Object was found in the passed {@link Collection} or not
     */
    public Chain<Pair<T, Boolean>> in(Collection<T> collection, BiPredicate<T, T> comparator) {
        boolean inCollection = false;
        if (collection != null && !collection.isEmpty()) {
            inCollection = isObjectInCollection(collection, comparator);
        }
        return new Chain<>(Pair.with(item, inCollection), configuration);
    }

    private Boolean isObjectInCollection(Collection<T> collection, final BiPredicate<T, T> comparator) {
        return new Chain<>(collection, configuration)
                .apply(removeNullItems())
                .flatMap(toObservableFromIterable())
                .any(hasPassedComparatorTest(comparator))
                .blockingGet();
    }

    /**
     * a flat map function that converts this {@link Chain} to another Object
     *
     * @param flatMapper a function that will convert the current {@link Chain} to another Object
     * @param <R>        the type of the new Object
     * @return the new Object
     */
    public <R> R flatMap(@NonNull Function<T, R> flatMapper) {
        try {
            return flatMapper.apply(item);
        } catch (Throwable e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * apply an action to the stored item
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    public Chain<T> apply(Consumer<T> action) {
        try {
            action.accept(item);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
        return this;
    }

    private Consumer<Collection<T>> removeNullItems() {
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
            public Observable<T> apply(@NonNull Collection<T> source) throws Exception {
                return Observable.fromIterable(source);
            }
        };
    }

    private Predicate<T> hasPassedComparatorTest(final BiPredicate<T, T> comparator) {
        return new Predicate<T>() {
            @Override
            public boolean test(@NonNull T item) throws Exception {
                return comparator.test(Chain.this.item, item);
            }
        };
    }

    /**
     * a map function to convert the current Object in the Chain to another Object
     *
     * @param mapper the mapper {@link Function}
     * @param <R>    the new type to be held in the Map
     * @return {@code this} instance for chaining
     */
    public <R> Chain<R> map(@NonNull Function<T, R> mapper) {
        try {
            return new Chain<>(mapper.apply(item), configuration);
        } catch (Throwable e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * call the Object after being updated
     *
     * @return the Object stored in this chain
     */
    @Override
    public T call() {
        return item;
    }

    @Override
    public Chain<T> defaultIfEmpty(@NonNull T defaultValue) {
        return new Optional<>(this).defaultIfEmpty(defaultValue);
    }

    /**
     * invoke the passed {@link Consumer} if the Application is in the debug mode, you can set the
     * debugging mode in {@link ChainConfiguration} in the Application's {@code onCreate()}
     *
     * @param action a {@link Consumer} to be invoked in debugging only
     */
    public Chain<T> debug(Consumer<T> action) {
        if (configuration.isDebugging()) {
            try {
                action.accept(item);
            } catch (Exception e) {
                throw new RuntimeExceptionConverter().apply(e);
            }
        }
        return this;
    }
}
