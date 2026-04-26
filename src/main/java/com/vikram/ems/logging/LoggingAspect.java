package com.vikram.ems.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Log all service method executions with timing
    @Around("execution(* com.vikram.ems.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("→ {}.{}() called with args: {}",
                  className, methodName,
                  Arrays.toString(joinPoint.getArgs()));

        long start  = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        log.debug("← {}.{}() completed in {}ms", className, methodName, elapsed);
        return result;
    }

    // Log every REST controller request
    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void logControllerRequest(JoinPoint joinPoint) {
        log.info("[API] {}.{}()",
                 joinPoint.getSignature().getDeclaringType().getSimpleName(),
                 joinPoint.getSignature().getName());
    }

    // Log any exception thrown by service layer
    @AfterThrowing(
        pointcut = "execution(* com.vikram.ems.service..*(..))",
        throwing  = "ex"
    )
    public void logServiceException(JoinPoint joinPoint, Exception ex) {
        log.error("[Exception] {}.{}() threw: {}",
                  joinPoint.getSignature().getDeclaringType().getSimpleName(),
                  joinPoint.getSignature().getName(),
                  ex.getMessage());
    }
}