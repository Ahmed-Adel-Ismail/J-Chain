package com.chaining.types.eithers;

public class EitherException extends RuntimeException {

    private Object otherValue;

    public EitherException(Object value) {
        this.otherValue = value;
    }

    @SuppressWarnings("unchecked")
    public <V> V getOtherValue() {
        return (V) otherValue;
    }

}
