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
import com.tsspdcl.sas.entity.nsts.WebRegistrations;
import com.tsspdcl.sas.entry.ccts.BurntConmplaint;
import com.tsspdcl.sas.entry.ccts.CategoryChangeList;
import com.tsspdcl.sas.entry.ccts.MRTtesting;
import com.tsspdcl.sas.entry.ccts.QualityCheckList;
import com.tsspdcl.sas.entry.ccts.UploadPowerSupplyModel;
import com.tsspdcl.sas.service.FileStatements;
import com.tsspdcl.sas.service.SmsMsg;

//@Configuration
//@PropertySource("classpath:application.properties")	
public class CctsDAO {
	
	@Autowired
	private Environment env;
	
	StringBuilder result = new StringBuilder();
	public CctsDAO() {
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
	
	private String offtype = "";
	
	public String getRegData(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		System.out.println("regid>>>>>>>>>>>>>>>>>>>>>>>>>"+regid);
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		String cirUserName="",doc_vrdt="";
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement("select complaint_no FROM complaint_details where  complaint_no=? and complaint_natureid in(40,49,50,21,79)");) {
			//System.out.println("Connection..."+connection);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        Connection con = SASDB.getConnection(); 
	        PreparedStatement ps = con.prepareStatement(QueryBuilder.getCircleUser);
	        ps.setString(1, user.getSascircd());
	        ResultSet rs1 = ps.executeQuery();
	        if(rs1.next()) {cirUserName=rs1.getString(1);}
	        
	        PreparedStatement ps2 = connection.prepareStatement("select to_char(max(TO_DATE(MODIFIED_DATE)),'DD/MM/YYYY') from reg_log where regno=?");
	        ps2.setString(1, regid);
        	ResultSet rs2 = ps2.executeQuery();
        	if(rs2.next()) {doc_vrdt=rs2.getString(1);}
	            
	        if (rs.next()) {
	        	sql = QueryBuilder.ccregQry1+cirUserName+".metertrans@intcsc_dblink M where a.complaint_natureid=b.id AND a.RECORD_STATUS='ACTIVE' and p.reg_no(+)=a.COMPLAINT_NO and A.COMPLAINT_NO=STRCSCREGNO (+) and complaint_no=? and a.seccd=c.uk_seccd and a.bank_name=d.id(+)";
	        }	
	        else {
	        	sql = QueryBuilder.ccregQry2;
	        }
	        System.out.println(sql);
	        regStmt = connection.prepareStatement(sql);
        	regStmt.setString(1, regid);
        	regrs = regStmt.executeQuery();
	        	
        	if(regrs.next()) {
				/*
				 * regData.put("nrregno", regrs.getString(1)); regData.put("consname",
				 * regrs.getString(2)); regData.put("nrregdate", regrs.getString(3));
				 * regData.put("secname", regrs.getString(4)); regData.put("group",
				 * regrs.getString(6)); regData.put("catdesc",
				 * regrs.getString(7)+"-"+regrs.getString(8)); regData.put("subcat",
				 * regrs.getString(9)); regData.put("contload", regrs.getString(10));
				 * regData.put("phase", regrs.getString(11)); regData.put("meterno",
				 * regrs.getString(17)); regData.put("cscremarks", regrs.getString(18));
				 * regData.put("aeremarks", regrs.getString(19)); regData.put("aderemarks",
				 * regrs.getString(20)); regData.put("tobereleasedt", regrs.getString(21));
				 * regData.put("aeremarksdt", regrs.getString(22)); regData.put("aderemarksdt",
				 * regrs.getString(23)); regData.put("status", regrs.getString(24));
				 * regData.put("mobileno", regrs.getString(29)); regData.put("catcd",
				 * regrs.getString(7)); regData.put("estreq", regrs.getString(31));
				 */
        			regData.put("nrregno", regrs.getString(1));
        			regData.put("consname",	regrs.getString(2));
        			regData.put("nrregdate", regrs.getString(3));
       				regData.put("secname", regrs.getString(4));
       				regData.put("serviceno", regrs.getString(5));
       				regData.put("comp_nature", regrs.getString(6));
       				regData.put("tobe_rectified_dt", regrs.getString(7));
       				regData.put("rectifiedOn", regrs.getString(8));
       				regData.put("status", regrs.getString(10));
       				regData.put("aeremarks", regrs.getString(13));
       				regData.put("aderemarks", regrs.getString(14));
       				regData.put("docverify_dt", doc_vrdt);
       				 
        		System.out.println("regData>>>>>>>"+regData);
        		result.append(getCCRegHTMLTable("Registration Details",regData));
        	}
	       	        
	        estStmt = connection.prepareStatement(QueryBuilder.ccEstQry);
        	estStmt.setString(1, regid);
        	
        	estrs = estStmt.executeQuery();
        	
        	if(estrs.next()) {
        		estData.put("estst", estrs.getString(2));
        		estData.put("sapestno", estrs.getString(1));
        		estData.put("estamt", estrs.getString(3));
        		estData.put("noofdayspending", estrs.getString(4));
        		estData.put("paymenttobeest", estrs.getString(6));
        		estData.put("estsandt", estrs.getString(23));
        		estData.put("workcompdt", estrs.getString(24));
        		estData.put("estcreatedt", estrs.getString(25));
        		estData.put("eststcode", estrs.getString(26));
        		
        		//result.append(getEstHTMLTable("Estimation Details",estData));
        	}
        	
        	//System.out.println(QueryBuilder.docsQry);
        	docsStmt = connection.prepareStatement(QueryBuilder.ccDocQry);
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
         		result.append(getCCDocsHTMLTable("Submitted Documents List",docsData));
         	}
         	
         	connection.close();
	        rs.close();
	        preparedStatement.close();
        	
		} catch (SQLException e) {System.out.println("error>>"+e);
			printSQLException(e);
	    } catch (Exception e1) {System.out.println("error>>"+e1);
			e1.printStackTrace();
		}
		//System.out.println("result...."+result);
		return result.toString();
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
	private String getCCRegHTMLTable(String title, HashMap regData) {
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
					htmlTable.append("<th class=\"wd-15p\">Service Number</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("serviceno")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">Complaint Nature</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("comp_nature")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">To Be Rectified date</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("tobe_rectified_dt")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">Rectified On</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("rectifiedOn")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">Status</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("status")+"</td>");
				htmlTable.append("</tr>");
				htmlTable.append("<tr>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("aeremarks")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("aeremarks")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">"+commonUtil.columnTitles("aderemarks")+"</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("aderemarks")+"</td>");
					htmlTable.append("<th class=\"wd-15p\">Document Verification Date</th>");
					htmlTable.append("<td class=\"wd-20p\">"+regData.get("docverify_dt")+"</td>");
				htmlTable.append("</tr>");
				
			htmlTable.append("</tbody>");	
		htmlTable.append("</table>");
		htmlTable.append("<input type=\"hidden\" name=\"catcd\" id=\"catcd\" value=\""+regData.get("catcd")+"\" />");
		htmlTable.append("<input type=\"hidden\" name=\"estreq\" id=\"estreq\" value=\""+regData.get("estreq")+"\" />");
		htmlTable.append("</div>");
		return htmlTable.toString();
	}
	
	
	@SuppressWarnings("rawtypes")
	private String getCCDocsHTMLTable(String title, HashMap<String, String> docsData) {
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
	
	
	public List<QualityCheckList> ccQualityOfPowerSupply(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <QualityCheckList> otpList = new ArrayList<QualityCheckList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.qualityCheckList);) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, user.getSasseccd());
			preparedStatement.setString(3, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new QualityCheckList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(8),rs.getString(9)));
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
	
	public List<QualityCheckList> ccRestorationOfPowerSupply(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <QualityCheckList> otpList = new ArrayList<QualityCheckList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.restorationCheckList);) {
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, user.getSasseccd());
			preparedStatement.setString(3, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new QualityCheckList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(8),rs.getString(9)));
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
	
