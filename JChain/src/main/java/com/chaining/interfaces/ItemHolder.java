package com.chaining.interfaces;

/**
 * an interface implemented by classes that holds one item
 * <p>
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public interface ItemHolder<T> {

    /**
     * get the item held by this Object
     *
     * @return the item in this Object
     * @deprecated used internally by the library
     */
    T getItem();

}
