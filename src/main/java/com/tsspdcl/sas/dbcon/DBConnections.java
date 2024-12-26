package com.tsspdcl.sas.dbcon;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.io.InputStream;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.SQLException;
	import java.util.Calendar;
	import java.util.Properties;
	import java.util.Vector;

	import com.tsspdcl.sas.service.AESEncryptionDecryption;

	@Component
	public class DBConnections {
		
		private static final String secretKey = "secrete";
		private final Properties databaseProperties = new Properties();
		private Connection con = null;
		
		AESEncryptionDecryption aesEncryptionDecryption = new AESEncryptionDecryption();
		
		public DBConnections() {
			
		    try {
		    	InputStream inputStream = 
		    		getClass().getClassLoader().getResourceAsStream("database.properties");
		    	
		    	databaseProperties.load(inputStream);
		    	
		    	//System.out.println("url : "+databaseProperties.getProperty("cscdatabase.url"));
		    	
		    } catch (IOException ioException) {
		    	System.err.println(ioException.getMessage());
		    }
		}
		
		/*
		public static void main(String[] args) throws Exception {
			CSCDB obj = new CSCDB();
			System.out.println(obj.openConnection());
		}*/
		
		@SuppressWarnings("deprecation")
		private Connection openConnection() {
			if (con != null)
				closeConnection();
			try {
				Class.forName(databaseProperties.getProperty
							("cscdatabase.driver")).newInstance();
				
				String url = databaseProperties.getProperty("cscdatabase.url");
	        	String username = databaseProperties.getProperty("cscdatabase.user");
	        	String password = aesEncryptionDecryption.decrypt(databaseProperties.getProperty("cscdatabase.password"), secretKey);
	        	
	        	//System.out.println(url);
	        	//System.out.println(username);
	        	//System.out.println(password);
	        	
				con = DriverManager.getConnection(url, username, password);
			} catch (ClassNotFoundException e) {
	        	//e.printStackTrace();
				System.err.println(e.getMessage());
	        } catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			return con;
		}
		
		private void closeConnection() {
			if (con != null) {
				try {
		            con.close();
		        } catch (SQLException e) {
		        	System.err.println(e.getMessage());
		        }
			}
		}
		
		public Connection getConnection() throws SQLException {
			con = openConnection();
	    	return con;
	    }
		
		public boolean isValidClient(String uname, String pword) {
			if(uname.equals(databaseProperties.getProperty("cscclient.user")) && 
					aesEncryptionDecryption.decrypt(databaseProperties.getProperty("cscclient.password"), secretKey)
					.equals(aesEncryptionDecryption.decrypt(pword, secretKey)))
				return true;
			else
				return false;
		}
		
		/*public static Vector getRow(Connection pcon, String strSQL) {
			
			Vector vRs = CSCDBExcecute.execute(pcon, strSQL);
			//System.out.println("vRs : "+vRs);
			if(vRs.isEmpty())
			{
				vRs.clear();
				return vRs;
			}	
			else
			{
				Vector vRow = (Vector)vRs.firstElement();
				if(vRow.isEmpty())
					vRow.clear();
				else
				{
					//System.out.println("Vector : "+vRow);
					return vRow;
				}
				return vRow;
			}
			
		}
		
		public static String getOne(Connection pcon, String strSQL) {
			
			Vector vRs = CSCDBExcecute.execute(pcon, strSQL);
				
			if(vRs.isEmpty())
			{
				vRs.clear();
				return vRs.toString();
			}

			Vector vRow  = (Vector)vRs.firstElement();

			if(vRow.isEmpty())
				vRow.clear();

			return (String)vRow.firstElement();
		}*/
	}	

