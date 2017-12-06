package com.chaining;

import io.reactivex.functions.Action;

import static org.junit.Assert.assertTrue;

/**
 * a class that runs tests for {@link Proxy} subclasses
 * <p>
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
public class ProxyTester<S extends Internal<S, T>, T> implements Action {

    private final Proxy<S, T> target;
    private final T item;
    private final T newItem;
    private final InternalConfiguration configuration;

    ProxyTester(Proxy<S, T> target, T newItem) {
        this.target = target;
        this.item = target.getItem();
        this.newItem = newItem;
        this.configuration = target.getConfiguration();
    }

    @Override
    public void run() {
        assertTrue("copyWithNoParameters", copyWithNoParameters());
        assertTrue("copyWithNewItem", copyWithNewItem());
        assertTrue("copyWithNewItemAndConfiguration", copyWithNewItemAndConfiguration());
    }

    private boolean copyWithNoParameters() {
        if (target.copy().proxy().getItem() == null) {
            return null == item;
        } else {
            return target.copy().proxy().getItem().equals(item);
        }
    }

    private boolean copyWithNewItem() {
        if(newItem == null){
            return target.copy(null).proxy().getItem() == null;
        }else {
            return target.copy(newItem).proxy().getItem().equals(newItem);
        }
    }

    private boolean copyWithNewItemAndConfiguration() {
        S newTarget = target.copy(newItem, configuration);
        if(newItem == null ){
            return newTarget.proxy().getItem() == null
                    && newTarget.proxy().getConfiguration().equals(configuration);
        }else {
            return newTarget.proxy().getItem().equals(newItem)
                    && newTarget.proxy().getConfiguration().equals(configuration);
        }
    }

}
