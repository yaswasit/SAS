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
import javax.servlet.http.HttpSession;

//import org.omg.CORBA.portable.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
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
import com.tsspdcl.sas.entry.mm.AreaList;
import com.tsspdcl.sas.entry.mm.LinemanList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentAutoList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentModel;
import com.tsspdcl.sas.entry.mm.MeterAllotmentServicesList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentStuckupModel;
import com.tsspdcl.sas.entry.mm.MeterMakeList;
import com.tsspdcl.sas.entry.mm.MeterStatusList;
import com.tsspdcl.sas.entry.mm.SessionMeterAllotment;
import com.tsspdcl.sas.entry.mm.StuckupDataList;
import com.tsspdcl.sas.service.FileStatements;
import com.tsspdcl.sas.service.SmsMsg;

//@Configuration
//@PropertySource("classpath:application.properties")	
public class MmDAO {
	
	@Autowired
	private Environment env;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	StringBuilder result = new StringBuilder();
	public MmDAO() {
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
	
	
	
	
	
	
	
	public List<AreaList> mmAreaList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			PreparedStatement preparedStatement = con.prepareStatement(QueryBuilder.arealist);
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	areaList.add(new AreaList(rs.getString(1),rs.getString(1)+" - "+rs.getString(2)));
	        }
	        
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return areaList;
	}
	
	public List<MeterAllotmentServicesList> allotmentServiceList(User user, String area) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <MeterAllotmentServicesList> masList = new ArrayList<MeterAllotmentServicesList>();
		if(area.equals("All"))
		{
			area="%";
		}
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			String allotment_service_list = "SELECT CTSCNO, CTUKSCNO, CTBILSTAT, CTNAME, CTPHONE, CTCAT, CTSUBCAT, CTCONLD, CTCTRLD, CTSECCD, CTUKSECCD, CTEROCD, CTCIR_CODE, TO_CHAR(TO_DATE(BTBLDT),'DD/MM/YYYY') BTBLDT, (CASE WHEN NVL(CTCOUNTBNT,0)>0 THEN TO_CHAR(TO_DATE(CTMTREXPDT),'DD/MM/YYYY') ELSE TO_CHAR(TO_DATE(BTBLDT),'DD/MM/YYYY') END) CTMTREXPDT, CTMTRPHASE, CTAREACD, CTCLUBREL FROM CONSUMER_EBS,SECTION WHERE CTUKSECCD=UKSECCD and NVL(ctareacd,' ')  like ? AND  UKSECCD=? AND CTBILSTAT='11' AND NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS WHERE CTSCNO=STRSCNO AND STRISSUE_SEC=CTUKSECCD AND STRALLOCATION='Y' AND TO_DATE(NVL(CTMTREXPDT,'01-JAN-54'))<=TO_DATE(STRISSUEDT) ) AND (TO_DATE(BTBLDT)>(SELECT TO_DATE(LEDMONTH) FROM BILLCONTROL) OR NVL(CTCLUBREL,'LT')='CH')  AND NOT EXISTS(SELECT SCNO FROM sas.sas_bill_burnt_complaints S WHERE  CTSCNO=SCNO AND S.UKSECCD=CTUKSECCD AND S.RECORD_STATUS='ACTIVE' AND APPROVE_STAT='Y' AND TO_DATE(NVL((CASE WHEN NVL(CTCOUNTBNT,0)>0 THEN TO_CHAR(TO_DATE(CTMTREXPDT),'DD-MON-YY') ELSE TO_CHAR(TO_DATE(BTBLDT),'DD-MON-YY') END),'01-JAN-54'))<=TO_DATE(MTREXPDT)) ORDER BY CTAREACD,CTSCNO,TO_DATE(CTMTREXPDT)";
			PreparedStatement preparedStatement = con.prepareStatement(allotment_service_list);
			preparedStatement.setString(1, area);
			preparedStatement.setString(2, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	masList.add(new MeterAllotmentServicesList(rs.getString("CTSCNO"),rs.getString("CTUKSCNO"),rs.getString("CTBILSTAT")
	        			,rs.getString("CTNAME"),rs.getString("CTPHONE"),rs.getString("CTCAT"),rs.getString("CTSUBCAT"),rs.getString("CTCONLD"),rs.getString("CTCTRLD"),rs.getString("CTSECCD"),rs.getString("CTUKSECCD")
	        			,rs.getString("CTEROCD"),rs.getString("CTCIR_CODE"),rs.getString("BTBLDT"),rs.getString("CTMTREXPDT"),rs.getString("CTMTRPHASE"),rs.getString("CTAREACD"),rs.getString("CTCLUBREL")));
	        }
	        
	        connection.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return masList;
	}
	
	
	public void saveServiceData(HashMap<String, String> formData, Integer rows) {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+serviceNo);
		String sql = "INSERT INTO ServiceData (CTSCNO, CTCAT, CTCTRLD, CTMTRPHASE, CTMTREXPDT, approv, CTMobile) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection con = db.getConnection()){
			PreparedStatement ps = con.prepareStatement(sql);
			for(int i=1; i<=rows; i++) {
				//ps.setString(1, formData.get(""));
				System.out.println(formData.get("chk"+i)+"==========================================================================================="+formData.get("approv"+i));
				//System.out.println(formData.getOrDefault("CTSCNO"+i, "CTSCNO"+i.value));
			}
			con.close();
	        //rs.close();
	        ps.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
        //jdbcTemplate.update(sql, serviceNo, cat, load, phase, expDate, approv, mobile);
    }
	
	
	public List<MeterStatusList> meterStatusList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="";
		List <MeterStatusList> msList = new ArrayList<MeterStatusList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			PreparedStatement preparedStatement = con.prepareStatement(QueryBuilder.meterStatus);
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	msList.add(new MeterStatusList(rs.getString(1),rs.getString(2)));
	        }
	        
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return msList;
	}
	
	public List<LinemanList> mmLinemanList(User user) throws SQLException, Exception {
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="";
		List <LinemanList> lmList = new ArrayList<LinemanList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			PreparedStatement preparedStatement = con.prepareStatement(QueryBuilder.mmLinemanList);
			preparedStatement.setString(1, user.getSasseccd());
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	lmList.add(new LinemanList(rs.getString(1),rs.getString(1)));
	        }
	        
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return lmList;
	}
	
	public List<MeterMakeList> mmMeterMakeList(User user) throws SQLException, Exception {
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <MeterMakeList> mkList = new ArrayList<MeterMakeList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			String makeList = "SELECT UNIQUE STRMTRMAKE FROM "+cirUserName+".METERTRANS WHERE  STRALLOCATION='N' AND STRCANCELID='n'  AND STRMRTFLAG='N' and (STRMTRDRAWOFFCD=? OR STRISSUE_SEC=?)";
			PreparedStatement ps2 = con.prepareStatement(makeList);
			ps2.setString(1, user.getSasseccd());
			ps2.setString(2, user.getSasseccd());
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
	        	mkList.add(new MeterMakeList(rs2.getString(1),rs2.getString(1)));
	        }
			
	        con.close();
	        connection.close();
	        ers.close();
	        rs1.close();
	        rs2.close();
	        ps1.close();
	        ps2.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return mkList;
	}
	
	public String getContent(String mmake, String ids, String mphase, String realPath, User user) throws SQLException, Exception {
		
		Connection con=null;
		String meterSlno="",optionList="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			//String str1="select strmtrslno from "+cirUserName+".metertrans@intcsc_dblink  WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd ='"+user.getSasseccd()+"' or strissue_sec='"+user.getSasseccd()+"')  AND strmtrphase='"+mphase+"' AND   strmtrmake='"+mmake+"' AND TO_DATE(STRINSERTDT)>'31-JUL-10'  AND  NVL(STRMRT_SEAL1,'0')!='0' AND( STRDOCNUMBER IN(SELECT STRDOCNUMBER FROM ( SELECT STRDOCNUMBER,  COUNT(*),COUNT(STRMRT_SEAL1 ) FROM "+cirUserName+".METERTRANS@intcsc_dblink WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd ='"+user.getSasseccd()+"' or strissue_sec='"+user.getSasseccd()+"')  AND strmtrphase='"+mphase+"'  AND   strmtrmake='"+mmake+"'  AND TO_DATE(STRINSERTDT)>'31-JUL-10'  GROUP BY STRDOCNUMBER HAVING COUNT(*)=COUNT(STRMRT_SEAL1 ) )) ) UNION select strmtrslno from "+cirUserName+".metertrans@intcsc_dblink  WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd ='"+user.getSasseccd()+"' or strissue_sec='"+user.getSasseccd()+"')  AND strmtrphase='"+mphase+"' AND   strmtrmake='"+mmake+"' AND TO_DATE(STRINSERTDT)>'31-JUL-10'  AND  NVL(STRMRT_SEAL1,'0')!='0'  order by strmtrslno";
			String str="select strmtrslno from "+cirUserName+".metertrans  WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd =? or strissue_sec=?)  AND strmtrphase=? AND   strmtrmake=? AND TO_DATE(STRINSERTDT)>'31-JUL-10'  AND  NVL(STRMRT_SEAL1,'0')!='0' AND( STRDOCNUMBER IN(SELECT STRDOCNUMBER FROM ( SELECT STRDOCNUMBER,  COUNT(*),COUNT(STRMRT_SEAL1 ) FROM "+cirUserName+".METERTRANS WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd =? or strissue_sec=?)  AND strmtrphase=?  AND   strmtrmake=?  AND TO_DATE(STRINSERTDT)>'31-JUL-10'  GROUP BY STRDOCNUMBER HAVING COUNT(*)=COUNT(STRMRT_SEAL1 ) )) ) UNION select strmtrslno from "+cirUserName+".metertrans  WHERE  STRCANCELID='n' AND STRMRTFLAG='N' and NVL(strallocation,'N')='N' AND  (strmtrdrawoffcd =? or strissue_sec=?)  AND strmtrphase=? AND   strmtrmake=? AND TO_DATE(STRINSERTDT)>'31-JUL-10'  AND  NVL(STRMRT_SEAL1,'0')!='0'  order by strmtrslno";
			//System.out.println(EBSUserName+"=="+EBSPassWord+"=="+str);
			PreparedStatement preparedStatement = con.prepareStatement(str);
			preparedStatement.setString(1, user.getSasseccd());
			preparedStatement.setString(2, user.getSasseccd());
			preparedStatement.setString(3, mphase);
			preparedStatement.setString(4, mmake);
			preparedStatement.setString(5, user.getSasseccd());
			preparedStatement.setString(6, user.getSasseccd());
			preparedStatement.setString(7, mphase);
			preparedStatement.setString(8, mmake);
			preparedStatement.setString(9, user.getSasseccd());
			preparedStatement.setString(10, user.getSasseccd());
			preparedStatement.setString(11, mphase);
			preparedStatement.setString(12, mmake);
			ResultSet rs = preparedStatement.executeQuery();
	            
			meterSlno="<select id='mtrnos"+ids+"' name='mtrnos"+ids+"'  class='select2'   onFocus='this.select();' onKeyDown='if(event.keyCode==13){event.keyCode=9;return true;}' style='width:100px;' onChange='getMeterDetails1("+ids+")'><option value='0'>Select Meter No</option>";
			while(rs.next())
			{
				optionList=optionList+"<option value='"+rs.getString(1)+"'>"+rs.getString(1)+"</option>";
				//System.out.println(rs.getString(1));

		    }
			meterSlno=meterSlno+optionList+"</select>";
	        
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return meterSlno;
	}
	
