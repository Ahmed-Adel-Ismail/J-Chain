package com.chaining;

import org.junit.Test;

import io.reactivex.functions.BiConsumer;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
public class ChainConfigurationTest {


    @Test
    public void setAllValuesTheUpdateDefaultInInternalConfiguration() {
        ChainConfiguration.setDebugging(true);
        ChainConfiguration.setLogging(true);
        ChainConfiguration.setInfoLogger(new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) throws Exception {

            }
        });
        ChainConfiguration.setErrorLogger(new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) throws Exception {

            }
        });
        ChainConfiguration.setExceptionLogger(new BiConsumer<Object, Throwable>() {
            @Override
            public void accept(Object o, Throwable throwable) throws Exception {

            }
        });

        InternalConfiguration c = InternalConfiguration.getInstance(null);

        assertTrue(c.isDebugging()
                && c.isLogging()
                && c.getInfoLogger() != null
                && c.getErrorLogger() != null
                && c.getExceptionLogger() != null);
    }

}