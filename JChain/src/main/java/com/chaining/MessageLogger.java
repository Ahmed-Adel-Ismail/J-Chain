package com.chaining;

/**
 * a logger that uses a pre-build Message
 * <p>
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public class MessageLogger<S extends Internal<S,T>, T> {

    private final Logger<S, T> logger;
    private final Object message;

    public MessageLogger(Logger<S, T> logger, Object message) {
        this.logger = logger;
        this.message = message;
    }

    /**
     * log the generated message as an info log
     *
     * @return the source that started the logging operation
     */
    public S info() {
        return logger.info(message);
    }

    /**
     * log the generated message as an error log
     *
     * @return the source that started the logging operation
     */
    public S error() {
        return logger.error(message);
    }

}


