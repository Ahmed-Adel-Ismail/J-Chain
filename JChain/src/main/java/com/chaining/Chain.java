package com.chaining;


import com.chaining.annotations.SideEffect;
import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.And;
import com.chaining.interfaces.DefaultIfEmpty;
import com.chaining.interfaces.Monad;

import org.javatuples.Pair;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Action;
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
        DefaultIfEmpty<T>,
        And<T>,
        Monad<T> {

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
     * start a chain of Functions throw passing an item
     *
     * @param item the item that will be the root of this chain, should not be null
     * @param <T>  the type of this root Object
     * @return a new {@link Chain}
     */
    public static <T> Chain<T> let(@NonNull T item) {
        return new Chain<>(item, ChainConfigurationImpl.getInstance(null));
    }

    /**
     * start a chain of Functions throw passing a {@link Callable} that will return the root item
     *
     * @param callable the {@link Callable} that will return the root item for this {@link Chain},
     *                 should not be {@code null}
     * @param <T>      the type of this root Object
     * @return a new {@link Chain}
     */
    public static <T> Chain<T> call(@NonNull Callable<T> callable) {
        try {
            return new Chain<>(callable.call(), ChainConfigurationImpl.getInstance(null));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * invoke a mapper function that may crash
     *
     * @param guardMapper the mapper function that may crash
     * @param <R>         the expected return type
     * @return a {@link Guard} with the new returned item
     */
    public <R> Guard<R> guardMap(Function<T, R> guardMapper) {
        return new Guard<>(toCallable(guardMapper, item), configuration);
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
            public T apply(Consumer<T> action) throws Exception {
                return invokeGuard(action);
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
    public Condition<T> whenNot(Predicate<T> predicate) {
        return Condition.createNegated(this, predicate);
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
        return in(collection, isEqual());
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
     * return the original Object, and {@link Pair#getValue1()} will return a boolean indicating
     * weather the the Object was found in the passed {@link Collection} or not
     */
    public Chain<Pair<T, Boolean>> in(Collection<T> collection, BiPredicate<T, T> comparator) {
        boolean inCollection = false;
        if (collection != null && !collection.isEmpty()) {
            inCollection = isObjectInCollection(collection, comparator);
        }
        return new Chain<>(Pair.with(item, inCollection), configuration);
    }

    private BiPredicate<T, T> isEqual() {
        return new BiPredicate<T, T>() {
            @Override
            public boolean test(T t, T o) throws Exception {
                return t.equals(o);
            }
        };
    }

    private Boolean isObjectInCollection(final Collection<T> collection,
                                         final BiPredicate<T, T> comparator) {
        return new Chain<>(collection, configuration)
                .apply(removeNulls())
                .flatMap(toObservableFromIterable())
                .any(hasComparatorTestPassed(comparator))
                .blockingGet();
    }

    @Override
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

    private Predicate<T> hasComparatorTestPassed(final BiPredicate<T, T> comparator) {
        return new Predicate<T>() {
            @Override
            public boolean test(T item) throws Exception {
                return comparator.test(Chain.this.item, item);
            }
        };
    }

    /**
     * invoke an action before going to the next step in this chain, this operation is
     * intended for side-effects
     *
     * @param action an {@link Action} to be executed
     * @return {@code this} instance for chaining
     */
    @SideEffect("usually this operation is done for side-effects")
    public Chain<T> invoke(Action action) {
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
        return this;
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
     * convert the current {@link Chain} to another {@link Chain}
     *
     * @param item an item to be the root for the new {@link Chain}
     * @return a new {@link Chain}
     */
    public <R> Chain<R> to(@NonNull R item) {
        return new Chain<>(item, ChainConfigurationImpl.getInstance(null));
    }

    /**
     * convert the current {@link Chain} to another {@link Chain}
     *
     * @param itemCallable a {@link Callable} that will return an item to be the root for the new
     *                     {@link Chain}
     * @return a new {@link Chain}
     */
    public <R> Chain<R> to(@NonNull Callable<R> itemCallable) {
        try {
            return new Chain<>(itemCallable.call(), ChainConfigurationImpl.getInstance(null));
        } catch (Exception e) {
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


    @Override
    public Collector<T> and(T item) {
        return new Collector<T>(configuration)
                .and(this.item)
                .and(item);
    }

    /**
     * pair the current item with another item
     *
     * @param item another item to be paired with
     * @param <R>  the other item type
     * @return a {@link Chain} that holds a {@link Pair}, holding the current item as it's
     * first value, and the new Item as a second Value
     */
    public <R> Chain<Pair<T, R>> pair(R item) {
        return new Chain<>(Pair.with(this.item, item), configuration);
    }

    /**
     * pair the current item with another item which is the result of the passed function
     *
     * @param pairedItemMapper a function that it's result will be used to be put in a {@link Pair}
     * @param <R>              the other item type
     * @return a {@link Chain} that holds a {@link Pair}, holding the current item as it's
     * first value, and the function result as a second value
     */
    public <R> Chain<Pair<T, R>> pair(Function<T, R> pairedItemMapper) {
        try {
            return new Chain<>(Pair.with(item, pairedItemMapper.apply(item)), configuration);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * collect the stored {@link Iterable} item into a {@link Collector} Object,
     * if the stored item is not of type
     * {@link Iterable}, this method will create a {@link Collector} with a {@link List} of
     * one item, which is the stored item, you can then invoke {@link Collector#and(Object)}
     * to append other items to the current items
     *
     * @param type the type of the elements in the stored {@link Iterable} item
     * @param <R>  the expected type of elements to collect over
     * @return a {@link Collector} for managing those items
     */
    @SuppressWarnings("unchecked")
    public <R> Collector<R> collect(Class<R> type) {
        if (item == null) {
            return new Collector<>(configuration);
        } else if (item instanceof Iterable) {
            return iterableCollector();
        } else if (!type.isAssignableFrom(item.getClass())) {
            throw new UnsupportedOperationException("collect() parameter type mismatch");
        } else {
            return new Collector<R>(configuration).and((R) item);
        }
    }

    @SuppressWarnings("unchecked")
    private <R> Collector<R> iterableCollector() {

        List<R> items = Observable.fromIterable((Iterable<R>) item)
                .toList()
                .blockingGet();

        Collector<R> collector = new Collector<>(configuration);
        collector.items.addAll(items);
        return collector;
    }
}
