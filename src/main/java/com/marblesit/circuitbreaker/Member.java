package com.marblesit.circuitbreaker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="members")
public class Member {
	
	@Id
	@Column(name="id")
	private Long id;

	@Column(name="first")
	private String first;

	@Column(name="middle")
	private String middle;

	@Column(name="last")
	private String last;

	public String getName() {
		return first + " " + middle + " " + last + "(" + id + ")";
	}
}
