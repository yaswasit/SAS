package com.tsspdcl.sas.entry.ccts;

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
public class QualityCheckList {
	@Id
	@Column(nullable = false)
	private String complaint_number;
	private String registration_on;
	private String serviceno;
	private String name;
	private String complaint_nature;
	private String tobe_rectified;
	private String catid;
	private String mobileno;
}
