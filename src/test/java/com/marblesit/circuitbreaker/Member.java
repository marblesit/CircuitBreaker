package com.marblesit.circuitbreaker;

public class Member {
	private Long id;
	private String first;
	private String middle;
	private String last;

	public String getName() {
		return first + " " + middle + " " + last + "(" + id + ")";
	}
}
