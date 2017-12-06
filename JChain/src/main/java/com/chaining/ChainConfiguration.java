package com.chaining;

import io.reactivex.functions.BiConsumer;

/**
 * a class that handles the configurations of the {@link Chain} class
 */
public class ChainConfiguration {

    private static final InternalConfiguration implementation;

    static {
        implementation = InternalConfiguration.getInstance(null);
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


