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
public class AGLRegistrationDetails {
	@Id
	@Column(nullable = false)
	private String regno;
	private String regdate;
	private String applicant_name;
	private String section;
	private String seccd;
	private String category;
	private String subcat;
	private String connection_type;
	private String connected_load;
	private String area_code;
	private String area_name;
	private String group;
	private String amount_tobe_paid;
	private String amount_paid;
	
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
