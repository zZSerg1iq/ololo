package ru.zinoviev.questbot.message_handlerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
//@Aspect
//@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* ru.zinoviev.questbot.message_handlerservice.domain.service.*.*(..))")
    public void loggingPointcut() {
    }

    @Before("loggingPointcut()")
    public void beforeServiceMethods(JoinPoint joinPoint) {
        LOG.warn("Calling method {} before", joinPoint.getSignature().getName());
    }

    @AfterThrowing(value = "loggingPointcut()", throwing = "ex")
    public void afterThrowingServiceMethods(JoinPoint joinPoint, Throwable ex) {
        LOG.error("Error {} occurred while calling method {} ", ex, joinPoint.getSignature().getName());
    }
}
