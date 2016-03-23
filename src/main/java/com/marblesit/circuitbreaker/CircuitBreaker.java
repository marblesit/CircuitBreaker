package com.marblesit.circuitbreaker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CircuitBreaker {
	String value() default "";
	String name() default "";
	int timeoutMilliseconds() default 1000;
	boolean useThreads() default true;
}
