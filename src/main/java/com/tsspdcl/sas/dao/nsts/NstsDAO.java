package com.tsspdcl.sas.dao.nsts;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import org.springframework.web.multipart.MultipartFile;


//import com.tsspdcl.database.TSSPDCLDBConnectionPool;
import com.tsspdcl.sas.common.CommonUtils;
import com.tsspdcl.sas.common.QueryBuilder;
import com.tsspdcl.sas.dbcon.DBConnections;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.entity.nsts.AGLRegistrations;
import com.tsspdcl.sas.entity.nsts.AddConnections;
import com.tsspdcl.sas.entity.nsts.AglTobeReleasedModel;
import com.tsspdcl.sas.entity.nsts.DocumentsVerification;
import com.tsspdcl.sas.entity.nsts.LTMSendOTPtoConsumer;
import com.tsspdcl.sas.entity.nsts.LTMmeterNoList;
import com.tsspdcl.sas.entity.nsts.LTMtestReport;
import com.tsspdcl.sas.entity.nsts.LTMtobeRelease;
import com.tsspdcl.sas.entity.nsts.NewRegistrations;
import com.tsspdcl.sas.entity.nsts.PendingConnections;
import com.tsspdcl.sas.entity.nsts.SessionInfo;
import com.tsspdcl.sas.entity.nsts.UploadTestReportModel;
import com.tsspdcl.sas.entity.nsts.UploadTestReportNewModel;
import com.tsspdcl.sas.entity.nsts.WebRegistrations;
import com.tsspdcl.sas.service.FileStatements;
import com.tsspdcl.sas.service.SmsMsg;

//@Configuration
//@PropertySource("classpath:application.properties")	
public class NstsDAO {
	
	@Autowired
	private Environment env;
	
	StringBuilder result = new StringBuilder();
	public NstsDAO() {
		//System.out.println("NSTS DAO STARTED....");
	}
	
	String removeNull(String st)
	{
		if(st==null)
			return "";
		else
			return st;
	}
	
	DBConnections db = new DBConnections();
	com.tsspdcl.sas.dbcon.SASDB SASDB = new com.tsspdcl.sas.dbcon.SASDB();
	
	final String rejecteddocslist = "SELECT docid,(CASE  WHEN docid=3 THEN 'ID' WHEN docid=5 THEN 'SALEDEED' WHEN docid=7 THEN 'BOND' WHEN docid=19 THEN 'PLAN' WHEN docid=8 THEN 'OCC_CERT' END) DOC_NAME from DOCTYPES where docid in(3,5,7) order by docid";
	final String linemenlist = "SELECT UNIQUE emp_id, emp_name FROM sec_onm WHERE uk_seccd=? AND emp_desg IN ('ALM','LI','JLM','Linemen')";
	final String linemenphone = "SELECT UNIQUE emp_id, nvl(mobileno,' ') FROM sec_onm WHERE uk_seccd=? AND emp_id = ?";
	
	private String offtype = "";
	
	public List<NewRegistrations> getAllNewRegistrations(User user) throws SQLException, Exception {
		//System.out.println("Query................"+query);
		
		List <NewRegistrations> regsList = new ArrayList<NewRegistrations>();
		
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.newreg+offtype+"='"+user.getSasseccd()+"' ORDER BY nrregdate desc");) {
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	regsList.add(new NewRegistrations(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getInt("cat"),rs.getDouble("load"),rs.getString("consname"),rs.getString("address"),rs.getDouble("amount"),rs.getInt("status"),rs.getString("meterno"),rs.getString("remarks")));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return regsList;
	}
	
	public List<WebRegistrations> getWebRegistrations(User user) throws SQLException, Exception {
		List <WebRegistrations> webRegList = new ArrayList<WebRegistrations>();
		
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.webreg+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("Connection..."+connection);	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	webRegList.add(new WebRegistrations(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("chqddno"),rs.getString("catdesc"),rs.getDouble("devchgs"),rs.getDouble("secdep"),rs.getDouble("contload"),rs.getString("secname")));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return webRegList;
	}
	
	public List<PendingConnections> getPendingConnections(User user) throws SQLException, Exception {
		//System.out.println("Query................"+query);
		List <PendingConnections> penRegList = new ArrayList<PendingConnections>();
		
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.penreg+offtype+"='"+user.getSasseccd()+"' ORDER BY to_date(nrregdate) desc");) {
			//System.out.println("Connection..."+connection);
			//System.out.println(QueryBuilder.penreg+offtype+"='"+user.getSasseccd()+"' ORDER BY to_date(nrregdate) desc");
	        ResultSet rs = preparedStatement.executeQuery();
	   	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	penRegList.add(new PendingConnections(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("prno"),rs.getString("secname"),rs.getString("tobereldt"),rs.getString("reason")));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return penRegList;
	}
	
	public List<PendingConnections> getRejectedConnections(User user) throws SQLException, Exception {
		//System.out.println("Query................"+query);
		List <PendingConnections> rejConsList = new ArrayList<PendingConnections>();
		
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.rejcons+offtype+"='"+user.getSasseccd()+"' ORDER BY to_date(nrregdate) desc");) {
			//System.out.println("Connection..."+connection);
			System.out.println(QueryBuilder.rejcons+offtype+"='"+user.getSasseccd()+"' ORDER BY to_date(nrregdate) desc");
	        ResultSet rs = preparedStatement.executeQuery();
	   	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	rejConsList.add(new PendingConnections(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("prno"),rs.getString("secname"),rs.getString("tobereldt"),rs.getString("reason")));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return rejConsList;
	}
	
	public List<PendingConnections> getEstimationRequiredList(User user) throws SQLException, Exception {
		//System.out.println("Query................"+query);
		List <PendingConnections> estReqdList = new ArrayList<PendingConnections>();
		
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.estreq+offtype+"='"+user.getSasseccd()+"' ORDER BY to_date(nrregdate) desc");) {
			//System.out.println("Connection..."+connection);
			System.out.println(QueryBuilder.estreq+offtype+"='"+user.getSasseccd()+"'");
	        ResultSet rs = preparedStatement.executeQuery();
	   	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	estReqdList.add(new PendingConnections(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("prno"),"","",""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return estReqdList;
	}
		
	public List<DocumentsVerification> getDocumentsVerification(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new DocumentsVerification(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("secname"),rs.getString("tobereldate"),rs.getString("meterno"),""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	public List<DocumentsVerification> getAssignToLineman(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.assignToLineman+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new DocumentsVerification(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("secname"),rs.getString("tobereldate"),rs.getString("meterno"),""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	public List<DocumentsVerification> getFieldVerificationList(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.fieldVerification+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new DocumentsVerification(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("secname"),rs.getString("tobereldate"),rs.getString("meterno"),""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	public List<DocumentsVerification> getFieldVerification(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.fieldverification+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new DocumentsVerification(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("secname"),rs.getString("tobereldate"),rs.getString("meterno"),""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	public List<DocumentsVerification> nrTestreport(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.nrTestreport+" and "+offtype+"='"+user.getSasseccd()+"'  order by to_date(toberel_dt,'DD-MM-RRRR')");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new DocumentsVerification(rs.getString("nrregno"),rs.getString("nrregdate"),rs.getString("consname"),rs.getString("catdesc"),rs.getString("secname"),rs.getString("tobereldate"),rs.getString("meterno"),""));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	public String getRegData(String regid, String realPath, String pageType) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null;
		PreparedStatement estStmt = null, payPS = null;
		PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		ResultSet payrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		DecimalFormat df = new DecimalFormat("####0.00");
		double paid_sd=0.0;
		double paid_dc=0.0;
		double paid_sc=0.0;
		double paid_slc=0.0;
		double paid_mcon=0.0;
		double paid_msh=0.0;
		double paid_dcon=0.0;
		double paid_dsh=0.0;
		double paid_OTH=0.0;
		String paid="0";
		String offname="";
		int app_fee=0;
		double ipaid=0;
		double estamt=0;
		double bal=0;

		String mdisp="";
		String mrk="";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM pmt_sms_link WHERE regno = ?");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        if (rs.next()) {
	        	sql = QueryBuilder.regQryStr1+QueryBuilder.regQryStr2+"'Pending for OTP Updation')),"+QueryBuilder.regQrystr3+" F.SECCD, SAS_OTP, TO_CHAR(sms_date,'DD-MON-YY'), P.sentby, MOBILENO, ' ' COL30, NVL(TYPE,'N') ESTREQ "//
	        			+ "FROM NEWCONNECTION_REGISTER F, DOCUMENTS E, category b, all_section c, subcategory d, "//
	        			+ "(select X.regno regno,X.sms_date sms_date,REQUEST_FLAG,ADE_REMARKS,AE_REMARKS,SENTBY,RESEND_FLAG from "//
	        			+ "(select REGNO,"+QueryBuilder.regQrystr4+QueryBuilder.regQrystr5+" NRREGNO=P.REGNO(+) AND  NRREGNO=A.REGNO(+)  AND REGNO_STATUS='ACTIVE' AND NRREGNO=?";
	        }	
	        else {
	        	sql = QueryBuilder.regQryStr1+"'Pending for OTP Updation',"+QueryBuilder.regQrystr3+"' ' COL24, ' ' COL25, ' ' COL26, ' ' COL27, MOBILENO, SAS_OTP, NVL(TYPE,'N') ESTREQ FROM NEWCONNECTION_REGISTER F, DOCUMENTS E, category b, all_section c, "//
	        			+ "subcategory d, "+QueryBuilder.regQrystr5+" a.regno(+)=f.nrregno and REGNO_STATUS='ACTIVE' AND NRREGNO=?";
	        }
	        
	        //System.out.println("query..."+sql);
	        
        	regStmt = connection.prepareStatement(sql);
        	regStmt.setString(1, regid);
        	
        	regrs = regStmt.executeQuery();
	        	
        	if(regrs.next()) {
        		regData.put("nrregno", regrs.getString(1));
        		regData.put("consname", regrs.getString(2));
        		regData.put("nrregdate", regrs.getString(3));
        		regData.put("secname", regrs.getString(4));
        		regData.put("group", regrs.getString(6));
        		regData.put("catdesc", regrs.getString(7)+"-"+regrs.getString(8));
        		regData.put("subcat", regrs.getString(9));
        		regData.put("contload", regrs.getString(10));
        		regData.put("phase", regrs.getString(11));
        		regData.put("meterno", regrs.getString(17));
        		regData.put("cscremarks", regrs.getString(18));
        		regData.put("aeremarks", regrs.getString(19));
        		regData.put("aderemarks", regrs.getString(20));
        		regData.put("tobereleasedt", regrs.getString(21));
        		regData.put("aeremarksdt", regrs.getString(22));
        		regData.put("aderemarksdt", regrs.getString(23));
        		regData.put("status", regrs.getString(24));
        		regData.put("mobileno", regrs.getString(29));
        		regData.put("catcd", regrs.getString(7));
        		regData.put("estreq", regrs.getString(31));
        		
        		result.append(getRegHTMLTable("Registration Details",regData));
        	}
	       	        
	        estStmt = connection.prepareStatement(QueryBuilder.estQry);
        	estStmt.setString(1, regid);
        	//System.out.println("estQry>>"+QueryBuilder.estQry);
        	estrs = estStmt.executeQuery();
        	estData.put("regid", regid);
        	if(estrs.next()) {
        		estData.put("estst", estrs.getString(2));
        		estData.put("sapestno", estrs.getString(1));
        		estData.put("estamt", estrs.getString(3));
        		estData.put("noofdayspending", estrs.getString(4));
        		estData.put("collected_estrs", estrs.getString(6));
        		estData.put("SAP_WO_STATUS", estrs.getString(13));
        		estData.put("work_order_status", estrs.getString(14));
        		estData.put("pending_days", estrs.getString(15));
        		estData.put("SAP_REL", estrs.getString(16));
        		estData.put("sap_reason", estrs.getString(17));
        		
        		estData.put("estsandt", estrs.getString(23));
        		estData.put("workcompdt", estrs.getString(24));
        		estData.put("estcreatedt", estrs.getString(25));
        		estData.put("eststcode", estrs.getString(26));
        		if(!"null".equals(estrs.getString(1))) {
	        		payPS = connection.prepareStatement(QueryBuilder.payment_details);
	        		payPS.setString(1, estrs.getString(1));
	        		payPS.setString(2, estrs.getString(1));
	        		payrs = payPS.executeQuery();
	        		
	        		if(payrs.next()) {
	        			paid_sd=Double.parseDouble(payrs.getString(1));
	    				paid_dc=Double.parseDouble(payrs.getString(2));
	    				paid_sc=Double.parseDouble(payrs.getString(3));
	    				paid_slc=Double.parseDouble(payrs.getString(4));
	    				paid_OTH=Double.parseDouble(payrs.getString(5));
	    				paid_mcon=Double.parseDouble(payrs.getString(6));
	    				paid_msh=Double.parseDouble(payrs.getString(7));
	    				paid_dcon=Double.parseDouble(payrs.getString(8));
	    				paid_dsh=Double.parseDouble(payrs.getString(9));
	        		}
	        		double reqsd=Double.parseDouble(estrs.getString(11));
	        		double tsdpd=0.0;
	    			if(reqsd<paid_sd)
	    			{
	    				tsdpd=reqsd;
	    			}
	    			else
	    			{
	    				tsdpd=paid_sd;
	    			}
	    			
	    			double reqdc=Double.parseDouble(estrs.getString(10));
	    			double tdcpd=0.0;
	    			if(reqdc<paid_dc)
	    			{
	    				tdcpd=reqdc;
	    			}
	    			else
	    			{
	    				tdcpd=paid_dc;
	    			}
	    			
	    			double reqsc=Double.parseDouble(estrs.getString(8));
	    			double tscpd=0.0;
	    			if(reqsc<paid_sc)
	    			{
	    				tscpd=reqsc;
	    			}
	    			else
	    			{
	    				tscpd=paid_sc;
	    			}
	    			
	    			double reqmcon=Double.parseDouble(estrs.getString(19));
	    			double tmconpd=0.0;
	    			if(reqmcon<paid_mcon)
	    			{
	    				tmconpd=reqmcon;
	    			}
	    			else
	    			{
	    				tmconpd=paid_mcon;
	    			}
	    			
	    			double reqmsh=Double.parseDouble(estrs.getString(20));
	    			double tmshpd=0.0;
	    			if(reqmsh<paid_msh)
	    			{
	    				tmshpd=reqmsh;
	    			}
	    			else
	    			{
	    				tmshpd=paid_msh;
	    			}
	    			
	    			double reqdcon=Double.parseDouble(estrs.getString(21));
	    			double tdconpd=0.0;
	    			if(reqdcon<paid_dcon)
	    			{
	    				tdconpd=reqdcon;
	    			}
	    			else
	    			{
	    				tdconpd=paid_dcon;
	    			}
	    			
	    			double reqdsh=Double.parseDouble(estrs.getString(22));
	    			double tdshpd=0.0;
	    			if(reqdsh<paid_dsh)
	    			{
	    				tdshpd=reqdsh;
	    			}
	    			else
	    			{
	    				tdshpd=paid_dsh;
	    			}
	    			
	    			double totpd=tsdpd+tdcpd+tscpd+paid_slc+tmconpd+tmshpd+tdconpd+tdshpd+paid_OTH;
	    			paid=totpd+"";
	    			
	    			ipaid=Double.parseDouble(paid);
	    			if(ipaid<0)
	    				ipaid=0;

	    				if(estrs.getString(5)==null||(estrs.getString(5)).equals("APPR"))
	    				{
	    		
	    				}
	    				else 
	    				{
	    					mdisp="* Estimate amount is tentative and liable for changes during estimate approval process";
	    					mrk="*";
	    				}
	        		
        		}
        		
        		estData.put("received_rsonreg", ipaid+"");
        		if(!"0".equals(estrs.getString(6))){
        			estamt=Double.parseDouble(estrs.getString(6));
      			    bal=(estamt - ipaid);
        		}
        		estData.put("bal_amt", df.format(bal)+"");
        		
        		result.append(getEstHTMLTable("Estimation Details",estData));
        	}
        	
        	
        	//System.out.println(QueryBuilder.docsQry);
        	docsStmt = connection.prepareStatement(QueryBuilder.docsQry);
         	docsStmt.setString(1, regid);
         	
         	docsrs = docsStmt.executeQuery();
         	
         	if(docsrs.next()) {
         		if(docsrs.getInt(4)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(4), docsrs.getBlob(3), realPath, docsrs.getString(1), "APPL_FORM");
         			docsData.put("applform", url);
         			System.out.println(">>"+url);
         		}
         		if(docsrs.getInt(6)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(6), docsrs.getBlob(5), realPath, docsrs.getString(1), "ID_PROOF");
         			docsData.put("idproof", url);
         		}
         		if(docsrs.getInt(8)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(8), docsrs.getBlob(7), realPath, docsrs.getString(1), "CAST_CERTIFICATE");
         			docsData.put("castcert", url);
         		}
         		if(docsrs.getInt(10)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(10), docsrs.getBlob(9), realPath, docsrs.getString(1), "SALE_DEED");
         			docsData.put("saledeed", url);
         		}
         		if(docsrs.getInt(12)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(12), docsrs.getBlob(11), realPath, docsrs.getString(1), "PANCHAYAT_LETTER");
         			docsData.put("panchayatletter", url);
         		}
         		if(docsrs.getInt(14)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(14), docsrs.getBlob(13), realPath, docsrs.getString(1), "PHOTO");
         			docsData.put("photo", url);
         		}
         		if(docsrs.getInt(16)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(16), docsrs.getBlob(15), realPath, docsrs.getString(1), "WIRING_CERTIFICATE");
         			docsData.put("wiringcert", url);
         		}
         		if(docsrs.getInt(18)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(18), docsrs.getBlob(17), realPath, docsrs.getString(1), "OCC_CERT");
         			docsData.put("occcert", url);
         		}
         		if(docsrs.getInt(20)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(20), docsrs.getBlob(19), realPath, docsrs.getString(1), "NOC_PCB");
         			docsData.put("nocpcb", url);
         		}
         		if(docsrs.getInt(22)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(22), docsrs.getBlob(21), realPath, docsrs.getString(1), "NOV_LBODY");
         			docsData.put("novlbody", url);
         		}
         		if(docsrs.getInt(24)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(24), docsrs.getBlob(23), realPath, docsrs.getString(1), "PATTA_DOC");
         			docsData.put("pattadoc", url);
         		}
         		if(docsrs.getInt(26)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(26), docsrs.getBlob(25), realPath, docsrs.getString(1), "MA_TAX");
         			docsData.put("mhtax", url);
         		}
         		if(docsrs.getInt(28)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(28), docsrs.getBlob(27), realPath, docsrs.getString(1), "SSI_CERT");
         			docsData.put("ssicert", url);
         		}
         		if(docsrs.getInt(30)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(30), docsrs.getBlob(29), realPath, docsrs.getString(1), "CERT_80G");
         			docsData.put("cert80g", url);
         		}
         		if(docsrs.getInt(32)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(32), docsrs.getBlob(31), realPath, docsrs.getString(1), "MRO_CERT");
         			docsData.put("mrocert", url);
         		}
         		if(docsrs.getInt(34)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(34), docsrs.getBlob(33), realPath, docsrs.getString(1), "CEIG_CERT");
         			docsData.put("ceigcert", url);
         		}
         		if(docsrs.getInt(36)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(36), docsrs.getBlob(35), realPath, docsrs.getString(1), "FORM_I2");
         			docsData.put("formI2", url);
         		}
         		if(docsrs.getInt(38)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(38), docsrs.getBlob(37), realPath, docsrs.getString(1), "LOC_PHOTO");
         			docsData.put("locphoto", url);
         		}
         		if(docsrs.getInt(40)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(40), docsrs.getBlob(39), realPath, docsrs.getString(1), "SAS_PHOTO");
         			docsData.put("sasphoto", url);
         		}
         		if(docsrs.getInt(42)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(42), docsrs.getBlob(41), realPath, docsrs.getString(1), "SAS_TSTRPT");
         			docsData.put("saststrpt", url);
         		}
         		if(docsrs.getInt(44)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(44), docsrs.getBlob(43), realPath, docsrs.getString(1), "SAS_AGRMT");
         			docsData.put("sasagrmt", url);
         		}
         		docsData.forEach((key, value) -> System.out.println(key+" "+value));
         		result.append(getDocsHTMLTable("Submitted Documents List",docsData));
         	}
         	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}
	