public String getSealInfo(String mmake, String meterno, String mphase, String realPath, User user) throws SQLException, Exception {
		
		Connection con=null;
		String sealinfo="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			String str="select STRMRT_SEAL1,STRMRT_SEAL2,NVL(STRINIRDG,0),NVL(STRINIRDG_KVAH,0),nvl(STRMTRMF,1),trim(STRMTRCAPACITY),'x' from "+cirUserName+".metertrans WHERE NVL(strallocation,'N')='N'  and STRMTRSLNO=?  AND STRMTRMAKE=? and  (strmtrdrawoffcd =? or strissue_sec=?) and strmtrphase=?";
			//System.out.println(EBSUserName+"=="+EBSPassWord+"=="+str);
			PreparedStatement preparedStatement = con.prepareStatement(str);
			preparedStatement.setString(1, meterno);
			preparedStatement.setString(2, mmake);
			preparedStatement.setString(3, user.getSasseccd());
			preparedStatement.setString(4, user.getSasseccd());
			preparedStatement.setString(5, mphase);
			ResultSet rs = preparedStatement.executeQuery();
	            
			if(rs.next())
			 {
				sealinfo=removeNull(rs.getString(1))+"@"+removeNull(rs.getString(2))+"@"+rs.getString(3) +"@"+rs.getString(4)+"@"+rs.getString(5)+"@"+rs.getString(6)+"@"+rs.getString(7);
			 }
	        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%sealinfo=%%%%%%%%%%%%%%%%%"+sealinfo);
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return sealinfo;
	}

	public String meterAllotmentInserts(MeterAllotmentModel mam, User user, String realPath,HttpSession session) throws SQLException, Exception {
		
		Connection con=null;
		String message="",invScno="",ukscno="";
		ResultSet rsCSC=null;
		PreparedStatement ps2=null;
		boolean flag=false;
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		
		List<SessionMeterAllotment> maList=new ArrayList();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			SessionMeterAllotment ma=new SessionMeterAllotment();
			ma.setScno(mam.getScno1());
			ma.setIssuedate(mam.getIssuedate1());
			ma.setPhase(mam.getPh1());
			ma.setMeterNo(mam.getMeternos1());
			ma.setIniReading(mam.getInireading1());
			ma.setLineman(mam.getLineman1());
			ma.setSeal1(mam.getMRTseal11());
			ma.setSeal2(mam.getMRTseal21());
			ma.setCompno(mam.getCompno1());
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			if (mam.getMeterStatus().equals("03")||mam.getMeterStatus().equals("99") )
			{
				ps2 = con.prepareStatement("SELECT ctscno,CTUKSCNO FROM "+EBSUserName+".CONSUMER WHERE CTSCNO=? and ctmtrrdgstat=? and (ctukseccd=?  OR ctukseccd IS NULL) AND CTMTRPHASE=?");
				ps2.setString(1,mam.getScno1());
				ps2.setString(2,mam.getMeterStatus());
				ps2.setString(3,user.getSasseccd());
				ps2.setString(4,mam.getPh1());
				message="/Not in the Selected status";
			}
			else if(mam.getMeterStatus().equals("18"))
			{
				ps2 = con.prepareStatement("SELECT ctscno,CTUKSCNO FROM "+EBSUserName+".CONSUMER WHERE CTSCNO=? and ctukseccd=? ");
				ps2.setString(1,mam.getScno1());
				ps2.setString(2,user.getSascircd());

				PreparedStatement ps3 = con.prepareStatement("SELECT ctscno,CTUKSCNO FROM "+EBSUserName+".CONSUMER,complaint_details WHERE ctscno=scno and CTSCNO=? and (ctukseccd=?  OR ctukseccd IS NULL)  and complaint_no=? and to_date(complaint_given_on)<'23-sep-14'");
				ps3.setString(1,mam.getScno1());
				ps3.setString(2,user.getSasseccd());
				ps3.setString(3,mam.getCompno1());
				rsCSC=ps3.executeQuery();
			}
			else
			{
				ps2 = con.prepareStatement("SELECT ctscno,CTUKSCNO FROM "+EBSUserName+".CONSUMER WHERE CTSCNO=? and (ctukseccd=?  OR ctukseccd IS NULL ) AND CTMTRPHASE=? and (ctmtrrdgstat not in('11') or CTOLDSTAT not in('11')) ");
				ps2.setString(1,mam.getScno1());
				ps2.setString(2,user.getSasseccd());
				ps2.setString(3,mam.getPh1());
			}
			ResultSet rsCons=ps2.executeQuery();
			
			if(!rsCons.next())
			{
				invScno+="Please enter meter details against Exceptionals as service is in exceptional list/Invalid Service Number"+message;
				ma.setRemarks(invScno);
				flag=true;
			}
			else
			{
				if(mam.getMeterStatus().equals("18"))
				{
					if(!rsCSC.next())
					{
						invScno+=" Invalid CSC complaint number !.., try again";
						ma.setRemarks(invScno);
						flag=true;
					}
					else
						ukscno=rsCons.getString(2);
					
				}
				else
				ukscno=rsCons.getString(2);
		
			}
			PreparedStatement pstmtTrans = con.prepareStatement("select strscno,stralloc_stat from metertrans where strscno=? and to_char(TO_DATE(STRISSUEDT),'MON/YY')=TO_CHAR(TO_DATE(?,'dd/mm/yyyy'),'MON/YY') AND STRALLOCATION='Y'");
			pstmtTrans.setString(1,mam.getScno1());
			pstmtTrans.setString(2,mam.getIssuedate1());
			ResultSet rsTrans=pstmtTrans.executeQuery();
			if(rsTrans.next())
			{
				
			 //System.out.println("*********"+removeNull(rsTrans.getString(2))+"***********"+meterStatus);
				if(mam.getMeterStatus().equals("11")|| mam.getMeterStatus().equals("20")  )
				{
					if(removeNull(rsTrans.getString(2)).equals("N"))
					{
							invScno+=" Please complete Removed Meter Details for the previous allotment";
							ma.setRemarks(invScno);
							flag=true;
				
					}
					else
					{
					flag=false;
					}
				
				}
				else
				{
					invScno+=" Already Entered in this Month";
					ma.setRemarks(invScno);
					flag=true;
				}
			}
			if(flag)
			{
				maList.add(ma);
			}
			else
			{
				//String str1="UPDATE METERTRANS SET STRSCNO='"+mam.getScno1()+"',STRISSUE_SEC='"+user.getSascircd()+"',STRSCNO_REPLSTAT='"+mam.getMeterStatus()+"', STRISSUE_ENTDT=SYSDATE,STRINIRDG='"+inireading+"',STRISSUE_LINEMAN='"+lineman+"',STRMRT_SEAL1='"+seal1+"',STRMRT_SEAL2='"+seal2+"',STRALLOCATION='Y',STRISSUEDT=to_date('"+issuedate+"','dd/mm/yyyy'),STRUKSCNO='"+ukscno+"',STRCSCREGNO='"+compno+"'   WHERE STRMTRSLNO='"+mtrno+"'  AND STRCANCELID='n'  AND STRMRTFLAG='N' and STRMTRMAKE='"+metermake+"' and strmtrphase='"+ph+"' and (STRMTRDRAWOFFCD= '"+seccd+"' OR STRISSUE_SEC='"+seccd+"') " ;
				//System.out.println(str1);
				java.sql.PreparedStatement pstmt = con.prepareStatement("UPDATE METERTRANS SET STRSCNO=?,STRISSUE_SEC=?,STRSCNO_REPLSTAT=?, STRISSUE_ENTDT=SYSDATE,STRINIRDG=?,STRISSUE_LINEMAN=?,STRMRT_SEAL1=?,STRMRT_SEAL2=?,STRALLOCATION='Y',STRISSUEDT=to_date(?,'dd/mm/yyyy') , STRUKSCNO=?,STRCSCREGNO=?  WHERE STRMTRSLNO=?  AND STRCANCELID='n'  AND STRMRTFLAG='N' AND STRMTRMAKE=?  and strmtrphase=? and  (STRMTRDRAWOFFCD= ? OR STRISSUE_SEC=?) ");

				pstmt.setString(1,mam.getScno1());
				pstmt.setString(2,user.getSasseccd());
				pstmt.setString(3,mam.getMeterStatus());
				pstmt.setString(4,mam.getInireading1());
				pstmt.setString(5,mam.getLineman1());
				pstmt.setString(6,mam.getMRTseal11());
				pstmt.setString(7,mam.getMRTseal21());
				pstmt.setString(8,mam.getIssuedate1());
				pstmt.setString(9,ukscno);
				pstmt.setString(10,mam.getCompno1());
				pstmt.setString(11,mam.getMeternos1());
				pstmt.setString(12,mam.getMetermake1());
				pstmt.setString(13,mam.getPh1());
				pstmt.setString(14,user.getSasseccd());
				pstmt.setString(15,user.getSasseccd());

				pstmt.executeUpdate();
							
				con.commit();
			}	
			
			flag=false;
			con.setAutoCommit(false);
			connection.close();
			con.close();
			if(maList.size()>0)
			{
				session.setAttribute("maList",maList);
				message = "E@"+"Meter Allotment Contains Errors ! Please Check Remarks Column";
				//System.out.println(errormsg);
				//response.sendRedirect("MeterAllotmentErrors.jsp?errormsg="+errormsg+"&meterStatus="+meterStatus+"&heading="+heading);
			}
			else
			{
				message="S@Meters Alloted Successfully";
				//System.out.println(msgsuccess);
				//response.sendRedirect("MeterAllotment.jsp?msg="+msgsuccess);
			}
		} catch (SQLException e) {
			message="NO";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
	
public String getLedDate(User user, String area, String meterStatus) throws SQLException, Exception {
		
		Connection con=null;
		DecimalFormat df = new DecimalFormat("0.00");
		String sealinfo="",str="",sq="",leddt="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
			PreparedStatement stmtdt=con.prepareStatement("SELECT TO_CHAR(LEDMONTH,'DD/MM/RRRR') FROM BILLCONTROL");
			ResultSet rsdt=stmtdt.executeQuery();
			
			if (rsdt.next())
				leddt=rsdt.getString(1);
			
			
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        rsdt.close();
	        stmtdt.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return leddt;
	}
	
public List<MeterAllotmentAutoList> meterAllotmentAutoList(User user, String area, String meterStatus) throws SQLException, Exception {
		
		Connection con=null;
		DecimalFormat df = new DecimalFormat("0.00");
		String sealinfo="",str="",sq="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		List <MeterAllotmentAutoList> maaList = new ArrayList<MeterAllotmentAutoList>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
			PreparedStatement stmtdt=con.prepareStatement("SELECT TO_CHAR(LEDMONTH,'DD/MM/RRRR') FROM BILLCONTROL");
			ResultSet rsdt=stmtdt.executeQuery();
			String leddt="";
			if (rsdt.next())
				leddt=rsdt.getString(1);
			
			if(area.equals("All"))
			{
				sq="AND NVL(ctareacd,' ') like '%'";
			} else {
				sq="AND NVL(ctareacd,' ') like '"+area+"'";
			}
			
			String dt_head=meterStatus.equals("18") ? "Complaint": "Exception" ;
			
			if(meterStatus.equals("16"))
			{
				str="select ctscno,CTCONLD,CTMTRPHASE,'' CTMTREXPDT,CTUKSCNO,CTCAT from consumer_ebs,SECTION where CTSECCD=SECCODE AND UKSECCD=? "+sq+" AND NVL(CTMTRMAKE,'NON HIGHQUALITY')='NON HIGHQUALITY' AND NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS WHERE CTSCNO=STRSCNO  AND STRALLOCATION='Y'  AND STRISSUE_SEC=CTUKSECCD ) ORDER BY GREATEST(NVL(CTAVERUNITS,0),NVL(CTOLDAVERUNITS,0))";
				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				rs = preparedStatement.executeQuery();
			}
			else if(meterStatus.equals("18"))
			{
				str="select ctscno,GREATEST(NVL(REQD_LOAD,nvl(TOTAL_LOAD,0)),NVL(CTCONLD,0),NVL(CTCTRLD,0)), NVL(REQPH,CTMTRPHASE), TO_CHAR(complaint_given_on,'DD/MM/YYYY'),CTUKSCNO,CTCAT FROM CONSUMER_EBS,SECTION,(select *  from complaint_details where COMPLAINT_NATUREID IN ('49','50')  AND seccd=? and to_date(complaint_given_on)>='01-jan-11' and status =4) C,(select *  from COMPLAINT_DETAILS_STATUS where  status=4)cd WHERE cd.regno=c.complaint_no and CTSCNO =SCNO AND CTSECCD=SECCODE "+sq+" AND  UKSECCD=? AND  NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS WHERE CTSCNO=STRSCNO  AND STRISSUE_SEC=CTUKSECCD AND STRALLOCATION='Y' AND TO_DATE(NVL(complaint_given_on,'01-JAN-54')) <=TO_DATE(STRISSUEDT) and strscno_replstat=? )  ORDER BY to_Date(complaint_given_on),CTAREACD,CTSCNO";
				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				preparedStatement.setString(2, user.getSasseccd());
				preparedStatement.setString(3, meterStatus);
				rs = preparedStatement.executeQuery();
			}
			else if(meterStatus.equals("11")){
				str="SELECT CTSCNO, GREATEST(NVL(CTCONLD,0),NVL(CTCTRLD,0)), CTMTRPHASE,TO_CHAR(TO_DATE(CTMTREXPDT),'DD/MM/YYYY'),CTUKSCNO,CTCAT FROM CONSUMER_EBS c,SECTION A,sas.sas_bill_burnt_complaints s WHERE CTUKSECCD=A.UKSECCD and s.UKSCNO=c.ctukscno AND  S.RECORD_STATUS='ACTIVE' AND NVL(CCNO,'-')='-'   and c.CTUKSECCD=s.UKSECCD  "+sq+" and APPROVE_STAT='Y' AND  A.UKSECCD=? AND CTBILSTAT=? AND NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS WHERE CTSCNO=STRSCNO AND STRISSUE_SEC=CTUKSECCD AND STRALLOCATION='Y' AND TO_DATE(NVL(CTMTREXPDT,'01-JAN-54'))<=TO_DATE(STRISSUEDT) ) AND (TO_DATE(BTBLDT)>(SELECT TO_DATE(LEDMONTH) FROM BILLCONTROL) OR NVL(CTCLUBREL,'LT')='CH') ORDER BY CTAREACD,CTSCNO,TO_DATE(CTMTREXPDT)";
				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				preparedStatement.setString(2, meterStatus);
				rs = preparedStatement.executeQuery();
			}
			else
			{ 
				str="SELECT CTSCNO, GREATEST(NVL(CTCONLD,0),NVL(CTCTRLD,0)), CTMTRPHASE,TO_CHAR(TO_DATE(CTMTREXPDT),'DD/MM/YYYY'),CTUKSCNO,CTCAT FROM CONSUMER_EBS,SECTION WHERE CTUKSECCD=UKSECCD "+sq+" AND  UKSECCD=? AND CTBILSTAT=? AND NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS WHERE CTSCNO=STRSCNO AND STRISSUE_SEC=CTUKSECCD AND STRALLOCATION='Y' AND TO_DATE(NVL(CTMTREXPDT,'01-JAN-54'))<=TO_DATE(STRISSUEDT) ) AND (TO_DATE(BTBLDT)>(SELECT TO_DATE(LEDMONTH) FROM BILLCONTROL) OR NVL(CTCLUBREL,'LT')='CH') ORDER BY CTAREACD,CTSCNO,TO_DATE(CTMTREXPDT)";
				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				preparedStatement.setString(2, meterStatus);
				rs = preparedStatement.executeQuery();
			}
			
	        while(rs.next())
			 {
				maaList.add(new MeterAllotmentAutoList(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),"","",""));
		     }
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return maaList;
	}

	
	public List<MeterAllotmentAutoList> meterAllotmentStuckupAutoList(User user, String area, String meterStatus) throws SQLException, Exception {
		
		Connection con=null;
		DecimalFormat df = new DecimalFormat("0.00");
		String sealinfo="",str="",sq="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		List <MeterAllotmentAutoList> maaList = new ArrayList<MeterAllotmentAutoList>();
		PreparedStatement preparedStatement = null,ps3=null;
		ResultSet rs = null,rs2=null;
		int mtrcount=0;
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
			PreparedStatement stmtdt=con.prepareStatement("SELECT TO_CHAR(LEDMONTH,'DD/MM/RRRR') FROM "+EBSUserName+".BILLCONTROL@intcsc_dblink");
			ResultSet rsdt=stmtdt.executeQuery();
			String leddt="";
			
			if (rsdt.next())
				leddt=rsdt.getString(1);
			
			if(area.equals("All"))
			{
				sq="AND NVL(ctareacd,' ') like '%'";
			} else {
				sq="AND NVL(ctareacd,' ') like '"+area+"'";
			}
			
			String dt_head=meterStatus.equals("02") ? "Complaint": "Exception" ;
			
			if(meterStatus.equals("02"))
			{
				str="select ctscno,GREATEST(NVL(ctload,0)), NVL(CTMTRPHASE,1), TO_CHAR(complaint_given_on,'DD/MM/YYYY'),CTUKSCNO,CTCAT ,complaint_no,reqd_load FROM "+EBSUserName+".CONSUMER_EBS@intcsc_dblink,"+EBSUserName+".SECTION@intcsc_dblink,(select A.reqd_load,a.complaint_no,A.SCNO,a.seccd,A.complaint_given_on,A.COMPLAINT_NATUREID,A.status,SAS_FLAG from complaint_details A where  A.COMPLAINT_NATUREID IN ('21') AND RECORD_STATUS='ACTIVE' and to_date(A.complaint_given_on)>='01-APR-14') C WHERE CTSCNO =SCNO AND CTSECCD=SECCODE and ctareacd like '"+area+"' AND  UKSECCD=? AND  NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS@intcsc_dblink WHERE CTSCNO=STRSCNO  AND STRALLOCATION='Y' AND TO_DATE(NVL(complaint_given_on,'01-JAN-54'))<=TO_DATE(STRISSUEDT) and strscno_replstat=? ) and status not in('4','6') and SAS_FLAG='S' ORDER BY to_Date(complaint_given_on),CTAREACD,CTSCNO";
				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				preparedStatement.setString(2, meterStatus);
				rs = preparedStatement.executeQuery();
			}
						
	        while(rs.next())
			 {
				maaList.add(new MeterAllotmentAutoList(rs.getString(1),rs.getString(2),rs.getString(3),"",rs.getString(5),rs.getString(6),rs.getString(4),rs.getString(7),rs.getString(8)));
		     }
	      	       
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return maaList;
	}
	
	
