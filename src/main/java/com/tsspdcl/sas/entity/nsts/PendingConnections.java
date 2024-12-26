package com.tsspdcl.sas.entity.nsts;

import java.util.Date;

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
public class PendingConnections {
	@Id
	@Column(nullable = false)
	private String nrregno;
	private String nrregdate;
	private String consname;
	private String catdesc;
	private String prno;
	private String secname;
	private String tobereldt;
	private String reason;
}