public String getDDDetails(String regid, String realPath) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null;
		PreparedStatement estStmt = null, payPS = null;
		PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet ddrs = null; ResultSet docsrs = null;
		ResultSet payrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
				
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.dd_details);) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
			preparedStatement.setString(2, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        if(rs.next()) {
        		regData.put("APPL_FEES_GST", rs.getString("APPL_FEES_GST"));
        		regData.put("DCGST", rs.getString("DCGST"));
        		
        		result.append(getDDHTMLTable("DD Details",regData, regid));
        	}
	        
         	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}


@SuppressWarnings("rawtypes")
	private String getDDHTMLTable(String title, HashMap regData, String regid) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		try(Connection connection = db.getConnection();
		PreparedStatement ps = connection.prepareStatement(QueryBuilder.dd_pay_details);){
    	ps.setString(1, regid);
    	ResultSet ddrs = ps.executeQuery();
    	
		htmlTable.append("<div>");
		//htmlTable.append("<h6 class=\"card-content-label mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		htmlTable.append("<table class=\"table table-bordered table-striped table-hover border-bottom mb-0 border dataTable no-footer\">"); 
			htmlTable.append("<thead>");
				htmlTable.append("<tr role='row'>");
					htmlTable.append("<th class=\"wd-15p\">PR No.</th>");
					htmlTable.append("<th class=\"wd-15p font-weight-bold\">Pay Mode</th>");
					htmlTable.append("<th class=\"wd-10p\">Amount</th>");
					htmlTable.append("<th class=\"wd-15p\">DD Number</th>");
					htmlTable.append("<th class=\"wd-15p\">DD Date</th>");
					htmlTable.append("<th class=\"wd-15p\">Bank Name</th>");
					htmlTable.append("<th class=\"wd-15p\">PR Date</th>");
				htmlTable.append("</tr>");
			htmlTable.append("</thead>");	
			htmlTable.append("<tbody>");	
				double sd=0.0;
				double dc=0.0;
				double applfee=0.0;
			while(ddrs.next()) {
				htmlTable.append("<tr>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(1)+"</td>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(2)+"</td>");
					htmlTable.append("<td class=\"wd-10p\">"+ddrs.getString(3)+"</td>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(4)+"</td>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(5)+"</td>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(6)+"</td>");
					htmlTable.append("<td class=\"wd-15p\">"+ddrs.getString(7)+"</td>");
				htmlTable.append("</tr>");
				sd=sd+Double.parseDouble(ddrs.getString(8));dc=dc=dc+Double.parseDouble(ddrs.getString(9));applfee=applfee+Double.parseDouble(ddrs.getString(10));
			}	
		htmlTable.append("</tbody>");	
		htmlTable.append("</table>");
		htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
		htmlTable.append("<tbody>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-80p\" align='right'>Application Fee</th>");
				htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+applfee+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-80p\" align='right'>18% GST on Application fee</th>");
				htmlTable.append("<td class=\"wd-20p\">"+regData.get("APPL_FEES_GST")+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-80p\" align='right'>Development Charges</th>");
				htmlTable.append("<td class=\"wd-20p\">"+dc+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-80p\" align='right'>18% GST on Development Charges</th>");
				htmlTable.append("<td class=\"wd-20p\">"+regData.get("DCGST")+"</td>");
			htmlTable.append("</tr>");
		htmlTable.append("</tbody>");	
		htmlTable.append("</table>");	
		htmlTable.append("</div>");
		
			connection.close();
			ddrs.close();
	        ps.close();
	    	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return htmlTable.toString();
	}

	
	public HashMap<String, String> getRejectedDocsList() throws SQLException, Exception {
		final HashMap<String, String> resultdocs = new HashMap<>();
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(rejecteddocslist);) {
			//System.out.println("Connection..."+connection);	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	resultdocs.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return resultdocs;
	}
	
	public HashMap<String, String> getLinemenList(User user) throws SQLException, Exception {
		final HashMap<String, String> linemen = new HashMap<>();
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(linemenlist);) {
			//System.out.println("Connection..."+connection);
			preparedStatement.setString(1, user.getSasseccd());
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	linemen.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return linemen;
	}
	
	public String getLinemenPhone(String empid, User user) throws SQLException, Exception {
		String phoneno = "";
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(linemenphone);) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, empid);
			 
	        ResultSet rs = preparedStatement.executeQuery();
	          
	        if (rs.next()) {
	        	phoneno = rs.getString(2);
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return phoneno;
	}
	
	private void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
	
	@SuppressWarnings("rawtypes")
	private String getRegHTMLTable(String title, HashMap regData) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
			htmlTable.append("<tbody>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregno")+"</th>");
					htmlTable.append("<td class=\"wd-20p font-weight-bold\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("nrregno")+"</span></td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("consname")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("consname")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregdate")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("nrregdate")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("secname")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("secname")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("group")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("group")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("contload")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("contload")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("catdesc")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("catdesc")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("subcat")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("subcat")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("tobereleasedt")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("tobereleasedt")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("phase")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("phase")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("meterno")+"</th>");
					htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("meterno")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("mobileno")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("mobileno")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("aeremarks")+", and Date</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("aeremarks")+", "+regData.get("aeremarksdt")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("aderemarks")+", and Date</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("aderemarks")+", "+regData.get("aderemarksdt")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("cscremarks")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("cscremarks")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">AE Latest Attended Date</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("aeremarksdt")+"</td>");
					htmlTable.append("<th class=\"wd-15p\"></th>");
					htmlTable.append("<td class=\"wd-20p\"></td>");	
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("status")+"</th>");
					htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-secondary bg-secondary-transparent py-2 px-2\">"+regData.get("status")+"</span></td>");
				htmlTable.append("</tr>");
		/*
		 * htmlTable.append("<tr>");
		 * htmlTable.append("<th class=\"wd-15p\">Lineman Name</th>");
		 * htmlTable.append("<td class=\"wd-20p\">"+regData.get("aeremarks")+"</td>");
		 * htmlTable.append("<th class=\"wd-15p\">Lineman Moble No.</th>");
		 * htmlTable.append("<td class=\"wd-20p\">"+regData.get("aderemarks")+"</td>");
		 * htmlTable.append("<th class=\"wd-15p\">Field Remarks by Lineman</th>");
		 * htmlTable.append("<td class=\"wd-20p\">"+regData.get("cscremarks")+"</td>");
		 * htmlTable.append("</tr>");
		 */
		htmlTable.append("</tbody>");	
		htmlTable.append("</table>");
		htmlTable.append("<input type=\"hidden\" name=\"catcd\" id=\"catcd\" value=\""+regData.get("catcd")+"\" />");
		htmlTable.append("<input type=\"hidden\" name=\"estreq\" id=\"estreq\" value=\""+regData.get("estreq")+"\" />");
		htmlTable.append("</div>");
		return htmlTable.toString();
	}
		
	@SuppressWarnings("rawtypes")
	private String getEstHTMLTable(String title, HashMap regData) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		if(commonUtil.removeNull(regData.get("eststcode")).equals("")) {
			
			htmlTable.append("<div class=\"alert alert-danger alert-bdleft-danger tx-18 d-flex align-items-center\" role=\"alert\"><i class=\"mdi mdi-alert-circle tx-20\" data-bs-toggle=\"tooltip\" title=\"\" data-bs-original-title=\"mdi-alert-circle\" aria-label=\"mdi-alert-circle\"></i><div>"+regData.get("estst")+"</div></div>");
		}
		else {
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("estst")+"</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("estst")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("sapestno")+"</th>");
						
						if(commonUtil.removeNull(regData.get("sapestno")).equals(""))
							htmlTable.append("<td class=\"wd-20p\"></td>");
						else {
							//href ="getDocument/${adm.regno}/aphoto"
							//htmlTable.append("<td class=\"wd-20p\"><a class=\"font-weight-bold\" href=\"javascript:;\" onclick=\"getSAPSanctionLetter('"+regData.get("sapestno")+"')\">"+regData.get("sapestno")+"</a><img src='../../assets/img/icons/pdf.png' title='PDF' /></td>");
							//htmlTable.append("<td class=\"wd-20p\"><a class=\"font-weight-bold\" target=\"_new\" href=\"/SAS/nsts/getSAPSanctionLetter/"+regData.get("sapestno")+"\">"+regData.get("sapestno")+"</a><img src=\"/SAS/assets/img/icons/pdf.png\" title=\"PDF\" /></td>");
							htmlTable.append("<td class=\"wd-20p\"><a class=\"font-weight-bold\" target=\"_new\" href=\"http://10.10.10.99:8080/CSC/SAPSanctionLetter.jsp?sapwbsno="+regData.get("sapestno")+"\">"+regData.get("sapestno")+"</a><img src=\"/SAS/assets/img/icons/pdf.png\" title=\"PDF\" /></td>");
							//getDocument/${adm.regno}/aphoto
						}
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("estamt")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("estamt")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("noofdayspending")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("noofdayspending")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("estsandt")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("estsandt")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("workcompdt")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("workcompdt")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("estcreatedt")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("estcreatedt")+"</td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
			htmlTable.append("</table>");
		}
		htmlTable.append("</div>");
		
		if(!"0".equals(regData.get("collected_estrs"))) {
			htmlTable.append("<div>");
			htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">Payment Details</h6>");
				htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
					htmlTable.append("<tbody>");
						htmlTable.append("<tr>");
							htmlTable.append("<th class=\"wd-10p\">Payments to be Collected as per Estimate&nbsp;Rs.</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("collected_estrs")+"</td>");
						htmlTable.append("</tr>");
						htmlTable.append("<tr>");
							htmlTable.append("<th class=\"wd-10p\">Payments Received at the time of Registration &nbsp;Rs.</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("received_rsonreg")+"</td>");
						htmlTable.append("</tr>");
						htmlTable.append("<tr>");
							htmlTable.append("<th class=\"wd-10p\">Balance to be paid by the Customer&nbsp;Rs.</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("bal_amt")+"</td>");
						htmlTable.append("</tr>");
					htmlTable.append("</tbody>");
				htmlTable.append("</table>");
			htmlTable.append("</div>");
		}
		if(!"".equals(regData.get("SAP_WO_STATUS")) && "0".equals(regData.get("SAP_REL"))) {
			htmlTable.append("<div>");
			htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">Work Order Details</h6>");
				htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
					htmlTable.append("<tbody>");
						htmlTable.append("<tr>");
							htmlTable.append("<th class=\"wd-10p\">Work Order Status</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("work_order_status")+"</td>");
							htmlTable.append("<th class=\"wd-10p\">No.of Days Pending</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("pending_days")+"</td>");
						htmlTable.append("</tr>");
					htmlTable.append("</tbody>");
				htmlTable.append("</table>");
			htmlTable.append("</div>");
		}
		if(regData.get("sap_reason")!=null) {
			htmlTable.append("<div>");
				htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
					htmlTable.append("<tbody>");
						htmlTable.append("<tr>");
							htmlTable.append("<th class=\"wd-10p\">Reason</th>");
							htmlTable.append("<td class=\"wd-25p\">"+regData.get("sap_reason")+"</td>");
						htmlTable.append("</tr>");
					htmlTable.append("</tbody>");
				htmlTable.append("</table>");
			htmlTable.append("</div>");
		}
		
		htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
			htmlTable.append("<tbody>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-10p\"></th>");
					htmlTable.append("<td class=\"wd-25p\"><a class=\"btn ripple btn-info\"  data-bs-target=\"#modaldemo9\" data-bs-toggle=\"modal\" href=\"javascript:;\" onClick=\"getDDDetails('"+regData.get("regid")+"')\"><B>Click here</B></a><B><FONT SIZE=\"4\" COLOR=\"green\">  for DD Details</FONT></B>.</td>");
					
				htmlTable.append("</tr>");
			htmlTable.append("</tbody>");
		htmlTable.append("</table>");
	
		return htmlTable.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private String getDocsHTMLTable(String title, HashMap<String, String> docsData) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		htmlTable.append("</div>");
		htmlTable.append("<div class=\"text-wrap\">"); 
			htmlTable.append("<div class=\"example\">"); 
				htmlTable.append("<div class=\"row row-sm\">");
				docsData.forEach((key, value) -> {
					htmlTable.append("<div class=\"col-xl-2 col-md-4 col-lg-3 col-sm-6\">");	
		 			//htmlTable.append("<img src='../../assets/img/icons/pdf.png' title='PDF' /><a class=\"font-weight-bold text-uppercase tx-15\" target=\"_new\" href=\"javascript:;\">"+commonUtil.columnTitles(key)+"</a>");
					/*htmlTable.append("<button type=\"button\" class=\"btn "+commonUtil.buttonColors(key)+" btn-rounded d-flex align-items-center\" onclick=\"viewDocument('"+docsData.get(key)+"')\">"+commonUtil.columnTitles(key)+"\r\n"
							+ "    <span class=\"bg-light ms-2 d-flex align-items-center justify-content-center\">\r\n"
							+ "        <i class=\"bi "+commonUtil.iconStyles(key)+" "+commonUtil.textColors(key)+"\"></i>\r\n"
							+ "    </span>\r\n"
							+ "</button>");*/
					/*htmlTable.append("<a class=\"button btn "+commonUtil.buttonColors(key)+" btn-rounded align-items-center\" target=\"_new\" href=\"/SAS/downloads/"+docsData.get(key)+"\">"+commonUtil.columnTitles(key)+"\r\n"
						+ " <span class=\"bg-light align-items-center justify-content-center\">\r\n"
						+ " <i class=\"bi "+commonUtil.iconStyles(key)+" "+commonUtil.textColors(key)+"\"></i>\r\n"
						+ " </span>\r\n"
						+ "</a>");*/
					htmlTable.append("<a class=\"btn btn-labeled "+commonUtil.buttonColors(key)+" btn-success\" target=\"_new\" href=\"/SAS/downloads/"+docsData.get(key)+"\">\r\n"
						+ "	<span class=\"btn-label\"><i class=\"bi text-light "+commonUtil.iconStyles(key)+" "+commonUtil.textColors(key)+"\"></i></span>"+commonUtil.columnTitles(key)+"</a>");
		 			htmlTable.append("</div>");
				});
				htmlTable.append("</div>");
			htmlTable.append("</div>");
		htmlTable.append("</div>");		
		
		return htmlTable.toString();
	}
	
	/*
	 * ravi coding
	 */
/*public String getLTMRegData(String regid, String realPath, String pageType) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String qry1="",paid="",doc_vrdt="";
		double ipaid=0;
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement("select to_char(max(TO_DATE(MODIFIED_DATE)),'DD/MM/YYYY') from reg_log where regno = ?");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        if (rs.next()) {
	        	doc_vrdt=rs.getString(1);
	        }
	        
        	sql = QueryBuilder.ltmDetails+" AND REGISTRATION_NUMBER=?";
        	regStmt = connection.prepareStatement(sql);
        	regStmt.setString(1, regid);
        	regrs = regStmt.executeQuery();
	        	
        	if(regrs.next()) {
        		if("0".contentEquals(commonUtil.removeNull(regrs.getString(12)))) {
        			qry1=QueryBuilder.sapqry+" where reg_no='"+regid+"'";
        		} else {
        			qry1=QueryBuilder.sapqry2+" and b.sap_wbsno='"+commonUtil.removeNull(regrs.getString(12))+"'";
        		}
        			PreparedStatement ps = connection.prepareStatement(qry1);
        			ResultSet saprs = ps.executeQuery();
        			if(saprs.next()){
        				paid=commonUtil.removeNull(saprs.getString(1));
        			}
        			ipaid=Double.parseDouble(paid);
        				if(ipaid<0){
        					ipaid=0;
        				}
        			saprs.close();
        			ps.close();
        		regData.put("nrregno", regrs.getString(1));
        		regData.put("consname", regrs.getString(2));
        		regData.put("nrregdate", regrs.getString(3));
        		regData.put("secname", regrs.getString(4));
        		regData.put("group", regrs.getString(6));
        		regData.put("catdesc", regrs.getString(7)+"-"+regrs.getString(8));
        		regData.put("subcat", regrs.getString(9));
        		regData.put("contload", regrs.getString(10));
        		regData.put("phase", regrs.getString(11));
        		regData.put("meterno", regrs.getString(17));
        		regData.put("cscremarks", regrs.getString(18));
        		regData.put("aeremarks", regrs.getString(19));
        		regData.put("aderemarks", regrs.getString(20));
        		regData.put("tobereleasedt", regrs.getString(21));
        		regData.put("aeremarksdt", regrs.getString(22));
        		regData.put("aderemarksdt", regrs.getString(23));
        		regData.put("status", regrs.getString(24));
        		regData.put("mobileno", regrs.getString(29));
        		regData.put("catcd", regrs.getString(7));
        		regData.put("estreq", regrs.getString(31));
        		
        		result.append(getRegHTMLTable("Registration Details",regData));
        	}
	       	        
	        estStmt = connection.prepareStatement(QueryBuilder.estQry);
        	estStmt.setString(1, regid);
        	
        	estrs = estStmt.executeQuery();
        	
        	if(estrs.next()) {
        		estData.put("estst", estrs.getString(2));
        		estData.put("sapestno", estrs.getString(1));
        		estData.put("estamt", estrs.getString(3));
        		estData.put("noofdayspending", estrs.getString(4));
        		estData.put("estsandt", estrs.getString(23));
        		estData.put("workcompdt", estrs.getString(24));
        		estData.put("estcreatedt", estrs.getString(25));
        		estData.put("eststcode", estrs.getString(26));
        		
        		result.append(getEstHTMLTable("Estimation Details",estData));
        	}
        	
        	//System.out.println(QueryBuilder.docsQry);
        	docsStmt = connection.prepareStatement(QueryBuilder.docsQry);
         	docsStmt.setString(1, regid);
         	
         	docsrs = docsStmt.executeQuery();
         	
         	if(docsrs.next()) {
         		if(docsrs.getInt(4)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(4), docsrs.getBlob(3), realPath, docsrs.getString(1), "APPL_FORM");
         			docsData.put("applform", url);
         			System.out.println(">>"+url);
         		}
         		if(docsrs.getInt(6)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(6), docsrs.getBlob(5), realPath, docsrs.getString(1), "ID_PROOF");
         			docsData.put("idproof", url);
         		}
         		if(docsrs.getInt(8)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(8), docsrs.getBlob(7), realPath, docsrs.getString(1), "CAST_CERTIFICATE");
         			docsData.put("castcert", url);
         		}
         		if(docsrs.getInt(10)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(10), docsrs.getBlob(9), realPath, docsrs.getString(1), "SALE_DEED");
         			docsData.put("saledeed", url);
         		}
         		if(docsrs.getInt(12)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(12), docsrs.getBlob(11), realPath, docsrs.getString(1), "PANCHAYAT_LETTER");
         			docsData.put("panchayatletter", url);
         		}
         		if(docsrs.getInt(14)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(14), docsrs.getBlob(13), realPath, docsrs.getString(1), "PHOTO");
         			docsData.put("photo", url);
         		}
         		if(docsrs.getInt(16)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(16), docsrs.getBlob(15), realPath, docsrs.getString(1), "WIRING_CERTIFICATE");
         			docsData.put("wiringcert", url);
         		}
         		if(docsrs.getInt(18)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(18), docsrs.getBlob(17), realPath, docsrs.getString(1), "OCC_CERT");
         			docsData.put("occcert", url);
         		}
         		if(docsrs.getInt(20)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(20), docsrs.getBlob(19), realPath, docsrs.getString(1), "NOC_PCB");
         			docsData.put("nocpcb", url);
         		}
         		if(docsrs.getInt(22)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(22), docsrs.getBlob(21), realPath, docsrs.getString(1), "NOV_LBODY");
         			docsData.put("novlbody", url);
         		}
         		if(docsrs.getInt(24)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(24), docsrs.getBlob(23), realPath, docsrs.getString(1), "PATTA_DOC");
         			docsData.put("pattadoc", url);
         		}
         		if(docsrs.getInt(26)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(26), docsrs.getBlob(25), realPath, docsrs.getString(1), "MA_TAX");
         			docsData.put("mhtax", url);
         		}
         		if(docsrs.getInt(28)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(28), docsrs.getBlob(27), realPath, docsrs.getString(1), "SSI_CERT");
         			docsData.put("ssicert", url);
         		}
         		if(docsrs.getInt(30)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(30), docsrs.getBlob(29), realPath, docsrs.getString(1), "CERT_80G");
         			docsData.put("cert80g", url);
         		}
         		if(docsrs.getInt(32)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(32), docsrs.getBlob(31), realPath, docsrs.getString(1), "MRO_CERT");
         			docsData.put("mrocert", url);
         		}
         		if(docsrs.getInt(34)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(34), docsrs.getBlob(33), realPath, docsrs.getString(1), "CEIG_CERT");
         			docsData.put("ceigcert", url);
         		}
         		if(docsrs.getInt(36)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(36), docsrs.getBlob(35), realPath, docsrs.getString(1), "FORM_I2");
         			docsData.put("formI2", url);
         		}
         		if(docsrs.getInt(38)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(38), docsrs.getBlob(37), realPath, docsrs.getString(1), "LOC_PHOTO");
         			docsData.put("locphoto", url);
         		}
         		if(docsrs.getInt(40)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(40), docsrs.getBlob(39), realPath, docsrs.getString(1), "SAS_PHOTO");
         			docsData.put("sasphoto", url);
         		}
         		if(docsrs.getInt(42)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(42), docsrs.getBlob(41), realPath, docsrs.getString(1), "SAS_TSTRPT");
         			docsData.put("saststrpt", url);
         		}
         		if(docsrs.getInt(44)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(44), docsrs.getBlob(43), realPath, docsrs.getString(1), "SAS_AGRMT");
         			docsData.put("sasagrmt", url);
         		}
         		docsData.forEach((key, value) -> System.out.println(key+" "+value));
         		result.append(getDocsHTMLTable("Submitted Documents List",docsData));
         	}
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}*/
	
public String getLTMRegData(String regid, String realPath, String pageType) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> lregData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		
		try (Connection connection = db.getConnection();){
				
		     
	       
			String decodedStatus = "DECODE(A.STATUS,null,decode(NVL(INT_PAY_REC,'N'),'Y','Pending from consumer for documents','Payment not made by consumer'),0,decode(NVL(INT_PAY_REC,'N'),"
	 				+ "'Y','Pending for documents verification',' '),4,'Pending for Material',5,'Service Released',11,'Service Released',6,'Rejected-'||a.REJ_REASON,1,"
	 				+ "'Extension of Work required',2,'Payment to be made by Customer',9,(case when C.METERS_CNT<>(a.NO_OF_CONN_REQD) then 'Pending for meter issue' "
	 				+ "else 'Pending for Release' end),10,(case when SAP_DTR_SD is not null and DTR_SD_PAID is null then 'DTR Performance Security tobe paid' "
	 				+ "else 'Material Bills to be submitted by Customer' end),8,'Extension of Work required') as STATUS";

			String qry = " SELECT distinct APARTMENT_NAME NAME, to_char(REGISTRATION_ON,'dd/mm/yyyy') REGDATE, decode(CREATED_BY,'7','ONLINE','CSC') REGTYPE, to_char(REJ_DATE,'dd-mm-yyyy') REJ_DATE, "
	     				+ "nvl(nof_domestic_con,0) DOM_CONN, nvl(domestic_load,0) DOM_LOAD, nvl(nof_commercial_con,0) COM_CONN, nvl(commercial_load,0) COM_LOAD, s.SECNAME, a.MOBILE, "
	     				+ "a.NATUREOFWORK EST_TYPE, P_ADDRESS1 CONNADD1, P_ADDRESS2 CONNADD2, P_AREA_NAME CONNADD3, P_ADDRESS4 CONNADD4,  " + decodedStatus
	     				+ ",nvl(A.STATUS,-1) STATUS_CODE,  nvl(sap_wbsno,'0') WBSNO, to_char(SAP_WCOM_DATE,'DD-MM-YYYY') WCOM_DATE,  nvl(sap_wbs_cost,0), a.OFFICE_ID, "
	     				+ "to_char(a.released_dt,'dd-mm-yyyy') REL_DATE, decode(A.STATUS,5,round(a.released_dt-REGISTRATION_ON),round(sysdate-REGISTRATION_ON)), "
	     				+ "decode(nvl(SAP_WCOM,nvl(SAP_REL,STATUS_DESC)),null,'Estimate not yet prepared by AE','AE','Estimate prepared but not forwarded by AE','APPR',"
	     				+ "PEND_DESC,'RJTD',PEND_DESC,'REL','Released to execute the work','WCOM','Work Completed','Estimate prepared and pending at '||PEND_DESC) EST_STATUS, "
	     				+ "decode(nvl(SAP_WCOM,nvl(SAP_REL,STATUS_DESC)),null,round(sysdate-REGISTRATION_ON),'AE',round(sysdate-REGISTRATION_ON),'APPR','0',"
	     				+ "'REL',round(sysdate-sap_rel_date),'WCOM',round(sysdate-sap_wcom_date),round(sysdate-sap_est_update)), sap_est_status, nvl(SAP_TOT,0), "
	     				+ "decode(SAP_WO_STATUS,'0','Waiting for DD amount realization','WADE','Pending at ADE','WJAO','Pending at JAO'),"
	     				+ "decode(SAP_WO_STATUS,'0',round(sysdate-bil_pay_update),'WADE',round(sysdate-SAP_WO_UPDATE),'WJAO',round(sysdate-SAP_WO_UPDATE),'0'),"
	     				+ "nvl(sap_rel,'0'),' ' delay_reason, decode(sap_est_status,'APPR',decode(bil_pay_update,null,round(sysdate-sap_est_update),round(bil_pay_update-sap_est_update)),'0')"
	     				+ "from apartment_connection a,sap_est_status b,(select apartment_id ,count(meter_slno) meters_cnt from connections group by apartment_id )C , all_section s "
	     				+ "WHERE c.APARTMENT_ID(+)=a.REGISTRATION_NUMBER and a.sap_est_status=b.STATUS_DESC(+) and a.RECORD_STATUS='ACTIVE' "
	     				+ "and a.office_id = s.uk_seccd  and REGISTRATION_NUMBER = ? ";
	        
	        //System.out.println("query..."+sql);
	        
        	regStmt = connection.prepareStatement(qry);
        	regStmt.setString(1, regid);
        	
        	regrs = regStmt.executeQuery();
	        	
        	if(regrs.next()) {
        		lregData.put("regno", regid);
        		lregData.put("consname", regrs.getString("NAME"));
        		lregData.put("nrregdate", regrs.getString("REGDATE"));
        		lregData.put("dom_conn", regrs.getString("DOM_CONN"));
        		lregData.put("dom_load", regrs.getString("DOM_LOAD"));
        		lregData.put("com_conn", regrs.getString("COM_CONN"));
        		lregData.put("com_load", regrs.getString("COM_LOAD"));
        		lregData.put("secname", regrs.getString("SECNAME"));
        		lregData.put("mobile", regrs.getString("MOBILE"));
        	
        		lregData.put("est_type", regrs.getString("EST_TYPE"));
        		lregData.put("connadd1", regrs.getString("CONNADD1"));
        		lregData.put("connadd2", regrs.getString("CONNADD2"));
        		lregData.put("connadd3", regrs.getString("CONNADD3"));
        		lregData.put("connadd4", regrs.getString("CONNADD4"));
        		lregData.put("rel_date", regrs.getString("REL_DATE"));
        		lregData.put("regtype", regrs.getString("REGTYPE"));
        		lregData.put("rej_date", regrs.getString("REJ_DATE"));
        		lregData.put("status", regrs.getString("STATUS"));
        		lregData.put("status_code", regrs.getString("STATUS_CODE"));
        		
        		
        		
        		result.append(getLTMRegHTMLTable("Registration Details",lregData));
        	}
	       	        
	    
        	//System.out.println(QueryBuilder.docsQry);
        	docsStmt = connection.prepareStatement(QueryBuilder.docsQry);
         	docsStmt.setString(1, regid);
         	
         	docsrs = docsStmt.executeQuery();
         	
         	if(docsrs.next()) {
         		
         		if(docsrs.getInt(4)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(4), docsrs.getBlob(3), realPath, docsrs.getString(1), "APPL_FORM");
         			docsData.put("applform", url);
         		}
         		if(docsrs.getInt(6)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(6), docsrs.getBlob(5), realPath, docsrs.getString(1), "ID_PROOF");
         			docsData.put("idproof", url);
         		}
         		if(docsrs.getInt(8)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(8), docsrs.getBlob(7), realPath, docsrs.getString(1), "CAST_CERTIFICATE");
         			docsData.put("castcert", url);
         		}
         		if(docsrs.getInt(10)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(10), docsrs.getBlob(9), realPath, docsrs.getString(1), "SALE_DEED");
         			docsData.put("saledeed", url);
         		}
         		if(docsrs.getInt(12)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(12), docsrs.getBlob(11), realPath, docsrs.getString(1), "PANCHAYAT_LETTER");
         			docsData.put("panchayatletter", url);
         		}
         		if(docsrs.getInt(14)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(14), docsrs.getBlob(13), realPath, docsrs.getString(1), "PHOTO");
         			docsData.put("photo", url);
         		}
         		if(docsrs.getInt(16)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(16), docsrs.getBlob(15), realPath, docsrs.getString(1), "WIRING_CERTIFICATE");
         			docsData.put("wiringcert", url);
         		}
         		if(docsrs.getInt(18)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(18), docsrs.getBlob(17), realPath, docsrs.getString(1), "OCC_CERT");
         			docsData.put("occcert", url);
         		}
         		if(docsrs.getInt(20)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(20), docsrs.getBlob(19), realPath, docsrs.getString(1), "NOC_PCB");
         			docsData.put("nocpcb", url);
         		}
         		if(docsrs.getInt(22)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(22), docsrs.getBlob(21), realPath, docsrs.getString(1), "NOV_LBODY");
         			docsData.put("novlbody", url);
         		}
         		if(docsrs.getInt(24)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(24), docsrs.getBlob(23), realPath, docsrs.getString(1), "PATTA_DOC");
         			docsData.put("pattadoc", url);
         		}
         		if(docsrs.getInt(26)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(26), docsrs.getBlob(25), realPath, docsrs.getString(1), "MA_TAX");
         			docsData.put("mhtax", url);
         		}
         		if(docsrs.getInt(28)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(28), docsrs.getBlob(27), realPath, docsrs.getString(1), "SSI_CERT");
         			docsData.put("ssicert", url);
         		}
         		if(docsrs.getInt(30)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(30), docsrs.getBlob(29), realPath, docsrs.getString(1), "CERT_80G");
         			docsData.put("cert80g", url);
         		}
         		if(docsrs.getInt(32)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(32), docsrs.getBlob(31), realPath, docsrs.getString(1), "MRO_CERT");
         			docsData.put("mrocert", url);
         		}
         		if(docsrs.getInt(34)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(34), docsrs.getBlob(33), realPath, docsrs.getString(1), "CEIG_CERT");
         			docsData.put("ceigcert", url);
         		}
         		if(docsrs.getInt(36)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(36), docsrs.getBlob(35), realPath, docsrs.getString(1), "FORM_I2");
         			docsData.put("formI2", url);
         		}
         		if(docsrs.getInt(38)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(38), docsrs.getBlob(37), realPath, docsrs.getString(1), "LOC_PHOTO");
         			docsData.put("locphoto", url);
         		}
         		if(docsrs.getInt(40)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(40), docsrs.getBlob(39), realPath, docsrs.getString(1), "SAS_PHOTO");
         			docsData.put("sasphoto", url);
         		}
         		if(docsrs.getInt(42)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(42), docsrs.getBlob(41), realPath, docsrs.getString(1), "SAS_TSTRPT");
         			docsData.put("saststrpt", url);
         		}
         		if(docsrs.getInt(44)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(44), docsrs.getBlob(43), realPath, docsrs.getString(1), "SAS_AGRMT");
         			docsData.put("sasagrmt", url);
         		}
         		docsData.forEach((key, value) -> System.out.println(key+" "+value));
         		result.append(getDocsHTMLTable("Submitted Documents List",docsData));
         	}
         	
         	connection.close();
	        
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}