public List<StuckupDataList> getStuckupDataList(User user, String area, String meterStatus) throws SQLException, Exception {
		
		Connection con=null;
		DecimalFormat df = new DecimalFormat("0.00");
		String sealinfo="",str="",sq="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <StuckupDataList> sdList = new ArrayList<StuckupDataList>();
		PreparedStatement preparedStatement = null,ps3=null;
		ResultSet rs = null,rs2=null;
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
	        String str1="SELECT STRMTRSLNO MTRNO,NVL(STRMTRPHASE,'1') STRMTRPHASE,STRMRT_SEAL1 mrtseal1,STRMRT_SEAL2 mrtseal2 FROM "+cirUserName+".METERTRANS@intcsc_dblink WHERE  STRALLOCATION='N' AND STRCANCELID='n'  AND STRMRTFLAG='N' and (STRMTRDRAWOFFCD= '"+user.getSasseccd()+"' OR STRISSUE_SEC='"+user.getSasseccd()+"') ORDER BY MTRNO";
	        ps3 = con.prepareStatement(str1);
			rs2 = ps3.executeQuery();
			while(rs2.next())
			{
				sdList.add(new StuckupDataList(rs2.getString(1),rs2.getString(3),rs2.getString(4),rs2.getString(2)));
			}
			
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return sdList;
	}


	public String meterAllotmentStuckupInserts(MeterAllotmentStuckupModel mam, User user, String realPath,HttpSession session) throws SQLException, Exception {
		
		Connection con=null;
		String message="",invScno="",ukscno="";
		ResultSet rsCSC=null;
		PreparedStatement ps2=null;
		boolean flag=false;
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		
		List<SessionMeterAllotment> maList=new ArrayList();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			SessionMeterAllotment ma=new SessionMeterAllotment();
			ma.setScno(mam.getScno());
			ma.setIssuedate(mam.getIssuedate());
			ma.setPhase(mam.getPh());
			ma.setMeterNo(mam.getMtrnos());
			ma.setIniReading(mam.getInireading());
			ma.setLineman(mam.getLineman());
			ma.setSeal1(mam.getMRTseal1());
			ma.setSeal2(mam.getMRTseal2());
			ma.setCompno(mam.getCompno());
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
			
			
			 if(mam.getChk()==1)
			  {
				//String str1="UPDATE METERTRANS SET STRSCNO='"+mam.getScno1()+"',STRISSUE_SEC='"+user.getSascircd()+"',STRSCNO_REPLSTAT='"+mam.getMeterStatus()+"', STRISSUE_ENTDT=SYSDATE,STRINIRDG='"+inireading+"',STRISSUE_LINEMAN='"+lineman+"',STRMRT_SEAL1='"+seal1+"',STRMRT_SEAL2='"+seal2+"',STRALLOCATION='Y',STRISSUEDT=to_date('"+issuedate+"','dd/mm/yyyy'),STRUKSCNO='"+ukscno+"',STRCSCREGNO='"+compno+"'   WHERE STRMTRSLNO='"+mtrno+"'  AND STRCANCELID='n'  AND STRMRTFLAG='N' and STRMTRMAKE='"+metermake+"' and strmtrphase='"+ph+"' and (STRMTRDRAWOFFCD= '"+seccd+"' OR STRISSUE_SEC='"+seccd+"') " ;
				//System.out.println(str1);
				 String str="update METERTRANS set STRALLOCATION='Y',STRSCNO=?,STRISSUEDT=to_date(?,'dd/mm/yyyy'),STRISSUE_LINEMAN=?,	STRSCNO_REPLSTAT=?,STRISSUE_ENTDT=SYSDATE, STRUKSCNO=?, STRISSUE_SEC=?,	STRINIRDG=?,STRMRT_SEAL1=?,STRMRT_SEAL2=?,STRCSCREGNO=? where STRMTRSLNO=? AND STRMTRMAKE=? AND STRMTRPHASE=? AND STRCANCELID='n' AND STRMRTFLAG='N' AND (STRISSUE_SEC=? OR STRMTRDRAWOFFCD=?)";
				java.sql.PreparedStatement pstmt = con.prepareStatement("UPDATE METERTRANS SET STRSCNO=?,STRISSUE_SEC=?,STRSCNO_REPLSTAT=?, STRISSUE_ENTDT=SYSDATE,STRINIRDG=?,STRISSUE_LINEMAN=?,STRMRT_SEAL1=?,STRMRT_SEAL2=?,STRALLOCATION='Y',STRISSUEDT=to_date(?,'dd/mm/yyyy') , STRUKSCNO=?,STRCSCREGNO=?  WHERE STRMTRSLNO=?  AND STRCANCELID='n'  AND STRMRTFLAG='N' AND STRMTRMAKE=?  and strmtrphase=? and  (STRMTRDRAWOFFCD= ? OR STRISSUE_SEC=?) ");
					//,STRINIRDG_KWHEXP='"+expinirdg+"',STRINIRDG_KVAHEXP='"+expinirdg_kvah+"'
				pstmt.setString(1,mam.getScno());
				pstmt.setString(2,mam.getIssuedate());
				pstmt.setString(3,mam.getLineman());
				pstmt.setString(4,mam.getMeterStatus());
				pstmt.setString(5,mam.getUkscno());
				pstmt.setString(6,user.getSasseccd());
				pstmt.setString(7,mam.getInireading());
				pstmt.setString(8,mam.getMRTseal1());
				pstmt.setString(9,mam.getMRTseal2());
				pstmt.setString(10,mam.getCompno());
				pstmt.setString(11,mam.getMtrnos());
				pstmt.setString(12,mam.getMetermake());
				pstmt.setString(13,mam.getPh());
				pstmt.setString(14,user.getSasseccd());
				pstmt.setString(15,user.getSasseccd());
	
				pstmt.executeUpdate();
							
				con.commit();
			}	
			
			flag=false;
			con.setAutoCommit(false);
			connection.close();
			con.close();
			if(maList.size()>0)
			{
				session.setAttribute("maList",maList);
				message = "E@"+"Meter Allotment Contains Errors ! Please Check Remarks Column";
				//System.out.println(errormsg);
				//response.sendRedirect("MeterAllotmentErrors.jsp?errormsg="+errormsg+"&meterStatus="+meterStatus+"&heading="+heading);
			}
			else
			{
				message="S@Meters Alloted Successfully";
				//System.out.println(msgsuccess);
				//response.sendRedirect("MeterAllotment.jsp?msg="+msgsuccess);
			}
		} catch (SQLException e) {
			message="NO";
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return message;
	}
	
	
