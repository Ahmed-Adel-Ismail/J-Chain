package com.chaining.types.eithers;

import com.functional.curry.RxBiConsumer;
import com.functional.curry.RxConsumer;

import java.util.NoSuchElementException;

import io.reactivex.Maybe;
import io.reactivex.functions.Consumer;

/**
 * a data strucure that will hold two values, the left value indicates the error value, and
 * the right value indicates the success value
 *
 * @param <L> the left value type - usually this is the error type
 * @param <R> the right value type, usually this is the success type
 */
public class Either<L, R> {

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <T> Either<T, ?> withLeft(T left) {
        return new Either<>(left, null);
    }

    public static <T> Either<?, T> withRight(T right) {
        return new Either<>(null, right);
    }

    public Maybe<R> getRight() {
        return right != null ? Maybe.just(right) : Maybe.<R>empty();
    }

    public <T> Either<L, T> setRight(T right) {
        return new Either<>(left, right);
    }

    public Maybe<L> getLeft() {
        return left != null ? Maybe.just(left) : Maybe.<L>empty();
    }

    public <T> Either<T, R> setLeft(T left) {
        return new Either<>(left, right);
    }

    public R getRightOrCrash() throws EitherException, NoSuchElementException {
        if (right != null) {
            return right;
        } else if (left != null) {
            throw new EitherException(left);
        } else {
            throw new NoSuchElementException();
        }

    }

    public L getLeftOrCrash() throws EitherException, NoSuchElementException {
        if (left != null) {
            return left;
        } else if (right != null) {
            throw new EitherException(right);
        } else {
            throw new NoSuchElementException();
        }
    }

    RxBiConsumer<Consumer<L>, Consumer<R>> leftBias() {
        return new RxBiConsumer<Consumer<L>, Consumer<R>>() {
            @Override
            public RxConsumer<Consumer<R>> apply(final Consumer<L> onLeftAvailable) {
                return new RxConsumer<Consumer<R>>() {
                    @Override
                    public void accept(Consumer<R> onRightAvailable) {
                        try {
                            invokeConsumers(onLeftAvailable, onRightAvailable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            private void invokeConsumers(Consumer<L> onLeftAvailable, Consumer<R> onRightAvailable)
                    throws Exception {
                if (left != null) {
                    onLeftAvailable.accept(left);
                } else if (right != null) {
                    onRightAvailable.accept(right);
                }
            }
        };
    }

    RxBiConsumer<Consumer<L>, Consumer<R>> rightBias() {
        return new RxBiConsumer<Consumer<L>, Consumer<R>>() {
            @Override
            public RxConsumer<Consumer<R>> apply(final Consumer<L> onLeftAvailable) {
                return new RxConsumer<Consumer<R>>() {
                    @Override
                    public void accept(Consumer<R> onRightAvailable) {
                        try {
                            invokeConsumers(onLeftAvailable, onRightAvailable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            private void invokeConsumers(Consumer<L> onLeftAvailable, Consumer<R> onRightAvailable)
                    throws Exception {
                if (right != null) {
                    onRightAvailable.accept(right);
                } else if (left != null) {
                    onLeftAvailable.accept(left);
                }
            }
        };
    }


}