@SuppressWarnings("rawtypes")
private String getLTMRegHTMLTable(String title, HashMap lregData) {
	CommonUtils commonUtil = new CommonUtils();
	StringBuilder htmlTable = new StringBuilder();
	htmlTable.append("<div>");
	htmlTable.append("<h6 class=\"card-content-label mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
	//System.out.println("********************"+htmlTable.toString());
	htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
		htmlTable.append("<tbody>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregno")+"</th>");
				htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+lregData.get("regno")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("consname")+"</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("consname")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregdate")+"</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("nrregdate")+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("secname")+"</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("secname")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Domestic Connections</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("dom_conn")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Domestic Load</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("dom_load")+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
				htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("mobileno")+"</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("mobile")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Commercial Connections</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("com_conn")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Commercial Load</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("com_load")+"</td>");
			htmlTable.append("</tr>");
			htmlTable.append("<tr>");
			
				htmlTable.append("<th class=\"wd-15p\">Address</th>");
				htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+lregData.get("connadd1")+","+lregData.get("connadd2")+","+lregData.get("connadd3")+","+lregData.get("connadd4")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Status</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("status")+"</td>");
				htmlTable.append("<th class=\"wd-15p\">Release date</th>");
				htmlTable.append("<td class=\"wd-20p\">"+lregData.get("rel_date")+"</td>");
				
				
			htmlTable.append("</tr>");
		
			
	htmlTable.append("</tbody>");	
	htmlTable.append("</table>");
	htmlTable.append("<input type=\"hidden\" name=\"catcd\" id=\"catcd\" value=\""+lregData.get("catcd")+"\" />");
	htmlTable.append("<input type=\"hidden\" name=\"estreq\" id=\"estreq\" value=\""+lregData.get("estreq")+"\" />");
	htmlTable.append("</div>");
	return htmlTable.toString();
}

	public List<AddConnections> getAddConnectionList(User user) throws SQLException, Exception {
		//System.out.println("Query................"+documentsverification);
		if(user.getSasusertype().equals("5"))
			offtype = "b.uk_seccd='"+user.getSasseccd()+"' and A.office_id='"+user.getSasseccd()+"'";
		
		//if(user.getSasusertype().equals("6"))
		//	offtype = "C.SUBDIVCD";
		
		List <AddConnections> docList = new ArrayList<AddConnections>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.addconnectionlist+offtype);) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	//String nrregno = rs.getString("nrregno");
	        	docList.add(new AddConnections(rs.getString("REGISTRATION_NUMBER"),rs.getString("REGISTRATION_ON"),rs.getString("APARTMENT_NAME"),rs.getString("NO_OF_FLATS"),rs.getString("NO_OF_CONN_REQD")));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