public List<MeterAllotmentAutoList> meterAllotmentBurntAutoList(User user, String area, String meterStatus) throws SQLException, Exception {
		
		Connection con=null;
		DecimalFormat df = new DecimalFormat("0.00");
		String sealinfo="",str="",sq="";
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <AreaList> areaList = new ArrayList<AreaList>();
		List <MeterAllotmentAutoList> maaList = new ArrayList<MeterAllotmentAutoList>();
		PreparedStatement preparedStatement = null,ps3=null;
		ResultSet rs = null,rs2=null;
		int mtrcount=0;
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			
			PreparedStatement stmtdt=con.prepareStatement("SELECT TO_CHAR(LEDMONTH,'DD/MM/RRRR') FROM "+EBSUserName+".BILLCONTROL@intcsc_dblink");
			ResultSet rsdt=stmtdt.executeQuery();
			String leddt="";
			
			if (rsdt.next())
				leddt=rsdt.getString(1);
			
			if(area.equals("All"))
			{
				sq="AND NVL(ctareacd,' ') like '%'";
			} else {
				sq="AND NVL(ctareacd,' ') like '"+area+"'";
			}
			
			String dt_head=meterStatus.equals("11") ? "Complaint": "Exception" ;
			
			if(meterStatus.equals("11"))
			{
				//str="select ctscno,GREATEST(NVL(ctload,0)), NVL(CTMTRPHASE,1), TO_CHAR(complaint_given_on,'DD/MM/YYYY'),CTUKSCNO,CTCAT ,complaint_no,reqd_load FROM "+EBSUserName+".CONSUMER_EBS@intcsc_dblink,"+EBSUserName+".SECTION@intcsc_dblink,(select A.reqd_load,a.complaint_no,A.SCNO,a.seccd,A.complaint_given_on,A.COMPLAINT_NATUREID,A.status,SAS_FLAG from complaint_details A where  A.COMPLAINT_NATUREID IN ('21') AND RECORD_STATUS='ACTIVE' and to_date(A.complaint_given_on)>='01-APR-14') C WHERE CTSCNO =SCNO AND CTSECCD=SECCODE and ctareacd like '"+area+"' AND  UKSECCD=? AND  NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS@intcsc_dblink WHERE CTSCNO=STRSCNO  AND STRALLOCATION='Y' AND TO_DATE(NVL(complaint_given_on,'01-JAN-54'))<=TO_DATE(STRISSUEDT) and strscno_replstat=? ) and status not in('4','6') and SAS_FLAG='S' ORDER BY to_Date(complaint_given_on),CTAREACD,CTSCNO";
				str="select ctscno,GREATEST(NVL(ctload,0)), NVL(CTMTRPHASE,1), TO_CHAR(complaint_given_on,'DD/MM/YYYY'),CTUKSCNO,CTCAT ,complaint_no,reqd_load FROM "+EBSUserName+".CONSUMER_EBS@intcsc_dblink,"+EBSUserName+".SECTION@intcsc_dblink,(select A.reqd_load,a.complaint_no,A.UKSCNO,a.seccd,A.complaint_given_on,A.COMPLAINT_NATUREID,A.status from complaint_details A where  A.COMPLAINT_NATUREID IN ('19','22','93','94','95','96','97','98','99','100','101','102','103')  and to_date(A.complaint_given_on)>='01-APR-14' AND A.SAS_FLAG='B' AND RECORD_STATUS='ACTIVE') C WHERE CTUKSCNO =trim(UKSCNO) AND CTSECCD=SECCODE and nvl(ctareacd,' ') like '"+area+"' AND  UKSECCD=? AND  NOT EXISTS(SELECT STRSCNO FROM "+cirUserName+".METERTRANS@intcsc_dblink WHERE CTSCNO=STRSCNO  AND STRALLOCATION='Y' AND TO_DATE(NVL(complaint_given_on,'01-JAN-54'))<=TO_DATE(STRISSUEDT) and strscno_replstat=? ) and status not in('4','6')  ORDER BY to_Date(complaint_given_on),CTAREACD,CTSCNO";

				preparedStatement = con.prepareStatement(str);
				preparedStatement.setString(1, user.getSasseccd());
				preparedStatement.setString(2, meterStatus);
				rs = preparedStatement.executeQuery();
			}
						
	        while(rs.next())
			 {
				maaList.add(new MeterAllotmentAutoList(rs.getString(1),rs.getString(2),rs.getString(3),"",rs.getString(5),rs.getString(6),rs.getString(4),rs.getString(7),rs.getString(8)));
		     }
	      	       
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return maaList;
	}

	public List<MeterStatusList> meterStatusCancelList(User user) throws SQLException, Exception {
		if(user.getSasusertype().equals("5"))
			offtype = "A.office_id";
		
		if(user.getSasusertype().equals("6"))
			offtype = "C.SUBDIVCD";
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="";
		List <MeterStatusList> msList = new ArrayList<MeterStatusList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			PreparedStatement preparedStatement = con.prepareStatement(QueryBuilder.meterStatusForCancel);
			ResultSet rs = preparedStatement.executeQuery();
	            
	        while (rs.next()) {
	        	msList.add(new MeterStatusList(rs.getString(1),rs.getString(2)));
	        }
	        
	        con.close();
	        connection.close();
	        ers.close();
	        rs.close();
	        preparedStatement.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return msList;
	}

