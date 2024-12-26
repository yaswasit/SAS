package com.tsspdcl.sas.dbcon;
import java.util.*;
import java.sql.*;
import java.text.*;

public class SASDB
{
	public static synchronized Connection getConnection(String app,String dbuser,String dbpwd)
	{
		Connection con=null;

		if(app==null)
			app="";

		if(app.equals(""))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.19:1521:ebsdb1","apseblt","sivaji");
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2","apseblt","sivaji");
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1","apseblt","sivaji");
			}
			catch(Exception e)
			{
				System.out.println("Error in MATS DataBase Connection..........");
			}
		}
		if(app.equals("corporate"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.19:1521:ebsdb1","apseblt","sivaji");
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2","corporate","corporate");
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1","corporate","corporate");
			}
			catch(Exception e)
			{
				System.out.println("Error in MATS DataBase Connection..........");
			}
		}
		else if(app.equals("MATS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.90.191:7722:tsspdb1","theft","rthft327");
				//con =DriverManager.getConnection("jdbc:oracle:thin:@tsspprodscn.tsspdcl.com:7722/tsspdb.tsspdcl.com", "theft", "the#0823ft");
			}
			catch(Exception e)
			{
				System.out.println("Error in MATS DataBase Connection..........");
			}
		}
		else if(app.equals("EBS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.19:1521:ebsdb1",dbuser,dbpwd);
				//con =DriverManager.getConnection("jdbc:oracle:thin:@ebsprodscn.tsspdcl.com:1521:EBSDB1",dbuser,dbpwd);
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2",dbuser,dbpwd);
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1",dbuser,dbpwd);

			}
			catch(Exception e)
			{
				System.out.println("Error in EBS DataBase Connection..........");
			}
		}
		else if(app.equals("SAS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.19:1521:ebsdb1",dbuser,dbpwd);
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2","SAS","sas$1122");
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1","SAS","sas$1122");

			}
			catch(Exception e)
			{
				System.out.println("Error in EBS DataBase Connection..........");
			}
		}
		else if(app.equals("EAUDIT"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2","test9","test9");
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1","test9","test9");
			}
			catch(Exception e)
			{
				System.out.println("Error in EBS DataBase Connection..........");
			}
		}

		else if(app.equals("CAT"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.7:1521:SIVAEBS","apcpdclpro3","apcpdclpro3");
			}
			catch(Exception e)
			{
				System.out.println("Error in CAT DataBase Connection.........."+e);
			}
		}
		else if(app.equals("APCPDCL"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.20.20:1521:tims","apcpdcl","apcpdcl");
			}
			catch(Exception e)
			{
				System.out.println("Error in APCPDCL DataBase Connection.........."+e);
			}
		}
		else if(app.equals("TIMS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.20.20:1521:tims","timsold","timsold");
			}
			catch(Exception e)
			{
				System.out.println("Error in TIMS DataBase Connection.........."+e);
			}
		}
		else if(app.equals("CSC"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				//con =DriverManager.getConnection("jdbc:oracle:thin:@ebsprodscn.tsspdcl.com:1521/EBSDB","INTCSC","SAMSUNG");

				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.90.91:2277:cscdb1","spdcsc","spdcsc#0519");
				//con =DriverManager.getConnection("jdbc:oracle:thin:@cscdbprodscn.tsspdcl.com:2277/cscdb","spdcsc","spdcsc#0519");
			}
			catch(Exception e)
			{
				System.out.println("Error in CSC DataBase Connection.........."+e);
			}
		}
		else if(app.equals("ESTIMATE"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.20.20:1521:tims","wots","wots");
			}
			catch(Exception e)
			{
				System.out.println("Error in WOTS DataBase Connection.........."+e);
			}
		}
		else if(app.equals("MIS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.20.20:1521:tims","mis","mis");
			}
			catch(Exception e)
			{
				System.out.println("Error in MIS DataBase Connection.........."+e);
			}
		}

		else if(app.equals("GIS"))
		{
			try
			{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.55.181:2001:gisprd","csapuser","csapuser");
			}
			catch(Exception e)
			{
				System.out.println("Error in GIS Connection ............."+e);
			}
		}
		return con;
	}

	public static synchronized Connection getConnection()
	{
		Connection con=null;

		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.19:1521:ebsdb1","apseblt","sivaji");
			//con =DriverManager.getConnection("jdbc:oracle:thin:@ebsprodscn.tsspdcl.com:1521:EBSDB1","apseblt","sivaji");
			//con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.94:1521:EBSDB2","apseblt","sivaji");
			con =DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.91:1521:EBSDB1","apseblt","sivaji");
		}
		catch(Exception e)
		{
			System.out.println("Error in SAS DataBase Connection..........");
		}
		return con;
	}

}