public String getAddConData(String regid, String realPath, String pageType) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.addcondetails+" order by REGISTRATION_ON desc");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	                	
        	if(rs.next()) {
        		regData.put("regno", rs.getString(1));
        		regData.put("apartment_name", rs.getString(2));
        		regData.put("builder_name", rs.getString(3));
        		regData.put("address_1", rs.getString(4));
        		regData.put("address_2", rs.getString(5));
        		regData.put("address_4", rs.getString(6));
        		regData.put("area_name", rs.getString(7));
        		regData.put("no_of_flats", rs.getString(8));
        		regData.put("no_of_conn_reqd", rs.getString(9));
        		regData.put("registration_on", rs.getString(10));
        		regData.put("natureofwork", rs.getString(11));
        		regData.put("loaddetails_doc", rs.getString(12));
        		regData.put("req_domestic_load", rs.getString(13));
        		regData.put("req_commercial_load", rs.getString(14));
        		regData.put("domestic_load", rs.getString(15));
        		regData.put("commercial_load", rs.getString(16));
        		regData.put("nof_commercial_con", rs.getString(17));
        		regData.put("nof_domestic_con", rs.getString(18));
        		regData.put("domesticadded", rs.getString(19));
        		regData.put("commercialadded", rs.getString(20));
        		regData.put("dbl_bedrm_scm", rs.getString(21));
        		
        		
        	}
	       	      
        	String catList="";
        	int added_con_count=0;
        	PreparedStatement ps = connection.prepareStatement(QueryBuilder.cat_qry);
        	ResultSet rscat = ps.executeQuery();
        	while(rscat.next()) {
        		catList=catList+"<option value='"+rscat.getString(1)+"'>"+rscat.getString(2)+"</option>";
        	}
        	
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.con_count_qry);
        	ps1.setString(1, regid);
        	ResultSet rscount = ps1.executeQuery();
        	if(rscount.next()) {
        		added_con_count=rscount.getInt(1);
        	}
        	
        	result.append(getAddConHTMLTable("Registration Details", regData, catList, added_con_count));
        	
        	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}


