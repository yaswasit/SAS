package com.tsspdcl.sas.entry.mm;

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
public class MeterAllotmentServicesList {
	@Id
	@Column(nullable = false)
	private String CTSCNO;
	private String CTUKSCNO;
	private String CTBILSTAT;
	private String CTNAME;
	private String CTPHONE;
	private String CTCAT;
	private String CTSUBCAT;
	private String CTCONLD;
	private String CTCTRLD;
	private String CTSECCD;
	private String CTUKSECCD;
	private String CTEROCD;
	private String CTCIR_CODE;
	private String CTBLDT;
	private String CTMTREXPDT;
	private String CTMTRPHASE;
	private String CTAREACD;
	private String CTCLUBREL;
		
}
