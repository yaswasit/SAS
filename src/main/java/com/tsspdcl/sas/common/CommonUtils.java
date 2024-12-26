package com.tsspdcl.sas.common;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;

public class CommonUtils {
	
	public String dashboardTitle(int id) {
		//System.out.println("********************************");
        HashMap<Integer, String> hash_map = new HashMap<Integer, String>();
        hash_map.put(1, "dashboard.nsts.title"); 
        hash_map.put(2, "dashboard.cc.title");  
        hash_map.put(3, "dashboard.mm.title");  
        hash_map.put(4, "dashboard.cgrf.title");  
        hash_map.put(5, "dashboard.default.title");  
          
        //System.out.println("HashMap.........."+hash_map);
        //System.out.println("The Value is: " + hash_map.get(id)); 
        return hash_map.get(id);
        
	}
	
	public String columnTitles(String key) {
		HashMap<String, String> thead_map = new HashMap<String, String>();
		// Registration
		thead_map.put("nrregno", "Regn. No.");
		thead_map.put("consname", "Consumer Name");
		thead_map.put("nrregdate", "Regn. Date");
		thead_map.put("secname", "Section Name");
		thead_map.put("group", "Social Group");
		thead_map.put("contload", "Contract Load");
		thead_map.put("catdesc", "Category");
		thead_map.put("subcat", "Subcategory");
		thead_map.put("tobereleasedt", "To Be Released Date");
		thead_map.put("phase", "Phase");
		thead_map.put("meterno", "Meter No.");
		thead_map.put("mobileno", "Mobile No.");
		thead_map.put("cscremarks", "CSC Remarks");
		thead_map.put("aeremarks", "AE Remarks");
		thead_map.put("aderemarks", "ADE Remarks");
		thead_map.put("aeremarksdt", "AE Remarks Date");
		thead_map.put("aderemarksdt", "ADE Remarks Date");
		thead_map.put("status", "Status");
		
		// estimation
		thead_map.put("estst", "Estimate Status");
		thead_map.put("sapestno", "SAP Estimate No.");
		thead_map.put("estamt", "Estimate Amount");
		thead_map.put("noofdayspending", "No.of Days Pending");
		thead_map.put("estsandt", "Estimate Sanction Date");
		thead_map.put("workcompdt", "Work Completion Date");
		thead_map.put("estcreatedt", "Estimate Created Date");
		
		// documents
		thead_map.put("applform", "Application Form");
		thead_map.put("idproof", "ID Proof");
		thead_map.put("castcert", "Cast Certificate");
		thead_map.put("saledeed", "Sale Deed");
		thead_map.put("panchayatletter", "Panchayat Letter");
		thead_map.put("photo", "Photo");
		thead_map.put("wiringcert", "Wiring Certificate");
		thead_map.put("occcert", "Occupency Certificate");
		thead_map.put("nocpcb", "No Objection from PCB");
		thead_map.put("novlbody", "Nov LBody");
		thead_map.put("pattadoc", "Patta Document");
		thead_map.put("mhtax", "Muncipal House Tax");
		thead_map.put("ssicert", "SSI Certificate");
		thead_map.put("cert80g", "Certificate 80G");
		thead_map.put("mrocert", "MRO Certificate");
		thead_map.put("ceigcert", "CEIG Certificate");
		thead_map.put("formI2", "Form-I 2");
		thead_map.put("locphoto", "Location Photo");
		thead_map.put("sasphoto", "SAS Photo");
		thead_map.put("saststrpt", "SAS Test Report");
		thead_map.put("sasagrmt", "SAS Agreement");
		
		// LTM
		thead_map.put("nofcons", "No.of Connections");
		thead_map.put("reqdomesticload", "Req. Domestic Load");
		thead_map.put("reqcommload", "Req. Commercial Load");
		thead_map.put("domesticloadadded", "Domestic Load Added");
		thead_map.put("commloadadded", "Commercial Load Added");
		thead_map.put("buildername", "Builder Name");
		thead_map.put("areaname", "Area Name");
		thead_map.put("natureofwork", "Nature Of Work");
		thead_map.put("nofflats", "No of Flats");
		
		thead_map.put("slno", "#");
		thead_map.put("flatno", "Flat No.");
		thead_map.put("name", "Name");
		thead_map.put("conload", "Contracted Load in watts");
		
		return thead_map.get(key);
	}
	
