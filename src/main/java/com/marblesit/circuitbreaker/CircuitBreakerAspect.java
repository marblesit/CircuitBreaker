package com.marblesit.circuitbreaker;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixCommandProperties.Setter;
import com.netflix.hystrix.exception.HystrixRuntimeException;

@Aspect
@Component
public class CircuitBreakerAspect {

	@Around("@annotation(com.marblesit.circuitbreaker.CircuitBreaker)")
    public Object circuitBreakerAround(final ProceedingJoinPoint aJoinPoint) throws Throwable {
		try {
			return getHystrixCommand(aJoinPoint).execute();
		} catch (HystrixRuntimeException e) {
			if (e.getCause() instanceof TimeoutException) {
				throw new CircuitBreakerTimeoutException();
			}
			if (e.getCause() != null) {
				throw e.getCause();
			}
			throw e;
		}
    }

	private HystrixCommand<?> getHystrixCommand(final ProceedingJoinPoint joinPoint) throws NoSuchMethodException, SecurityException {

		CircuitBreaker cb = getAnnotation(joinPoint);

		@SuppressWarnings("rawtypes")
		HystrixCommand<?> theCommand = new HystrixCommand(getCommandSetter(joinPoint, cb)) {
            @Override
            protected Object run() throws Exception {
                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                	throw e;
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }
        };
		return theCommand;
	}

	private CircuitBreaker getAnnotation(final ProceedingJoinPoint joinPoint) throws NoSuchMethodException, SecurityException {
		final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			final String methodName = joinPoint.getSignature().getName();
			method = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
		}
		return method.getAnnotation(CircuitBreaker.class);
	}

	private HystrixCommand.Setter getCommandSetter(ProceedingJoinPoint joinPoint, CircuitBreaker cb) {
		String name = getHystrixGroupName(joinPoint, cb);

		ExecutionIsolationStrategy strategy =
				cb.useThreads() ?
					ExecutionIsolationStrategy.THREAD :
					ExecutionIsolationStrategy.SEMAPHORE;

		Setter properties =
				HystrixCommandProperties.Setter().
					withExecutionTimeoutEnabled(true).
					withExecutionTimeoutInMilliseconds(cb.timeoutMilliseconds()).
					withExecutionIsolationStrategy(strategy);

		return HystrixCommand.Setter.
					withGroupKey(HystrixCommandGroupKey.Factory.asKey(name)).
					andCommandKey(HystrixCommandKey.Factory.asKey(name)).
					andCommandPropertiesDefaults(properties);
	}

	private String getHystrixGroupName(final ProceedingJoinPoint joinPoint, CircuitBreaker cb) {
		String name = cb.name().length() == 0 ? cb.value() : cb.name();
        return name.length() == 0 ? joinPoint.getSignature().toShortString() : name;
	}
}
