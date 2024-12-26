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
public class StuckupDataList {
	@Id
	@Column(nullable = false)
	private String meterno;
	private String seal1;
	private String seal2;
	private String phlist;
}
