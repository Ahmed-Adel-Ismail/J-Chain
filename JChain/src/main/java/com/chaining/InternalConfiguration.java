package com.chaining;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.functions.BiConsumer;

/**
 * an internal class for handling configurations for the Chain library
 * <p>
 * Created by Ahmed Adel Ismail on 12/6/2017.
 */
class InternalConfiguration {

    private static final Map<Object, InternalConfiguration> instances = new LinkedHashMap<>();

    private boolean debugging;
    private boolean logging;
    private BiConsumer<Object, Object> infoLogger;
    private BiConsumer<Object, Object> errorLogger;
    private BiConsumer<Object, Throwable> exceptionLogger;

    private InternalConfiguration() {
    }

    synchronized static InternalConfiguration getInstance(Object key) {
        InternalConfiguration instance = instances.get(key);
        if (instance == null) {
            instance = new InternalConfiguration();
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
