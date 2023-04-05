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
        logger.info(builderLogMessage(point.getSignature().getName(), TimeUnit.NANOSECONDS.toMillis(timeElapsed)));
        return object;
    }

    public String builderLogMessage(String name, Long timeElapsed) {
        final StringBuilder builder = new StringBuilder()
                .append("Time take by ").append(name).append("() method is ").append(timeElapsed).append(" ms");
        return builder.toString();
    }
}