@SuppressWarnings("rawtypes")
	private String getAddConHTMLTable(String title, HashMap regData, String catlist, int added_con_count) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregno")+"</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("regno")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("consname")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("apartment_name")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregdate")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("registration_on")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nofcons")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("no_of_conn_reqd")+"</td>");
						
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("reqdomesticload")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("req_domestic_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("reqcommload")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("req_commercial_load")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("domesticloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("domestic_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("commloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("commercial_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
			htmlTable.append("</table>");
		
			htmlTable.append("<BR/>");
			
			htmlTable.append("<table class=\"table table-bordered table-striped table-hover border-bottom mb-0 border dataTable no-footer\">");
			htmlTable.append("<thead>");
				htmlTable.append("<tr role='row'>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("slno")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("flatno")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("name")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("mobileno")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("catdesc")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("conload")+"</th>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("phase")+"</th>");
				htmlTable.append("</tr>");
			htmlTable.append("</thead>");
			htmlTable.append("<tbody>");
			
			int tnofcns=Integer.parseInt(regData.get("no_of_conn_reqd").toString());
			int reqcns=tnofcns-added_con_count;
			if(reqcns>20)
				reqcns=20;
			
			
			for(int k=1;k<=reqcns;k++) {
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+k+"</th>");
					htmlTable.append("<td class=\"wd-20p\"><input type='number' name='flat_no"+k+"' id='flat_no"+k+"' style='width:100px;' maxlength='5'></td>");
					htmlTable.append("<td class=\"wd-15p\"><input type='text' name='name"+k+"' id='name"+k+"' value='"+regData.get("apartment_name")+"'></td>");
					htmlTable.append("<td class=\"wd-20p\"><input type='text' name='phone"+k+"' id='phone"+k+"' style='width:90px;'></td>");
					htmlTable.append("<td class=\"wd-15p\"><select name='category"+k+"' id='category"+k+"'>"+catlist+"</select></td>");
					htmlTable.append("<td class=\"wd-20p\"><input type='text' name='ContractedLoad"+k+"' id='ContractedLoad"+k+"' style='width:100px;' onBlur=\"getPhase('"+k+"',htis.value)\"></td>");
					htmlTable.append("<td class=\"wd-20p\"><span id='phaseopt"+k+"'><select name='phone"+k+"' id='phone"+k+"'><option value=''>Select</option></select></span></td>");
				htmlTable.append("</tr>");
			}
			htmlTable.append("</tbody>");
			htmlTable.append("</table>");
		htmlTable.append("</div>");
		htmlTable.append("<div class='form-group row mb-0 mt-2 justify-content-center' id='btnSubmit2' style='margin:20px;'>"); 
				htmlTable.append("<div class='col text-center'>"); 
						htmlTable.append("<button class='btn ripple btn-primary' type='submit' id='btn-search'>Submit</button>"); 
				htmlTable.append("</div>"); 
		htmlTable.append("</div>"); 
		return htmlTable.toString();
	}


	public List<LTMSendOTPtoConsumer> getOTPtoConsumer(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <LTMSendOTPtoConsumer> docList = new ArrayList<LTMSendOTPtoConsumer>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmSendOTPto_Consumer+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	docList.add(new LTMSendOTPtoConsumer(rs.getString("REGISTRATION_NUMBER"),rs.getString("REGISTRATION_ON"),rs.getString("APARTMENT_NAME"),rs.getString("NO_OF_FLATS"),rs.getString("NO_OF_CONN_REQD"),rs.getString("MOBILE"),rs.getString("DOC_SUBMIT")));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	
public String getLTMDocOtpApproval(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String erouser="";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmSendOTPConsumerDetails+" and registration_number=?");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        
	        PreparedStatement ps = connection.prepareStatement(QueryBuilder.search_erouser);
	        ps.setString(1, user.getSasseccd());
	        ResultSet rsUser = ps.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
        	if(rs.next()) {
        		regData.put("regno", rs.getString(1));
        		regData.put("apartment_name", rs.getString(2));
        		regData.put("builder_name", rs.getString(3));
        		regData.put("area_name", rs.getString(4));
        		regData.put("no_of_flats", rs.getString(5));
        		regData.put("no_of_conn_reqd", rs.getString(6));
        		regData.put("registration_on", rs.getString(7));
        		regData.put("natureofwork", rs.getString(8));
        		regData.put("domestic_load", rs.getString(9));
        		regData.put("commercial_load", rs.getString(10));
        	}
	       	      
        	String catList="";
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.cat_qry);
        	ResultSet rscat = ps1.executeQuery();
        	while(rscat.next()) {
        		catList=catList+"<option value='"+rscat.getString(1)+"'>"+rscat.getString(2)+"</option>";
        	}
        	
        	result.append(getLtmOtpValidationHTMLTable("Registration Details",regData,catList));
        	
        	docsStmt = connection.prepareStatement(QueryBuilder.docsQry);
         	docsStmt.setString(1, regid);
         	
         	docsrs = docsStmt.executeQuery();
         	
         	if(docsrs.next()) {
         		
         		if(docsrs.getInt(4)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(4), docsrs.getBlob(3), realPath, docsrs.getString(1), "APPL_FORM");
         			docsData.put("applform", url);
         		}
         		if(docsrs.getInt(6)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(6), docsrs.getBlob(5), realPath, docsrs.getString(1), "ID_PROOF");
         			docsData.put("idproof", url);
         		}
         		if(docsrs.getInt(8)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(8), docsrs.getBlob(7), realPath, docsrs.getString(1), "CAST_CERTIFICATE");
         			docsData.put("castcert", url);
         		}
         		if(docsrs.getInt(10)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(10), docsrs.getBlob(9), realPath, docsrs.getString(1), "SALE_DEED");
         			docsData.put("saledeed", url);
         		}
         		if(docsrs.getInt(12)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(12), docsrs.getBlob(11), realPath, docsrs.getString(1), "PANCHAYAT_LETTER");
         			docsData.put("panchayatletter", url);
         		}
         		if(docsrs.getInt(14)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(14), docsrs.getBlob(13), realPath, docsrs.getString(1), "PHOTO");
         			docsData.put("photo", url);
         		}
         		if(docsrs.getInt(16)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(16), docsrs.getBlob(15), realPath, docsrs.getString(1), "WIRING_CERTIFICATE");
         			docsData.put("wiringcert", url);
         		}
         		if(docsrs.getInt(18)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(18), docsrs.getBlob(17), realPath, docsrs.getString(1), "OCC_CERT");
         			docsData.put("occcert", url);
         		}
         		if(docsrs.getInt(20)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(20), docsrs.getBlob(19), realPath, docsrs.getString(1), "NOC_PCB");
         			docsData.put("nocpcb", url);
         		}
         		if(docsrs.getInt(22)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(22), docsrs.getBlob(21), realPath, docsrs.getString(1), "NOV_LBODY");
         			docsData.put("novlbody", url);
         		}
         		if(docsrs.getInt(24)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(24), docsrs.getBlob(23), realPath, docsrs.getString(1), "PATTA_DOC");
         			docsData.put("pattadoc", url);
         		}
         		if(docsrs.getInt(26)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(26), docsrs.getBlob(25), realPath, docsrs.getString(1), "MA_TAX");
         			docsData.put("mhtax", url);
         		}
         		if(docsrs.getInt(28)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(28), docsrs.getBlob(27), realPath, docsrs.getString(1), "SSI_CERT");
         			docsData.put("ssicert", url);
         		}
         		if(docsrs.getInt(30)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(30), docsrs.getBlob(29), realPath, docsrs.getString(1), "CERT_80G");
         			docsData.put("cert80g", url);
         		}
         		if(docsrs.getInt(32)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(32), docsrs.getBlob(31), realPath, docsrs.getString(1), "MRO_CERT");
         			docsData.put("mrocert", url);
         		}
         		if(docsrs.getInt(34)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(34), docsrs.getBlob(33), realPath, docsrs.getString(1), "CEIG_CERT");
         			docsData.put("ceigcert", url);
         		}
         		if(docsrs.getInt(36)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(36), docsrs.getBlob(35), realPath, docsrs.getString(1), "FORM_I2");
         			docsData.put("formI2", url);
         		}
         		if(docsrs.getInt(38)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(38), docsrs.getBlob(37), realPath, docsrs.getString(1), "LOC_PHOTO");
         			docsData.put("locphoto", url);
         		}
         		if(docsrs.getInt(40)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(40), docsrs.getBlob(39), realPath, docsrs.getString(1), "SAS_PHOTO");
         			docsData.put("sasphoto", url);
         		}
         		if(docsrs.getInt(42)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(42), docsrs.getBlob(41), realPath, docsrs.getString(1), "SAS_TSTRPT");
         			docsData.put("saststrpt", url);
         		}
         		if(docsrs.getInt(44)>0) {
         			String url = commonUtil.readBlob(docsrs.getInt(44), docsrs.getBlob(43), realPath, docsrs.getString(1), "SAS_AGRMT");
         			docsData.put("sasagrmt", url);
         		}
         		docsData.forEach((key, value) -> System.out.println(key+" "+value));
         		result.append(getDocsHTMLTable("Submitted Documents List",docsData));
         	}
        	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}


	@SuppressWarnings("rawtypes")
	private String getLtmOtpValidationHTMLTable(String title, HashMap regData, String catlist) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregno")+"</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("regno")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("consname")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("apartment_name")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("buildername")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("builder_name")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("areaname")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("area_name")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nofflats")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("no_of_flats")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nofcons")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("no_of_conn_reqd")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");	
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregdate")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("registration_on")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("natureofwork")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("natureofwork")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("domesticloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("domestic_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("commloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("commercial_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
			htmlTable.append("</table>");
				
		return htmlTable.toString();
	}
	
	
	
	public String sendSMStoLTMConsumer(String regid, String empid, String lmphone, User user) throws SQLException, Exception {
		String subs="",message="",otp="",line_staff_phno="",regmbno="",lineotp="", meterno="",res_sms1="",res_sms="";
		if(user.getSasusertype().equals("5"))
			subs="AE";
		
		if(user.getSasusertype().equals("6"))
			subs="ADE";
		
		
		try (Connection connection = db.getConnection();) {
			
	        /*PreparedStatement ps = connection.prepareStatement("select round(DBMS_RANDOM.value*1000) otp from dual");
	        ResultSet rs=ps.executeQuery();
	        if(rs.next()){
				otp=rs.getString(1);
			}*/
			int randomPIN=2205;
			randomPIN = (int) (Math.random() * 9000) + 1000;
			otp = String.valueOf(randomPIN);
	        
	        PreparedStatement preparedStatement = connection.prepareStatement("update APARTMENT_CONNECTION set SAS_OTP='"+otp+"',SAS_AE_UPDT=SYSDATE where REGISTRATION_NUMBER='"+regid+"'");
	        preparedStatement.executeUpdate();
	        
	        String cellno="";
	        PreparedStatement ps2=connection.prepareStatement("select nvl(MOBILE,'0') from APARTMENT_CONNECTION where REGISTRATION_NUMBER='"+regid+"'");
	        ResultSet rs1=ps2.executeQuery();
	        if(rs1.next()){
	        	cellno=rs1.getString(1);
			}
	        //cellno="9676290789";
			if(!cellno.equals("0")&&!cellno.equals("9999999999")){
				lineotp=lmphone+" and OTP is"+otp;
				res_sms=SmsMsg.sendMsg(cellno, "Dear Consumer,Meter is alloted against Registration No."+regid+".Please provide Your OTP No to our lineman.Lineman Phone No is "+lineotp+".");
			 }
	        
	        if(!lmphone.equals("0")&&!lmphone.equals("9999999999")){
	    		regmbno=regid+" / "+cellno;
	    		res_sms1=SmsMsg.sendMsg(lmphone, "Inspect and fix the meter No."+meterno+" with in 48 hours at consumer premises. The Registration No. and Consumer Mobile No. details are "+regmbno+". ");
	    	}
	        
	        PreparedStatement ps1=connection.prepareStatement("insert into pmt_sms_link(regno,sms_res,SMS_DATE,SENTBY) values('"+regid+"','"+res_sms1+"',sysdate,'"+subs+"')");
	        ps1.executeQuery();
	        
	        message="YES";
	        
			connection.close();
	        //rs.close();
	        //ps.close();
		} catch (SQLException e) {
			message="NO";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
	
	
	
	public List<LTMtestReport> getTestReportInfo(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <LTMtestReport> docList = new ArrayList<LTMtestReport>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmtestreport+offtype+"='"+user.getSasseccd()+"'");) {
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {System.out.println("rs==>"+rs.getString("REGISTRATION_NUMBER"));
	        		
			        List<String> meterList = new ArrayList<String>();
		    		PreparedStatement preparedStatement1 = connection.prepareStatement(QueryBuilder.ltmMeterNos+" and APARTMENT_ID=?");
		        	preparedStatement1.setString(1, rs.getString("REGISTRATION_NUMBER"));
		            ResultSet rs1 = preparedStatement1.executeQuery();
		           
		            while (rs1.next()) {
		            	meterList.add(rs1.getString("METER_SLNO"));   	    		
		            }
	        
	        		docList.add(new LTMtestReport(rs.getString("REGISTRATION_NUMBER"),rs.getString("REGISTRATION_ON"),rs.getString("APARTMENT_NAME"),rs.getString("NO_OF_FLATS"),rs.getString("NO_OF_CONN_REQD"),rs.getString("MOBILE"),rs.getString("BUILDER_NAME"), meterList));
	        	}
	        //System.out.println("docList::"+docList);
	        
	        
	        rs.close();
	        preparedStatement.close();
	        connection.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	
	/*public List<String> getMeters(String regno) {

        List<String> meterno = new ArrayList<String>();      
        try (Connection con = db.getConnection();
            
        	PreparedStatement preparedStatement1 = con.prepareStatement(QueryBuilder.ltmMeterNos+" and APARTMENT_ID=?");) {
        	preparedStatement1.setString(1, regno);
            //System.out.println("Regno>>>"+regno+"ltmMeterNos>>>"+QueryBuilder.ltmMeterNos+" and APARTMENT_ID=?");           
            ResultSet rs1 = preparedStatement1.executeQuery();
           
            while (rs1.next()) {
            	meterno.add(rs1.getString("METER_SLNO"));   	    		
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        } catch (Exception e1) {
			e1.printStackTrace();
		}
        System.out.println("meterno...."+meterno);
        return meterno;
    }*/
	
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
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return regData;
	}


	public HashMap getDashboardInfo(int menuid, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		String erouser="";
		
		if(menuid==1) {
			sql=QueryBuilder.dashboardInfo;
		} else {
			sql=QueryBuilder.dashboardInfo;
		}
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.dashboardInfo);) {
			//System.out.println("Connection..."+QueryBuilder.dashboardInfo);
			preparedStatement.setString(1, user.getSasseccd());
			/*preparedStatement.setString(2, user.getSasseccd());
			preparedStatement.setString(3, user.getSasseccd());
			preparedStatement.setString(4, user.getSasseccd());*/
			ResultSet rs = preparedStatement.executeQuery();
	        
	    	if(rs.next()) {
	    		regData.put("totalreg", rs.getString(1));
	    		regData.put("pending_atsap", rs.getString(2));
	    		regData.put("pending_atcons", rs.getString(3));
	    		regData.put("inprogress", rs.getString(4));
	    	}
	    	//result.append(getLtmOtpValidationHTMLTable("Registration Details",regData,catList));
	    	connection.close();
	        rs.close();
	        preparedStatement.close();
	    	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return regData;
	}
	
public String getDashboardBarChartInfo(int menuid, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		String barchartinfo="";
		String labels="",nr_data="",ltm_data="";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.barChartInfo);) {
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	        
	    	while(rs.next()) {
	    		if(!"".equals(labels))
	    			labels=labels+","+rs.getString(2);
	    		else
	    			labels=rs.getString(2);
	    		
	    		if(!"".equals(nr_data))
	    			nr_data=nr_data+","+rs.getString(3);
	    		else
	    			nr_data=rs.getString(3);
	    		
	    		if(!"".equals(ltm_data))
	    			ltm_data=ltm_data+","+rs.getString(4);
	    		else
	    			ltm_data=rs.getString(4);
	    		
	    	}
	    	barchartinfo=labels+"@@"+nr_data+"@@"+ltm_data;
	    	//result.append(getLtmOtpValidationHTMLTable("Registration Details",regData,catList));
	    	connection.close();
	        rs.close();
	        preparedStatement.close();
	    	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return barchartinfo;
	}

	public List<LTMSendOTPtoConsumer> getOTPUpdationList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <LTMSendOTPtoConsumer> otpList = new ArrayList<LTMSendOTPtoConsumer>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmOtpUpdation+" and "+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("query..."+QueryBuilder.documentsverification+offtype+"='"+user.getSasseccd()+"'");	
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new LTMSendOTPtoConsumer(rs.getString("REGISTRATION_NUMBER"),rs.getString("REGISTRATION_ON"),rs.getString("APARTMENT_NAME"),rs.getString("NO_OF_FLATS"),rs.getString("NO_OF_CONN_REQD"),rs.getString("MOBILE"),rs.getString("DOC_SUBMIT")));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return otpList;
	}
	
	
	public String uploadTestReport(UploadTestReportModel utr, User user, String realPath) throws SQLException, Exception {
		String subs="",message="",otp="",line_staff_phno="",regmbno="",lineotp="", meterno="",res_sms1="",res_sms="";
		if(user.getSasusertype().equals("5"))
			subs="AE";
		
		if(user.getSasusertype().equals("6"))
			subs="ADE";
		
		int i=0,x=0,x1=0;
		
		try (Connection connection = db.getConnection();) {
			
			int randomPIN=2205;
			randomPIN = (int) (Math.random() * 9000) + 1000;
			otp = String.valueOf(randomPIN);
	        
	        PreparedStatement preparedStatement = connection.prepareStatement("Update APARTMENT_CONNECTION SET SAS_IMAGE='1' where REGISTRATION_NUMBER=?");
	        preparedStatement.setString(1, utr.getReg_Id());
	        preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2=connection.prepareStatement("Update spddocs.DOCUMENTS SET SAS_TSTRPT=?,SAS_PHOTO=?,sas_flag='S' where REGNO=?");
	        
	        FileStatements fs = new FileStatements(realPath,utr.getReg_Id());
	        MultipartFile meter_img=utr.getMeter_img();
	        MultipartFile testreport=utr.getTest_report();
			FileInputStream fis_meter_img = fs.FileRead(meter_img);
			FileStatements fs1 = new FileStatements(realPath,utr.getReg_Id());
			FileInputStream fis_test_report = fs.FileRead(testreport);
			
	        ps2.setBinaryStream(1,fis_test_report,fis_test_report.available());
	        ps2.setBinaryStream(2,fis_meter_img,fis_meter_img.available());
	        ps2.setString(3, utr.getReg_Id());
	        ps2.executeUpdate();
	        
	        PreparedStatement ps3=connection.prepareStatement("Update DOCUMENTS SET SAS_TSTRPT=?,SAS_PHOTO=?,sas_flag='S' where REGNO=?");
	        
	        ps3.setBinaryStream(1,fis_test_report,fis_test_report.available());
	        ps3.setBinaryStream(2,fis_meter_img,fis_meter_img.available());
	        ps3.setString(3, utr.getReg_Id());
	        x=ps3.executeUpdate();
	        
	        if(x==0) {
		        PreparedStatement ps1=connection.prepareStatement("insert into DOCUMENTS (REGNO,SAS_TSTRPT,SAS_PHOTO,ENTRY_DATE,sas_flag) VALUES(?,?,?,SYSDATE,'S')");
		        ps1.setString(1,utr.getReg_Id());
		        ps1.setBinaryStream(2,fis_test_report,fis_test_report.available());
		        ps1.setBinaryStream(3,fis_meter_img,fis_meter_img.available());
		        ps1.executeUpdate();
		        
		        PreparedStatement ps4=connection.prepareStatement("insert into  spddocs.DOCUMENTS (REGNO,SAS_TSTRPT,SAS_PHOTO,ENTRY_DATE,sas_flag) VALUES(?,?,?,SYSDATE,'S')");
		        ps4.setString(1,utr.getReg_Id());
		        ps4.setBinaryStream(2,fis_test_report,fis_test_report.available());
		        ps4.setBinaryStream(3,fis_meter_img,fis_meter_img.available());
		        ps4.executeUpdate();
		   }
	        
	        message="YES";
	        
			connection.close();
	        //rs.close();
	        //ps.close();
		} catch (SQLException e) {
			message="NO";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
	
	public List<LTMSendOTPtoConsumer> getToBeReleasedList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		String excessmeterString="NO";
		List <LTMSendOTPtoConsumer> otpList = new ArrayList<LTMSendOTPtoConsumer>();
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmtobeReleased+" and "+offtype+"='"+user.getSasseccd()+"'");) {
			//System.out.println("toberelesed Query>>>>>"+QueryBuilder.ltmtobeReleased+" and "+offtype+"='"+user.getSasseccd()+"'");
			ResultSet rs = preparedStatement.executeQuery();
	            //SELECT REGISTRATION_NUMBER,APARTMENT_NAME, BUILDER_NAME, ADDRESS_1, ADDRESS_2, ADDRESS_4, AREA_NAME, NO_OF_FLATS, NO_OF_CONN_REQD, to_char(REGISTRATION_ON,'dd/mm/yyyy'), NATUREOFWORK,LOADDETAILS_DOC,b.secname,a.MOBILE,(case when D.REGNO is null then 'NO' else 'YES' end) DOC_SUBMIT  FROM APARTMENT_CONNECTION a , all_section b,DOCUMENTS D where RECORD_STATUS='ACTIVE'  and a.office_id=b.uk_seccd AND to_date(REGISTRATION_ON)>='24-oct-13' and STATUS=9 and IS_FLATSADDED='YES' AND REGISTRATION_NUMBER=REGNO and sas_flag='S' AND  NVL(SAS_IMAGE,0)=1  and registration_number in(select unique apartment_id from connections where METER_SLNO is not null and scno is null) and A.office_id='248'
	        while (rs.next()) {
	        	PreparedStatement ps = connection.prepareStatement(QueryBuilder.excessMeterCheck+" and REGISTRATION_NUMBER=?");
	        	ps.setString(1, rs.getString("REGISTRATION_NUMBER"));
	        	ResultSet rs1=ps.executeQuery();
	        	if(rs1.next()) {excessmeterString="YES";}
	        	otpList.add(new LTMSendOTPtoConsumer(rs.getString("REGISTRATION_NUMBER"),rs.getString("REGISTRATION_ON"),rs.getString("APARTMENT_NAME"),rs.getString("NO_OF_FLATS"),rs.getString("NO_OF_CONN_REQD"),rs.getString("MOBILE"),excessmeterString));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return otpList;
	}
	
	
public String getLTMtobeReleasedDetails(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String erouser="";
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ltmtobeReleasedDetails+" and registration_number=?");) {
			//System.out.println("ltmSendOTPConsumerDetails...Executed>>>"+QueryBuilder.ltmSendOTPConsumerDetails);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        
	        PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT EROUSERORA  FROM EROS,(SELECT uk_seccd,secname,A.EROCD FROM all_section  a,all_subdivision b WHERE uk_subdiv=subdiv   and uk_seccd=?) WHERE ERONO=EROCD");
	        ps.setString(1, user.getSasseccd());
	        ResultSet rsUser = ps.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
        	if(rs.next()) {
        		regData.put("regno", rs.getString(1));
        		regData.put("apartment_name", rs.getString(2));
        		regData.put("builder_name", rs.getString(3));
        		
        		regData.put("address_1", rs.getString(4));
        		regData.put("address_2", rs.getString(5));
        		regData.put("address_4", rs.getString(6));
        		
        		regData.put("area_name", rs.getString(7));
        		regData.put("no_of_flats", rs.getString(8));
        		regData.put("no_of_conn_reqd", rs.getString(9));
        		regData.put("registration_on", rs.getString(10));
        		regData.put("natureofwork", rs.getString(11));
        		regData.put("domestic_load", rs.getString(13));
        		regData.put("commercial_load", rs.getString(14));
        		
        		regData.put("catid_1_conload", rs.getString(15));
        		regData.put("catid_2_conload", rs.getString(16));
        		regData.put("officeid", rs.getString(17));
        		regData.put("regdt_flag", rs.getString(18));
        		regData.put("occ_cert", rs.getString(19));
        	}
	       	      
        	String catList="";
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.cat_qry);
        	ResultSet rscat = ps1.executeQuery();
        	while(rscat.next()) {
        		catList=catList+"<option value='"+rscat.getString(1)+"'>"+rscat.getString(2)+"</option>";
        	}
        	
        	result.append(getLtmTobeReleasedHTMLTable("Registration Details",regData,catList));
        	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}

	@SuppressWarnings("rawtypes")
	private String getLtmTobeReleasedHTMLTable(String title, HashMap regData, String catlist) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregno")+"</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("regno")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("consname")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("apartment_name")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("buildername")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("builder_name")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("areaname")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("area_name")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nofflats")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("no_of_flats")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nofcons")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("no_of_conn_reqd")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");	
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("nrregdate")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("registration_on")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("natureofwork")+"</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("natureofwork")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("domesticloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("domestic_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("commloadadded")+"</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("commercial_load")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"><input type='hidden' name='regdt_flag' id='regdt_flag' value='"+regData.get("regdt_flag")+"'/></td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
			htmlTable.append("</table>");
				
		return htmlTable.toString();
	}

	public HashMap<String, String> getAreaList(User user) throws SQLException, Exception {
		final HashMap<String, String> area = new HashMap<>();
		String erouser=getEroUser(user.getSasseccd());
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT AREACODE,AREACODE||' '||AREANAME FROM ALL_SECTION,"+erouser+".AREA@intcsc_dblink WHERE AREASECCD=SECCD AND UK_SECCD=? ORDER BY AREACODE");) {
			//System.out.println("Connection..."+connection);
			preparedStatement.setString(1, user.getSasseccd());
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	area.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return area;
	}
	
	public String getEroUser(String seccode) throws SQLException, Exception {
		String erouser="";
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.getEroUser);) {
			preparedStatement.setString(1, seccode);
	        ResultSet rs = preparedStatement.executeQuery();
	        if (rs.next()) {
	        	erouser=rs.getString(1);
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return erouser;
	}
	
	public HashMap<String, String> getFeederList(User user) throws SQLException, Exception {
		final HashMap<String, String> feederList = new HashMap<>();
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.getFeederList);) {
			//System.out.println("Connection..."+connection);
			preparedStatement.setString(1, user.getSasseccd());
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	feederList.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return feederList;
	}
	
	public HashMap<String, String> getStructureList(User user, String feedercode) throws SQLException, Exception {
		final HashMap<String, String> structures = new HashMap<>();
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.getStructures);) {
			//System.out.println("Connection..."+connection);
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, feedercode);
	        ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	structures.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return structures;
	}
	
	public String getAmountDetails(String regid) throws SQLException, Exception {
		String amt_tobe_paid="",amt_paid="";
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.tobePaidAmt);) {
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        if (rs.next()) {
	        	amt_tobe_paid=rs.getString(2);
	        }
	        
	        PreparedStatement ps = connection.prepareStatement(QueryBuilder.paidAmt);
	        ps.setString(1, regid);
	        ResultSet rs1 = ps.executeQuery();
	        if (rs1.next()) {
	        	amt_paid=rs1.getString(2);
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return amt_tobe_paid+"@@"+amt_paid;
	}
	
	
public String getFlatDetails(String regid, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		String cscno="";
		PreparedStatement regStmt = null;
		ResultSet regrs = null;
		//HashMap<String,String> regData = new HashMap<String,String>();
		List <LTMtobeRelease> regsList = new ArrayList<LTMtobeRelease>();
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.flatData);) {
			//System.out.println(">>>>>>>>>>>user.getSasusertype()=="+user.getSasusertype());
			//System.out.println(">>>>>>>>>>>user.getSasseccd()=="+user.getSasseccd());
			preparedStatement.setString(1, regid);
			preparedStatement.setString(2, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
			while (rs.next()) { regsList.add(new LTMtobeRelease(regid,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6))); }
			
			PreparedStatement ps = connection.prepareStatement("select CSCNO from ALL_SUBDIVISION a,ALL_SECTION b where b.uk_seccd="+user.getSasseccd()+" and a.UK_SUBDIV=b.SUBDIVCD");
			ResultSet rs1=ps.executeQuery();
			if(rs1.next())
			{
				
				cscno=rs1.getString(1);
				
			}
			
			String circleid = user.getSascircd();
			String saserocd = user.getSaserono();
			String seccd = user.getSasseccd();
			
        	result.append(getFlatDataHTMLTable("Meter Details", regsList, circleid, saserocd, seccd, cscno));
        	
        	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}

	@SuppressWarnings("rawtypes")
	private String getFlatDataHTMLTable(String title, List <LTMtobeRelease> regData, String circleid, String saserocd, String seccd, String cscno) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		
		int eroid=Integer.parseInt(saserocd);
		int cirid=Integer.parseInt(circleid);
		int xibox = 0;
		int xjbox = 0;

		switch(cirid)
		{	
			
			case 1:
			case 2:
			case 8:
			case 14:
				xibox=4;
				xjbox=5;
				break;
			case 6:
				case 7:
				xibox=4;
				xjbox=6;
				break;
			case 9:
				xibox=5;
				xjbox=5;
				break;
			default:
				xibox=2;
				xjbox=6;
		}
		if(cscno.equals("907") || cscno.equals("512")|| cscno.equals("610")|| cscno.equals("614")|| cscno.equals("606")|| cscno.equals("603")||cscno.equals("605")|| cscno.equals("511")||cscno.equals("608")||cscno.equals("609")||cscno.equals("611")||cscno.equals("613")||cscno.equals("513")||cscno.equals("602")||cscno.equals("507")||cscno.equals("601")||cscno.equals("615")||cscno.equals("614")||cscno.equals("504")||cscno.equals("505")||cscno.equals("501")||cscno.equals("653")||cscno.equals("654")||cscno.equals("657")||cscno.equals("655")||cscno.equals("652")||cscno.equals("659")||cscno.equals("656")||cscno.equals("665")||cscno.equals("151")||cscno.equals("913")||cscno.equals("617")||cscno.equals("915")||cscno.equals("675")||cscno.equals("671")||cscno.equals("670")||cscno.equals("669")||cscno.equals("674")||cscno.equals("673")||cscno.equals("436"))
		{
			xibox=5;
			xjbox=5;

		}	if(cscno.equals("506")||cscno.equals("502")||cscno.equals("508")||cscno.equals("503")||cscno.equals("515")||cscno.equals("503")||cscno.equals("509")||cscno.equals("514")||cscno.equals("510")||cscno.equals("516")||cscno.equals("906")||cscno.equals("662")||cscno.equals("663")||cscno.equals("658")||cscno.equals("666")||cscno.equals("911")||cscno.equals("108")||cscno.equals("664")||cscno.equals("660")||cscno.equals("155")||cscno.equals("651")||cscno.equals("667")||cscno.equals("914")||cscno.equals("152")||cscno.equals("153")||cscno.equals("154")||cscno.equals("158")||cscno.equals("161")||cscno.equals("668")||cscno.equals("678")||cscno.equals("661"))
		{
				xibox=4;
				xjbox=5;

		}

		if(cscno.equals("159")||cscno.equals("156")||cscno.equals("160")||cscno.equals("677"))
		{
				xibox=4;
				xjbox=6;
		}
		if(seccd.equals("429")) {
				xibox=10;
				xjbox=0;
			}
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"<input type='hidden' name='meters_count' id='meters_count' value='"+regData.size()+"'/><input type='hidden' name='xibox' id='xibox' value='"+xibox+"'/><input type='hidden' name='xjbox' id='xjbox' value='"+xjbox+"'/></h6>");
			
			htmlTable.append("<table class=\"table table-bordered table-striped table-hover border-bottom mb-0 border dataTable no-footer\">");
			htmlTable.append("<thead>");
				htmlTable.append("<tr role='row'>");
					htmlTable.append("<th class=\"wd-3p\">"+commonUtil.columnTitles("slno")+"</th>");
					htmlTable.append("<th class=\"wd-10p\"><input type='checkbox' name='allCheck' id='allCheck'  onClick='checkStatus(this.checked);' title='Select or Deselect All' class='checkbox' checked>&nbsp;&nbsp;&nbsp;<span id='chkInfo'>Deselect All</span>&nbsp;&nbsp;&nbsp;<br/>"+commonUtil.columnTitles("flatno")+"</th>");
					htmlTable.append("<th class=\"wd-5p\">"+commonUtil.columnTitles("catdesc")+"</th>");
					htmlTable.append("<th class=\"wd-5p\">Load</th>");
					htmlTable.append("<th class=\"wd-17p\">Meter No</th>");
					htmlTable.append("<th class=\"wd-5p\">"+commonUtil.columnTitles("phase")+"</th>");
					htmlTable.append("<th class=\"wd-5p\">Capacity</th>");
					htmlTable.append("<th class=\"wd-20p\">Service No</th>");
					htmlTable.append("<th class=\"wd-10p\">Common Service</th>");
					htmlTable.append("<th class=\"wd-10p\">TC Cell1</th>");
					htmlTable.append("<th class=\"wd-10p\">TC Cell2</th>");
				htmlTable.append("</tr>");
			htmlTable.append("</thead>");
			htmlTable.append("<tbody>");
			
			/*int tnofcns=regData.size();
			if(tnofcns>20)
				tnofcns=20;
			*/
			int tnofcns = Math.min(regData.size(), 20); // Limit to 20 entries
			int j=0;
			for(int k=0;k<tnofcns;k++) {
				LTMtobeRelease record = regData.get(k);
				j=k+1;
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-3p\">"+j+"</th>");
					htmlTable.append("<td class=\"wd-10p\"><INPUT  TYPE='checkbox' NAME='flatno"+j+"' ID='flatno"+j+"' checked value='"+record.getFlatno()+"'>&nbsp;&nbsp;&nbsp;"+record.getFlatno()+"</td>");
					htmlTable.append("<td class=\"wd-5p\">"+record.getCapacity()+"</td>");
					htmlTable.append("<td class=\"wd-5p\">"+record.getLoad()+"</td>");
					htmlTable.append("<td class=\"wd-17p\">"+record.getMeterno()+"</td>");
					htmlTable.append("<td class=\"wd-5p\">"+record.getPhase()+"</td>");
					htmlTable.append("<td class=\"wd-5p\">"+record.getCapacity()+"</td>");
					htmlTable.append("<td class=\"wd-20p\">"
							+ "<div class=\"input-group\"><input type='text' class=\"form-control\" name='scno1"+j+"' id='scno1"+j+"' size='8' style='width:30px;' maxlength='' required='' onkeydown=\"if(event.keyCode==9 || event.keyCode==13) return checkIBox(this.id);\">&nbsp;&nbsp;<input type='text' class=\"form-control\" style='width:30px;' name='scno2"+j+"' id='scno2"+j+"' size='8' maxlength='' required=''></div></td>");
					htmlTable.append("<td class=\"wd-10p\"><select name='cmnserv"+j+"' class=\"form-control\" id='cmnserv"+j+"'><option value='x'>-Select-</option><option value='Y'>Yes</option><option value='N'>No</option></select></td>");
					htmlTable.append("<td class=\"wd-10p\"><input type='text' class=\"form-control\" name='tcceal1"+j+"' id='tcceal1"+j+"' size='10' maxlength=''></td>");
					htmlTable.append("<td class=\"wd-10p\"><input type='text' class=\"form-control\" name='tcceal2"+j+"' id='tcceal2"+j+"' size='10' maxlength=''></td>");
				htmlTable.append("</tr>");
			}
			htmlTable.append("</tbody>");
			htmlTable.append("</table>");
		htmlTable.append("</div>");
		 
		return htmlTable.toString();
	}
	
