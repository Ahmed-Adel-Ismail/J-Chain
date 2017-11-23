package com.chaining;

/**
 * a function that checks for null values, and crashes if it is {@code null}
 * <p>
 * Created by Ahmed Adel Ismail on 11/23/2017.
 */
class NullChecker {

    static void crashIfNull(Object item) {
        if (item == null) {
            throw new NullPointerException("null values not accepted, use optional() instead");
        }
    }
}
