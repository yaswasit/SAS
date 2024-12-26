package com.tsspdcl.sas.dto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.tsspdcl.sas.service.PDFGenerator;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tsspdcl.sas"})
public class SpringBoot2PdfGenerationApplication {
	
	/*public static void main(String[] args) {
		
		ApplicationContext ac = SpringApplication.run(SpringBoot2PdfGenerationApplication.class, args);
		
		PDFGenerator pDFGenerator = ac.getBean("pdfGenerator",PDFGenerator.class);
		
		pDFGenerator.generatePdfReport();
	}*/
	
}