/*public String getSessionInfo(String sasusername) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null;
		ResultSet regrs = null;
		//HashMap<String,String> regData = new HashMap<String,String>();
		SessionInfo ses = new SessionInfo();
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.sessionInfo);) {
			System.out.println(sasusername+"sasusername>>>>>>>>>>>"+QueryBuilder.sessionInfo);
			preparedStatement.setString(1, sasusername);
			ResultSet rs = preparedStatement.executeQuery();
	        
			if (rs.next()) { 
				ses.setCirclecd(rs.getString(1));
				ses.setErocd(rs.getString(2));
				ses.setSeccd(rs.getString(3));
				ses.setSecname(rs.getString(4));
				ses.setSasuserid(rs.getString(5));
				ses.setSasdesg(rs.getString(6));
				ses.setSasoffadd(rs.getString(7));
				ses.setSasuser(rs.getString(9));
				ses.setEBSSeccd(rs.getString(10));
				ses.setSapseccd(rs.getString(11));
				ses.setSasusertype(rs.getString(12));
				ses.setCirclename(rs.getString(13));
				ses.setMtrsealcond(rs.getString(14));
				ses.setOldmtrchgflag(rs.getString(15));
				ses.setRollingstock(rs.getString(16));
			}
			
        	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return ses.getCirclecd();
	}*/
	
	public List<AGLRegistrations> getAglToBeReleasedList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <AGLRegistrations> aglList = new ArrayList<AGLRegistrations>();
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.agltobereleaselist+" and a.seccd like '"+user.getSasseccd()+"%' order by d.slno");) {
			ResultSet rs = preparedStatement.executeQuery();
	        while (rs.next()) {
	        	//select a.NRREGNO,to_char(a.NRREGDATE,'DD-MM-RR') NRREGDATE,APPLICANT_NAME,NRPRNO,b.catcode||'-'||catdesc,to_char(toberel_dt,'DD-MM-RR'),c.SECNAME,a.SECCD,nvl(meterno,'0'),d.slno,decode(nvl(a.scheme,' '),'I','Y','CI','Y','CLD','Y','SC','Y','ST','Y','J','Y','IT','Y','LI','Y','   '),decode(nvl(a.scheme,' '),'I','INDIRA PRABHA','CI','CI WELLS','CLD','CLDP','SC','SC Corporation','ST','ST WELFARE','J','IJP','IT','ITDA','LI','Lift Irrigation',' '),nvl(a.scheme,' ') FROM NEWCONNECTION_REGISTER a,category b,all_section c,agl_seniority d where a.catcode=b.catcode and a.SECCD=c.UK_SECCD and a.nrregno=d.nrregno and status in (4,9) AND a.CATCODE=5 and regno_status='ACTIVE' and nvl(to_date(sap_est_update),to_date('01/01/2014','dd/mm/yyyy'))>to_date('31/12/2015','dd/mm/yyyy')
	        	aglList.add(new AGLRegistrations(rs.getString("NRREGNO"),rs.getString("APPLICANT_NAME"),rs.getString("NRREGDATE"),rs.getString("SECNAME"),rs.getString("SECCD"),rs.getString("category"),rs.getString("meterno"), rs.getString("slno"),rs.getString("scheme"),rs.getString("scheme_desc")));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return aglList;
	}
	
public String getAGLtobeReleasedDetails(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String erouser="",wbsno="",makeList="",ddamount="",areacd="",area_group="";
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.agltobeReleasedDetails+" and NRREGNO=?");) {
			//System.out.println("ltmSendOTPConsumerDetails...Executed>>>"+QueryBuilder.ltmSendOTPConsumerDetails);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        PreparedStatement prs = connection.prepareStatement("SELECT DISTINCT EROUSERORA  FROM EROS,(SELECT uk_seccd,secname,A.EROCD FROM all_section  a,all_subdivision b WHERE uk_subdiv=subdiv   and uk_seccd=?) WHERE ERONO=EROCD");
	        prs.setString(1, user.getSasseccd());
	        ResultSet rsUser = prs.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
	        
	        if(rs.next()) {
        		regData.put("NRREGNO", rs.getString(1));
        		regData.put("APPLICANT_NAME", rs.getString(2));
        		regData.put("NRREGDATE", rs.getString(3));
        		
        		regData.put("SECNAME", rs.getString(4));
        		regData.put("APPLICANT_TYPE", rs.getString(5));
        		regData.put("SOCIALGROUP", rs.getString(6));
        		
        		regData.put("catcode", rs.getString(7));
        		regData.put("SUBCATCD", rs.getString(8));
        		regData.put("CONTRACTLD", rs.getString(9));
        		regData.put("CONNECTLD", rs.getString(10));
        		regData.put("SERVTYPECD", rs.getString(11));
        		regData.put("GOVTCD", rs.getString(13));
        		regData.put("HODCD", rs.getString(14));
        		
        		regData.put("PHASE", rs.getString(15));
        		regData.put("seccd", rs.getString(16));
        		regData.put("status", rs.getString(17));
        		regData.put("amttobepaid", rs.getString(18));
        		regData.put("subcatname", rs.getString(19));
        		
        		regData.put("areacd", rs.getString(21));
        		areacd=rs.getString(21);
        		regData.put("areaname", rs.getString(22));
        		regData.put("depttype", rs.getString(23));
        		regData.put("catg", rs.getString(24));
        		regData.put("mtrno", rs.getString(26));
        		regData.put("wbsno", rs.getString(27));
        		regData.put("meter_make", rs.getString("METERMAKE"));
        		
        		wbsno=rs.getString(27);
        		if(!"0".equals(rs.getString(26)))
        			regData.put("ismtr", "Y");
        		else 
        			regData.put("ismtr", "N");
        		regData.put("circd", user.getSascircd());
        	}
        	      
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.ddamount);
        	ps1.setString(1, wbsno);
        	ResultSet rs1 = ps1.executeQuery();
        	if(rs1.next()) {
        		ddamount=rs1.getString(1);
        	}
        	regData.put("ddamount", ddamount);
        	
        	
        	PreparedStatement ps2 = connection.prepareStatement("SELECT NVL(AREAGROUP,'M') FROM "+erouser+".AREA@intcsc_dblink WHERE NVL(AREACODE,'00')=?");
        	ps2.setString(1, areacd);
        	ResultSet rs2 = ps2.executeQuery();
        	if(rs2.next()) {
        		area_group=rs2.getString(1);
        	}
        	regData.put("area_group", area_group);
        	
        	
        	PreparedStatement ps = connection.prepareStatement("select distinct METERMAKE,METERMAKE from METERMAKE@intcsc_dblink");
	        ResultSet rsMake = ps.executeQuery();
	        while(rsMake.next())
	        	makeList=makeList+"<option value='"+rsMake.getString(1)+"'>"+rsMake.getString(2)+"</option>";
        	
        	result.append(getAGLTobeReleasedHTMLTable("Registration Details",regData,makeList));
        	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}

	@SuppressWarnings("rawtypes")
	private String getAGLTobeReleasedHTMLTable(String title, HashMap regData, String catlist) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Registration No.</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-3\">"+regData.get("NRREGNO")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">Registration Date</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("NRREGDATE")+"<input type='hidden' name='regdate' id='regdate' value='"+regData.get("NRREGDATE")+"'/></td>");
						htmlTable.append("<th class=\"wd-15p\">Applicant Name</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("APPLICANT_NAME")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Section Name</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("SECNAME")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Category</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("catcode")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Sub Category</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("SUBCATCD")+" - "+regData.get("subcatname")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");	
						htmlTable.append("<th class=\"wd-15p\">Connection Type</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("catcode")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Connected Load</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("CONNECTLD")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Area Code & Name</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("areacd")+" - "+regData.get("areaname")+"<input type='hidden' name='area' id='area' value='"+regData.get("areacd")+"'/></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Group</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("area_group")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Amount to be Paid</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("amttobepaid")+"<input type='hidden' name='tobepaid' id='tobepaid' value='"+regData.get("amttobepaid")+"'/></td>");
						htmlTable.append("<th class=\"wd-15p\">Amount Paid</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("ddamount")+"<input type='hidden' name='paid' id='paid' value='"+regData.get("ddamount")+"'/><input type='hidden' name='depttype' value='"+regData.get("depttype")+"'><input type=\"hidden\" name=\"catg\" id='catg' value='"+regData.get("catg")+"'></td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
				htmlTable.append("</table>");
				htmlTable.append("<input type=\"hidden\" name=\"ismtrissue\" id='ismtrissue' value='"+regData.get("ismtr")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"cphase\" id='cphase' value='"+regData.get("PHASE")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"seccd\" id='seccd' value='"+regData.get("seccd")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"meter_make\" id='meter_make' value='"+regData.get("meter_make")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"meterno\" id='meterno' value='"+regData.get("mtrno")+"'>");
		return htmlTable.toString();
	}
	
	public String getSequenceNumber() throws SQLException, Exception {
		String sql = null;
		String scno_seq="";
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		try (Connection connection = db.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.sequenceNumber);) {
				ResultSet rs = preparedStatement.executeQuery();
				if(rs.next()) {
					scno_seq=rs.getString(1);
				}
				connection.close();
		        rs.close();
		        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return scno_seq;
	}
	
	public String getServiceNoBoxDetails(User user) throws SQLException, Exception {
		result.setLength(0);
		String cscno="";
		int cirid=Integer.parseInt(user.getSascircd());
		int xibox = 0;
		int xjbox = 0;
		
		try (Connection connection = SASDB.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("select CSCNO from ALL_SUBDIVISIONS a,ALL_SECTION b where b.uk_seccd=? and a.UK_SUBDIV=b.SUBDIVCD");) {
				//System.out.println(user.getSasseccd()+"===select CSCNO from ALL_SUBDIVISIONS a,ALL_SECTION b where b.uk_seccd=? and a.UK_SUBDIV=b.SUBDIVCD");
				preparedStatement.setString(1, user.getSasseccd());
				ResultSet rs = preparedStatement.executeQuery();
				if(rs.next()) {
					cscno=rs.getString(1);
				}
				
		
				switch(cirid)
				{	
					case 1:
					case 2:
						xibox=4;
						xjbox=5;
						break;
					case 6:
						xibox=4;
						xjbox=6;
						break;
					case 7:
						xibox=4;
						xjbox=6;
						break;
					case 8:
						xibox=4;
						xjbox=5;
						break;
					case 9:
						xibox=5;
						xjbox=5;
						break;
					case 15:
						xibox=4;
						xjbox=5;
						break;
						
					default:
						xibox=2;
						xjbox=6;
				}
			
				if(cscno.equals("907") || cscno.equals("153")|| cscno.equals("512")|| cscno.equals("610")|| cscno.equals("614")|| cscno.equals("606")|| cscno.equals("603")|| cscno.equals("511")||cscno.equals("608")||cscno.equals("609")||cscno.equals("611")||cscno.equals("613")||cscno.equals("513")||cscno.equals("602")||cscno.equals("507")||cscno.equals("601")||cscno.equals("615")||cscno.equals("908")||cscno.equals("614")||cscno.equals("504")||cscno.equals("505")||cscno.equals("501")||cscno.equals("653")||cscno.equals("654")||cscno.equals("657")||cscno.equals("655")||cscno.equals("652")||cscno.equals("659")||cscno.equals("656")||cscno.equals("665")||cscno.equals("151")||cscno.equals("913")||cscno.equals("670")||cscno.equals("669")||cscno.equals("671")||cscno.equals("612")||cscno.equals("605")||cscno.equals("617")||cscno.equals("673")||cscno.equals("675")||cscno.equals("674")||cscno.equals("510")||cscno.equals("506")||cscno.equals("616")||cscno.equals("676"))
				{
					xibox=5;
					xjbox=5;
		
				}	if(cscno.equals("502")||cscno.equals("508")||cscno.equals("503")||cscno.equals("515")||cscno.equals("503")||cscno.equals("509")||cscno.equals("514")||cscno.equals("516")||cscno.equals("906")||cscno.equals("662")||cscno.equals("663")||cscno.equals("658")||cscno.equals("666")||cscno.equals("911")||cscno.equals("108")||cscno.equals("664")||cscno.equals("660")||cscno.equals("155")||cscno.equals("651")||cscno.equals("161")||cscno.equals("667")||cscno.equals("668")||cscno.equals("154")||cscno.equals("158")||cscno.equals("677")||cscno.equals("678"))
				{
					xibox=4;
					xjbox=5;
				}
		
				if(cscno.equals("159")||cscno.equals("156")||cscno.equals("160")||cscno.equals("152"))
				{
					xibox=4;
					xjbox=6;
				}
		
		
				connection.close();
		        rs.close();
		        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return xibox+"@@"+xjbox;
	}
	
	
	public String getCheckServiceAvailable(AglTobeReleasedModel atr, User user) throws SQLException, Exception {
		
		String erouser="",error_msg="";
		int available_flag=0;
		try (Connection connection = db.getConnection();) {
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.search_erouser);
	        ps.setString(1, user.getSasseccd());
	        ResultSet rsUser = ps.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
	        
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.checkScno_inNcr);
	        preparedStatement.setString(1, atr.getTxtscno());
	        preparedStatement.setString(2, user.getSaserono());
	        ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				available_flag=1;
				error_msg="Service Number Already Exist with Registration Number : "+rs.getString(1);
			}
			
			PreparedStatement ps1 = connection.prepareStatement("select ctscno,' ' from "+erouser+".consumer@INTCSC_DBLINK where ctscno=? union select ctscno,' ,  Dismantled' from "+erouser+".dismantleconsumer@INTCSC_DBLINK where ctscno=?");
	        ps1.setString(1, atr.getTxtscno());
	        ps1.setString(2, atr.getTxtscno());
	        ResultSet rs1 = ps1.executeQuery();
			if(rs1.next()) {
				available_flag=1;
				error_msg="Service Number Already Exist in EBS :"+rs1.getString(1)+rs1.getString(2);
			}
			
			PreparedStatement ps2 = connection.prepareStatement(QueryBuilder.checkScno_inCons);
			ps2.setString(1, atr.getTxtscno());
			ps2.setString(2, user.getSaserono());
	        ResultSet rs2 = ps2.executeQuery();
			if(rs2.next()) {
				available_flag=1;
				error_msg="Service Number Already Exists  in LTM with Registration Number : "+rs2.getString(1)+" with Flat no : "+rs2.getString(2);
			}
	        
	        
			connection.close();
	        //rs.close();
	        //ps.close();
		} catch (SQLException e) {
			available_flag=1;
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return error_msg+"@@"+available_flag;
	}
	
	
	public String updateAglTobeReleased(AglTobeReleasedModel atr, User user, String client_ip) throws SQLException, Exception {
		String rapdrpflag="N", subs="",cellno="",cscuid="",message="";
		int x=0,x1=0;
		if(user.getSasusertype().equals("5"))
			subs="AE";
		
		if(user.getSasusertype().equals("6"))
			subs="ADE";
		
		try (Connection connection = db.getConnection();) {
			
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE NEWCONNECTION_REGISTER SET update_date=to_date(sysdate),  TC_SEAL1=?, TC_SEAL2=?, MRBPAGENO=?, MTRREADERCODE=?, RELEASED_DT=to_date(?,'DD/MM/RRRR'),STRCODE=?,cycle=?,POLENO_GIS=?,status=5,ALLOTEDSCNO=?,ALLOTEDUKSCNO=?,"
					+ "METERIR=?,AREACD=?,AEFLAG='Y',AEUPDDATE=SYSDATE,FIELD_STAFF_NAME=?,cleint_ip_rel=?,rapdrpflag=? WHERE NRREGNO=? and SECCD=?");
	        preparedStatement.setString(1, atr.getTCs1());
	        preparedStatement.setString(2, atr.getTCs2());
	        preparedStatement.setString(3, atr.getMrbpage());
	        preparedStatement.setString(4, atr.getMcode());
	        preparedStatement.setString(5, atr.getReldt());
	        preparedStatement.setString(6, atr.getStructcode());
	        preparedStatement.setString(7, atr.getCycle());
	        preparedStatement.setString(8, atr.getMrbpole());
	        preparedStatement.setString(9, atr.getTxtscno());
	        preparedStatement.setString(10, atr.getAscno());
	        preparedStatement.setString(11, "");
	        preparedStatement.setString(12, atr.getArea());
	        preparedStatement.setString(13, atr.getLinemenlist());
	        preparedStatement.setString(14, client_ip);
	        preparedStatement.setString(15, rapdrpflag);
	        preparedStatement.setString(16, atr.getReg_Id());
	        preparedStatement.setString(17, atr.getSeccd());
	        x=preparedStatement.executeUpdate();
	        
	        PreparedStatement ps2=connection.prepareStatement("update  metermaster set STRUKSCNO=?,STRSCNO=?, STRTC_SEAL1=?, STRTC_SEAL2=? where MMTRMTRMAKE=? and STRMTRSLNO=? and nvl(strallocation,'N')='Y' AND STRISSUE_SEC=? and strcscregno=?");
	        ps2.setString(1, atr.getAscno());
	        ps2.setString(2, atr.getTxtscno());
	        ps2.setString(3, atr.getTCs1());
	        ps2.setString(4, atr.getTCs2());
	        ps2.setString(5, atr.getMeter_make());
	        ps2.setString(6, atr.getMeterno());
	        ps2.setString(7, atr.getSeccd());
	        ps2.setString(8, atr.getReg_Id());
	        x1=ps2.executeUpdate();
	        
	        connection.commit();
	        
	        if(x==1 && x1==1) {
	        	PreparedStatement ps3=connection.prepareStatement("select nvl(mobileno,'0'),nvl(sap_wbsno,0),cscuserid from NEWCONNECTION_REGISTER where NRREGNO=?");
		        ps3.setString(1, atr.getReg_Id());
		        ResultSet rs=ps3.executeQuery();
		        if(rs.next()) {
		        	cellno=rs.getString(1);
		    		cscuid=rs.getString(3);
		        }
		        if(!cellno.equals("0")&&!cellno.equals("9999999999")){
		        	//regscno=nregno+" for scno. "+scno;
		    		String res_sms=SmsMsg.sendMsg(cellno, "Dear Consumer,Meter is fixed against Registration No."+atr.getReg_Id()+". Please spare a few minutes for giving your valuable feedback by Clicking https://webportal.tgsouthernpower.org/SPDCL/ProspSurveyForm.jsp?regno="+atr.getReg_Id()+" .");
				 }
		   }
	        message="Record Updated Successfully.";
	        connection.close();
	        //rs.close();
	        //ps.close();
		} catch (SQLException e) {
			message="Error in Insertion..! Try Again..";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
	public List<DocumentsVerification> getNscsTobeReleasedList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.SECCD";
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <DocumentsVerification> docList = new ArrayList<DocumentsVerification>();
		
		try (Connection connection = db.getConnection();
			
			//PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.nscstobereleased_qry1+" and "+offtype+"='"+user.getSasseccd()+"'"+" UNION "+QueryBuilder.nscstobereleased_qry2+" and "+offtype+"='"+user.getSasseccd()+"'");
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.nrtestreportpendinglist);) {
			preparedStatement.setString(1, user.getSasseccd());
	        ResultSet rs = preparedStatement.executeQuery();
	        while (rs.next()) {
	        	docList.add(new DocumentsVerification(rs.getString("NRREGNO"),rs.getString("nrregdate"),rs.getString("APPLICANT_NAME"),rs.getString("catdesc"),rs.getString("SECNAME"),rs.getString("toberel_dt"),"",rs.getString("MOBILENO")));
	        }
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docList;
	}
	
	