	public String buttonColors(String key) {
		HashMap<String, String> btn_map = new HashMap<String, String>();
		
		/*btn_map.put("photo", "btn-primary");
		btn_map.put("idproof", "btn-danger");
		btn_map.put("saledeed", "btn-success");
		btn_map.put("locphoto", "btn-warning");
		*/
		btn_map.put("photo", "btn-success");
		btn_map.put("idproof", "btn-success");
		btn_map.put("saledeed", "btn-success");
		btn_map.put("locphoto", "btn-success");
		
		return btn_map.get(key);
	}
	
	public String textColors(String key) {
		HashMap<String, String> text_map = new HashMap<String, String>();
		
		text_map.put("photo", "text-primary");
		text_map.put("idproof", "text-danger");
		text_map.put("saledeed", "text-success");
		text_map.put("locphoto", "text-warning");
		
		return text_map.get(key);
	}
	
	public String iconStyles(String key) {
		HashMap<String, String> icon_map = new HashMap<String, String>();
		
		icon_map.put("photo", "bi-person-circle");
		icon_map.put("idproof", "bi-credit-card-2-front");
		icon_map.put("saledeed", "bi-receipt");
		icon_map.put("locphoto", "bi-geo-alt-fill");
		icon_map.put("applform", "bi-receipt");
		icon_map.put("sasphoto", "bi-image");
		icon_map.put("saststrpt", "bi-file-richtext");
		
		return icon_map.get(key);
	}
	
	public String removeNull(String str)
	{
		if(str==null)
			return "";
		else
			return str;
	}
	
	public String removeNull(Object str)
	{
		if(str==null)
			return "".toString();
		else
			return str.toString();
	}
	
	/*public byte[] readBlob(oracle.sql.BLOB blob) throws SQLException {

	    if (blob != null) {
	        byte[] buffer = new byte[(int) blob.length()];
	        int bufsz = blob.getBufferSize();
	        InputStream is = blob.getBinaryStream();
	        int len = -1, off = 0;
	        try {
	            while ((len = is.read(buffer, off, bufsz)) != -1) {
	                off += len;
	            }
	        } catch (IOException ioe) {
	            //logger.debug("IOException when reading blob", ioe);
	        	ioe.printStackTrace();
	        }
	        return buffer;
	    } else {
	        return null;
	    }
	}*/
	
	public String readBlob(int blobLen, Blob blob, String realPath, String nrregno, String doctype) throws SQLException, Exception {
		//Blob blob = docsrs.getBlob(5);
		String fileExtn = null;
		//System.out.println("blob length.."+blobLen);
		byte[] bytes = blob.getBytes(1, blobLen);
		//byte[] bytes=new byte[blobLen];
		InputStream inputStream = new ByteArrayInputStream(bytes);
		
		inputStream = blob.getBinaryStream();
		
		if(inputStream.read(bytes)>0) {
			
			if(doctype.equals("PHOTO") || doctype.equals("ID_PROOF") || doctype.equals("LOC_PHOTO") || doctype.equals("SAS_PHOTO"))
				fileExtn = ".jpg";
			else
				fileExtn = ".pdf";
			
			FileOutputStream os = new FileOutputStream(realPath+doctype+"_"+nrregno+fileExtn);
			//FileOutputStream os = new FileOutputStream("/SAS/downloads/"+doctype+"_"+nrregno+".pdf");
			os.write(bytes);
			os.close();
		}
		
		//return realPath+doctype+"_"+nrregno+".pdf";
		return doctype+"_"+nrregno+fileExtn;
	}	
}
