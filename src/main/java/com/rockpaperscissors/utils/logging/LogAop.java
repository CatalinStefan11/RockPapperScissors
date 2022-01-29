package com.rockpaperscissors.utils.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LogAop {

    @AfterReturning(pointcut = "@annotation(com.rockpaperscissors.utils.logging.Logger)")
    private static void log(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodMessage = method.getAnnotation(Logger.class).value();

        log.info("Method {} returned with message: {}", method.getName(), methodMessage);

    }


}
