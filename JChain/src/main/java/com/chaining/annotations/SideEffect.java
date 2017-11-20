package com.chaining.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * a marker annotation to notify that a side effect will occur ... an annotation for sake
 * of readability only
 * <p>
 * you should pass the {@code String} that describes what side effect will occur in the
 * {@link #value()}
 * <p>
 * Created by Ahmed Adel Ismail on 9/28/2017.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface SideEffect {

    String value();

}
