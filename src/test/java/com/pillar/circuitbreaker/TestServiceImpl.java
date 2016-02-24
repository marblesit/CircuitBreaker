package com.pillar.circuitbreaker;

public class TestServiceImpl implements TestService {
	public long fibinacciOf(int i) {
		if (i == 0) {
			return 0;
		}
		if (i == 1) {
			return 1;
		}
		return fibinacciOf(i - 1) + fibinacciOf(i - 2);
	}
}
