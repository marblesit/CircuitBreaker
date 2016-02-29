package com.marblesit.circuitbreaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private MemberRepository repo;

	@Override
	public Iterable<Member> getMembers() {
		return repo.findAll();
	}
}
