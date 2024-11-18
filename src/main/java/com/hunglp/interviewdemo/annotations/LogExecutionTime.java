package com.hunglp.interviewdemo.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // Specifies that this annotation can be applied to methods
@Retention(RetentionPolicy.RUNTIME)  // The annotation will be available at runtime
public @interface LogExecutionTime {
}