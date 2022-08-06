package com.google.android.ads.nativetemplates;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({METHOD, CONSTRUCTOR, TYPE})
@Retention(CLASS)
public @interface CanIgnoreReturnValue {}
