package com.chaining.types.eithers;

import org.junit.Test;

import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EitherTest {

    @Test
    public void getValueWhenGetRightOrCrashWithLeftValueOnlyThenThrowEitherException() {
        try {
            Either.withLeft("---").getRightOrCrash();
        } catch (EitherException e) {
            assertNotNull(e.getOtherValue());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void getRightOrCrashWithNoValuesThenThrowEitherException() throws Exception {
        Either.withRight(null).getRightOrCrash();
    }

    @Test(expected = NoSuchElementException.class)
    public void getLeftOrCrashWithNoValuesThenThrowEitherException() throws Exception {
        Either.withLeft(null).getLeftOrCrash();
    }

    @Test(expected = EitherException.class)
    public void getRightOrCrashWithLeftValueOnlyThenThrowEitherException() throws Exception {
        Either.withLeft("---").getRightOrCrash();
    }

    @Test(expected = EitherException.class)
    public void getLeftOrCrashWithRightValueOnlyThenThrowEitherException() throws Exception {
        Either.withRight("---").getLeftOrCrash();
    }

    @Test
    public void getRightOrCrashWithLeftAndRightValuesThenReturnValue() throws Exception {
        Object result = Either.withLeft("---")
                .setRight(1)
                .getRightOrCrash();

        assertNotNull(result);
    }

    @Test
    public void getLeftOrCrashWithLeftAndRightValuesThenReturnValue() throws Exception {
        Object result = Either.withLeft("---")
                .setRight(1)
                .getLeftOrCrash();

        assertNotNull(result);
    }

    @Test
    public void getRightOrCrashWithRightValueOnlyThenReturnValue() throws Exception {
        Object result = Either.withRight("---").getRightOrCrash();
        assertNotNull(result);
    }

    @Test
    public void getLeftOrCrashWithLeftValueOnlyThenReturnValue() throws Exception {
        Object result = Either.withLeft("---").getLeftOrCrash();
        assertNotNull(result);
    }


    @Test
    public void getRightWithLeftValueOnlyThenReturnEmptyMaybe() throws Exception {
        Object result = Either.withLeft("---")
                .getRight()
                .blockingGet();

        assertNull(result);
    }

    @Test
    public void getLeftWithRightValueOnlyThenReturnEmptyMaybe() throws Exception {
        Object result = Either.withRight("---")
                .getLeft()
                .blockingGet();

        assertNull(result);
    }


    @Test
    public void getRightWithRightAndLeftValueThenReturnNonEmptyMaybe() throws Exception {
        Object result = Either.withLeft("---")
                .setRight(1)
                .getRight()
                .blockingGet();

        assertNotNull(result);
    }

    @Test
    public void getLeftWithRightAndLeftValueThenReturnNonEmptyMaybe() throws Exception {
        Object result = Either.withRight("---")
                .setLeft(1)
                .getLeft()
                .blockingGet();

        assertNotNull(result);
    }

    @Test
    public void biasRightWithLeftAndRightValuesThenInvokeConsumer() {

        final boolean[] result = {false};

        Either.withLeft(1)
                .setRight(2)
                .rightBias()
                .apply(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                })
                .accept(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        result[0] = true;
                    }
                });

        assertTrue(result[0]);
    }

    @Test
    public void biasLeftWithLeftAndRightValuesThenInvokeConsumer() {

        final boolean[] result = {false};

        Either.withLeft(1)
                .setRight(2)
                .leftBias()
                .apply(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        result[0] = true;
                    }
                })
                .accept(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });

        assertTrue(result[0]);
    }

}