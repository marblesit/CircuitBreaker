package com.marblesit.circuitbreaker.service.nocircuitbreaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marblesit.circuitbreaker.entity.Member;
import com.marblesit.circuitbreaker.repository.MemberRepository;
import com.marblesit.circuitbreaker.service.TestService;

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
