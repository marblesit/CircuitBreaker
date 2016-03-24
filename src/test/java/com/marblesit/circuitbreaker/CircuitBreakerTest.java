package com.marblesit.circuitbreaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationConfig.xml"})
public class CircuitBreakerTest {

	private static final String TEST_STR = "TEST_STR";
	@Autowired
	private Service service;

	@Test
	public void testHystrix() {
		assertEquals(TEST_STR, service.get(TEST_STR));
	}

	@Test
	public void testTimeoutHystrix() {
		long start = System.currentTimeMillis();
		try {
			service.withTimeout(TEST_STR);
		} catch (CircuitBreakerTimeoutException e) {}

		long end = System.currentTimeMillis();
		assertTrue(end - start > ServiceImpl.TEST_TIMEOUT);
		assertTrue(end - start < ServiceImpl.TEST_TIMEOUT * 1.1f);
	}

	@Test
	public void testZeroTimeoutHystrix() {
		long start = System.currentTimeMillis();
		service.withZeroTimeout(TEST_STR);

		long end = System.currentTimeMillis();
		assertTrue(end - start > ServiceImpl.TEST_TIMEOUT * 2);
		assertTrue(end - start < ServiceImpl.TEST_TIMEOUT * 2.1f);
	}


	@Test(expected=MyException.class)
	public void testException() throws MyException {
		service.throwException();
	}

	@Test
	public void testThreaded() throws MyException {
		int threadId = Thread.currentThread().hashCode();
		int serviceThreadId = service.getThreadId();

		assertNotEquals(threadId, serviceThreadId);
	}

	@Test
	public void testNonThreaded() throws MyException {
		int threadId = Thread.currentThread().hashCode();
		int serviceThreadId = service.getNonThreadedThreadThreadId();

		assertEquals(threadId, serviceThreadId);
	}
}
