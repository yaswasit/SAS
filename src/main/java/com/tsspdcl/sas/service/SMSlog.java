package com.tsspdcl.sas.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

//import com.tsspdcl.database.TSSPDCLDBConnectionPool;
import com.tsspdcl.sas.dbcon.DBConnections;

public class SMSlog {

	
	public static void InsertSMSLog(String mobileno, String msg, String user, String ack)
	{
		DBConnections db = new DBConnections();
		
		try(Connection con = db.getConnection();){
			
						
			String sql="insert into route_sms_log(mobileno,message,createdby,ack) values (?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString ( 1 , mobileno );
			pstmt.setString ( 2 , msg );
			pstmt.setString ( 3 , user);
			pstmt.setString ( 4 , ack );
			int i=pstmt.executeUpdate();

		} catch(Exception ex) {
				System.out.println("\n Error In SMSlog Class : "+ex.getMessage()+" \n");
		}
	}
	
}
