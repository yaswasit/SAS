package com.tsspdcl.sas.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;



public class FileStatements {
	String FILE_DIRECTORY = "", REGNO = "";
	public FileStatements(String FILE_DIRECTORY, String REGNO){
		this.FILE_DIRECTORY = FILE_DIRECTORY;
		this.REGNO = REGNO;
	}

	public FileInputStream FileRead (MultipartFile multipart_file) throws IOException {
        String newFileName = REGNO + "_"+ multipart_file.getOriginalFilename();
        File File = new File(FILE_DIRECTORY + newFileName);
        File.createNewFile();
        FileOutputStream FOS = new FileOutputStream(File);
        FOS.write(multipart_file.getBytes());
        FOS.close();
        FileInputStream FIS = new FileInputStream(FILE_DIRECTORY + newFileName);
        return FIS;
}
}
