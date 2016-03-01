package com.marblesit.circuitbreaker.service;

import java.util.List;

import com.marblesit.circuitbreaker.entity.Member;

public interface TestService {
	Iterable<Member> getMembers();
}