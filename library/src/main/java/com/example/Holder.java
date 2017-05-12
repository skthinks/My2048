package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Skthinks on 10/05/17.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
@Inherited
public @interface Holder {
}
