package com.chaining;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public class LoggerTest {


    @Test
    public void infoWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<Callable<Boolean>, Boolean> logger = logger(false,
                "infoWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.info(logMessage);

        assertTrue(logMessage.value);

    }

    private static Logger<Callable<Boolean>, Boolean> logger(final boolean crash, String configName) {

        ChainConfigurationImpl config = ChainConfigurationImpl.getInstance(configName);
        config.setLogging(true);

        BiConsumer<Object, Object> assertTrueBiConsumer = new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) throws Exception {
                if (!crash) {
                    ((LogMessage) o2).value = true;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        };

        config.setInfoLogger(assertTrueBiConsumer);
        config.setErrorLogger(assertTrueBiConsumer);
        config.setExceptionLogger(new BiConsumer<Object, Throwable>() {
            @Override
            public void accept(Object o, Throwable throwable) throws Exception {
                if (!crash) {
                    ((LogMessage) throwable).value = true;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        });

        return new Logger<Callable<Boolean>, Boolean>(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                return !crash;
            }
        }, config, LoggerTest.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void infoWithCrashingMessageThenCrash() {

        Logger<Callable<Boolean>, Boolean> logger = logger(true,
                "infoWithCrashingMessageThenCrash");
        LogMessage logMessage = new LogMessage();

        logger.info(logMessage);
    }

    @Test
    public void errorWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<Callable<Boolean>, Boolean> logger = logger(false,
                "errorWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.error(logMessage);

        assertTrue(logMessage.value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void errorWithCrashingMessageThenCrash() {

        Logger<Callable<Boolean>, Boolean> logger = logger(true,
                "errorWithCrashingMessageThenCrash");
        LogMessage logMessage = new LogMessage();

        logger.error(logMessage);
    }

    @Test
    public void exceptionWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<Callable<Boolean>, Boolean> logger = logger(false,
                "exceptionWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.exception(logMessage);

        assertTrue(logMessage.value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionWithCrashingMessageThenCrash() {

        Logger<Callable<Boolean>, Boolean> logger = logger(true,
                "exceptionWithCrashingMessageThenCrash");
        LogMessage logMessage = new LogMessage();

        logger.exception(logMessage);
    }

    @Test
    public void messageThenInfoWithValidFunctionThenUseTheItemInLogging() {

        final boolean[] result = {false};

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("messageWithValidFunctionThenUseTheItemInLogging");

        config.setLogging(true);
        config.setInfoLogger(booleanBiConsumer(result));

        booleanLogger(config).message(new Function<Boolean, Object>() {
            @Override
            public Object apply(Boolean item) throws Exception {
                return item;
            }
        }).info();

        assertTrue(result[0]);
    }

    private BiConsumer<Object, Object> booleanBiConsumer(final boolean[] result) {
        return new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) throws Exception {
                result[0] = (boolean) o2;
            }
        };
    }

    private Logger<Callable<Boolean>, Boolean> booleanLogger(ChainConfigurationImpl config) {
        return new Logger<Callable<Boolean>, Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return true;
            }
        }, config, Logger.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void messageThenInfoWithCrashingFunctionThenCrash() {

        final boolean[] result = {false};

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("messageWithCrashingFunctionThenCrash");
        config.setLogging(true);
        config.setInfoLogger(booleanBiConsumer(result));

        booleanLogger(config).message(new Function<Boolean, Object>() {
            @Override
            public Object apply(Boolean item) throws Exception {
                throw new UnsupportedOperationException();
            }
        }).info();

    }

    @Test
    public void messageThenErrorWithValidFunctionThenUseTheItemInLogging() {

        final boolean[] result = {false};

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("messageWithValidFunctionThenUseTheItemInLogging");
        config.setLogging(true);
        config.setErrorLogger(booleanBiConsumer(result));

        booleanLogger(config).message(new Function<Boolean, Object>() {
            @Override
            public Object apply(Boolean item) throws Exception {
                return item;
            }
        }).error();

        assertTrue(result[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void messageThenErrorWithCrashingFunctionThenCrash() {

        final boolean[] result = {false};

        ChainConfigurationImpl config = ChainConfigurationImpl
                .getInstance("messageWithCrashingFunctionThenCrash");
        config.setLogging(true);
        config.setErrorLogger(booleanBiConsumer(result));

        booleanLogger(config).message(new Function<Boolean, Object>() {
            @Override
            public Object apply(Boolean item) throws Exception {
                throw new UnsupportedOperationException();
            }
        }).error();

    }


}


class LogMessage extends Throwable {
    boolean value = false;
}