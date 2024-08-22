package com.example.authenticationauthorization.configuration.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //ghi log tất cả

    //    @Pointcut("within(@org.springframework.stereotype.Component *) || within(@org.springframework.context.annotation.Configuration *) || within(@org.springframework.stereotype.Service *)||within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerClasses() {
    }
    @Around("controllerClasses()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // Log the method name and arguments
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        System.out.println("Entering method: " + methodName + " with arguments: " + Arrays.toString(args));

        // Proceed with the method execution and get the return value
        Object result = joinPoint.proceed();

        // Log the return value
        System.out.println("Method " + methodName + " returned: " + result);

        // Return the result to the caller
        return result;
    }
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceClasses() {
        logger.info("Entering serviceClasses...");
    }
    @Pointcut("within(@org.springframework.context.annotation.Configuration *)")
    public void configurationClasses() {
    }

    //
    @Before("controllerClasses()")
    public void logBeforeConfigMethods(JoinPoint joinPoint) {
        logger.info("Entering method configuration: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerClasses()", returning = "result")
    public void logAfterConfigMethods(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method configuration: {} with result: {}", joinPoint.getSignature(), result);
    }

}
