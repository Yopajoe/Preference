package org.etsntesla.gava.utils.annotations;


import org.etsntesla.gava.utils.enums.PreferenceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreferenceRedisCacheDelete  {
    PreferenceType type() default PreferenceType.UNDEFINED;
}
