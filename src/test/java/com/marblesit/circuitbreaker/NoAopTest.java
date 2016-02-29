package com.marblesit.circuitbreaker;

import org.junit.Before;
import org.junit.Test;

public class NoAopTest {
	
	private TestService service;

	@Before
	public void init() {
		service = new TestServiceImpl();
	}

	@Test
	public void testNoHystrix() {
		service.getMembers().stream().forEach(member -> System.out.println(member.getName()));
	}
}
