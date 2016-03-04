package com.marblesit.circuitbreaker.repository;

import org.springframework.data.repository.CrudRepository;

import com.marblesit.circuitbreaker.entity.Member;

public interface MemberRepository extends CrudRepository<Member, Long> {

}
