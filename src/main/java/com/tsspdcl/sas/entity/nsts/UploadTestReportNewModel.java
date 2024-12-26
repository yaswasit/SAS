package com.tsspdcl.sas.entity.nsts;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class UploadTestReportNewModel {
	private String regid;
	private String catid;
	private String scheme;
	private String SAP_WBSNO;
	private MultipartFile report1;
	private MultipartFile agrmnt;
}
