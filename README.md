# CircuitBreaker
An Spring AOP annotation CircuitBreaker using the NetFlix Hystrix circuit breaker (https://github.com/Netflix/Hystrix)

Based on http://blog.mirkosertic.de/architecturedesign/springhystrix

## Use

```java
	@Override
	@CircuitBreaker
	public String methodName() throws MyException {
	}
```

This will use a Hytrix command to execute the method `methodName`.  The name used for the Hystrix command will be the signature of the method.

You must also include Spring's AOP configuration [see below](#spring-config)

### Options

There are five options
* name - Name of the command used to collect statistics and for display when used with 
the [Hystrix dashboard](https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard)
* value - Name of the command if no name is specified
* timeoutMilliseconds - Execution timeout in milliseconds.  Default is 1000 ms. 
* useThreads - If true then a thread pool is used to handle the command execution.  Otherwise 
the command runs in the current thread and semaphores are used to control the number of concurrent executions.
* fallback - Method name for the fallback method (i.e. method executed if an exception is thrown. If a 
fallback is not specified then the exception is thrown for the calling application to handle.


Create a Circuit Breaker with name "getValue".  The timeout will be 5,000 ms and it will use thread pools.

```java
	@CircuitBreaker("getValue")
```

Create a Circuit Breaker with name "getValue".  The timeout will be 10 sec and it will use semaphore to control access, not thread pools.

```java
	@CircuitBreaker(name="getValue", timeoutMilliseconds=10000, useThreads=false)
```

### Fallback
The fallback method should have the same signature as the method that was annotated.  For instance

```java
	@CircuitBreaker(fallback="fallback")
	public String exceptionWithFallback(String s) {
		throw new MyRuntimeException();
	}

	public String fallback(String s) {
		return s;
	}
```

The fallback can also add a ```Throwable``` argument to the end of the parameter list to
get the exception that caused the fallback to be executed.

```java
	@CircuitBreaker(fallback="fallback")
	public String exceptionWithFallback(String s) {
		throw new MyRuntimeException();
	}

	public String fallback(String s, Throwable t) {
		t.printStackTrace();
		return s;
	}
```

### Spring config
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

	<aop:aspectj-autoproxy/>
	<context:component-scan base-package="com.marblesit.circuitbreaker"/>

</beans>
```
