package com.chaining;

import org.junit.Test;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 12/4/2017.
 */
public class LoggerTest {


    @Test
    public void infoWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<InternalObject, Boolean> logger = logger(false,
                "infoWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.info(logMessage);

        assertTrue(logMessage.value);

    }

    private static Logger<InternalObject, Boolean> logger(final boolean crash, String configName) {

        InternalConfiguration config = InternalConfiguration.getInstance(configName);
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

        return new Logger<>(new InternalObject(!crash,config), config, LoggerTest.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void infoWithCrashingMessageThenCrash() {

        Logger<InternalObject, Boolean> logger = logger(true,
                "infoWithCrashingMessageThenCrash");

        LogMessage logMessage = new LogMessage();

        logger.info(logMessage);
    }

    @Test
    public void errorWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<InternalObject, Boolean> logger = logger(false,
                "errorWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.error(logMessage);

        assertTrue(logMessage.value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void errorWithCrashingMessageThenCrash() {

        Logger<InternalObject, Boolean> logger = logger(true,
                "errorWithCrashingMessageThenCrash");
        LogMessage logMessage = new LogMessage();

        logger.error(logMessage);
    }

    @Test
    public void exceptionWithNonCrashingMessageThenUpdateLogMessageWithTrueValue() {

        Logger<InternalObject, Boolean> logger = logger(false,
                "exceptionWithNonCrashingMessageThenUpdateLogMessageWithTrueValue");
        LogMessage logMessage = new LogMessage();

        logger.exception(logMessage);

        assertTrue(logMessage.value);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionWithCrashingMessageThenCrash() {

        Logger<InternalObject, Boolean> logger = logger(true,
                "exceptionWithCrashingMessageThenCrash");
        LogMessage logMessage = new LogMessage();

        logger.exception(logMessage);
    }

    @Test
    public void messageThenInfoWithValidFunctionThenUseTheItemInLogging() {

        final boolean[] result = {false};

        InternalConfiguration config = InternalConfiguration
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

    private Logger<InternalObject, Boolean> booleanLogger(InternalConfiguration config) {
        return new Logger<>(new InternalObject(true,config), config, Logger.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void messageThenInfoWithCrashingFunctionThenCrash() {

        final boolean[] result = {false};

        InternalConfiguration config = InternalConfiguration
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

        InternalConfiguration config = InternalConfiguration
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

        InternalConfiguration config = InternalConfiguration
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

class InternalObject implements Internal<InternalObject, Boolean> {


    final Boolean item;
    final InternalConfiguration configuration;

    InternalObject(Boolean item, InternalConfiguration configuration) {
        this.item = item;
        this.configuration = configuration;
    }

    @Override
    public Proxy<InternalObject, Boolean> proxy() {
        return new Proxy<InternalObject, Boolean>() {
            @Override
            InternalObject copy(Boolean item, InternalConfiguration configuration) {
                return new InternalObject(item, configuration);
            }

            @Override
            InternalConfiguration getConfiguration() {
                return configuration;
            }

            @Override
            Boolean getItem() {
                return item;
            }
        };
    }


}