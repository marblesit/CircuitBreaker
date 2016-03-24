package com.marblesit.circuitbreaker;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

	public static final int TEST_TIMEOUT = 2000;

	@Override
	@CircuitBreaker("Name")
	public String get(String str) {
		return str;
	}

	@Override
	@CircuitBreaker
	public String throwException() throws MyException {
		throw new MyException();
	}

	@Override
	@CircuitBreaker(timeoutMilliseconds=TEST_TIMEOUT)
	public String withTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {}
		return str;
	}

	@Override
	@CircuitBreaker(timeoutMilliseconds=0)
	public String withZeroTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {}
		return str;
	}


	@Override
	@CircuitBreaker(useThreads=true)
	public int getThreadId() {
		return Thread.currentThread().hashCode();
	}

	@Override
	@CircuitBreaker(useThreads=false)
	public int getNonThreadedThreadThreadId() {
		return Thread.currentThread().hashCode();
	}

}
