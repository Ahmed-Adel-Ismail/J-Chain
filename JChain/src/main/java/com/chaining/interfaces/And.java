package com.chaining.interfaces;


import com.chaining.Collector;

/**
 * an interface implemented by Classes that has an and() method
 * <p>
 * Created by Ahmed Adel Ismail on 11/13/2017.
 */
public interface And<T> {


    /**
     * add the item to the group of items to be used all together later on
     *
     * @param item an item to be added
     * @return a {@link Collector} for managing those items
     */
    Collector<T> and(T item);

}
