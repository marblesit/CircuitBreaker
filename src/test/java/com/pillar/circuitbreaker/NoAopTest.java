package com.pillar.circuitbreaker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class NoAopTest {
	
	private TestService service;

	private static final int MAX_SIZE = 40;
	
	class Result {
		public int i;
		public long fib;
		public Result(int i, long fib) {
			super();
			this.i = i;
			this.fib = fib;
		}
		public String toString() {
			return i + " -> " + fib;
		}
	}
	
	@Before
	public void init() {
		service = new TestServiceImpl();
	}

	@Test
	public void testNoHystrix() {
		List<Result> fib =
				Stream.iterate(0, i -> i + 1).limit(MAX_SIZE)
					.peek(i -> System.out.println(i))
					.map(i -> new Result(i, service.fibinacciOf(i)))
					.collect(Collectors.toList());
		
		fib.stream().forEach(result -> System.out.println(result));
	}
}
