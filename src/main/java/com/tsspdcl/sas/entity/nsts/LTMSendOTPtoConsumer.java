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
public class LTMSendOTPtoConsumer {
	@Id
	@Column(nullable = false)
	private String registration_number;
	private String registration_on;
	private String apartment_name;
	private String no_of_flats;
	private String no_of_conn_reqd;
	private String mobile;
	private String doc_submit;
}
