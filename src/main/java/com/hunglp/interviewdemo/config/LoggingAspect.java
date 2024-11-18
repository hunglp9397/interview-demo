package com.hunglp.interviewdemo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(com.hunglp.interviewdemo.annotations.LogExecutionTime)")  // Indicates that this applies to the custom annotation
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();  // Proceed with the method execution

        Object [] args =   joinPoint.getArgs();

        long executionTime = System.currentTimeMillis() - start;

        logger.info("{} executed with args: {} in {} ms", joinPoint.getSignature(), args, executionTime);
        return proceed;
    }
}
