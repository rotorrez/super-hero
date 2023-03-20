package com.w2m.superhero.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ExecutionTimeRequestAdvice {
    private static final Logger logger = LogManager.getLogger(ExecutionTimeRequestAdvice.class);

    @Around("@annotation(com.w2m.superhero.util.ExecutionTimeRequest)")
    public Object trackTime(ProceedingJoinPoint point) throws Throwable {
        long start = System.nanoTime();
        Object object = point.proceed();
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        logger.info("Time take by " + point.getSignature().getName() + "() method is " + TimeUnit.NANOSECONDS.toMillis(timeElapsed) + " ms");
        return object;
    }
}
