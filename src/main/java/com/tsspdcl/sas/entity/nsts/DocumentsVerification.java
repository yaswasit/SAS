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
public class DocumentsVerification {
	@Id
	@Column(nullable = false)
	private String nrregno;
	private String nrregdate;
	private String consname;
	private String catdesc;
	private String secname;
	private String tobereldate;
	private String meterno;
	private String mobileno;
}
