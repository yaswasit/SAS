package com.tsspdcl.sas.entity.nsts;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class AGLRegistrations {
	@Id
	@Column(nullable = false)
	private String regno;
	private String applicant_name;
	private String regdate;
	private String section;
	private String seccd;
	private String category;
	private String meterno;
	private String seniority_order;
	private String scheme;
	private String seniority_exempt;
	
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
