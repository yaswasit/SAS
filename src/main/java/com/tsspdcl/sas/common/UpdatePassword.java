package com.tsspdcl.sas.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UpdatePassword {
		
	private static Connection con = null;
	private static ResultSet rs= null;
	private static AESEncryptionDecryption aesEncryptionDecryption = new AESEncryptionDecryption();
	private static final String secretKey = "secrete";
	
	public static void main(String[] args) throws SQLException {
		
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		
		/*try(Connection con = getConnection()){	
			System.out.println("Connection ..."+con);
			String qry = "SELECT sasuserid, saspassword FROM sasusersold ORDER BY sasuserid";
     		PreparedStatement pstmt=con.prepareStatement(qry);  
     		rs=pstmt.executeQuery();
     		
     		while(rs.next()) {
     			String uQuery = "UPDATE sasusersold SET encpassword = ? WHERE sasuserid = ?";
     			PreparedStatement upstmt = con.prepareStatement(uQuery);
     			upstmt.setString(1, bCryptPasswordEncoder.encode(rs.getString(2)));
     			upstmt.setString(2, rs.getString(1));
    	             
    	        int z = upstmt.executeUpdate();
     			
     		}
     		
		}catch(Exception e) {
			e.printStackTrace();
		}*/
		
        
        System.out.println(bCryptPasswordEncoder.encode("admin"));
        //System.out.println(bCryptPasswordEncoder.encode("Hello#123"));
    }
	
	
	private static Connection openConnection() {
		
		Properties databaseProperties = new Properties();
				
		if (con != null)
			closeConnection();
		try {
			
			databaseProperties.load(new FileInputStream("src/main/resources/application.properties"));

			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			
			String url = databaseProperties.getProperty("spring.datasource.url");
	        String username = databaseProperties.getProperty("spring.datasource.username");
	        //String password = databaseProperties.getProperty("spring.datasource.password");
	        String password = aesEncryptionDecryption.decrypt(databaseProperties.getProperty("spring.datasource.password"), secretKey);
			
			//String password = aesEncryptionDecryption.decrypt(databaseProperties.getProperty("cscdatabase.password"), secretKey);
        	        	
        	System.out.println(url);
        	System.out.println(username);
        	System.out.println(password);
        	
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
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		return con;
	}
	
	private static void closeConnection() {
		if (con != null) {
			try {
	            con.close();
	        } catch (SQLException e) {
	        	System.err.println(e.getMessage());
	        }
		}
	}
	
	public static Connection getConnection() throws SQLException {
		con = openConnection();
    	return con;
    }

}
