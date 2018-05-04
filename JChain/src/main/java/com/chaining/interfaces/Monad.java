package com.chaining.interfaces;


import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * an interface implemented by Monads, or classes with flatMap() methods
 * <p>
 * Created by Ahmed Adel Ismail on 11/13/2017.
 */
public interface Monad<T> {

    /**
     * a flat map function that converts this Object into another Object using the item held in it
     *
     * @param flatMapper a function that will convert the current Object to another Object based
     *                   on the item stored
     * @param <R>        the type of the new Object
     * @return the new Object
     */
    <R> R flatMap(@NonNull Function<T, R> flatMapper);
}


