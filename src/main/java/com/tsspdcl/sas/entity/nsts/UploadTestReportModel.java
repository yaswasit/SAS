package com.tsspdcl.sas.entity.nsts;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class UploadTestReportModel {
	private String reg_Id;
	private int otp;
	private MultipartFile meter_img;
	private MultipartFile test_report;
}
