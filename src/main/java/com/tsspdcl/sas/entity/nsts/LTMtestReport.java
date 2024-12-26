package com.tsspdcl.sas.entity.nsts;

import java.util.List;

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
public class LTMtestReport {
	@Id
	@Column(nullable = false)
	private String registration_number;
	private String registration_on;
	private String apartment_name;
	private String no_of_flats;
	private String no_of_conn_reqd;
	private String mobile;
	private String builder_name;
	@Column
	@ElementCollection(targetClass = String.class)
	private List<String> meters;

	
	 @Override public String toString() { return
	  "LTMtestReport [registration_number=" + registration_number + ", registration_on=" + registration_on + ", apartment_name=" + apartment_name
	  + ", no_of_flats=" + no_of_flats + ", no_of_conn_reqd=" + no_of_conn_reqd + ", mobile=" + mobile + ", builder_name=" + builder_name + ", meters=" +
	  meters + "]"; }
	 

}
