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
public class NewRegistrations {
	@Id
	@Column(nullable = false)
	private String nrregno;
	private String nrregdate;
	private int cat;
	private double load;
	private String consname;
	private String address;
	private double amount;
	private int status;
	private String meterno;
	private String remarks;
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
