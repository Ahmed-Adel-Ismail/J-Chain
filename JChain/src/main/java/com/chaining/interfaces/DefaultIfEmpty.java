package com.chaining.interfaces;


import com.chaining.Chain;

import io.reactivex.annotations.NonNull;

/**
 * an interface that is implemented by optional classes
 * <p>
 * Created by Ahmed Adel Ismail on 11/6/2017.
 */
public interface DefaultIfEmpty<T> {

    /**
     * if the root item is null, this function will set it to a new default value
     *
     * @param defaultValue the default value if the Object is {@code null}
     * @return a new {@link Chain} with the default value, if the current {@link Chain} was broken
     * by a {@code null} root, else it will return the same {@link Chain} to continue
     */
    Chain<T> defaultIfEmpty(@NonNull T defaultValue);
}
