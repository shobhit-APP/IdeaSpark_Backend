package com.ideaspark.api.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging exceptions across the application
 */
@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.ideaspark.api.service..*(..))", throwing = "exception")
    public void logServiceExceptions(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.error("Exception in service {}.{}: {} - {}", 
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.ideaspark.api.controller..*(..))", throwing = "exception")
    public void logControllerExceptions(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.error("Exception in controller {}.{}: {} - {}", 
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.ideaspark.api.repository..*(..))", throwing = "exception")
    public void logRepositoryExceptions(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.error("Exception in repository {}.{}: {} - {}", 
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }
}