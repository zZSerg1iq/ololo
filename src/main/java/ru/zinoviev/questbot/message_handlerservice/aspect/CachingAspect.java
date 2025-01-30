package ru.zinoviev.questbot.message_handlerservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Aspect
public class CachingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* ru.zinoviev.questbot.message_handlerservice.response(..))")
    public void tracePointcut() {
    }

    @Before("tracePointcut()")
    public void beforeServiceMethods(JoinPoint joinPoint) {
        LOG.warn("Вызван метод: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(value = "tracePointcut()", throwing = "ex")
    public void afterThrowingServiceMethods(JoinPoint joinPoint, Throwable ex) {
        LOG.error("Метод: {} выбросил исключение {} ", joinPoint.getSignature().getName(), ex);
    }
}
