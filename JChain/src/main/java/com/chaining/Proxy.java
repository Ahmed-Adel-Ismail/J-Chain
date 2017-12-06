package com.chaining;

/**
 * a proxy class that helps classes to communicate internally without exposing the unnecessary API
 *
 * @param <S> the Outer Object type
 * @param <T> the stored item type
 */
abstract class Proxy<S, T> {

    /**
     * access the Outer Object of this {@link Proxy}
     *
     * @return the Outer Object
     */
    abstract S owner();

    /**
     * copy the Outer Object with a different item in it
     *
     * @return a new instance of the Outer Object
     */
    S copy(T item) {
        return copy(item, getConfiguration());
    }

    /**
     * copy an instance of the outer Object
     *
     * @param item the new item for the new copy
     * @return a new copy of the outer Object
     */
    abstract S copy(T item, InternalConfiguration configuration);

    /**
     * get the {@link InternalConfiguration} associated with the outer Object
     *
     * @return the {@link InternalConfiguration} for the outer Object
     */
    abstract InternalConfiguration getConfiguration();

    /**
     * copy the exact Outer Object
     *
     * @return a new instance of the Outer Object
     */
    S copy() {
        return copy(getItem(), getConfiguration());
    }

    /**
     * get the current item in the current Object
     *
     * @return the item stored in the outer Object
     */
    abstract T getItem();
}