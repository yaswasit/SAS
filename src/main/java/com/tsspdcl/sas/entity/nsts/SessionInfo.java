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
public class SessionInfo {
	@Id
	@Column(nullable = false)
	private String circlecd;
	private String erocd;
	private String seccd;
	private String secname;
	private String sasuserid;
	private String sasdesg;
	private String sasoffadd;
	private String sasuser;
	private String EBSSeccd;
	private String sapseccd;
	private String sasusertype;
	private String circlename;
	private String mtrsealcond;
	private String oldmtrchgflag;
	private String rollingstock;
	/*
	@Override
	public String toString() {
		return "NewRegistrations [nrregno=" + nrregno + "]";
	}*/
}
