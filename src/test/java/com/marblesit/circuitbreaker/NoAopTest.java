package com.marblesit.circuitbreaker;

import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationConfig.xml"})
public class NoAopTest {
	
	@Autowired
	private TestService service;

	@Test
	public void testNoHystrix() {
		StreamSupport.stream(service.getMembers().spliterator(), false).forEach(member -> System.out.println(member.getName()));
	}
}
