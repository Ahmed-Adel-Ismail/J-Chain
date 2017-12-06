package com.chaining;

import com.chaining.exceptions.RuntimeExceptionConverter;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;


/**
 * a class responsible for logging, you must set the logging behavior and logger functions in
 * {@link ChainConfiguration}
 * <p>
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public class Logger<S extends Internal<S,T>, T> {

    final S source;
    final Object tag;
    private final InternalConfiguration configuration;


    Logger(S source, InternalConfiguration configuration, Object tag) {
        this.source = source;
        this.configuration = configuration;
        this.tag = tag;
    }

    /**
     * build a message from the currently stored item
     *
     * @param messageComposer the message composer function
     * @return a {@link MessageLogger} to handle logging the message
     */
    public MessageLogger<S, T> message(Function<T, Object> messageComposer) {
        try {
            return new MessageLogger<>(this, messageComposer.apply(source.proxy().getItem()));
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * log an info message, to activate this operation, you must set
     * {@link ChainConfiguration#setLogging(boolean)} to
     * {@code true} and set {@link ChainConfiguration#setInfoLogger(BiConsumer)} with the logger
     * function that will be invoked
     *
     * @param message the message to be logged
     * @return the starter of this {@link Logger}
     */
    public S info(Object message) {
        if (configuration.isLogging() && configuration.getInfoLogger() != null) {
            guardAccept(configuration.getInfoLogger(), message);
        }
        return source;
    }

    private <V> void guardAccept(BiConsumer<Object, V> biConsumer, V message) {
        try {
            biConsumer.accept(tag, message);
        } catch (Exception e) {
            throw new RuntimeExceptionConverter().apply(e);
        }
    }

    /**
     * log an error message, to activate this operation, you must set
     * {@link ChainConfiguration#setLogging(boolean)} to
     * {@code true} and set {@link ChainConfiguration#setErrorLogger(BiConsumer)} with the logger
     * function that will be invoked
     *
     * @param message the message to be logged
     * @return the starter of this {@link Logger}
     */
    public S error(Object message) {
        if (configuration.isLogging() && configuration.getErrorLogger() != null) {
            guardAccept(configuration.getErrorLogger(), message);
        }
        return source;
    }

    /**
     * log an exception, to activate this operation, you must set
     * {@link ChainConfiguration#setLogging(boolean)} to
     * {@code true} and set {@link ChainConfiguration#setExceptionLogger(BiConsumer)} with the logger
     * function that will be invoked
     *
     * @param exception the exception to be logged
     * @return the starter of this {@link Logger}
     */
    public S exception(Throwable exception) {
        if (configuration.isLogging() && configuration.getErrorLogger() != null) {
            guardAccept(configuration.getExceptionLogger(), exception);
        }
        return source;
    }

}
