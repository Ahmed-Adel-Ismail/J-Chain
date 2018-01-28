package com.chaining;

import com.chaining.interfaces.Monad;

import java.util.concurrent.Callable;

import io.reactivex.functions.Function;

/**
 * a state of the Chain that does not take action until it's {@link #call()} or {@link #flatMap(Function)}
 * methods are invoked
 * <p>
 * Created by Ahmed Adel Ismail on 1/28/2018.
 */
public class Lazy<T> implements Callable<T>, Monad<T> {

    T item;
    final Callable<T> delayedAction;

    Lazy(Callable<T> delayedAction) {
        this.delayedAction = delayedAction;
    }

    /**
     * create a {@link Lazy} that will invoke the passed {@link Callable} later
     *
     * @param delayedAction the {@link Callable} that will be called later
     * @param <T>           the expected item type
     * @return a {@link Lazy} that will invoke the passed {@link Callable} when invoking
     * {@link #call()} or {@link #flatMap(Function)}
     */
    public static <T> Lazy<T> defer(Callable<T> delayedAction) {
        return new Lazy<>(delayedAction);
    }


    @Override
    public T call() {
        if (item == null) {
            item = Invoker.invoke(delayedAction);
        }
        return item;
    }

    @Override
    public <R> R flatMap(Function<T, R> flatMapper) {
        return Invoker.invoke(flatMapper, call());
    }
}
