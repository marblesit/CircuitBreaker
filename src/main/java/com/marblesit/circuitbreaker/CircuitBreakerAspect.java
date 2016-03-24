package com.marblesit.circuitbreaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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
    public Object circuitBreakerAround(final ProceedingJoinPoint joinPoint) throws Throwable {
		CircuitBreaker cb = getAnnotation(joinPoint);
		try {
			return getHystrixCommand(joinPoint, cb).execute();
		} catch (HystrixRuntimeException e) {
			return handleException(e, joinPoint, cb);
		}
    }

	private Object handleException(HystrixRuntimeException e, ProceedingJoinPoint joinPoint, CircuitBreaker cb) throws Throwable {
		if (cb.fallback().length() > 0) {
			return executeFallback(e, joinPoint, cb);
		}
		if (e.getCause() instanceof TimeoutException) {
			throw new CircuitBreakerTimeoutException();
		}
		if (e.getCause() != null) {
			throw e.getCause();
		}
		throw e;
	}

	private Object executeFallback(HystrixRuntimeException e, ProceedingJoinPoint joinPoint, CircuitBreaker cb) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = getMethod(joinPoint);
		Class<?> clazz = method.getDeclaringClass();
		String name = cb.fallback();
		Class<?> params[] = method.getParameterTypes();
		Object[] args = joinPoint.getArgs();

		Method m = ReflectionUtils.findMethod(clazz, name, params);
		if (m == null) {
			Class<?>[] temp = params;
			params = new Class<?>[params.length + 1];
			System.arraycopy(temp, 0, params, 0, temp.length);
			params[params.length - 1] = Throwable.class;

			Object[] tempArgs = args;
			args = new Object[tempArgs.length + 1];
			System.arraycopy(tempArgs, 0, args, 0, tempArgs.length);
			args[args.length - 1] = e.getCause() == null ? e : e.getCause();

			m = ReflectionUtils.findMethod(clazz, name, params);
		}
		if (m == null) {
			throw new CircuitBreakerFallbackMethodMissing(clazz, name, params);
		}
		return m.invoke(joinPoint.getTarget(), args);
	}

	private HystrixCommand<?> getHystrixCommand(final ProceedingJoinPoint joinPoint, CircuitBreaker cb) throws NoSuchMethodException, SecurityException {

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

	private CircuitBreaker getAnnotation(final ProceedingJoinPoint joinPoint) {
		Method method = getMethod(joinPoint);
		return method.getAnnotation(CircuitBreaker.class);
	}

	private Method getMethod(final ProceedingJoinPoint joinPoint) {
		try {
			final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
			Method method = methodSignature.getMethod();
			if (method.getDeclaringClass().isInterface()) {
				final String methodName = joinPoint.getSignature().getName();
				method = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
			}
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private HystrixCommand.Setter getCommandSetter(ProceedingJoinPoint joinPoint, CircuitBreaker cb) {
		String name = getHystrixGroupName(joinPoint, cb);

		ExecutionIsolationStrategy strategy =
				cb.useThreads() ?
					ExecutionIsolationStrategy.THREAD :
					ExecutionIsolationStrategy.SEMAPHORE;

		Setter properties =
				HystrixCommandProperties.Setter().
					withExecutionTimeoutEnabled(cb.timeoutMilliseconds() == 0 ? false : true).
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