public String getNscTobeReleasedDetails(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String erouser="",wbsno="",makeList="",areaLiSt="",ddamount="",areacd="",area_group="";
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.nscTobeReleasedDetails);) {
			//System.out.println("ltmSendOTPConsumerDetails...Executed>>>"+QueryBuilder.ltmSendOTPConsumerDetails);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        PreparedStatement prs = connection.prepareStatement("SELECT DISTINCT EROUSERORA  FROM EROS,(SELECT uk_seccd,secname,A.EROCD FROM all_section  a,all_subdivision b WHERE uk_subdiv=subdiv   and uk_seccd=?) WHERE ERONO=EROCD");
	        prs.setString(1, user.getSasseccd());
	        ResultSet rsUser = prs.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
	        
	        if(rs.next()) {
        		regData.put("NRREGNO", rs.getString(1));
        		regData.put("APPLICANT_NAME", rs.getString(2));
        		regData.put("NRREGDATE", rs.getString(3));
        		regData.put("SECNAME", rs.getString(4));
        		regData.put("APPLICANT_TYPE", rs.getString(5));
        		regData.put("SOCIALGROUP", rs.getString(6));
        		regData.put("catcode", rs.getString(7));
        		regData.put("SUBCATCD", rs.getString(8));
        		regData.put("CONTRACTLD", rs.getString(9));
        		regData.put("CONNECTLD", rs.getString(10));
        		regData.put("SERVTYPECD", rs.getString(11));
        		regData.put("GOVTCD", rs.getString(12));
        		regData.put("HODCD", rs.getString(13));
        		
        		regData.put("PHASE", rs.getString(14));
        		//regData.put("seccd", rs.getString(16));
        		regData.put("status", rs.getString(17));
        		regData.put("amttobepaid", rs.getString(18));
        		regData.put("subcatname", rs.getString(19));
        		regData.put("METERMAKE", rs.getString(20));
        		regData.put("METERNO", rs.getString(21));
        		regData.put("MF", rs.getString(22));
        		regData.put("MRTSEAL1", rs.getString(23));
        		regData.put("MRTSEAL2", rs.getString(24));
        		
        		regData.put("METERCAP", rs.getString(25));
        		regData.put("depttype", rs.getString(26));
        		regData.put("catg", rs.getString(27));
        		regData.put("wbsno", rs.getString(28));
        		wbsno=rs.getString(28);
        	}
	        
        if(!"".equals(wbsno)) {      
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.nr_ddamount);
        	ps1.setString(1, wbsno);
        	ps1.setString(2, wbsno);
        	ps1.setString(3, wbsno);
        	ps1.setString(4, wbsno);
        	ResultSet rs1 = ps1.executeQuery();
        	if(rs1.next()) {
        		ddamount=rs1.getString(1);
        	}
        }
        	regData.put("ddamount", ddamount);
        	
        	
        	PreparedStatement ps2 = connection.prepareStatement("SELECT NVL(AREAGROUP,'M') FROM "+erouser+".AREA@intcsc_dblink WHERE NVL(AREACODE,'00')=?");
        	ps2.setString(1, areacd);
        	ResultSet rs2 = ps2.executeQuery();
        	if(rs2.next()) {
        		area_group=rs2.getString(1);
        	}
        	regData.put("area_group", area_group);
        	
        	PreparedStatement ps3 = connection.prepareStatement("SELECT  areacode,areaname FROM  "+erouser+".area@intcsc_dblink WHERE  areaukseccd=? order by areacode");
        	ps3.setString(1, user.getSasseccd());
        	ResultSet rs3 = ps3.executeQuery();
        	if(rs3.next()) {
        		areaLiSt=areaLiSt+"<option value='"+rs3.getString(1)+"'>"+rs3.getString(2)+"</option>";
        	}
        	        	
        	PreparedStatement ps = connection.prepareStatement("select distinct METERMAKE from METERMAKE@intcsc_dblink");
	        ResultSet rsMake = ps.executeQuery();
	        while(rsMake.next())
	        	makeList=makeList+"<option value='"+rsMake.getString(1)+"'>"+rsMake.getString(1)+"</option>";
        	
        	result.append(getNSCTobeReleasedHTMLTable("Registration Details",regData,makeList,areaLiSt));
        	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}

	@SuppressWarnings("rawtypes")
	private String getNSCTobeReleasedHTMLTable(String title, HashMap regData, String makeList, String areaLiSt) {
		CommonUtils commonUtil = new CommonUtils();
		StringBuilder htmlTable = new StringBuilder();
		//String contextPath = messageSource.getMessage("server.sas.contextpath", null, Locale.ENGLISH);
		//System.out.println("Context Path..."+contextPath);
		
		htmlTable.append("<div>");
		htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">"+title+"</h6>");
		//System.out.println("********************"+htmlTable.toString());
		
			htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
				htmlTable.append("<tbody>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Registration No.</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-3\">"+regData.get("NRREGNO")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">Registration Date</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("NRREGDATE")+"<input type='hidden' name='regdate' id='regdate' value='"+regData.get("NRREGDATE")+"'/></td>");
						htmlTable.append("<th class=\"wd-15p\">Applicant Name</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("APPLICANT_NAME")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Section Name</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("SECNAME")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Category</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("catcode")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Sub Category</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("subcatname")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");	
						htmlTable.append("<th class=\"wd-15p\">Connection Type</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("catcode")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Connected Load</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("CONNECTLD")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Meter Make</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("METERMAKE")+"<input type=\"hidden\" name=\"meter_make\" id='meter_make' value='\"+regData.get(\"meter_make\")+\"'></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Amount to be Paid</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("amttobepaid")+"<input type='hidden' name='tobepaid' id='tobepaid' value='"+regData.get("amttobepaid")+"'/></td>");
						htmlTable.append("<th class=\"wd-15p\">Amount Paid</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("ddamount")+"<input type='hidden' name='paid' id='paid' value='"+regData.get("ddamount")+"'/><input type='hidden' name='depttype' value='"+regData.get("depttype")+"'><input type=\"hidden\" name=\"catg\" id='catg' value='"+regData.get("catg")+"'></td>");
						htmlTable.append("<th class=\"wd-15p\">Meter Number</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("METERNO")+"<input type=\"hidden\" name=\"meterno\" id='meterno' value='"+regData.get("mtrno")+"'></td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Meter MF</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("MF")+"<input type=\"hidden\" name=\"mf\" id=\"mf\" value='"+regData.get("MF")+"'></td>");						htmlTable.append("<th class=\"wd-15p\">Meter Capacity</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("METERCAP")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">MRT Seal1</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("MRTSEAL1")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">MRT SEAL2</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("MRTSEAL2")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Longitude</th>");
						htmlTable.append("<td class=\"wd-20p\"><font color='red'><i class=\"fa-solid fa-location-dot\"></i></font> "+regData.get("LONGITUDE")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Latitude</th>");
						htmlTable.append("<td class=\"wd-20p\"><font color='red'><i class=\"fa-solid fa-location-dot\"></i></font> "+regData.get("LATITUDE")+"</td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
				htmlTable.append("</table>");
				htmlTable.append("<input type=\"hidden\" name=\"cphase\" id='cphase' value='"+regData.get("PHASE")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"seccd\" id='seccd' value='"+regData.get("seccd")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"regid\" id=\"regid\" value='"+regData.get("NRREGNO")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"catid\" id=\"catid\" value='"+regData.get("catcode")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"scheme\" id=\"scheme\" value='"+regData.get("SECNAME")+"'>");
				htmlTable.append("<input type=\"hidden\" name=\"SAP_WBSNO\" id=\"SAP_WBSNO\" value='"+regData.get("wbsno")+"'/>");
		return htmlTable.toString();
	}
	
public String getNsTestReportUploadDetails(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String erouser="",wbsno="",makeList="",areaLiSt="",ddamount="",areacd="",area_group="";
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.nscTobeReleasedDetails);) {
			//System.out.println("ltmSendOTPConsumerDetails...Executed>>>"+QueryBuilder.ltmSendOTPConsumerDetails);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        PreparedStatement prs = connection.prepareStatement("SELECT DISTINCT EROUSERORA  FROM EROS,(SELECT uk_seccd,secname,A.EROCD FROM all_section  a,all_subdivision b WHERE uk_subdiv=subdiv   and uk_seccd=?) WHERE ERONO=EROCD");
	        prs.setString(1, user.getSasseccd());
	        ResultSet rsUser = prs.executeQuery();
	        if(rsUser.next())
	        	erouser=rsUser.getString(1);
	        
	        if(rs.next()) {
        		regData.put("NRREGNO", rs.getString(1));
        		regData.put("APPLICANT_NAME", rs.getString(2));
        		regData.put("NRREGDATE", rs.getString(3));
        		regData.put("SECNAME", rs.getString(4));
        		regData.put("APPLICANT_TYPE", rs.getString(5));
        		regData.put("SOCIALGROUP", rs.getString(6));
        		regData.put("catcode", rs.getString(7));
        		regData.put("SUBCATCD", rs.getString(8));
        		regData.put("CONTRACTLD", rs.getString(9));
        		regData.put("CONNECTLD", rs.getString(10));
        		regData.put("SERVTYPECD", rs.getString(11));
        		regData.put("GOVTCD", rs.getString(12));
        		regData.put("HODCD", rs.getString(13));
        		
        		regData.put("PHASE", rs.getString(14));
        		//regData.put("seccd", rs.getString(16));
        		regData.put("status", rs.getString(17));
        		regData.put("amttobepaid", rs.getString(18));
        		regData.put("subcatname", rs.getString(19));
        		regData.put("METERMAKE", rs.getString(20));
        		regData.put("METERNO", rs.getString(21));
        		regData.put("MF", rs.getString(22));
        		regData.put("MRTSEAL1", rs.getString(23));
        		regData.put("MRTSEAL2", rs.getString(24));
        		
        		regData.put("METERCAP", rs.getString(25));
        		regData.put("depttype", rs.getString(26));
        		regData.put("catg", rs.getString(27));
        		regData.put("wbsno", rs.getString(28));
        		regData.put("LONGITUDE", rs.getString("LONGITUDE"));
        		regData.put("LATITUDE", rs.getString("LATITUDE"));
        		wbsno=rs.getString(28);
        	}
	        
        if(!"".equals(wbsno)) {      
        	PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.nr_ddamount);
        	ps1.setString(1, wbsno);
        	ps1.setString(2, wbsno);
        	ps1.setString(3, wbsno);
        	ps1.setString(4, wbsno);
        	ResultSet rs1 = ps1.executeQuery();
        	if(rs1.next()) {
        		ddamount=rs1.getString(1);
        	}
        }
        	regData.put("ddamount", ddamount);
        	
        	
        	PreparedStatement ps2 = connection.prepareStatement("SELECT NVL(AREAGROUP,'M') FROM "+erouser+".AREA@intcsc_dblink WHERE NVL(AREACODE,'00')=?");
        	ps2.setString(1, areacd);
        	ResultSet rs2 = ps2.executeQuery();
        	if(rs2.next()) {
        		area_group=rs2.getString(1);
        	}
        	regData.put("area_group", area_group);
        	
        	PreparedStatement ps3 = connection.prepareStatement("SELECT  areacode,areaname FROM  "+erouser+".area@intcsc_dblink WHERE  areaukseccd=? order by areacode");
        	ps3.setString(1, user.getSasseccd());
        	ResultSet rs3 = ps3.executeQuery();
        	if(rs3.next()) {
        		areaLiSt=areaLiSt+"<option value='"+rs3.getString(1)+"'>"+rs3.getString(2)+"</option>";
        	}
        	        	
        	PreparedStatement ps = connection.prepareStatement("select distinct METERMAKE from METERMAKE@intcsc_dblink");
	        ResultSet rsMake = ps.executeQuery();
	        while(rsMake.next())
	        	makeList=makeList+"<option value='"+rsMake.getString(1)+"'>"+rsMake.getString(1)+"</option>";
        	
        	result.append(getNSCTobeReleasedHTMLTable("Registration Details",regData,makeList,areaLiSt));
        	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
	}


	public String uploadNRTestReport(UploadTestReportNewModel utr, User user, String realPath) throws SQLException, Exception {
		String subs="",message="",otp="",line_staff_phno="",regmbno="",lineotp="", meterno="",res_sms1="",res_sms="";
		if(user.getSasusertype().equals("5"))
			subs="AE";
		
		if(user.getSasusertype().equals("6"))
			subs="ADE";
		
		int i=0,x=0,x1=0;
		
		try (Connection connection = db.getConnection();) {
			
			FileStatements fs = new FileStatements(realPath,utr.getRegid());
	        MultipartFile testreport=utr.getReport1();
			FileInputStream fis_test_report = fs.FileRead(testreport);
			
			MultipartFile agriment=utr.getAgrmnt();
			FileInputStream fis_agriment = fs.FileRead(agriment);
			
			if(utr.getCatid().equals("3") || utr.getCatid().equals("4")){
				if(utr.getReport1()!=null){
					String duqry2="Update spddocs.DOCUMENTS SET SAS_TSTRPT=?,SAS_AGRMT=?,sas_flag='S' where REGNO="+utr.getRegid()+" ";			
					PreparedStatement ptstmt1=connection.prepareStatement(duqry2);
					
					if(utr.getReport1().getSize()!=0){ 
						 	ptstmt1.setBinaryStream(1,fis_test_report,fis_test_report.available());
						}
					else{
							ptstmt1.setString(1,"");
						}
					if(utr.getAgrmnt().getSize()!=0){ 
							ptstmt1.setBinaryStream(2,fis_agriment,fis_agriment.available());
						}
					else{
							ptstmt1.setString(2,"");
						}
				  x1=ptstmt1.executeUpdate();
				  
				  String duqry="Update DOCUMENTS SET SAS_TSTRPT=?,SAS_AGRMT=?,sas_flag='S' where REGNO="+utr.getRegid()+" ";
				  PreparedStatement ptstmt=connection.prepareStatement(duqry);
					if(utr.getReport1().getSize()!=0){ 
						 	ptstmt.setBinaryStream(1,fis_test_report,fis_test_report.available());
						}
					else{
							ptstmt.setString(1,"");
						}
					if(utr.getAgrmnt().getSize()!=0){ 
							ptstmt.setBinaryStream(2,fis_agriment,fis_agriment.available());
						}
					else{
							ptstmt.setString(2,"");
						}
				  x=ptstmt.executeUpdate();
				  
				  if(x==0){
					  String duqry1="insert into DOCUMENTS (REGNO,SAS_TSTRPT,SAS_AGRMT,ENTRY_DATE,sas_flag) VALUES(?,?,?,SYSDATE,'S')";	
					  ptstmt=connection.prepareStatement(duqry1);	
						 ptstmt.setString(1,utr.getRegid());
						 ptstmt.setBinaryStream(2, fis_test_report,fis_test_report.available());
						 ptstmt.setBinaryStream(3, fis_agriment,fis_agriment.available());
						 ptstmt.executeUpdate();
						 
					 String duqry3="insert into  spddocs.DOCUMENTS (REGNO,SAS_TSTRPT,SAS_AGRMT,ENTRY_DATE,sas_flag) VALUES(?,?,?,SYSDATE,'S')";			
						 ptstmt1=connection.prepareStatement(duqry3);	
						 ptstmt1.setString(1,utr.getRegid());
						 ptstmt1.setBinaryStream(2, fis_test_report,fis_test_report.available());
						 ptstmt1.setBinaryStream(3, fis_agriment,fis_agriment.available());
						 ptstmt1.executeUpdate();
				  }
				  ptstmt.close();
				  ptstmt1.close();
				}
			} else {
				if(utr.getReport1()!=null){
					String duqry2="Update spddocs.DOCUMENTS SET SAS_TSTRPT=?,sas_flag='S' where REGNO="+utr.getRegid()+" ";			
					PreparedStatement ptstmt1=connection.prepareStatement(duqry2);			
					if(utr.getReport1().getSize()!=0){ 
						ptstmt1.setBinaryStream(1,fis_test_report,fis_test_report.available());
					}
					else{
						ptstmt1.setString(1,"");
					}
				
				  x1=ptstmt1.executeUpdate();
				  
				  String duqry="Update DOCUMENTS SET SAS_TSTRPT=?,sas_flag='S' where REGNO="+utr.getRegid()+" ";			
				  PreparedStatement ptstmt=connection.prepareStatement(duqry);
				
					if(utr.getReport1().getSize()!=0){ 
						 ptstmt.setBinaryStream(1,fis_test_report,fis_test_report.available());
					}
					else{
						ptstmt.setString(1,"");
					}
					
					x=ptstmt.executeUpdate();
					
					if(x==0){
						String duqry1="insert into DOCUMENTS (REGNO,SAS_TSTRPT,ENTRY_DATE,sas_flag) VALUES(?,?,SYSDATE,'S')";			
						ptstmt=connection.prepareStatement(duqry1);	
						 ptstmt.setString(1,utr.getRegid());
						 ptstmt.setBinaryStream(2,fis_test_report,fis_test_report.available());
						
						 ptstmt.executeUpdate();
						 

						 String duqry3="insert into  spddocs.DOCUMENTS (REGNO,SAS_TSTRPT,ENTRY_DATE,sas_flag) VALUES(?,?,SYSDATE,'S')";			
						 ptstmt1=connection.prepareStatement(duqry3);	
						 ptstmt1.setString(1,utr.getRegid());
						 ptstmt1.setBinaryStream(2,fis_test_report,fis_test_report.available());
						
						 ptstmt1.executeUpdate();
						
					}
					 ptstmt.close();
					 ptstmt1.close();
				}
			}
			
			String qry="Update NEWCONNECTION_REGISTER SET SAS_IMAGE='1',SAS_IMG_DT=sysdate where NRREGNO="+utr.getRegid()+" ";
			PreparedStatement stmt=connection.prepareStatement(qry);
			stmt.execute();
			
			connection.commit();
			connection.close();
			message="Test report Uploaded Successfully";
				        
			connection.close();
	        //rs.close();
	        //ps.close();
		} catch (SQLException e) {
			message="NO";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
}
