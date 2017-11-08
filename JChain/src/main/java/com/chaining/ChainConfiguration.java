package com.chaining;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a class that handles the configurations of the {@link Chain} class
 */
public class ChainConfiguration {

    /**
     * set the debugging behavior of the {@link Chain} class
     *
     * @param debugging pass {@code true} if the application is in the debugging mode
     */
    public static void setDebugging(boolean debugging) {
        ChainConfigurationImpl.getInstance(null).setDebugging(debugging);
    }
}


class ChainConfigurationImpl {

    private static final Map<Object, ChainConfigurationImpl> instances = new LinkedHashMap<>();

    private boolean debugging;

    private ChainConfigurationImpl() {
    }

    synchronized static ChainConfigurationImpl getInstance(Object key) {
        ChainConfigurationImpl instance = instances.get(key);
        if (instance == null) {
            instance = new ChainConfigurationImpl();
            instances.put(key, instance);
        }
        return instance;
    }


    boolean isDebugging() {
        return debugging;
    }

    void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }
}