public String getPowerSupplyDetails(String regid, String realPath, String pageType, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		PreparedStatement regStmt = null; PreparedStatement estStmt = null; PreparedStatement docsStmt = null;
		ResultSet regrs = null; ResultSet estrs = null; ResultSet docsrs = null;
		HashMap<String,String> regData = new HashMap<String,String>();
		HashMap<String,String> estData = new HashMap<String,String>();
		HashMap<String,String> docsData = new HashMap<String,String>();
		String reqcat="", status="",remarks="",mobileno="",mdisp="",mrk="",docflag="N";
		String paid="", qry="";
		double ipaid=0, bal=0;
		
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.CCDetailsForAE+" and complaint_no=?");) {
			//System.out.println("CC Details..."+QueryBuilder.CCDetailsForAE+" and complaint_no="+regid);	
			preparedStatement.setString(1, regid);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        
	        PreparedStatement ps = connection.prepareStatement(QueryBuilder.getCC_Cat);
	        ps.setString(1, regid);
	        ResultSet rsCat = ps.executeQuery();
	        if(rsCat.next())
	        	reqcat=rsCat.getString(1);
	        
	        PreparedStatement pstat = connection.prepareStatement(QueryBuilder.ccstatus);
	        pstat.setString(1, regid);
	        ResultSet rstat = pstat.executeQuery();
	        if(rstat.next()) {
	        	status=rstat.getString(10);
	        	remarks=rstat.getString(12);
	        	mobileno=rstat.getString("mobileno");
	        }
	        //String com_nature="",statuscode="";
	        PreparedStatement ps4 = connection.prepareStatement("select A.COMPLAINT_NO,to_char(COMPLAINT_GIVEN_ON,'dd-MON-yyyy'),SCNO,A.NAME,B.COMPLAINT_NATURE,UKSCNO,COMPLAINT_NATUREID,status FROM COMPLAINT_DETAILS A,cOMPLAINT_NATURE B where A.COMPLAINT_NATUREID=B.ID AND A.COMPLAINT_NO=?");
	        ps4.setString(1, regid);
	        ResultSet rs4 = ps4.executeQuery();
	        if(rs4.next()) {
	        	estData.put("com_nature", rs4.getString(5));
	        	estData.put("statuscode", rs4.getString(8));
	        }
	        
	        PreparedStatement psest = connection.prepareStatement(QueryBuilder.sapest);
	        psest.setString(1, regid);
	        ResultSet rsest = psest.executeQuery();
	        if(rsest.next()) {
	        	estData.put("estflag1", "Y");
	        	estData.put("wbsno", rsest.getString(1));
	        	estData.put("eststatus", rsest.getString(2));
	        	estData.put("estimateno", rsest.getString(1).toString().substring(0,rsest.getString(1).toString().length()-1));
	        	estData.put("estimate_amt", rsest.getString(3));
	        	estData.put("nofdays_pending", rsest.getString(4));
	        	if(!rsest.getString(6).toString().equals("0")){
	        		estData.put("estflag2", "Y");
	        		double estamt=Double.parseDouble((String)rsest.getString(6));

		  			if(paid==null){paid="0";}
		  			ipaid=Double.parseDouble(paid);
	  			
	  			if(ipaid<0)
	  				ipaid=0;
	        	
	        	if(rsest.getString(5)==null||((String)rsest.getString(5)).equals("APPR"))
				{
		
				}
				else 
				{
					mdisp="* Estimate amount is tentative and liable for changes during estimate approval process";
					mrk="*";
				}

			  bal=(estamt - ipaid);
			  
			  estData.put("mdisp", mdisp);
			  estData.put("est_mrk", mrk);
			  estData.put("est_amt", rsest.getString(6));
			  estData.put("payment_on_reg", String.valueOf(ipaid));
			  estData.put("balance_amt", String.valueOf(bal));
			  estData.put("workorder_status", removeNull(rsest.getString(13)));
			  estData.put("nofpending", removeNull(rsest.getString(14)));
			  estData.put("workorder_flag", removeNull(rsest.getString(16)));
			  estData.put("workorder_nodpending", removeNull(rsest.getString(15)));
			  estData.put("workorder_reason", removeNull(rsest.getString(17)));
			  
	        }
	        	if("0".equals(rstat.getString(1)))
	        		qry="select sum(nvl(amount,0)) from payment_master where reg_no="+regid+"";
	        	else
	        		qry="select sum(nvl(a.amount,0))-sum(nvl(a.app_fee,0)) from payment_master a,complaint_details b where a.reg_no=b.complaint_no and b.record_status='ACTIVE' and b.sap_wbsno='"+rstat.getString(1)+"'";
	        	
	        	PreparedStatement ps1 = connection.prepareStatement(qry);
		        ResultSet rs1 = ps1.executeQuery();
		        if(rs1.next()) {
		        	paid = rs1.getString(1);
		        }
	        }
	        
	        String qry1="",ddamt="0",divname="";
	        
        	if(rs.next()) {
        		if("0".equals(rs.getString(20)))
				{
        			qry1="select sum(nvl(amount,0)) from payment_master where reg_no='"+regid+"'";
				}
				else
				{
					qry1="select sum(amount) amt from payment_master where reg_no in (select nrregno from newconnection_register where sap_wbsno='"+rs.getString(20)+"' union select registration_number from apartment_connection where sap_wbsno='"+rs.getString(20)+"' union select complaint_no from complaint_details where sap_wbsno='"+rs.getString(20)+"')";
				}
        		//System.out.println("qry1>>>"+qry1);
        		PreparedStatement ps2 = connection.prepareStatement(qry1);
		        ResultSet rs2 = ps2.executeQuery();
		        if(rs2.next()) {
		        	ddamt = rs2.getString(1);
		        }
		        if(ddamt==null)
					ddamt="0";
		        
		        PreparedStatement ps3 = connection.prepareStatement("select division_name from division where division_cd=(select divcd from all_subdivision where uk_subdiv=(select subdiv from all_section where uk_seccd="+rs.getString(23)+"))");
		        ResultSet rs3 = ps3.executeQuery();
		        if(rs3.next()) {
		        	divname = rs3.getString(1);
		        }
        		
        		regData.put("regno", rs.getString(1));
        		regData.put("serviceno", rs.getString(3));
        		regData.put("name_address", rs.getString(2)+", "+removeNull(rs.getString(4))+", "+removeNull(rs.getString(5))+", "+removeNull(rs.getString(6))+", "+removeNull(rs.getString(7)));
        		regData.put("extload", removeNull(rs.getString(9)));
        		regData.put("status", status);
        		regData.put("mobileno", removeNull(rs.getString("MOBILE_NO")));
        		regData.put("extcat", removeNull(rs.getString(11)));
        		regData.put("amount_tobe_paid", removeNull(rs.getString(10)));
        		regData.put("compdetails", remarks);
        		regData.put("cons_mobile", removeNull(mobileno));
        		regData.put("ddamt", removeNull(ddamt));
        		regData.put("divname", removeNull(divname));
        		regData.put("cnatureid", removeNull(rs.getString(21)));
        		regData.put("createdby", removeNull(rs.getString(22)));
        		regData.put("sectioncd", removeNull(rs.getString(23)));
        		regData.put("mobile", removeNull(rs.getString(24)));
        		regData.put("cfrom", removeNull(rs.getString(25)));
        		regData.put("compname", removeNull(rs.getString(26)));
        		regData.put("amount", removeNull(rs.getString(13)));
        	}
	       	        	
        	//result.append(getCCPowerSupplyHTMLTable("Complaint Details",regData,paid,estData));
        	
        	docsStmt = connection.prepareStatement(QueryBuilder.ccDocsQry);
        	//System.out.println("ccDocsQry>>>>>>"+QueryBuilder.ccDocsQry+regid);
         	docsStmt.setString(1, regid);
         	docsrs = docsStmt.executeQuery();
         	
         	if(docsrs.next()) {
         		
         		if(docsrs.getInt(4)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(4), docsrs.getBlob(3), realPath, docsrs.getString(1), "APPL_FORM");
         			docsData.put("applform", url);
         		}
         		if(docsrs.getInt(6)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(6), docsrs.getBlob(5), realPath, docsrs.getString(1), "ID_PROOF");
         			docsData.put("idproof", url);
         		}
         		if(docsrs.getInt(8)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(8), docsrs.getBlob(7), realPath, docsrs.getString(1), "CAST_CERTIFICATE");
         			docsData.put("castcert", url);
         		}
         		if(docsrs.getInt(10)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(10), docsrs.getBlob(9), realPath, docsrs.getString(1), "SALE_DEED");
         			docsData.put("saledeed", url);
         		}
         		if(docsrs.getInt(12)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(12), docsrs.getBlob(11), realPath, docsrs.getString(1), "PANCHAYAT_LETTER");
         			docsData.put("panchayatletter", url);
         		}
         		if(docsrs.getInt(14)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(14), docsrs.getBlob(13), realPath, docsrs.getString(1), "PHOTO");
         			docsData.put("photo", url);
         		}
         		if(docsrs.getInt(16)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(16), docsrs.getBlob(15), realPath, docsrs.getString(1), "WIRING_CERTIFICATE");
         			docsData.put("wiringcert", url);
         		}
         		if(docsrs.getInt(18)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(18), docsrs.getBlob(17), realPath, docsrs.getString(1), "OCC_CERT");
         			docsData.put("occcert", url);
         		}
         		if(docsrs.getInt(20)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(20), docsrs.getBlob(19), realPath, docsrs.getString(1), "NOC_PCB");
         			docsData.put("nocpcb", url);
         		}
         		if(docsrs.getInt(22)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(22), docsrs.getBlob(21), realPath, docsrs.getString(1), "NOV_LBODY");
         			docsData.put("novlbody", url);
         		}
         		if(docsrs.getInt(24)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(24), docsrs.getBlob(23), realPath, docsrs.getString(1), "PATTA_DOC");
         			docsData.put("pattadoc", url);
         		}
         		if(docsrs.getInt(26)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(26), docsrs.getBlob(25), realPath, docsrs.getString(1), "MA_TAX");
         			docsData.put("mhtax", url);
         		}
         		if(docsrs.getInt(28)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(28), docsrs.getBlob(27), realPath, docsrs.getString(1), "SSI_CERT");
         			docsData.put("ssicert", url);
         		}
         		if(docsrs.getInt(30)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(30), docsrs.getBlob(29), realPath, docsrs.getString(1), "CERT_80G");
         			docsData.put("cert80g", url);
         		}
         		if(docsrs.getInt(32)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(32), docsrs.getBlob(31), realPath, docsrs.getString(1), "MRO_CERT");
         			docsData.put("mrocert", url);
         		}
         		if(docsrs.getInt(34)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(34), docsrs.getBlob(33), realPath, docsrs.getString(1), "CEIG_CERT");
         			docsData.put("ceigcert", url);
         		}
         		if(docsrs.getInt(36)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(36), docsrs.getBlob(35), realPath, docsrs.getString(1), "FORM_I2");
         			docsData.put("formI2", url);
         		}
         		if(docsrs.getInt(38)>0) {docflag="Y";
         			String url = commonUtil.readBlob(docsrs.getInt(38), docsrs.getBlob(37), realPath, docsrs.getString(1), "LOC_PHOTO");
         			docsData.put("locphoto", url);
         		}
         		         		
         		docsData.forEach((key, value) -> System.out.println(key+" "+value));
         		//result.append(getDocsHTMLTable("Submitted Documents List",docsData));
         	}
        	
         	regData.put("docflag", docflag);
         	result.append(getCCPowerSupplyHTMLTable("Complaint Details",regData,paid,estData));
         	if("Y".equals(docflag))
         	result.append(getDocsHTMLTable("Submitted Documents List",docsData));
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
	private String getCCPowerSupplyHTMLTable(String title, HashMap regData, String catlist, HashMap estData) {
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
						htmlTable.append("<th class=\"wd-15p\">Complaint No.</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+regData.get("regno")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">Service No.</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("serviceno")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Name & Address</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("name_address")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Existing Load</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("extload")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Status</th>");
						htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-danger bg-danger-transparent py-2 px-2\">"+regData.get("status")+"</span></td>");
						htmlTable.append("<th class=\"wd-15p\">Mobile No</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("mobileno")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");	
						htmlTable.append("<th class=\"wd-15p\">Existing Category</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("extcat")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Amount to be Paid</th>");
						htmlTable.append("<td class=\"wd-20p font-weight-bold\">"+regData.get("amount_tobe_paid")+"</td>");
						htmlTable.append("<th class=\"wd-15p\">Complaint Details</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("compdetails")+"</td>");
					htmlTable.append("</tr>");
					htmlTable.append("<tr>");
						htmlTable.append("<th class=\"wd-15p\">Consumer Mobile No</th>");
						htmlTable.append("<td class=\"wd-20p\">"+regData.get("cons_mobile")+"</td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
						htmlTable.append("<th class=\"wd-15p\"></th>");
						htmlTable.append("<td class=\"wd-20p\"></td>");
					htmlTable.append("</tr>");
				htmlTable.append("</tbody>");	
			htmlTable.append("</table>");
			htmlTable.append("<input type='hidden' name='amtpd' id='amtpd' value='"+regData.get("ddamt")+"'>");
			htmlTable.append("<input type='hidden' name='cnatureid' id='cnatureid' value='"+regData.get("cnatureid")+"'>");
			htmlTable.append("<input type='hidden' name='createdby' id='createdby' value='"+regData.get("createdby")+"'>");
			htmlTable.append("<input type='hidden' name='divname' id='divname' value='"+regData.get("divname")+"'>");
			htmlTable.append("<input type='hidden' name='sectioncd' id='sectioncd' value='"+regData.get("sectioncd")+"'>");
			htmlTable.append("<input type='hidden' name='mobile' id='mobile' value='"+regData.get("mobile")+"'>");
			htmlTable.append("<input type='hidden' name='cfrom' id='cfrom' value='"+regData.get("cfrom")+"'>");
			htmlTable.append("<input type='hidden' name='compname' id='compname' value='"+regData.get("compname")+"'>");
			htmlTable.append("<input type='hidden' name='amtbepd' id='amtbepd' value='"+regData.get("amount_tobe_paid")+"'>");
			htmlTable.append("<input type='hidden' name='docflag' id='docflag' value='"+regData.get("docflag")+"'>");
			
			htmlTable.append("<input type='hidden' name='com_nature' id='com_nature' value='"+regData.get("com_nature")+"'>");
			htmlTable.append("<input type='hidden' name='statuscode' id='statuscode' value='"+regData.get("statuscode")+"'>");
			
			
			if("Y".equals(estData.get("estflag1"))) {
				htmlTable.append("<div>");
				htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">Estimate Details</h6>");
				//System.out.println("********************"+htmlTable.toString());
				
					htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
						htmlTable.append("<tbody>");
							htmlTable.append("<tr>");
								htmlTable.append("<th class=\"wd-15p\">Estimate Status</th>");
								htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+estData.get("eststatus")+"</span></td>");
								htmlTable.append("<th class=\"wd-15p\">SAP Estimate No.</th>");
								htmlTable.append("<td class=\"wd-20p\">"+estData.get("estimateno")+"</td>");
								htmlTable.append("<th class=\"wd-15p\">Estimate Amount</th>");
								htmlTable.append("<td class=\"wd-20p\">"+estData.get("estimate_amt")+" - "+estData.get("est_mrk")+"</td>");
							htmlTable.append("</tr>");
							htmlTable.append("<tr>");
								htmlTable.append("<th class=\"wd-15p\">No.of Days Pending</th>");
								htmlTable.append("<td class=\"wd-20p\">"+estData.get("nofdays_pending")+"</td>");
								htmlTable.append("<th class=\"wd-15p\"></th>");
								htmlTable.append("<td class=\"wd-20p\"></td>");
								htmlTable.append("<th class=\"wd-15p\"></th>");
								htmlTable.append("<td class=\"wd-20p\"></td>");
							htmlTable.append("</tr>");
						htmlTable.append("</tbody>");	
					htmlTable.append("</table>");
					htmlTable.append("</div>");
					
					htmlTable.append("<div>");
					htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">Payment Details</h6>");
					//System.out.println("********************"+htmlTable.toString());
					
						htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
							htmlTable.append("<tbody>");
								htmlTable.append("<tr>");
									htmlTable.append("<th class=\"wd-15p\">Payments to be Collected as per Estimate&nbsp;Rs.</th>");
									htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+estData.get("paymenttobeest")+"</span></td>");
								htmlTable.append("</tr>");
								htmlTable.append("<tr>");
									htmlTable.append("<th class=\"wd-15p\">Payments Received at the time of Registration Rs.</th>");
									htmlTable.append("<td class=\"wd-20p\">"+estData.get("payment_on_reg")+"</td>");
								htmlTable.append("</tr>");
								htmlTable.append("<tr>");
								htmlTable.append("<th class=\"wd-15p\">Balance to be paid by the Customer&nbsp;Rs.</th>");
								htmlTable.append("<td class=\"wd-20p\">"+estData.get("balance_amt")+"</td>");
							htmlTable.append("</tr>");
							htmlTable.append("</tbody>");	
						htmlTable.append("</table>");
						htmlTable.append("</div>");
						
						if(!"".equals(estData.get("workorder_status")) && "0".equals(estData.get("workorder_flag"))) {
							htmlTable.append("<div>");
							htmlTable.append("<h6 class=\"card-content-label mt-3 mb-1 tx-15 font-weight-bold\">Work Order Details</h6>");
							//System.out.println("********************"+htmlTable.toString());
							
								htmlTable.append("<table class=\"table main-table-reference mt-0 mb-0\">"); 
									htmlTable.append("<tbody>");
										htmlTable.append("<tr>");
											htmlTable.append("<th class=\"wd-15p\">Work Order Status</th>");
											htmlTable.append("<td class=\"wd-20p\"><span class=\"font-weight-bold badge rounded-pill text-success bg-success-transparent py-2 px-2\">"+estData.get("workorder_status")+"</span></td>");
										
											htmlTable.append("<th class=\"wd-15p\">No.of Days Pending</th>");
											htmlTable.append("<td class=\"wd-20p\">"+estData.get("nofpending")+"</td>");
										htmlTable.append("</tr>");
										htmlTable.append("<tr>");
										htmlTable.append("<th class=\"wd-15p\">Reason</th>");
										htmlTable.append("<td class=\"wd-20p\" colspan='3'>"+estData.get("workorder_reason")+"</td>");
									htmlTable.append("</tr>");
									htmlTable.append("</tbody>");	
								htmlTable.append("</table>");
								htmlTable.append("</div>");
						}
			}
				
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
	
	
	public String uploadPowerSupply(UploadPowerSupplyModel utr, User user, String realPath) throws SQLException, Exception {
		String subs="",q1="",q2="",message="",uqry="";
		if(user.getSasusertype().equals("5")) {
			subs="AE"; q1="SAS_AE_RMKS"; q2=",SAS_FLAG='A'";
		}
		if(user.getSasusertype().equals("6")) {
			subs="ADE";
		}
		int i=0,x=0,x1=0;
		
		try (Connection connection = db.getConnection();) {
			
			uqry="UPDATE COMPLAINT_DETAILS SET "+q1+"=? "+q2+",SAS_FLAG_DATE=SYSDATE WHERE COMPLAINT_NO=?";	 
			
	        PreparedStatement ps2=connection.prepareStatement(uqry);
	        
	        FileStatements fs = new FileStatements(realPath,utr.getReg_Id());
	        MultipartFile testreport=utr.getQps_doc();
			FileStatements fs1 = new FileStatements(realPath,utr.getReg_Id());
			FileInputStream fis_qpsdoc = fs.FileRead(testreport);
			
			ps2.setString(1,utr.getRemarks());
	        //ps2.setBinaryStream(2,fis_test_report,fis_test_report.available());
	        ps2.setString(2, utr.getReg_Id());
	        ps2.executeUpdate();
	        
	        PreparedStatement pstmt2 = connection.prepareStatement("select REGNO from DOCUMENTS where REGNO=?");
			pstmt2.setString(1,utr.getReg_Id());
			ResultSet rs=pstmt2.executeQuery();
			 
			if(rs.next()){
				PreparedStatement ps3=connection.prepareStatement("Update DOCUMENTS SET SAS_PHOTO=?,SAS_FLAG='S'  where REGNO=?");
		        ps3.setBinaryStream(1,fis_qpsdoc,fis_qpsdoc.available());
		        ps3.setString(2, utr.getReg_Id());
		        x=ps3.executeUpdate();
		        
		        PreparedStatement ps1=connection.prepareStatement("Update spddocs.DOCUMENTS SET SAS_PHOTO=?,SAS_FLAG='S'  where REGNO=? ");
		        ps1.setBinaryStream(1, fis_qpsdoc,fis_qpsdoc.available());
		        ps1.setString(2,utr.getReg_Id());
		        ps1.executeUpdate();
		        
			}
			else {
		        PreparedStatement ps4=connection.prepareStatement("insert INTO DOCUMENTS(REGNO,SAS_PHOTO,ENTRY_DATE,SAS_FLAG) values (?,?,SYSDATE,'S')");
		        ps4.setString(1,utr.getReg_Id());
		        ps4.setBinaryStream(2,fis_qpsdoc,fis_qpsdoc.available());
		        ps4.executeUpdate();
		        
		        PreparedStatement ps5=connection.prepareStatement("insert INTO SPDDOCS.DOCUMENTS(REGNO,SAS_PHOTO,ENTRY_DATE,SAS_FLAG) values (?,?,SYSDATE,'S')");
		        ps5.setString(1,utr.getReg_Id());
		        ps5.setBinaryStream(2,fis_qpsdoc,fis_qpsdoc.available());
		        ps5.executeUpdate();
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
	
	public List<CategoryChangeList> ccCategoryChangeList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <CategoryChangeList> ccList = new ArrayList<CategoryChangeList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.categoryChangeList);) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	ccList.add(new CategoryChangeList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(8),rs.getString(9)));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return ccList;
	}
	
	public HashMap<String, String> getDocumentsList(User user) throws SQLException, Exception {
		final HashMap<String, String> docs = new HashMap<>();
		try (Connection connection = db.getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.ccDocsList);) {
			//System.out.println("Connection..."+connection);
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	docs.put(rs.getString(1), rs.getString(2));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return docs;
	}
	
	public String uploadCatChange(UploadPowerSupplyModel utr, User user, String realPath, String ipaddr) throws SQLException, Exception {
		String subs="",q1="",q2="",message="",uqry="",PREV_STATUS="",cellno="";
		if(user.getSasusertype().equals("5")) {
			subs="AE"; q1="SAS_AE_RMKS"; q2=",SAS_FLAG='A'";
		}
		if(user.getSasusertype().equals("6")) {
			subs="ADE";
		}
		int i=0,x=0,x1=0;
		
		
		try (Connection connection = db.getConnection();) {
			
			PreparedStatement pstmt0 = connection.prepareStatement("select STATUS,mobile_no FROM COMPLAINT_DETAILS  WHERE COMPLAINT_NO=?");
			pstmt0.setString(1,utr.getReg_Id());
			ResultSet rs0=pstmt0.executeQuery();
			 
			if(rs0.next()){
				PREV_STATUS=rs0.getString(1);
				cellno=rs0.getString(1);
			}
			
			if("rejected".equals(utr.getStatus())){message="Rejected ";
				PreparedStatement ps=connection.prepareStatement("UPDATE COMPLAINT_DETAILS SET  SAS_AE_RMKS=?,SAS_FLAG='R',SAS_FLAG_DATE=SYSDATE WHERE COMPLAINT_NO=?");
		        ps.setString(1, utr.getRemarks());
		        ps.setString(2, utr.getReg_Id());
		        ps.executeUpdate();
			}
			else if("ERO".equals(utr.getStatus())) {message="Forward ";
				PreparedStatement ps=connection.prepareStatement("UPDATE COMPLAINT_DETAILS SET  SAS_AE_RMKS=?,SAS_FLAG='A',SAS_FLAG_DATE=SYSDATE WHERE COMPLAINT_NO=?");
				ps.setString(1, utr.getRemarks());
		        ps.setString(2, utr.getReg_Id());
		        ps.executeUpdate();
			}
			else if(!"".equals(utr.getDoc_reason())){
				PreparedStatement ps=connection.prepareStatement("update documents set PREV_STATUS=? where REGNO=?");
				ps.setString(1, PREV_STATUS);
		        ps.setString(2, utr.getReg_Id());
		        ps.executeUpdate();
		        
		        PreparedStatement ps1=connection.prepareStatement("update complaint_details set status=null,REMARKS=?,UPDATE_DATE=SYSDATE where complaint_no=?");
				ps1.setString(1, utr.getRemarks());
		        ps1.setString(2, utr.getReg_Id());
		        ps1.executeUpdate();
		        
		        PreparedStatement ps2=connection.prepareStatement("Insert into REG_LOG (REGNO,STATUS_from,STATUS_to,REMARKS,CLIENT_IP) "
		        		+ "values(?,?,'','AESAS',?)");
				ps2.setString(1, utr.getReg_Id());
		        ps2.setString(2, utr.getStatuscode());
		        ps2.setString(3, ipaddr);
		        ps2.executeUpdate();
		        
		        String qry1="insert into DOC_QUARIES(REGNO,DOC_TYPEID) values(?,?)";
		        PreparedStatement ps3=connection.prepareStatement(qry1);
		        ps3.setString(1, utr.getReg_Id());
		        ps3.setString(2, utr.getDoc_reason());
		        ps3.executeUpdate();
		        
		        String docsqry="",docnames="",smsmsg="";
		        if(utr.getDoc_reason()==""||utr.getDoc_reason().equals("")){
		   		 	docsqry="select docid, (CASE  WHEN docid=3 THEN 'ID' WHEN docid=5 THEN 'SALEDEED' WHEN docid=7 THEN 'BOND' WHEN docid=19 THEN 'PLAN'   WHEN docid=8 THEN 'OCC_CERT' END) DOC_NAME from DOCTYPES where docid in("+utr.getDoc_reason()+") ";
		   		 }else{
		   			 docsqry="select docid, (CASE  WHEN docid=3 THEN 'ID' WHEN docid=5 THEN 'SALEDEED' WHEN docid=7 THEN 'BOND' WHEN docid=19 THEN 'PLAN'   WHEN docid=8 THEN 'OCC_CERT' END) DOC_NAME from DOCTYPES where docid IN("+utr.getDoc_reason()+")";
		   		 }
		        PreparedStatement ps4=connection.prepareStatement(docsqry);
		        //ps4.setString(1, utr.getDoc_reason());
		        ResultSet rs4=ps4.executeQuery();  
				 while (rs4.next()){
					 docnames=docnames+","+rs4.getString(2);
				 }
				 
				 smsmsg="Please Upload "+docnames+" as "+utr.getRemarks()+". Within 48 hours else your application will be auto rejected. Click below link to Upload https://webportal.tgsouthernpower.org/onlinecsc/reUpload/"+utr.getReg_Id()+" ";
				 if(!cellno.equals("0") && !cellno.equals("9999999999"))
					{
						try
						{
							String res_sms=SmsMsg.sendMsg(cellno, smsmsg);	

							String logqry1="Insert into ROUTE_SMS_LOG (CRETED_DT,MOBILENO,CREATEDBY,MESSAGE,ACK) values(SYSDATE,?,'ADE',?,?)";
							PreparedStatement ps5=connection.prepareStatement(logqry1);
					        ps5.setString(1, cellno);
					        ps5.setString(2, smsmsg);
					        ps5.setString(3, res_sms);
					        ps5.executeQuery();  
						}
						catch(Exception esms)
						{
							message = "\nError::"+esms.getMessage()+"\n";
						}
					}
			}
			
			
			if(utr.getQps_doc()!=null){
			   	String chkReg="select REGNO from DOCUMENTS where REGNO=?";
			   	PreparedStatement ps6=connection.prepareStatement(chkReg);
			   	ps6.setString(1, utr.getReg_Id());
			   	ResultSet rs6=ps6.executeQuery();
			   	if(rs6.next()){
	             	String duqry="Update DOCUMENTS SET SAS_TSTRPT=?,SAS_FLAG='S' where REGNO=? ";		
	             	PreparedStatement ps7=connection.prepareStatement(duqry);
	             	
	             	FileStatements fs = new FileStatements(realPath,utr.getReg_Id());
	    	        MultipartFile qps_doc=utr.getQps_doc();
	    	        FileInputStream fis_qps_doc = fs.FileRead(qps_doc);
	    			
					if(utr.getQps_doc().getSize()!=0){ 
						ps7.setBinaryStream(1,fis_qps_doc,fis_qps_doc.available());
						ps7.setString(2, utr.getReg_Id());
					} else{
						ps7.setString(1,"");
						ps7.setString(2, utr.getReg_Id());
					}
					ps7.executeUpdate();
					
					String duqry1="Update spddocs.DOCUMENTS SET SAS_TSTRPT=?,SAS_FLAG='S' where REGNO=?";
					PreparedStatement ps8=connection.prepareStatement(duqry);
					if(utr.getQps_doc().getSize()!=0){ 
						ps8.setBinaryStream(1,fis_qps_doc,fis_qps_doc.available());
						ps8.setString(2, utr.getReg_Id());
					} else{
						ps8.setString(1,"");
						ps8.setString(2, utr.getReg_Id());
					}
					ps8.executeUpdate();
			  }
			}else{	
				String duqry="insert into DOCUMENTS (REGNO,SAS_TSTRPT,ENTRY_DATE,SAS_FLAG) VALUES(?,?,SYSDATE,'S')";			
				PreparedStatement ps9=connection.prepareStatement(duqry);
				
				FileStatements fs = new FileStatements(realPath,utr.getReg_Id());
    	        MultipartFile qps_doc=utr.getQps_doc();
    	        FileInputStream fis_qps_doc = fs.FileRead(qps_doc);
				 ps9.setString(1,utr.getReg_Id());
				 ps9.setBinaryStream(2,fis_qps_doc,fis_qps_doc.available());
				
				 ps9.executeUpdate();
				
				 String duqry1="insert into spddocs.DOCUMENTS (REGNO,SAS_TSTRPT,ENTRY_DATE,SAS_FLAG) VALUES(?,?,SYSDATE,'S')";			
				 PreparedStatement ps10=connection.prepareStatement(duqry1);	
				 ps10.setString(1,utr.getReg_Id());
				 ps10.setBinaryStream(2,fis_qps_doc,fis_qps_doc.available());
				
				 ps10.executeUpdate();
				 
			}
			
	       message="Record Updated Successfully";
	        
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
	
	public List<QualityCheckList> ccMeterComplaintsList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <QualityCheckList> otpList = new ArrayList<QualityCheckList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.meterCCList+"  AND A.seccd=? order by B.COMPLAINT_NATURE,TO_DATE(toberec_dt)");) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new QualityCheckList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(8),rs.getString(9)));
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
	
	public List<QualityCheckList> ccBurntComplaintsList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <QualityCheckList> otpList = new ArrayList<QualityCheckList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.burntCCList);) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new QualityCheckList(rs.getString(1),rs.getString(2),rs.getString(4),rs.getString(3),rs.getString(5),rs.getString(6),"",rs.getString(7)));
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
	
	public List<MRTtesting> ccMRTtestNoticeDownloadList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <MRTtesting> otpList = new ArrayList<MRTtesting>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.mrtTestingDownloadList);) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new MRTtesting(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
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
	
	public List<MRTtesting> ccStuckupList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		List <MRTtesting> otpList = new ArrayList<MRTtesting>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.meterStuckupList);) {
			//System.out.println("QueryBuilder.qualityCheckList>>>"+QueryBuilder.qualityCheckList+user.getSasseccd());
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new MRTtesting(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
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
	
	public List<QualityCheckList> ccTestforBillRevisionList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = QueryBuilder.testreport4billrevisionAE;
		
		if(user.getSasusertype().equals("6"))
			offtype = QueryBuilder.testreport4billrevisionADE;
		
		List <QualityCheckList> otpList = new ArrayList<QualityCheckList>();
		
		try (Connection connection = db.getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement(offtype);) {
			//System.out.println(offtype+"====================="+user.getSasseccd()+"---"+user.getSasusertype());
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	otpList.add(new QualityCheckList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(8),rs.getString(9)));
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
	
	public String burntComplaintUpdate(User user, List<BurntConmplaint> burnt) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = QueryBuilder.testreport4billrevisionAE;
		
		if(user.getSasusertype().equals("6"))
			offtype = QueryBuilder.testreport4billrevisionADE;
		
		String message="";
		try(Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("Update COMPLAINT_DETAILS SET SAS_FLAG=?,SAS_FLAG_DATE=SYSDATE where COMPLAINT_NO=?");) {
				
			for(int j=1; j<=burnt.size(); j++) {
				BurntConmplaint complaint = burnt.get(j);
	            preparedStatement.setString(1, complaint.getBurntStatus());
				preparedStatement.setString(2, complaint.getComplaint_number());
				int i = preparedStatement.executeUpdate();
				
				if (i > 0) {
	                message="Updated Successfully";
	            }
				//System.out.println("Burnt.size()==@@@@@@@@@@@@"+burnt.size()+"=="+burnt.);
			}
		
		} catch(Exception ee) {message="Unexpected error: "+ee;}
		return message;
	}
	
public HashMap meterPendingComplaints(int menuid, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		HashMap<String,String> regData = new HashMap<String,String>();
		String erouser="";
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.pendingMeterComplaints);) {
			
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	        
	    	if(rs.next()) {
	    		regData.put("total", rs.getString(1));
	    		regData.put("power", rs.getString(2));
	    		regData.put("category", rs.getString(3));
	    		regData.put("meter", rs.getString(4));
	    		regData.put("load", rs.getString(5));
	    		regData.put("billing", rs.getString(6));
	    		regData.put("others", rs.getString(7));
	    	}
	    	connection.close();
	        rs.close();
	        preparedStatement.close();
	    	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return regData;
	}

	public HashMap pendingComplaintsList(int menuid, User user) throws SQLException, Exception {
		
		String sql = null;
		result.setLength(0);
		CommonUtils commonUtil = new CommonUtils();
		
		HashMap<String,String> regData = new HashMap<String,String>();
		String erouser="";
		
		try (Connection connection = db.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(QueryBuilder.pendingMeterComplaints);) {
			
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	        
	    	if(rs.next()) {
	    		regData.put("total", rs.getString(1));
	    		regData.put("power", rs.getString(2));
	    		regData.put("category", rs.getString(3));
	    		regData.put("meter", rs.getString(4));
	    		regData.put("load", rs.getString(5));
	    		regData.put("billing", rs.getString(6));
	    		regData.put("others", rs.getString(7));
	    	}
	    	connection.close();
	        rs.close();
	        preparedStatement.close();
	    	
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return regData;
	}

}
