package com.inventory_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionAspect {

    @AfterThrowing(
        pointcut = "execution(* com.inventory_service.service..*(..))",
        throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Exception ex) {

        log.error("❌ [INVENTORY ERROR] {} | {}",
                joinPoint.getSignature(),
                ex.getMessage());
    }
}