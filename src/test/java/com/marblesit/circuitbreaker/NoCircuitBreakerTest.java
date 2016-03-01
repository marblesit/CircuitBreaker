package com.marblesit.circuitbreaker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.marblesit.circuitbreaker.entity.Member;
import com.marblesit.circuitbreaker.service.TestService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationConfig.xml"})
public class NoCircuitBreakerTest {
	
	@Autowired
	private TestService service;

	@Test
	public void testNoHystrix() {
		List<Member> members = StreamSupport.stream(service.getMembers().spliterator(), false).collect(Collectors.toList());
		Assert.assertEquals(1, members.size());
		Assert.assertEquals("Cory L Wandling", members.get(0).getName());
		Assert.assertEquals(1L, members.get(0).getId().longValue());
	}
}
