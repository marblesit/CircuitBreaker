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

### Options

Create a Circuit Breaker with name "getValue".  The timeout will be 5,000 ms and it will use thread pools.

```java
	@CircuitBreaker("getValue")
```

Create a Circuit Breaker with name "getValue".  The timeout will be 10 sec and it will use semaphore to control access, not thread pools.

```java
	@CircuitBreaker(name="getValue", timeoutMilliseconds=10000, useThreads=false)
```

### Spring config

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

	<aop:aspectj-autoproxy/>
	<context:component-scan base-package="com.marblesit.circuitbreaker"/>

</beans>
```