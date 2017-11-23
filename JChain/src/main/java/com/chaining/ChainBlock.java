package com.chaining;

import com.chaining.annotations.SideEffect;
import com.chaining.exceptions.RuntimeExceptionConverter;
import com.chaining.interfaces.Monad;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.functional.curry.Curry.toCallable;

/**
 * a generic class that holds common patterns for chaining functions
 * <p>
 * Created by Ahmed Adel Ismail on 11/23/2017.
 */
//@SuppressWarnings("unchecked")
abstract class ChainBlock<T, S extends ChainBlock<T, S>>
        implements
        Function<Consumer<T>, S>,
        Monad<T> {

    final T item;
    final ChainConfigurationImpl configuration;

    ChainBlock(T item, ChainConfigurationImpl configuration) {
        this.item = item;
        this.configuration = configuration;
    }

    /**
     * copy the subclass of {@link ChainBlock} into a new immutable copy, with the same
     * {@link ChainConfigurationImpl}
     *
     * @param item the item for the new copy
     * @return a new immutable copy with the same {@link ChainConfigurationImpl}
     */
    S copy(T item) {
        return copy(item, configuration);
    }

    /**
     * copy the subclass of {@link ChainBlock} into a new immutable copy
     *
     * @param item          the item for the new copy
     * @param configuration the {@link ChainConfigurationImpl} for the new copy
     * @return a new immutable copy
     */
    abstract S copy(T item, ChainConfigurationImpl configuration);

    /**
     *
     * @param item
     * @param <R>
     * @param <N>
     * @return
     */
    <R, N extends ChainBlock<R, N>> N convert(R item){
        return convert(item,configuration);
    }


    /**
     * convert the subclass into a copy of another type as it's item
     *
     * @param item          the item for the new copy
     * @param configuration the {@link ChainConfigurationImpl} for the new copy
     * @param <R>           the expected item item
     * @param <N>           the new sub-class generic type
     * @return a new immutable copy with different type
     */
    abstract <R, N extends ChainBlock<R, N>> N convert(R item, ChainConfigurationImpl configuration);

    /**
     * apply an action to the stored item
     *
     * @param action the action to be applied
     * @return {@code this} instance for chaining
     */
    public S apply(Consumer<T> action) {
        try {
            action.accept(item);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
        return (S) this;
    }

    /**
     * apply an action before going to the next step in this chain, this operation is
     * intended for side-effects
     *
     * @param action an {@link Action} to be executed
     * @return {@code this} instance for chaining
     */
    @SideEffect("usually this operation is done for side-effects")
    public S apply(Action action) {
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
        return (S) this;
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
     * invoke an action on the root item that may throw an {@link Exception}
     *
     * @param action the {@link Consumer} to be invoked
     * @return a {@link Guard} to handle safe execution
     */
    public Guard<T, S> guard(Consumer<T> action) {
        try {
            return new Guard<>(toCallable(invokeGuardFunction(), action), (S) this);
        } catch (Exception e) {
            return new Guard<>(e, (S) this);
        }

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

    public S defaultIfEmpty(@NonNull T defaultValue) {
        if (item == null) {
            return copy(defaultValue, configuration);
        }
        return (S) this;
    }
}
