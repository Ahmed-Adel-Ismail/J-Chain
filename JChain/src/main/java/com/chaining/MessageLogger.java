package com.chaining;

import java.util.concurrent.Callable;

/**
 * a logger that uses a pre-build Message
 * <p>
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public class MessageLogger<T extends Callable<I>, I> {

    private final Logger<T, I> logger;
    private final Object message;

    public MessageLogger(Logger<T, I> logger, Object message) {
        this.logger = logger;
        this.message = message;
    }

    /**
     * log the generated message as an info log
     *
     * @return the source that started the logging operation
     */
    public T info() {
        return logger.info(message);
    }

    /**
     * log the generated message as an error log
     *
     * @return the source that started the logging operation
     */
    public T error() {
        return logger.error(message);
    }

}


