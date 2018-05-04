package com.chaining;

import com.chaining.interfaces.Functor;
import com.chaining.interfaces.Monad;
import com.functional.curry.Curry;
import com.functional.curry.Invoker;

import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a state of the Chain that does not take action until it's {@link #call()} or {@link #flatMap(Function)}
 * methods are invoked
 * <p>
 * Created by Ahmed Adel Ismail on 1/28/2018.
 */
public class Lazy<T> implements Callable<T>, Monad<T>, Functor<T>, Function<Consumer<T>, Lazy<T>> {

    final Callable<T> delayedAction;
    T item;

    Lazy(Callable<T> delayedAction) {
        this.delayedAction = delayedAction;
    }

    /**
     * create a {@link Lazy} that will invoke the delayed-Initializer {@link Function} with the
     * passed parameter when {@link #call()} is invoked the first time
     *
     * @param delayedInitializer the {@link Function} that will be called later
     * @param parameter          the parameter that will be passed to the {@link Function} when it is called
     * @param <T>                the type of the item to be lazy initialized
     * @param <P>                the type of the parameter that will be passed later to the initializer {@link Function}
     * @return a {@link Lazy} instance
     */
    public static <T, P> Lazy<T> defer(Function<P, T> delayedInitializer, P parameter) {
        return new Lazy<>(Curry.toCallable(delayedInitializer, parameter));
    }

    @Override
    public <R> R flatMap(Function<T, R> flatMapper) {
        return Invoker.invoke(flatMapper, call());
    }

    /**
     * request the item in this {@link Lazy} instance, if the item was not initialized before, it
     * will initialize it for the first time only, then it will act as a normal getter to the item
     * held in this {@link Lazy} instance
     *
     * @return the item stored if initialized, or initialize and get the item if not initialized
     */
    @Override
    public T call() {
        if (item == null) {
            item = Invoker.invoke(delayedAction);
        }
        return item;
    }

    /**
     * lazily convert the item stored in this {@link Lazy} into another {@link Lazy} ...
     * the new {@link Lazy} will not initialize itself unless it's {@link #call()} or
     * {@link #flatMap(Function)} is invoked ... so you can invoke multiple map operations on an
     * un-initialized {@link Lazy}, and all of them will initialize them selves when you invoke the
     * {@link #call()} method, for example :
     * <p>
     * <p>
     * Lazy.defer(() -> requestInteger())<br>
     * .map(requestedInteger -> requestString(requestedInteger))<br>
     * .map(requestedString -> convertToJson(requestedString))<br>
     * .call();     // all actions are invoked at this step
     * <p>
     * if the current item was not initialized tey, it will be initialized when the new {@link Lazy}
     * calls it's {@link #call()} ... if it is already initialized, the new {@link Lazy} will not
     *
     * @param mapper the lazy converter {@link Function} that will convert the stored item
     *               (or the item that will be initialized) into another item when the {@link #call()}
     *               method is invoked
     * @param <R>    the new converted item type
     * @return another {@link Lazy} that will not initialize it'self until the {@link #call()} method
     * is invoked
     */
    public <R> Lazy<R> map(final Function<T, R> mapper) {
        return Lazy.defer(new Callable<R>() {
            @Override
            public R call() throws Exception {
                return mapper.apply(Lazy.this.call());
            }
        });
    }

    /**
     * create a {@link Lazy} that will invoke the passed {@link Callable}
     * when {@link #call()} is invoked the first time
     *
     * @param delayedInitializer the {@link Callable} that will be called later
     * @param <T>                the expected item type
     * @return a {@link Lazy} that will invoke the passed {@link Callable} when invoking
     * {@link #call()} or {@link #flatMap(Function)}
     */
    public static <T> Lazy<T> defer(Callable<T> delayedInitializer) {
        return new Lazy<>(delayedInitializer);
    }

    /**
     * lazily apply an action to the stored item in this {@link Lazy}, the operation will not be
     * executed unless the {@link #call()} or {@link #flatMap(Function)} methods are invoked, so
     * invoking this function will not cause this {@link Lazy} to initialize it's item ... but the
     * operation itself will be delayed until the {@link #call()} or {@link #flatMap(Function)}
     * methods are invoked
     *
     * @param lazyAction an action that will be invoked when the {@link #call()} method is invoked
     * @return a new {@link Lazy} that waits for it's {@link #call()} or {@link #flatMap(Function)}
     * method is invoked
     */
    @Override
    public Lazy<T> apply(final Consumer<T> lazyAction) {
        return Lazy.defer(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T item = Lazy.this.call();
                lazyAction.accept(item);
                return item;
            }
        });
    }
}
