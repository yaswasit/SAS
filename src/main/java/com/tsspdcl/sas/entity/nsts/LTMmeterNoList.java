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
public class LTMmeterNoList {
	@Id
	@Column(nullable = false)
	private String meter_no;
}
