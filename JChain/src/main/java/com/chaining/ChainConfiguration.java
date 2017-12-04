package com.chaining;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.functions.BiConsumer;

/**
 * a class that handles the configurations of the {@link Chain} class
 */
public class ChainConfiguration {

    private static final ChainConfigurationImpl implementation;

    static {
        implementation = ChainConfigurationImpl.getInstance(null);
    }

    /**
     * set the debugging behavior of the {@link Chain} class
     *
     * @param debugging pass {@code true} if the application is in the debugging mode
     */
    public static void setDebugging(boolean debugging) {
        implementation.setDebugging(debugging);
    }

    /**
     * set weather the logging behavior is enabled or disabled
     *
     * @param logging pass {@code true} to enable logging, or {@code false} to disable it
     */
    public static void setLogging(boolean logging) {
        implementation.setLogging(logging);
    }

    /**
     * set the logging function that will be executed when executing an info log
     *
     * @param infoLogger the info logger function
     */
    public static void setInfoLogger(BiConsumer<Object, Object> infoLogger) {
        implementation.setInfoLogger(infoLogger);
    }

    /**
     * set the logging function that will be executed when executing an error log
     *
     * @param errorLogger the error logger function
     */
    public static void setErrorLogger(BiConsumer<Object, Object> errorLogger) {
        implementation.setErrorLogger(errorLogger);
    }

    /**
     * set the logging function that will be executed when executing an exception log
     *
     * @param exceptionLogger the exception logger function
     */
    public static void setExceptionLogger(BiConsumer<Object, Throwable> exceptionLogger) {
        implementation.setExceptionLogger(exceptionLogger);
    }
}


class ChainConfigurationImpl {

    private static final Map<Object, ChainConfigurationImpl> instances = new LinkedHashMap<>();

    private boolean debugging;
    private boolean logging;
    private BiConsumer<Object, Object> infoLogger;
    private BiConsumer<Object, Object> errorLogger;
    private BiConsumer<Object, Throwable> exceptionLogger;

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

    boolean isLogging() {
        return logging;
    }

    void setLogging(boolean logging) {
        this.logging = logging;
    }

    BiConsumer<Object, Object> getInfoLogger() {
        return infoLogger;
    }

    void setInfoLogger(BiConsumer<Object, Object> infoLogger) {
        this.infoLogger = infoLogger;
    }

    BiConsumer<Object, Object> getErrorLogger() {
        return errorLogger;
    }

    void setErrorLogger(BiConsumer<Object, Object> errorLogger) {
        this.errorLogger = errorLogger;
    }

    BiConsumer<Object, Throwable> getExceptionLogger() {
        return exceptionLogger;
    }

    void setExceptionLogger(BiConsumer<Object, Throwable> exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }
}
