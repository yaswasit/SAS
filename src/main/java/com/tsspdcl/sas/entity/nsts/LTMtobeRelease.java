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
public class LTMtobeRelease {
	@Id
	@Column(nullable = false)
	private String regno;
	private String flatno;
	private String cat;
	private String load;
	private String meterno;
	private String phase;
	private String capacity;
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
