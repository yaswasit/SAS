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
public class LTMsmsSendToLineman {
	@Id
	@Column(nullable = false)
	private String reg_Id;
	private String actionType;
	private String linemenlist;
	private String lmphone;
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
