package com.chaining;

/**
 * a proxy interface to hide the internal communication between classes
 * <p>
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
interface Internal<S, T> {

    /**
     * access the {@link Proxy} of the current Object
     *
     * @return a {@link Proxy} to access the internal methods based on visibility restriction
     */
    Proxy<S, T> proxy();

}


