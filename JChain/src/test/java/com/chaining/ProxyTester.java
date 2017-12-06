package com.chaining;

import io.reactivex.functions.Action;

import static org.junit.Assert.assertTrue;

/**
 * a class that runs tests for {@link Proxy} subclasses
 * <p>
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
public class ProxyTester<S extends Internal<S, T>, T> implements Action {

    private final Internal<S, T> owner;
    private final Proxy<S, T> target;
    private final T item;
    private final T newItem;
    private final InternalConfiguration configuration;

    ProxyTester(Internal<S, T> internal, T newItem) {
        this.owner = internal;
        this.target = internal.access();
        this.item = target.getItem();
        this.newItem = newItem;
        this.configuration = target.getConfiguration();
    }

    @Override
    public void run() {
        assertTrue("copyWithNoParameters", copyWithNoParameters());
        assertTrue("copyWithNewItem", copyWithNewItem());
        assertTrue("copyWithNewItemAndConfiguration", copyWithNewItemAndConfiguration());
        assertTrue("owner", owner());
    }

    private boolean owner() {
        return target.owner() == owner;
    }

    private boolean copyWithNoParameters() {
        if (target.copy().access().getItem() == null) {
            return null == item;
        } else {
            return target.copy().access().getItem().equals(item);
        }
    }

    private boolean copyWithNewItem() {
        if (newItem == null) {
            return target.copy(null).access().getItem() == null;
        } else {
            return target.copy(newItem).access().getItem().equals(newItem);
        }
    }

    private boolean copyWithNewItemAndConfiguration() {
        S newTarget = target.copy(newItem, configuration);
        if (newItem == null) {
            return newTarget.access().getItem() == null
                    && newTarget.access().getConfiguration().equals(configuration);
        } else {
            return newTarget.access().getItem().equals(newItem)
                    && newTarget.access().getConfiguration().equals(configuration);
        }
    }

}
