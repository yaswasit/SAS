package com.tsspdcl.sas.entry.ccts;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class UploadPowerSupplyModel {
	private String status;
	private String reg_Id;
	private String remarks;
	private String doc_reason;
	private String com_nature;
	private String statuscode;
	private MultipartFile qps_doc;
}
