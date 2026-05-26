package com.inventory_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 🔥 Log all controller calls
    @Before("execution(* com.inventory_service.controller..*(..))")
    public void logControllerEntry(JoinPoint joinPoint) {

        log.info("➡️ [INVENTORY CONTROLLER] {}",
                joinPoint.getSignature());
    }

    // 🔥 Log successful service execution
    @AfterReturning("execution(* com.inventory_service.service..*(..))")
    public void logServiceExit(JoinPoint joinPoint) {

        log.info("✅ [INVENTORY SERVICE] {}",
                joinPoint.getSignature());
    }
}