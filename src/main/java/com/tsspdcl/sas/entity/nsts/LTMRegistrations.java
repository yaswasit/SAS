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
public class LTMRegistrations {
	@Id
	@Column(nullable = false)
	private String regno;
	private String apartment_name;
	private String regdate;
	private int reg_source;
	private double load;
	private String non_domestic_cons;
	private String status;
	private double status_id;
	private int nof_days_pending;
	private String sec_cd;
	private String relese_date;
	private String estimation_status;
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
