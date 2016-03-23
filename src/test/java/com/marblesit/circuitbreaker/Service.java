package com.marblesit.circuitbreaker;

public interface Service {
	public String get(String str);
	public String throwException() throws MyException;
	public String withTimeout(String str);
	public int getThreadId();
	public int getNonThreadedThreadThreadId();
}
