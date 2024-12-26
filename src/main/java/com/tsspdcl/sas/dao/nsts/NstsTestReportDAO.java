package com.tsspdcl.sas.dao.nsts;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

//import org.omg.CORBA.portable.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//import com.tsspdcl.database.TSSPDCLDBConnectionPool;
import com.tsspdcl.sas.common.CommonUtils;
import com.tsspdcl.sas.common.QueryBuilder;
import com.tsspdcl.sas.dbcon.DBConnections;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.entity.nsts.AddConnections;
import com.tsspdcl.sas.entity.nsts.DocumentsVerification;
import com.tsspdcl.sas.entity.nsts.LTMSendOTPtoConsumer;
import com.tsspdcl.sas.entity.nsts.LTMmeterNoList;
import com.tsspdcl.sas.entity.nsts.LTMtestReport;
import com.tsspdcl.sas.entity.nsts.NewRegistrations;
import com.tsspdcl.sas.entity.nsts.PendingConnections;
import com.tsspdcl.sas.entity.nsts.WebRegistrations;
import com.tsspdcl.sas.service.SmsMsg;

//@Configuration
//@PropertySource("classpath:application.properties")	
public class NstsTestReportDAO {
	
	@Autowired
	private Environment env;
	
	StringBuilder result = new StringBuilder();
	public NstsTestReportDAO() {
		//System.out.println("NSTS DAO STARTED....");
	}
	
	DBConnections db = new DBConnections();
	
	
	private String offtype = "";
	
	
	public List<LTMtestReport> getTestReportInfo(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <LTMtestReport> docList = new ArrayList<LTMtestReport>();
		
		try (Connection con = db.getConnection();
			
			PreparedStatement preparedStatement = con.prepareStatement(QueryBuilder.ltmtestreport+offtype+"='"+user.getSasseccd()+"'");) {
			ResultSet rslist = preparedStatement.executeQuery();
	            
	        while (rslist.next()) {System.out.println("rs==>"+rslist.getString("REGISTRATION_NUMBER"));
	        		//List<String> meterList = getMeters(rslist.getString("REGISTRATION_NUMBER"));
	        		List<String> meterList = new ArrayList<String>();
	        		//System.out.println("meterList>>>"+meterList);
		        	PreparedStatement preparedStatement1 = con.prepareStatement(QueryBuilder.ltmMeterNos+" and APARTMENT_ID=?");
		        	preparedStatement1.setString(1, rslist.getString("REGISTRATION_NUMBER"));
		            System.out.println("Regno>>>"+rslist.getString("REGISTRATION_NUMBER")+"ltmMeterNos>>>"+QueryBuilder.ltmMeterNos+" and APARTMENT_ID=?");           
		            ResultSet rs1 = preparedStatement1.executeQuery();
		           
		            while (rs1.next()) {
		            	meterList.add(rs1.getString("METER_SLNO"));   	    		
		            }
	        		
	        		docList.add(new LTMtestReport(rslist.getString("REGISTRATION_NUMBER"),rslist.getString("REGISTRATION_ON"),rslist.getString("APARTMENT_NAME"),rslist.getString("NO_OF_FLATS"),rslist.getString("NO_OF_CONN_REQD"),rslist.getString("MOBILE"),rslist.getString("BUILDER_NAME"), meterList));
	        	}
	        System.out.println("docList::"+docList);
	        
	        
	        rslist.close();
	        preparedStatement.close();
	        con.close();
		} catch (SQLException e) {
			//printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	
	
	
public HashMap getLTMTestReport(String regid, String meterno, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		String erouser="";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.getTestReportDetails+" where registration_number=?");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        	        
	        PreparedStatement ps = connection.prepareStatement(QueryBuilder.getTestReportMeterDetails+" where APARTMENT_ID=? and METER_SLNO=?");
	        ps.setString(1, regid);
	        ps.setString(2, meterno);
	        ResultSet rs1 = ps.executeQuery();
	        
        	if(rs.next()) {
        		regData.put("regno", rs.getString(1));
        		regData.put("registration_on", rs.getString(2));
        		regData.put("builder_name", rs.getString(3));
        		regData.put("cat_id", rs.getString(4));
        		regData.put("apartment_name", rs.getString(5));
        		regData.put("adderss1", rs.getString(6));
        		regData.put("address2", rs.getString(7));
        		regData.put("area_name", rs.getString(8));
        	}
        	if(rs1.next()){
        		regData.put("meter_slno", rs1.getString(1));
        		regData.put("meter_make", rs1.getString(2));
        		regData.put("meter_mf", rs1.getString(3));
        		regData.put("capacity", rs1.getString(4));
        		regData.put("initial_reading", rs1.getString(5));
        		regData.put("adderss3", rs1.getString(6));
        		regData.put("address4", rs1.getString(7));
        		regData.put("area_name2", rs1.getString(8));
        	}
	        
        	//result.append(getLtmOtpValidationHTMLTable("Registration Details",regData,catList));
        	
	        rs.close();
	        preparedStatement.close();
	        connection.close();
		} catch (SQLException e) {
			//printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return regData;
	}

}
