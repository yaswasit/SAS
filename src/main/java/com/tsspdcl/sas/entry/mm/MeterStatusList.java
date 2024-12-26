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
public class MeterStatusList {
	@Id
	@Column(nullable = false)
	private String MCRCODE;
	private String MCRNAME;
	
}
