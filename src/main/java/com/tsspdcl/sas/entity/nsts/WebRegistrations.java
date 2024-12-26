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
public class WebRegistrations {
	@Id
	@Column(nullable = false)
	private String nrregno;
	private String nrregdate;
	private String consname;
	private String chqddno;
	private String catdesc;
	private double devchgs;
	private double secdep;
	private double contload;
	private String secname;
}