public List<MeterMakeList> mmMeterMakeTotalList(User user) throws SQLException, Exception {
		
		Connection con=null;
		String EBSUserName="",EBSPassWord="",eroname="",cirUserName="",cirPassWord="";
		List <MeterMakeList> mkList = new ArrayList<MeterMakeList>();
		
		try (Connection connection = SASDB.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(QueryBuilder.ebs_credencials);) {
			
			ps.setString(1, user.getSaserono());
			ResultSet ers = ps.executeQuery();
			
			if(ers.next())
			{
				EBSUserName=removeNull(ers.getString(1));
				EBSPassWord=removeNull(ers.getString(2));
				eroname=removeNull(ers.getString(3));
			}
			
			PreparedStatement ps1 = connection.prepareStatement(QueryBuilder.getCircleUserName);
			ps1.setString(1, user.getSascircd());
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				cirUserName=rs1.getString(1);
				cirPassWord=rs1.getString(2);
			}
			
			con=SASDB.getConnection("EBS",EBSUserName,EBSPassWord);
			//String makeList = "SELECT UNIQUE STRMTRMAKE FROM "+cirUserName+".METERTRANS WHERE  STRALLOCATION='N' AND STRCANCELID='n'  AND STRMRTFLAG='N' and (STRMTRDRAWOFFCD=? OR STRISSUE_SEC=?)";
			String makeList = "SELECT METERMAKE FROM METERMAKE ORDER BY METERMAKE";
			PreparedStatement ps2 = con.prepareStatement(makeList);
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
	        	mkList.add(new MeterMakeList(rs2.getString(1),rs2.getString(1)));
	        }
			
	        con.close();
	        connection.close();
	        ers.close();
	        rs1.close();
	        rs2.close();
	        ps1.close();
	        ps2.close();
		} catch (SQLException e) {
			printSQLException(e);
	    } catch (Exception e1) {
			e1.printStackTrace();
		}
		return mkList;
	}

}
