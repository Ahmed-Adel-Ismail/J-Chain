package com.chaining;

import io.reactivex.functions.BiPredicate;

/**
 * a {@link BiPredicate} that compares two Objects through there {@link Object#equals(Object)},
 * it is safe to have any of them as {@code null}
 * <p>
 * Created by Ahmed Adel Ismail on 12/12/2017.
 */
class IsEqualComparator<T> implements BiPredicate<T, T> {
    @Override
    public boolean test(T leftItem, T rightItem) {
        return (leftItem == null && rightItem == null)
                || (leftItem != null && leftItem.equals(rightItem));
    }
}
