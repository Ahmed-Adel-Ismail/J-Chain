package com.chaining;

import org.junit.Test;

import java.io.IOException;

public class RuntimeExceptionConverterTest {

    @Test(expected = RuntimeException.class)
    public void applyWithExceptionThenReturnRuntimeException(){
        throw new Invoker.RuntimeExceptionConverter().apply(new IOException());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void applyWithRuntimeExceptionThenReturnThatException(){
        throw new Invoker.RuntimeExceptionConverter()
                .apply(new UnsupportedOperationException());
    }

}