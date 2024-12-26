package com.tsspdcl.sas.controller;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;

import com.tsspdcl.sas.dao.nsts.NstsDAO;
import com.tsspdcl.sas.dao.nsts.NstsTestReportDAO;
import com.tsspdcl.sas.entity.nsts.NewRegistrations;
import com.tsspdcl.sas.entity.nsts.PendingConnections;
import com.tsspdcl.sas.entity.nsts.UploadTestReportModel;
import com.tsspdcl.sas.entity.nsts.UploadTestReportNewModel;
import com.tsspdcl.sas.entity.nsts.WebRegistrations;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.entity.nsts.AGLRegistrations;
import com.tsspdcl.sas.entity.nsts.AddConnections;
import com.tsspdcl.sas.entity.nsts.AglTobeReleasedModel;
import com.tsspdcl.sas.entity.nsts.DocumentsVerification;
import com.tsspdcl.sas.entity.nsts.LTMSendOTPtoConsumer;
import com.tsspdcl.sas.entity.nsts.LTMsmsSendToLineman;
import com.tsspdcl.sas.entity.nsts.LTMtestReport;

@Controller
@RequestMapping("/nsts")
public class NSTSController {
	
	@Autowired   
	private MessageSource messageSource; 
		
	NstsDAO nstsDAO = new NstsDAO();
	//NstsTestReportDAO nstspDAO = new NstsTestReportDAO();
	
	//@Autowired
	//private Environment environment;
		
	/*@GetMapping("/dashboard")
    public String dashboard(@RequestParam("menuId") int menuId, Model model, HttpServletRequest request, HttpSession session){
        System.out.println("report id..."+menuId);
    	model.addAttribute("menuId", menuId);
        model.addAttribute("userData", session.getAttribute("userData"));
    	System.out.println("context path..."+environment.getProperty("server.context-path"));
    	//return environment.getProperty("server.context-path") + "/dashboard";
        return "dashboard";
    }*/
	
	@GetMapping("/newregistrations/{linkId}/{menuId}")
	public String newRegistrationsList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <NewRegistrations> newRegList = nstsDAO.getAllNewRegistrations((User) session.getAttribute("userData"));
		//System.out.println("reglist..."+newRegList);
		//show(newRegList);
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("reglist", newRegList);
		return "nsts/newregistrations";
	}
	
	@GetMapping("/webregistrations/{linkId}/{menuId}")
	public String webRegistrationsList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <WebRegistrations> webRegList = nstsDAO.getWebRegistrations((User)session.getAttribute("userData"));
		
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("webreglist", webRegList);
		return "nsts/webregistrations";
	}
	
	@GetMapping("/pendingconnections/{linkId}/{menuId}")
	public String pendingConnectionsList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <PendingConnections> pendingConsList = nstsDAO.getPendingConnections((User)session.getAttribute("userData"));
		
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("pendingconslist", pendingConsList);
		return "nsts/pendingconnections";
	}
	
	@GetMapping("/estimationrequired/{linkId}/{menuId}")
	public String estimationRequiredList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <PendingConnections> estRequiredList = nstsDAO.getEstimationRequiredList((User)session.getAttribute("userData"));
		
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("estreqlist", estRequiredList);
		return "nsts/estimationrequired";
	}
	
	@GetMapping("/rejectedconnections/{linkId}/{menuId}")
	public String rejectedConnectionsList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <PendingConnections> rejectedConsList = nstsDAO.getRejectedConnections((User)session.getAttribute("userData"));
		
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("rejconslist", rejectedConsList);
		return "nsts/rejectedconnections";
	}
	
	@RequestMapping(value = "/releasednrs", method = RequestMethod.POST)
	public ResponseEntity<?> handleForm(@RequestParam("startDate") String startDate,
	    @RequestParam("endDate") String endDate) {
		
		return ResponseEntity.ok().body(startDate+" "+endDate);
	}
	
	@GetMapping("/releasednrs/{linkId}/{menuId}")
	public String releasedNewServicesList(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session){
		return "nsts/releasednrs";
	}
	
	@GetMapping("/nrapplicationstatus/{pagetype}/{menuId}")
	public String NRApplicationStatus(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		//List <DocumentsVerification> docsList = nstsDAO.getDocumentsVerification((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "New Registration Application Status");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		//model.addAttribute("docslist", docsList);
		return "nsts/nrapplicationstatus";
	}
	
	@GetMapping("/documentsverification/{pagetype}/{menuId}")
	public String DocumentsVerification(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <DocumentsVerification> docsList = nstsDAO.getDocumentsVerification((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Verification of Documents");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/documentsverification";
	}
	
	@GetMapping("/getAssignToLineman/{pagetype}/{menuId}")
	public String getAssignToLineman(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <DocumentsVerification> docsList = nstsDAO.getAssignToLineman((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Assign To Lineman");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/documentsverification";
	}
	
	@GetMapping("/getFieldVerificationList/{pagetype}/{menuId}")
	public String getFieldVerificationList(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <DocumentsVerification> docsList = nstsDAO.getFieldVerificationList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Field Verification");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/documentsverification";
	}
	
	@GetMapping("/getNRTestReport/{pagetype}/{menuId}")
	public String nrtestreport(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <DocumentsVerification> docsList = nstsDAO.nrTestreport((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "NR Test Report");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/documentsverification";
	}
	
	@GetMapping("/nscstobereleased/{pagetype}/{menuId}")
	public String NscsTobeReleased(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <DocumentsVerification> docsList = nstsDAO.getNscsTobeReleasedList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Final Action by AE");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/nrtestreportupload";
	}
	
	/*@GetMapping("/fieldverification/{menuId}")
	public String FieldVerification(@PathVariable int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		List <DocumentsVerification> docsList = nstsDAO.getFieldVerification((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Field Verification");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", docsList);
		return "nsts/documentsverification";
	}*/
	
	@RequestMapping(value = "/getRegistrationDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getRegistrationDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request) throws SQLException, Exception {
		//System.out.println("Registration ID..."+regId);
		//System.out.println(nstsDAO.getRegData(regId));
		//System.out.println("context path..."+request.getContextPath());
		//System.out.println("request path..."+request.getServletContext().getRealPath(""));
		//System.out.println("real path message resource..."+messageSource.getMessage("sas.realpath", null, Locale.ENGLISH));
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getRegData(regId, realPath, pagetype));
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getDDDetails", method = RequestMethod.POST)
	public ResponseEntity<?> getDDDetails(@RequestParam("regid") String regId,
		HttpServletRequest request) throws SQLException, Exception {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+regId+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		return ResponseEntity.ok().body(nstsDAO.getDDDetails(regId, realPath));
	}
	
	@RequestMapping(value = "/getRejectedDocsList", method = RequestMethod.POST)
	public ResponseEntity<?> getRejectedDocsList(HttpServletRequest request) throws SQLException, Exception {
		StringBuilder docsList = new StringBuilder();
		//System.out.println("calling getrejecteddocslist");
		HashMap<String,String> rejdocs = nstsDAO.getRejectedDocsList();
		docsList.append("<option value='x'>Choose one/Multiple</option>");
		//System.out.println("rejdocs..."+rejdocs);
		rejdocs.forEach((key, value) -> {
			docsList.append("<option value='" + key + "'>" + value + "</option>");
		});
		
		return ResponseEntity.ok().body(docsList);
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getLinemenList", method = RequestMethod.POST)
	public ResponseEntity<?> getLinemenList(@RequestParam("empid") String empid, 
			HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder lineMenList = new StringBuilder();
		HashMap<String,String> linemen = new HashMap<>();
		String phoneno = "";
		//System.out.println("empid...."+empid);
		//System.out.println(linemen);
		if(empid.equals("null")) {
			linemen = nstsDAO.getLinemenList((User)session.getAttribute("userData"));
			lineMenList.append("<option value='x'>Choose Lineman</option>");
	
			linemen.forEach((key, value) -> {
				lineMenList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			return ResponseEntity.ok().body(lineMenList);
			//return nstsDAO.getRegData(regId);
		} else {
			phoneno = nstsDAO.getLinemenPhone(empid, (User)session.getAttribute("userData"));
			//System.out.println("phoneno...."+phoneno); 
			return ResponseEntity.ok().body(phoneno);
		}	
	}
	
	@GetMapping(value = "/getSAPSanctionLetter/{sapestno}")
	public void getDocument(HttpServletResponse response, 
			HttpServletRequest request,
			@PathVariable("sapestno") String sapestno) throws Exception {
		/*response.setContentType("application/pdf");
		InputStream inputStream = nstsDAO.selectFile(sapestno);
		if (inputStream != null)
			IOUtils.copy(inputStream, response.getOutputStream());
		*/
		//System.out.println("context path...");
		
		System.out.println("SAP Sanction Letter..."+sapestno);
	}
	
	@GetMapping(value = "/getPDF/{pagetype}/{menuId}/{nrregno}")
	public void getPDF(HttpServletResponse response, 
	        @PathVariable int menuId, @PathVariable String pagetype, @PathVariable String nrregno, HttpSession session) throws Exception {
		Document document = new Document(PageSize.A4);
	    try {
	        // Set response headers and content type
	        response.setHeader("Content-Disposition", "inline; filename=TEST.pdf");
	        response.setContentType("application/pdf");

	        // Create a new PDF document
	        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
	        document.open();

	        // Fonts
	        Font font1 = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font font2 = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
			Font font3 = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
			Font font4 = new Font(FontFamily.COURIER, 10, Font.BOLD);
			Font font5 = new Font(FontFamily.TIMES_ROMAN, 16, Font.BOLD);
			Font font6 = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font font7 = new Font(FontFamily.HELVETICA, 8, Font.NORMAL);

	        // Header Table
	        PdfPTable headerTable = new PdfPTable(1);
	        headerTable.setWidthPercentage(100f);
	        //headerTable.setSpacingBefore(5f);
	        
	      	PdfPCell headCell = new PdfPCell();
	      	Paragraph headText1=new Paragraph("SOUTHERN POWER DISTRBUTION COMPANY OF TELANGANA LTD.",font2);
	      	headText1.setAlignment(Element.ALIGN_CENTER);
	      	headText1.setSpacingAfter(3f);
	      	headCell.setBorder(PdfPCell.NO_BORDER);
			headCell .addElement(headText1);
			

			/*Paragraph headText2=new Paragraph("CORPORATE OFFICE, MINT COMPOUND :: HYDERABAD-63",font2);
			headText2.setAlignment(Element.ALIGN_CENTER);
			headText2.setSpacingAfter(5f);
			headCell .addElement(headText2);
			*/
			
			Paragraph headText3=new Paragraph("OPERATION",font2);
			headText3.setAlignment(Element.ALIGN_CENTER);
			headText3.setSpacingAfter(5f);
			headCell .addElement(headText3);
			
	      	headCell.setBorder(PdfPCell.NO_BORDER);
	        headCell.setPadding(0); 
			//rightCell .setPaddingLeft(5);
			//headCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        headCell.setBorder(Rectangle.OUT_BOTTOM);
	        headCell.setBorder(Rectangle.OUT_TOP);
	        //headCell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        headCell.setBorderColor(BaseColor.BLACK); // Optional: Set border color
	        headCell.setPadding(5);
			headerTable.addCell(headCell);
			
			//headerTable.setWidths(new int[]{500});
			document.add(headerTable);
			
			PdfPTable sampleTable = new PdfPTable(2);
	        sampleTable.setWidthPercentage(100);
	        sampleTable.setWidths(new int[]{60, 40});
	        //sampleTable.setSpacingBefore(5f);
	        sampleTable.setSpacingAfter(3f);
	        
	        Font tfont = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
	        PdfPCell cellOne = new PdfPCell(new Phrase("WBS NO :"+" E2023650113010370"+", Amount : 240063",tfont));
	        PdfPCell cellTwo = new PdfPCell(new Phrase(nrregno,tfont));

	        cellOne.setBorder(PdfPCell.NO_BORDER);
	        sampleTable.addCell(cellOne);
	        cellTwo.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        cellTwo.setBorder(PdfPCell.NO_BORDER);

	        // Add the second cell to the table
	        sampleTable.addCell(cellTwo);
	        document.add(sampleTable);
			
			//===========body title==============
			PdfPTable titleTable = new PdfPTable(3);
			titleTable.setWidthPercentage(100);
			titleTable.setWidths(new int[]{35, 30, 35});
			
			PdfPCell titleCel1 = new PdfPCell();
			Paragraph titleText1=new Paragraph("",font3);
			titleText1.setAlignment(Element.ALIGN_CENTER);
			titleText1.setSpacingBefore(5f);
			titleCel1.setRowspan(3);
			titleCel1 .addElement(titleText1);
			
				//------- sub table------
				PdfPTable titleSub = new PdfPTable(2);
				titleSub.setWidthPercentage(100);
				titleSub.setWidths(new int[]{30, 70});
							
				PdfPCell titleSubCel1 = new PdfPCell();
				Paragraph titleSubTxt1=new Paragraph("Sanction No",font7);
				titleSubTxt1.setAlignment(Element.ALIGN_LEFT);
				titleSubCel1 .addElement(titleSubTxt1);
				titleSubCel1.setBorder(PdfPCell.NO_BORDER);
				titleSub .addCell(titleSubCel1);
				
				PdfPCell titleSubCel11 = new PdfPCell();
				Paragraph titleSubTxt11=new Paragraph(":",font7);
				titleSubTxt11.setAlignment(Element.ALIGN_LEFT);
				titleSubCel11 .addElement(titleSubTxt11);
				titleSubCel11.setBorder(PdfPCell.NO_BORDER);
				titleSub .addCell(titleSubCel11);
				
				PdfPCell titleSubCel2 = new PdfPCell();
				Paragraph titleSubTxt2=new Paragraph("W.O. No. ",font7);
				titleSubTxt2.setAlignment(Element.ALIGN_LEFT);
				titleSubCel2 .addElement(titleSubTxt2);
				titleSubCel2.setBorder(PdfPCell.NO_BORDER);
				titleSub .addCell(titleSubCel2);
				
				PdfPCell titleSubCel21 = new PdfPCell();
				Paragraph titleSubTxt21=new Paragraph(":",font7);
				titleSubTxt21.setAlignment(Element.ALIGN_LEFT);
				titleSubCel21 .addElement(titleSubTxt21);
				titleSubCel21.setBorder(PdfPCell.NO_BORDER);
				titleSub .addCell(titleSubCel21);
				
				PdfPCell titleSubCel3 = new PdfPCell();
				Paragraph titleSubTxt3=new Paragraph("TGSPDCL portion estimated charges",font7);
				titleSubTxt3.setAlignment(Element.ALIGN_LEFT);
				titleSubCel3 .addElement(titleSubTxt3);
				titleSubCel3.setColspan(2);
				titleSubCel3.setBorder(PdfPCell.NO_BORDER);
				titleSub .addCell(titleSubCel3);
			
			titleCel1.addElement(titleSub);
			titleTable.addCell(titleCel1);
			
			
			PdfPCell titleCel2 = new PdfPCell();
			Paragraph titleText2=new Paragraph("TEST REPORTPART - A",font3);
			titleText2.setAlignment(Element.ALIGN_CENTER);
			titleCel2.setBorder(PdfPCell.NO_BORDER);
			titleCel2 .addElement(titleText2);
			titleTable.addCell(titleCel2);
			
			PdfPCell titleCel3 = new PdfPCell();
			Paragraph titleText3=new Paragraph("",font3);
			titleText3.setAlignment(Element.ALIGN_CENTER);
			titleText3.setSpacingBefore(5f);
			//headText4.setSpacingAfter(10f);
			titleCel3.setRowspan(3);
			titleCel3.addElement(titleText3);
				
				//------- sub table2------
				PdfPTable titleSub2 = new PdfPTable(2);
				titleSub2.setWidthPercentage(100);
				titleSub2.setWidths(new int[]{30, 70});
							
				PdfPCell titleSubCel12 = new PdfPCell();
				Paragraph titleSubTxt12=new Paragraph("S.C.No ",font7);
				titleSubTxt12.setAlignment(Element.ALIGN_LEFT);
				titleSubCel12 .addElement(titleSubTxt12);
				titleSubCel12.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel12);
				
				PdfPCell titleSubCel112 = new PdfPCell();
				Paragraph titleSubTxt112=new Paragraph(":",font7);
				titleSubTxt112.setAlignment(Element.ALIGN_LEFT);
				titleSubCel112 .addElement(titleSubTxt112);
				titleSubCel112.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel112);
				
				PdfPCell titleSubCel22 = new PdfPCell();
				Paragraph titleSubTxt22=new Paragraph("Distribution ",font7);
				titleSubTxt22.setAlignment(Element.ALIGN_LEFT);
				titleSubCel22 .addElement(titleSubTxt22);
				titleSubCel22.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel22);
				
				PdfPCell titleSubCel212 = new PdfPCell();
				Paragraph titleSubTxt212=new Paragraph(":",font7);
				titleSubTxt212.setAlignment(Element.ALIGN_LEFT);
				titleSubCel212 .addElement(titleSubTxt212);
				titleSubCel212.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel212);
				
				PdfPCell titleSubCel32 = new PdfPCell();
				Paragraph titleSubTxt32=new Paragraph("Date",font7);
				titleSubTxt32.setAlignment(Element.ALIGN_LEFT);
				titleSubCel32 .addElement(titleSubTxt32);
				titleSubCel32.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel32);
				
				PdfPCell titleSubCel322 = new PdfPCell();
				Paragraph titleSubTxt322=new Paragraph(":",font7);
				titleSubTxt322.setAlignment(Element.ALIGN_LEFT);
				titleSubCel322 .addElement(titleSubTxt322);
				titleSubCel322.setBorder(PdfPCell.NO_BORDER);
				titleSub2 .addCell(titleSubCel322);
			
			titleCel3.addElement(titleSub2);
			titleTable.addCell(titleCel3);
			
			PdfPCell titleCel21 = new PdfPCell();
			Paragraph titleText21=new Paragraph("(To be filled in by Distribution Engineers )",font7);
			titleText21.setAlignment(Element.ALIGN_CENTER);
			titleCel21.setBorder(PdfPCell.NO_BORDER);
			titleCel21 .addElement(titleText21);
			titleTable.addCell(titleCel21);
			
			PdfPCell titleCel22 = new PdfPCell();
			Paragraph titleText22=new Paragraph("*****",font6);
			titleText22.setAlignment(Element.ALIGN_CENTER);
			titleCel22.setBorder(PdfPCell.NO_BORDER);
			titleCel22 .addElement(titleText22);
			titleTable.addCell(titleCel22);
			//headCel2 .setBorder(Rectangle.OUT_RIGHT);
			//headCel2 .setBorder(Rectangle.OUT_LEFT);
			//titleTable.addCell(headCel2);
			document.add(titleTable);
	      	//headCell.setBorder(PdfPCell.NO_BORDER);
	        //headCell.setPadding(0); 
	        

	        
	        
	        //===============LINE 1========================
	        
	        Font txtfont = new Font(Font.FontFamily.UNDEFINED, 9);
	        
	        
	        //==================BODY======================
	        
	        PdfPTable bodyTable = new PdfPTable(4);
	        bodyTable.setWidthPercentage(100);
	        bodyTable.setWidths(new int[]{3, 3, 40, 54});
	        
	        //----------------index 1 a)----------------------------
			PdfPCell indexCel1 = new PdfPCell();
			Paragraph indexText1=new Paragraph("1.",txtfont);
			indexText1.setAlignment(Element.ALIGN_LEFT);
			indexText1.setSpacingBefore(2f);
			indexCel1.setPadding(0); 
			indexCel1 .addElement(indexText1);
			indexCel1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel1);
			
			PdfPCell indexCel2 = new PdfPCell();
			Paragraph indexText2=new Paragraph("a)",txtfont);
			indexText2.setAlignment(Element.ALIGN_LEFT);
			indexText2.setSpacingBefore(2f);
			indexCel2.setPadding(0); 
			indexCel2.addElement(indexText2);
			indexCel2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel2);
			
			PdfPCell indexCel3 = new PdfPCell();
			Paragraph indexText3=new Paragraph("Name of the Consumer",txtfont);
			indexText3.setAlignment(Element.ALIGN_LEFT);
			indexText3.setSpacingBefore(2f);
			indexCel3.setPadding(0); 
			indexCel3 .addElement(indexText3);
			indexCel3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel3);
			
			PdfPCell indexCel4 = new PdfPCell();
			Paragraph indexText4=new Paragraph(":",txtfont);
			indexText4.setAlignment(Element.ALIGN_LEFT);
			indexText4.setSpacingBefore(2f);
			indexCel4.setPadding(0); 
			indexCel4.addElement(indexText4);
			indexCel4.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel4);
			
			//----------------index 1 - sub ----------------------------
			PdfPCell indexCel1s = new PdfPCell();
			Paragraph indexText1s=new Paragraph("",txtfont);
			indexText1s.setAlignment(Element.ALIGN_LEFT);
			indexText1s.setSpacingBefore(2f);
			indexCel1s .addElement(indexText1s);
			indexCel1s.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel1s);
			
			PdfPCell indexCel2s = new PdfPCell();
			Paragraph indexText2s=new Paragraph("",txtfont);
			indexText2s.setAlignment(Element.ALIGN_LEFT);
			indexText2s.setSpacingBefore(2f);
			indexCel2s.addElement(indexText2s);
			indexCel2s.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel2s);
			
			PdfPCell indexCel3s = new PdfPCell();
			Paragraph indexText3s=new Paragraph("(Full Surname and Name in block letters) ",txtfont);
			indexText3s.setAlignment(Element.ALIGN_LEFT);
			indexText3s.setSpacingBefore(2f);
			indexCel3s .addElement(indexText3s);
			indexCel3s.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel3s);
			
			PdfPCell indexCel4s = new PdfPCell();
			Paragraph indexText4s=new Paragraph("",txtfont);
			indexText4s.setAlignment(Element.ALIGN_LEFT);
			indexText4s.setSpacingBefore(2f);
			indexCel4s.addElement(indexText4s);
			indexCel4s.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel4s);
			
			//----------------index 1 b)----------------------------
			PdfPCell indexCel11 = new PdfPCell();
			Paragraph indexText11=new Paragraph("",txtfont);
			indexText11.setAlignment(Element.ALIGN_LEFT);
			indexText11.setSpacingBefore(2f);
			indexCel11 .addElement(indexText11);
			indexCel11.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel11);
			
			PdfPCell indexCel21 = new PdfPCell();
			Paragraph indexText21=new Paragraph("b)",txtfont);
			indexText21.setAlignment(Element.ALIGN_LEFT);
			indexText21.setSpacingBefore(2f);
			indexCel21.addElement(indexText21);
			indexCel21.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel21);
			
			PdfPCell indexCel31 = new PdfPCell();
			Paragraph indexText31=new Paragraph("Father's Name  ",txtfont);
			indexText31.setAlignment(Element.ALIGN_LEFT);
			indexText31.setSpacingBefore(2f);
			indexCel31 .addElement(indexText31);
			indexCel31.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel31);
			
			PdfPCell indexCel41 = new PdfPCell();
			Paragraph indexText41=new Paragraph(":",txtfont);
			indexText41.setAlignment(Element.ALIGN_LEFT);
			indexText41.setSpacingBefore(2f);
			indexCel41.addElement(indexText41);
			indexCel41.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel41);
			
			//----------------index 1 c)----------------------------
			PdfPCell indexCel11c = new PdfPCell();
			Paragraph indexText11c=new Paragraph("",txtfont);
			indexText11c.setAlignment(Element.ALIGN_LEFT);
			indexText11c.setSpacingBefore(2f);
			indexCel11c .addElement(indexText11c);
			indexCel11c.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel11c);
			
			PdfPCell indexCel21c = new PdfPCell();
			Paragraph indexText21c=new Paragraph("c)",txtfont);
			indexText21c.setAlignment(Element.ALIGN_LEFT);
			indexText21c.setSpacingBefore(2f);
			indexCel21c.addElement(indexText21c);
			indexCel21c.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel21c);
			
			PdfPCell indexCel31c = new PdfPCell();
			Paragraph indexText31c=new Paragraph("In case the consumer is not owner of the premises specify Owners name and address",txtfont);
			indexText31c.setAlignment(Element.ALIGN_LEFT);
			indexText31c.setSpacingBefore(2f);
			indexCel31c .addElement(indexText31c);
			indexCel31c.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel31c);
			
			PdfPCell indexCel41c = new PdfPCell();
			Paragraph indexText41c=new Paragraph(":",txtfont);
			indexText41c.setAlignment(Element.ALIGN_LEFT);
			indexText41c.setSpacingBefore(2f);
			indexCel41c.addElement(indexText41c);
			indexCel41c.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel41c);
			
			//----------------index 2 ----------------------------
			PdfPCell indexCel12 = new PdfPCell();
			Paragraph indexText12=new Paragraph("2.",txtfont);
			indexText12.setAlignment(Element.ALIGN_LEFT);
			indexText12.setSpacingBefore(2f);
			indexCel12 .addElement(indexText12);
			indexCel12.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel12);
			
			PdfPCell indexCel22 = new PdfPCell();
			Paragraph indexText22=new Paragraph("",txtfont);
			indexText22.setAlignment(Element.ALIGN_LEFT);
			indexText22.setSpacingBefore(2f);
			indexCel22.addElement(indexText22);
			indexCel22.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel22);
			
			PdfPCell indexCel32 = new PdfPCell();
			Paragraph indexText32=new Paragraph("Premises to which supply relates specify",txtfont);
			indexText32.setAlignment(Element.ALIGN_LEFT);
			indexText32.setSpacingBefore(2f);
			indexCel32 .addElement(indexText32);
			indexCel32.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel32);
			
			PdfPCell indexCel42 = new PdfPCell();
			Paragraph indexText42=new Paragraph("",txtfont);
			indexText42.setAlignment(Element.ALIGN_LEFT);
			indexText42.setSpacingBefore(2f);
			indexCel42.addElement(indexText42);
			indexCel42.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel42);
			
			//----------------index 2 i)----------------------------
			PdfPCell indexCel12i = new PdfPCell();
			Paragraph indexText12i=new Paragraph("",txtfont);
			indexText12i.setAlignment(Element.ALIGN_LEFT);
			indexText12i.setSpacingBefore(2f);
			indexCel12i .addElement(indexText12i);
			indexCel12i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel12i);
			
			PdfPCell indexCel22i = new PdfPCell();
			Paragraph indexText22i=new Paragraph("i.",txtfont);
			indexText22i.setAlignment(Element.ALIGN_LEFT);
			indexText22i.setSpacingBefore(2f);
			indexCel22i.addElement(indexText22i);
			indexCel22i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel22i);
			
			PdfPCell indexCel32i = new PdfPCell();
			Paragraph indexText32i=new Paragraph("Nature of Business",txtfont);
			indexText32i.setAlignment(Element.ALIGN_LEFT);
			indexText32i.setSpacingBefore(2f);
			indexCel32i .addElement(indexText32i);
			indexCel32i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel32i);
			
			PdfPCell indexCel42i = new PdfPCell();
			Paragraph indexText42i=new Paragraph(":",txtfont);
			indexText42i.setAlignment(Element.ALIGN_LEFT);
			indexText42i.setSpacingBefore(2f);
			indexCel42i.addElement(indexText42i);
			indexCel42i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel42i);
			
			//----------------index 2 ii)----------------------------
			PdfPCell indexCel12ii = new PdfPCell();
			Paragraph indexText12ii=new Paragraph("",txtfont);
			indexText12ii.setAlignment(Element.ALIGN_LEFT);
			indexText12ii.setSpacingBefore(2f);
			indexCel12ii .addElement(indexText12ii);
			indexCel12ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel12ii);
			
			PdfPCell indexCel22ii = new PdfPCell();
			Paragraph indexText22ii=new Paragraph("ii.",txtfont);
			indexText22ii.setAlignment(Element.ALIGN_LEFT);
			indexText22ii.setSpacingBefore(2f);
			indexCel22ii.addElement(indexText22ii);
			indexCel22ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel22ii);
			
			PdfPCell indexCel32ii = new PdfPCell();
			Paragraph indexText32ii=new Paragraph("Purpose in case of non domestic / Industrial supply",txtfont);
			indexText32ii.setAlignment(Element.ALIGN_LEFT);
			indexText32ii.setSpacingBefore(2f);
			indexCel32ii .addElement(indexText32ii);
			indexCel32ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel32ii);
			
			PdfPCell indexCel42ii = new PdfPCell();
			Paragraph indexText42ii=new Paragraph(":",txtfont);
			indexText42ii.setAlignment(Element.ALIGN_LEFT);
			indexText42ii.setSpacingBefore(2f);
			indexCel42ii.addElement(indexText42ii);
			indexCel42ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel42ii);
			
			//----------------index 2 iii)----------------------------
			PdfPCell indexCel12iii = new PdfPCell();
			Paragraph indexText12iii=new Paragraph("",txtfont);
			indexText12iii.setAlignment(Element.ALIGN_LEFT);
			indexText12iii.setSpacingBefore(2f);
			indexCel12iii .addElement(indexText12iii);
			indexCel12iii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel12iii);
			
			PdfPCell indexCel22iii = new PdfPCell();
			Paragraph indexText22iii=new Paragraph("iii.",txtfont);
			indexText22iii.setAlignment(Element.ALIGN_LEFT);
			indexText22iii.setSpacingBefore(2f);
			indexCel22iii.addElement(indexText22iii);
			indexCel22iii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel22iii);
			
			PdfPCell indexCel32iii = new PdfPCell();
			Paragraph indexText32iii=new Paragraph("Type of Industry",txtfont);
			indexText32iii.setAlignment(Element.ALIGN_LEFT);
			indexText32iii.setSpacingBefore(2f);
			indexCel32iii .addElement(indexText32iii);
			indexCel32iii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel32iii);
			
			PdfPCell indexCel42iii = new PdfPCell();
			Paragraph indexText42iii=new Paragraph(":",txtfont);
			indexText42iii.setAlignment(Element.ALIGN_LEFT);
			indexText42iii.setSpacingBefore(2f);
			indexCel42iii.addElement(indexText42iii);
			indexCel42iii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel42iii);
			
			//----------------index 3----------------------------
			PdfPCell indexCel13 = new PdfPCell();
			Paragraph indexText13=new Paragraph("3.",txtfont);
			indexText13.setAlignment(Element.ALIGN_LEFT);
			indexText13.setSpacingBefore(2f);
			indexCel13 .addElement(indexText13);
			indexCel13.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel13);
			
			PdfPCell indexCel23 = new PdfPCell();
			Paragraph indexText23=new Paragraph("",txtfont);
			indexText23.setAlignment(Element.ALIGN_LEFT);
			indexText23.setSpacingBefore(2f);
			indexCel23.addElement(indexText23);
			indexCel23.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel23);
			
			PdfPCell indexCel33 = new PdfPCell();
			Paragraph indexText33=new Paragraph("Particulars of premises to which supply relates Door No",txtfont);
			indexText33.setAlignment(Element.ALIGN_LEFT);
			indexText33.setSpacingBefore(2f);
			indexCel33 .addElement(indexText33);
			indexCel33.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel33);
			
			PdfPCell indexCel43 = new PdfPCell();
			Paragraph indexText43=new Paragraph(":",txtfont);
			indexText43.setAlignment(Element.ALIGN_LEFT);
			indexText43.setSpacingBefore(2f);
			indexCel43.addElement(indexText43);
			indexCel43.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel43);
			
			//----------------index 3 sub1----------------------------
			PdfPCell indexCel13s1 = new PdfPCell();
			Paragraph indexText13s1=new Paragraph("",txtfont);
			indexText13s1.setAlignment(Element.ALIGN_LEFT);
			indexText13s1.setSpacingBefore(2f);
			indexCel13s1 .addElement(indexText13s1);
			indexCel13s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel13s1);
			
			PdfPCell indexCel23s1 = new PdfPCell();
			Paragraph indexText23s1=new Paragraph("",txtfont);
			indexText23s1.setAlignment(Element.ALIGN_LEFT);
			indexText23s1.setSpacingBefore(2f);
			indexCel23s1.addElement(indexText23s1);
			indexCel23s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel23s1);
			
			PdfPCell indexCel33s1 = new PdfPCell();
			Paragraph indexText33s1=new Paragraph("	S.Y. No.",txtfont);
			indexText33s1.setAlignment(Element.ALIGN_LEFT);
			indexText33s1.setSpacingBefore(2f);
			indexCel33s1 .addElement(indexText33s1);
			indexCel33s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel33s1);
			
			PdfPCell indexCel43s1 = new PdfPCell();
			Paragraph indexText43s1=new Paragraph(":",txtfont);
			indexText43s1.setAlignment(Element.ALIGN_LEFT);
			indexText43s1.setSpacingBefore(2f);
			indexCel43s1.addElement(indexText43s1);
			indexCel43s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel43s1);
			
			//----------------index 3 sub2----------------------------
			PdfPCell indexCel13s2 = new PdfPCell();
			Paragraph indexText13s2=new Paragraph("",txtfont);
			indexText13s2.setAlignment(Element.ALIGN_LEFT);
			indexText13s2.setSpacingBefore(2f);
			indexCel13s2 .addElement(indexText13s2);
			indexCel13s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel13s2);
			
			PdfPCell indexCel23s2 = new PdfPCell();
			Paragraph indexText23s2=new Paragraph("",txtfont);
			indexText23s2.setAlignment(Element.ALIGN_LEFT);
			indexText23s2.setSpacingBefore(2f);
			indexCel23s2.addElement(indexText23s2);
			indexCel23s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel23s2);
			
			PdfPCell indexCel33s2 = new PdfPCell();
			Paragraph indexText33s2=new Paragraph("	Street",txtfont);
			indexText33s2.setAlignment(Element.ALIGN_LEFT);
			indexText33s2.setSpacingBefore(2f);
			indexCel33s2 .addElement(indexText33s2);
			indexCel33s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel33s2);
			
			PdfPCell indexCel43s2 = new PdfPCell();
			Paragraph indexText43s2=new Paragraph(":",txtfont);
			indexText43s2.setAlignment(Element.ALIGN_LEFT);
			indexText43s2.setSpacingBefore(2f);
			indexCel43s2.addElement(indexText43s2);
			indexCel43s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel43s2);
			
			//----------------index 3 sub3----------------------------
			PdfPCell indexCel13s3 = new PdfPCell();
			Paragraph indexText13s3=new Paragraph("",txtfont);
			indexText13s3.setAlignment(Element.ALIGN_LEFT);
			indexText13s3.setSpacingBefore(2f);
			indexCel13s3 .addElement(indexText13s3);
			indexCel13s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel13s3);
			
			PdfPCell indexCel23s3 = new PdfPCell();
			Paragraph indexText23s3=new Paragraph("",txtfont);
			indexText23s3.setAlignment(Element.ALIGN_LEFT);
			indexText23s3.setSpacingBefore(2f);
			indexCel23s3.addElement(indexText23s3);
			indexCel23s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel23s3);
			
			PdfPCell indexCel33s3 = new PdfPCell();
			Paragraph indexText33s3=new Paragraph("	Town / Village",txtfont);
			indexText33s3.setAlignment(Element.ALIGN_LEFT);
			indexText33s3.setSpacingBefore(2f);
			indexCel33s3 .addElement(indexText33s3);
			indexCel33s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel33s3);
			
			PdfPCell indexCel43s3 = new PdfPCell();
			Paragraph indexText43s3=new Paragraph(":",txtfont);
			indexText43s3.setAlignment(Element.ALIGN_LEFT);
			indexText43s3.setSpacingBefore(2f);
			indexCel43s3.addElement(indexText43s3);
			indexCel43s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel43s3);
			
			//----------------index 4----------------------------
			PdfPCell indexCel14 = new PdfPCell();
			Paragraph indexText14=new Paragraph("4.",txtfont);
			indexText14.setAlignment(Element.ALIGN_LEFT);
			indexText14.setSpacingBefore(2f);
			indexCel14 .addElement(indexText14);
			indexCel14.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel14);
			
			PdfPCell indexCel24 = new PdfPCell();
			Paragraph indexText24=new Paragraph("",txtfont);
			indexText24.setAlignment(Element.ALIGN_LEFT);
			indexText24.setSpacingBefore(2f);
			indexCel24.addElement(indexText24);
			indexCel24.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel24);
			
			PdfPCell indexCel34 = new PdfPCell();
			Paragraph indexText34=new Paragraph("Permanent address of Consumer ",txtfont);
			indexText34.setAlignment(Element.ALIGN_LEFT);
			indexText34.setSpacingBefore(2f);
			indexCel34 .addElement(indexText34);
			indexCel34.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel34);
			
			PdfPCell indexCel44 = new PdfPCell();
			Paragraph indexText44=new Paragraph("",txtfont);
			indexText44.setAlignment(Element.ALIGN_LEFT);
			indexText44.setSpacingBefore(2f);
			indexCel44.addElement(indexText44);
			indexCel44.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel44);
			
			//----------------index 4 sub1----------------------------
			PdfPCell indexCel14s1 = new PdfPCell();
			Paragraph indexText14s1=new Paragraph("",txtfont);
			indexText14s1.setAlignment(Element.ALIGN_LEFT);
			indexText14s1.setSpacingBefore(2f);
			indexCel14s1 .addElement(indexText14s1);
			indexCel14s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel14s1);
			
			PdfPCell indexCel24s1 = new PdfPCell();
			Paragraph indexText24s1=new Paragraph("",txtfont);
			indexText24s1.setAlignment(Element.ALIGN_LEFT);
			indexText24s1.setSpacingBefore(2f);
			indexCel24s1.addElement(indexText24s1);
			indexCel24s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel24s1);
			
			PdfPCell indexCel34s1 = new PdfPCell();
			Paragraph indexText34s1=new Paragraph("	S.Y. No.",txtfont);
			indexText34s1.setAlignment(Element.ALIGN_LEFT);
			indexText34s1.setSpacingBefore(2f);
			indexCel34s1 .addElement(indexText34s1);
			indexCel34s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel34s1);
			
			PdfPCell indexCel44s1 = new PdfPCell();
			Paragraph indexText44s1=new Paragraph(":",txtfont);
			indexText44s1.setAlignment(Element.ALIGN_LEFT);
			indexText44s1.setSpacingBefore(2f);
			indexCel44s1.addElement(indexText44s1);
			indexCel44s1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel44s1);
			
			//----------------index 4 sub2----------------------------
			PdfPCell indexCel14s2 = new PdfPCell();
			Paragraph indexText14s2=new Paragraph("",txtfont);
			indexText14s2.setAlignment(Element.ALIGN_LEFT);
			indexText14s2.setSpacingBefore(2f);
			indexCel14s2 .addElement(indexText14s2);
			indexCel14s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel14s2);
			
			PdfPCell indexCel24s2 = new PdfPCell();
			Paragraph indexText24s2=new Paragraph("",txtfont);
			indexText24s2.setAlignment(Element.ALIGN_LEFT);
			indexText24s2.setSpacingBefore(2f);
			indexCel24s2.addElement(indexText24s2);
			indexCel24s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel24s2);
			
			PdfPCell indexCel34s2 = new PdfPCell();
			Paragraph indexText34s2=new Paragraph("	Street",txtfont);
			indexText34s2.setAlignment(Element.ALIGN_LEFT);
			indexText34s2.setSpacingBefore(2f);
			indexCel34s2 .addElement(indexText34s2);
			indexCel34s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel34s2);
			
			PdfPCell indexCel44s2 = new PdfPCell();
			Paragraph indexText44s2=new Paragraph(":",txtfont);
			indexText44s2.setAlignment(Element.ALIGN_LEFT);
			indexText44s2.setSpacingBefore(2f);
			indexCel44s2.addElement(indexText44s2);
			indexCel44s2.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel44s2);
			
			//----------------index 4 sub3----------------------------
			PdfPCell indexCel14s3 = new PdfPCell();
			Paragraph indexText14s3=new Paragraph("",txtfont);
			indexText14s3.setAlignment(Element.ALIGN_LEFT);
			indexText14s3.setSpacingBefore(2f);
			indexCel14s3 .addElement(indexText14s3);
			indexCel14s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel14s3);
			
			PdfPCell indexCel24s3 = new PdfPCell();
			Paragraph indexText24s3=new Paragraph("",txtfont);
			indexText24s3.setAlignment(Element.ALIGN_LEFT);
			indexText24s3.setSpacingBefore(2f);
			indexCel24s3.addElement(indexText24s3);
			indexCel24s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel24s3);
			
			PdfPCell indexCel34s3 = new PdfPCell();
			Paragraph indexText34s3=new Paragraph("	Town / Village",txtfont);
			indexText34s3.setAlignment(Element.ALIGN_LEFT);
			indexText34s3.setSpacingBefore(2f);
			indexCel34s3 .addElement(indexText34s3);
			indexCel34s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel34s3);
			
			PdfPCell indexCel44s3 = new PdfPCell();
			Paragraph indexText44s3=new Paragraph(":",txtfont);
			indexText44s3.setAlignment(Element.ALIGN_LEFT);
			indexText44s3.setSpacingBefore(2f);
			indexCel44s3.addElement(indexText44s3);
			indexCel44s3.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel44s3);
			
			//----------------index 5----------------------------
			PdfPCell indexCel15 = new PdfPCell();
			Paragraph indexText15=new Paragraph("5.",txtfont);
			indexText15.setAlignment(Element.ALIGN_LEFT);
			indexText15.setSpacingBefore(2f);
			indexCel15 .addElement(indexText15);
			indexCel15.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel15);
			
			PdfPCell indexCel25 = new PdfPCell();
			Paragraph indexText25=new Paragraph("",txtfont);
			indexText25.setAlignment(Element.ALIGN_LEFT);
			indexText25.setSpacingBefore(2f);
			indexCel25.addElement(indexText25);
			indexCel25.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel25);
			
			PdfPCell indexCel35 = new PdfPCell();
			Paragraph indexText35=new Paragraph("Other S.C. No./Distribution in the name of the above " + 
					"consumer under the TGSPDCL. (Full details to be furnished.)",txtfont);
			indexText35.setAlignment(Element.ALIGN_LEFT);
			indexText35.setSpacingBefore(2f);
			indexCel35.setColspan(2);
			indexCel35.addElement(indexText35);
			indexCel35.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel35);
			
			/*PdfPCell indexCel45 = new PdfPCell();
			Paragraph indexText45=new Paragraph("",txtfont);
			indexText45.setAlignment(Element.ALIGN_LEFT);
			indexText45.setSpacingBefore(2f);
			indexCel45.addElement(indexText45);
			indexCel45.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel45);*/
			
			//----------------index 5 a)----------------------------
			PdfPCell indexCel15a = new PdfPCell();
			Paragraph indexText15a=new Paragraph("",txtfont);
			indexText15a.setAlignment(Element.ALIGN_LEFT);
			indexText15a.setSpacingBefore(2f);
			indexCel15a .addElement(indexText15a);
			indexCel15a.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel15a);
			
			PdfPCell indexCel25a = new PdfPCell();
			Paragraph indexText25a=new Paragraph("a.",txtfont);
			indexText25a.setAlignment(Element.ALIGN_LEFT);
			indexText25a.setSpacingBefore(2f);
			indexCel25a.addElement(indexText25a);
			indexCel25a.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel25a);
			
			PdfPCell indexCel35a = new PdfPCell();
			Paragraph indexText35a=new Paragraph("Whether the service now released is for a new premises or to an " + 
					"  old premises to a new consumer (Specify the S.C.No. if any available. Already under disconnection).",txtfont);
			indexText35a.setAlignment(Element.ALIGN_LEFT);
			indexText35a.setSpacingBefore(2f);
			//indexCel35a.setColspan(2);
			indexCel35a.addElement(indexText35a);
			indexCel35a.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel35a);
			
			PdfPCell indexCel45a = new PdfPCell();
			Paragraph indexText45a=new Paragraph(": YES/NO",txtfont);
			indexText45a.setAlignment(Element.ALIGN_LEFT);
			indexText45a.setSpacingBefore(2f);
			indexCel45a.addElement(indexText45a);
			indexCel45a.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel45a);
			
			//----------------index 5 b)----------------------------
			PdfPCell indexCel15b = new PdfPCell();
			Paragraph indexText15b=new Paragraph("",txtfont);
			indexText15b.setAlignment(Element.ALIGN_LEFT);
			indexText15b.setSpacingBefore(2f);
			indexCel15b .addElement(indexText15b);
			indexCel15b.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel15b);
			
			PdfPCell indexCel25b = new PdfPCell();
			Paragraph indexText25b=new Paragraph("b.",txtfont);
			indexText25b.setAlignment(Element.ALIGN_LEFT);
			indexText25b.setSpacingBefore(2f);
			indexCel25b.addElement(indexText25b);
			indexCel25b.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel25b);
			
			PdfPCell indexCel35b = new PdfPCell();
			Paragraph indexText35b=new Paragraph("Whether a clearance certificate is obtained from the Electricity" + 
					"  Revenue Office before releasing the supply to new consumer in the old premises.",txtfont);
			indexText35b.setAlignment(Element.ALIGN_LEFT);
			indexText35b.setSpacingBefore(2f);
			//indexCel35a.setColspan(2);
			indexCel35b.addElement(indexText35b);
			indexCel35b.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel35b);
			
			PdfPCell indexCel45b = new PdfPCell();
			Paragraph indexText45b=new Paragraph(": YES/NO",txtfont);
			indexText45b.setAlignment(Element.ALIGN_LEFT);
			indexText45b.setSpacingBefore(2f);
			indexCel45b.addElement(indexText45b);
			indexCel45b.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel45b);
			
			
			//----------------index 6----------------------------
			PdfPCell indexCel16 = new PdfPCell();
			Paragraph indexText16=new Paragraph("6.",txtfont);
			indexText16.setAlignment(Element.ALIGN_LEFT);
			indexText16.setSpacingBefore(2f);
			indexCel16 .addElement(indexText16);
			indexCel16.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel16);
			
			PdfPCell indexCel26 = new PdfPCell();
			Paragraph indexText26=new Paragraph("",txtfont);
			indexText26.setAlignment(Element.ALIGN_LEFT);
			indexText26.setSpacingBefore(2f);
			indexCel26.addElement(indexText26);
			indexCel26.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel26);
			
			PdfPCell indexCel36 = new PdfPCell();
			Paragraph indexText36=new Paragraph("Particulars of connected load",txtfont);
			indexText36.setAlignment(Element.ALIGN_LEFT);
			indexText36.setSpacingBefore(2f);
			//indexCel35a.setColspan(2);
			indexCel36.addElement(indexText36);
			indexCel36.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel36);
			
			PdfPCell indexCel46 = new PdfPCell();
			Paragraph indexText46=new Paragraph("",txtfont);
			indexText46.setAlignment(Element.ALIGN_LEFT);
			indexText46.setSpacingBefore(2f);
			indexCel46.addElement(indexText46);
			indexCel46.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel46);
			
			//----------------index 6 sub TABLE----------------------------
			PdfPCell indexCel16st = new PdfPCell();
			Paragraph indexText16st=new Paragraph("",txtfont);
			indexText16st.setAlignment(Element.ALIGN_LEFT);
			indexText16st.setSpacingBefore(2f);
			indexCel16st.setColspan(4);
			indexCel16st .addElement(indexText16st);
			indexCel16st.setBorder(PdfPCell.NO_BORDER);
			
			//------- sub table------
			PdfPTable subTable = new PdfPTable(4);
			subTable.setWidthPercentage(100);
			subTable.setWidths(new int[]{40, 20, 20, 20});
			
			insertTBLCell(subTable, "", Element.ALIGN_LEFT, font6, 0);
			insertTBLCell(subTable, "No.of Points", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable, "Waltage Points", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable, "Total Waltage", Element.ALIGN_CENTER, font6, 0);
			
			insertTBLCell(subTable, "Lights", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Fans", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Convenient wall plugs for Light and Fans", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Cookers", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Water Heaters", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Pump sets", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Motors", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Other purposes (Specify Details)", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Convenient wall plugs 5 Amps / 15 Amps", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Installed by TGSPDCL. / Consumer", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable, "Connected Load.", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable, "2000 Watts", Element.ALIGN_CENTER, font7, 0);
			
			//insertCell(sampleTable, "Data 2", Element.ALIGN_LEFT, font2, 0);
			indexCel16st.addElement(subTable);
			
			bodyTable.addCell(indexCel16st);
			
			
			//----------------index 7 i----------------------------
			PdfPCell indexCel17i = new PdfPCell();
			Paragraph indexText17i=new Paragraph("7.",txtfont);
			indexText17i.setAlignment(Element.ALIGN_LEFT);
			indexText17i.setSpacingBefore(2f);
			indexCel17i.setPadding(0); 
			indexCel17i .addElement(indexText17i);
			indexCel17i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel17i);
			
			PdfPCell indexCel27i = new PdfPCell();
			Paragraph indexText27i=new Paragraph("i.",txtfont);
			indexText27i.setAlignment(Element.ALIGN_LEFT);
			indexText27i.setSpacingBefore(2f);
			indexCel27i.setPadding(0); 
			indexCel27i.addElement(indexText27i);
			indexCel27i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel27i);
			
			PdfPCell indexCel37i = new PdfPCell();
			Paragraph indexText37i=new Paragraph("Insulation between conductor and earth",txtfont);
			indexText37i.setAlignment(Element.ALIGN_LEFT);
			indexText37i.setSpacingBefore(2f);
			indexCel37i.setPadding(0); 
			indexCel37i .addElement(indexText37i);
			indexCel37i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel37i);
			
			PdfPCell indexCel47i = new PdfPCell();
			Paragraph indexText47i=new Paragraph(":",txtfont);
			indexText47i.setAlignment(Element.ALIGN_LEFT);
			indexText47i.setSpacingBefore(2f);
			indexCel47i.setPadding(0); 
			indexCel47i.addElement(indexText47i);
			indexCel47i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel47i);
			
			//----------------index 7 ii----------------------------
			PdfPCell indexCel17ii = new PdfPCell();
			Paragraph indexText17ii=new Paragraph("",txtfont);
			indexText17ii.setAlignment(Element.ALIGN_LEFT);
			indexText17ii.setSpacingBefore(2f);
			indexCel17ii.setPadding(0); 
			indexCel17ii .addElement(indexText17ii);
			indexCel17ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel17ii);
			
			PdfPCell indexCel27ii = new PdfPCell();
			Paragraph indexText27ii=new Paragraph("ii.",txtfont);
			indexText27ii.setAlignment(Element.ALIGN_LEFT);
			indexText27ii.setSpacingBefore(2f);
			indexCel27ii.setPadding(0); 
			indexCel27ii.addElement(indexText27ii);
			indexCel27ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel27ii);
			
			PdfPCell indexCel37ii = new PdfPCell();
			Paragraph indexText37ii=new Paragraph("Insulation between Phases",txtfont);
			indexText37ii.setAlignment(Element.ALIGN_LEFT);
			indexText37ii.setSpacingBefore(2f);
			indexCel37ii.setPadding(0); 
			indexCel37ii.addElement(indexText37ii);
			indexCel37ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel37ii);
			
			PdfPCell indexCel47ii = new PdfPCell();
			Paragraph indexText47ii=new Paragraph(":  Polyphase / single phase",txtfont);
			indexText47ii.setAlignment(Element.ALIGN_LEFT);
			indexText47ii.setSpacingBefore(2f);
			indexCel47ii.setPadding(0); 
			indexCel47ii.addElement(indexText47ii);
			indexCel47ii.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel47ii);
			
			
			//----------------index 8----------------------------
			PdfPCell indexCel18 = new PdfPCell();
			Paragraph indexText18=new Paragraph("8.",txtfont);
			indexText18.setAlignment(Element.ALIGN_LEFT);
			indexText18.setSpacingBefore(2f);
			indexCel18.setPadding(0); 
			indexCel18 .addElement(indexText18);
			indexCel18.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel18);
			
			PdfPCell indexCel28 = new PdfPCell();
			Paragraph indexText28=new Paragraph("",txtfont);
			indexText28.setAlignment(Element.ALIGN_LEFT);
			indexText28.setSpacingBefore(2f);
			indexCel28.setPadding(0); 
			indexCel28.addElement(indexText28);
			indexCel28.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel28);
			
			PdfPCell indexCel38 = new PdfPCell();
			Paragraph indexText38=new Paragraph("Nature of supply",txtfont);
			indexText38.setAlignment(Element.ALIGN_LEFT);
			indexText38.setSpacingBefore(2f);
			indexCel38.setPadding(0); 
			indexCel38.addElement(indexText38);
			indexCel38.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel38);
			
			PdfPCell indexCel48 = new PdfPCell();
			Paragraph indexText48=new Paragraph(":",txtfont);
			indexText48.setAlignment(Element.ALIGN_LEFT);
			indexText48.setSpacingBefore(2f);
			indexCel48.setPadding(0); 
			indexCel48.addElement(indexText48);
			indexCel48.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel48);
			
			//----------------index 9----------------------------
			PdfPCell indexCel19 = new PdfPCell();
			Paragraph indexText19=new Paragraph("9.",txtfont);
			indexText19.setAlignment(Element.ALIGN_LEFT);
			indexText19.setSpacingBefore(2f);
			indexCel19.setPadding(0); 
			indexCel19 .addElement(indexText19);
			indexCel19.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel19);
			
			PdfPCell indexCel29 = new PdfPCell();
			Paragraph indexText29=new Paragraph("",txtfont);
			indexText29.setAlignment(Element.ALIGN_LEFT);
			indexText29.setSpacingBefore(2f);
			indexCel29.setPadding(0); 
			indexCel29.addElement(indexText29);
			indexCel29.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel29);
			
			PdfPCell indexCel39 = new PdfPCell();
			Paragraph indexText39=new Paragraph("Particulars of Meter" + 
					"  Whether owned by consumer / TGSPDCL.",txtfont);
			indexText39.setAlignment(Element.ALIGN_LEFT);
			indexText39.setSpacingBefore(2f);
			indexCel39.setPadding(0); 
			indexCel39.addElement(indexText39);
			indexCel39.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel39);
			
			PdfPCell indexCel49 = new PdfPCell();
			Paragraph indexText49=new Paragraph(":",txtfont);
			indexText49.setAlignment(Element.ALIGN_LEFT);
			indexText49.setSpacingBefore(2f);
			indexCel49.setPadding(0); 
			indexCel49.addElement(indexText49);
			indexCel49.setBorder(PdfPCell.NO_BORDER);
			
			//-------9th sub table------
			PdfPTable subTable9 = new PdfPTable(5);
			subTable9.setWidthPercentage(100);
			subTable9.setWidths(new int[]{20, 20, 20, 20, 20});
			
			insertTBLCell(subTable9, "MeterNo.", Element.ALIGN_LEFT, font6, 0);
			insertTBLCell(subTable9, "Capacity&Type", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable9, "Multiplying Factor", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable9, "Initial Reading", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable9, "Meter Make", Element.ALIGN_CENTER, font6, 0);
			
			insertTBLCell(subTable9, ".", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable9, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable9, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable9, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable9, "", Element.ALIGN_CENTER, font7, 0);
			
			indexCel49.addElement(subTable9);
			bodyTable.addCell(indexCel49);
			
			
			//----------------index 10----------------------------
			PdfPCell indexCel110 = new PdfPCell();
			Paragraph indexText110=new Paragraph("10.",txtfont);
			indexText110.setAlignment(Element.ALIGN_LEFT);
			indexText110.setSpacingBefore(2f);
			indexCel110 .addElement(indexText110);
			indexCel110.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel110);
			
			PdfPCell indexCel210 = new PdfPCell();
			Paragraph indexText210=new Paragraph("",txtfont);
			indexText210.setAlignment(Element.ALIGN_LEFT);
			indexText210.setSpacingBefore(2f);
			indexCel210.addElement(indexText210);
			indexCel210.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel210);
			
			PdfPCell indexCel310 = new PdfPCell();
			Paragraph indexText310=new Paragraph("Particulars of Seals fixed to the Meter",txtfont);
			indexText310.setAlignment(Element.ALIGN_LEFT);
			indexText310.setSpacingBefore(2f);
			//indexCel310.setColspan(2);
			indexCel310.addElement(indexText310);
			indexCel310.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel310);
			
			PdfPCell indexCel410 = new PdfPCell();
			Paragraph indexText410=new Paragraph("",txtfont);
			indexText410.setAlignment(Element.ALIGN_LEFT);
			indexText410.setSpacingBefore(2f);
			indexCel410.addElement(indexText410);
			indexCel410.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel410);
			
			//----------------index 10 sub TABLE----------------------------
			PdfPCell indexCel110st = new PdfPCell();
			Paragraph indexText110st=new Paragraph("",txtfont);
			indexText110st.setAlignment(Element.ALIGN_LEFT);
			//indexText110st.setSpacingBefore(2f);
			indexCel110st.setColspan(4);
			indexCel110st .addElement(indexText110st);
			indexCel110st.setBorder(PdfPCell.NO_BORDER);
			
			//------- sub table------
			PdfPTable subTable10 = new PdfPTable(5);
			subTable10.setWidthPercentage(100);
			subTable10.setWidths(new int[]{20, 20, 20, 20, 20});
			
			insertTBLCell(subTable10, "Location", Element.ALIGN_LEFT, font6, 0);
			insertTBLCell(subTable10, "No. Of Seals", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable10, "ImpressionOn seals", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable10, "Type of Sealer", Element.ALIGN_CENTER, font6, 0);
			insertTBLCell(subTable10, "Sl.No. of Seals", Element.ALIGN_CENTER, font6, 0);
			
			insertTBLCell(subTable10, "a) Meter Cover", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable10, "b) Terminal Cover", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable10, "c) Meter Box", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			
			insertTBLCell(subTable10, "d) Cut-out", Element.ALIGN_LEFT, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
			insertTBLCell(subTable10, "", Element.ALIGN_CENTER, font7, 0);
						
			//insertCell(sampleTable, "Data 2", Element.ALIGN_LEFT, font2, 0);
			indexCel110st.addElement(subTable10);
			bodyTable.addCell(indexCel110st);
			
			//----------------index 11----------------------------
			insertMainTBLCell(bodyTable, "11.","","Tariff Applicable",":", Element.ALIGN_LEFT, txtfont);
			//----------------index 12----------------------------
			insertMainTBLCell(bodyTable, "12.","","Security Deposit",":", Element.ALIGN_LEFT, txtfont);
						
			//================index 13========================
			insertMainTBLCell(bodyTable, "13.","","Agreement position (Despatch No. Agreement Regd. Item No. etc.)",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "14.","a.","Connected load",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "","b.","Connected load (as per sanctioned estimate)",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "15.","","Tariff guarantee",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "","a.","Special Guarantee",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "16.","","Date of commencement of Supply",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "17.","a.","Date if expiry of three months notice (for power services)",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "","b.","Whether the U.C.M. Charges were paid before releasing the services (furnish P.C.B. No. and date D.D.No. etc. /" + 
					"         Receipt No. of the E.R.O. where payment is made at E.R.O.)",":", Element.ALIGN_LEFT, txtfont);
			insertMainTBLCell(bodyTable, "18.","","Whether the land or the premises relates to the owner (Consent letter of the owner of indemnity bond to be enclosed)",":", Element.ALIGN_LEFT, txtfont);
			
			//================END index========================
			
			//----------------SINGLE LINE----------------------------
			PdfPCell indexCel = new PdfPCell();
			Paragraph indexText=new Paragraph("I am a witness to the above particulars and seals were put in my presence",txtfont);
			indexText.setAlignment(Element.ALIGN_LEFT);
			indexText.setSpacingBefore(10f);
			indexCel.setColspan(4);
			indexCel .addElement(indexText);
			indexCel.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel);
			
			//----------------1) WITNESS LINE----------------------------
			PdfPCell indexCels = new PdfPCell();
			Paragraph indexTexts=new Paragraph("1) WITNESS :",txtfont);
			indexTexts.setAlignment(Element.ALIGN_LEFT);
			indexTexts.setSpacingBefore(10f);
			indexCels.setColspan(3);
			indexCels .addElement(indexTexts);
			indexCels.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCels);
			
			PdfPCell indexCels1 = new PdfPCell();
			Paragraph indexTexts1=new Paragraph("SIGNATURE OF THE CONSUMER",txtfont);
			indexTexts1.setAlignment(Element.ALIGN_RIGHT);
			indexTexts1.setSpacingBefore(10f);
			indexCels1 .addElement(indexTexts1);
			indexCels1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCels1);
			
			//----------------2) WITNESS LINE----------------------------
			PdfPCell indexCel2ws = new PdfPCell();
			Paragraph indexText2ws=new Paragraph("2) WITNESS :",txtfont);
			indexText2ws.setAlignment(Element.ALIGN_LEFT);
			indexText2ws.setSpacingBefore(30f);
			indexCel2ws.setColspan(3);
			indexCel2ws .addElement(indexText2ws);
			indexCel2ws.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel2ws);
			
			PdfPCell indexCels2w1 = new PdfPCell();
			Paragraph indexTexts2w1=new Paragraph("Addl. Asst. Engineer / Asst. Divi. Engineer",txtfont);
			indexTexts2w1.setAlignment(Element.ALIGN_RIGHT);
			indexTexts2w1.setSpacingBefore(30f);
			indexCels2w1 .addElement(indexTexts2w1);
			indexCels2w1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCels2w1);
			
			//----------------LINE INSPECTOR----------------------------
			PdfPCell indexCelli = new PdfPCell();
			Paragraph indexTextli=new Paragraph("LINE INSPECTOR :",txtfont);
			indexTextli.setAlignment(Element.ALIGN_LEFT);
			indexTextli.setSpacingBefore(10f);
			indexCelli.setColspan(3);
			indexCelli .addElement(indexTextli);
			indexCelli.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCelli);
			
			PdfPCell indexCelli1 = new PdfPCell();
			Paragraph indexTextli1=new Paragraph("DISTRIBUTION : WITH DATE",txtfont);
			indexTextli1.setAlignment(Element.ALIGN_RIGHT);
			indexTextli1.setSpacingBefore(10f);
			indexCelli1 .addElement(indexTextli1);
			indexCelli1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCelli1);
			
			//----------------LINE INSPECTOR----------------------------
			PdfPCell indexCel2i = new PdfPCell();
			Paragraph indexText2i=new Paragraph("Forwarded to A.A.O. /ERO_____________________",txtfont);
			indexText2i.setAlignment(Element.ALIGN_LEFT);
			indexText2i.setSpacingBefore(10f);
			indexCel2i.setColspan(3);
			indexCel2i .addElement(indexText2i);
			indexCel2i.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCel2i);
			
			PdfPCell indexCel2i1 = new PdfPCell();
			Paragraph indexText2i1=new Paragraph("ADDL. ASST. ENGINEER",txtfont);
			indexText2i1.setAlignment(Element.ALIGN_RIGHT);
			indexText2i1.setSpacingBefore(10f);
			indexCel2i1 .addElement(indexText2i1);
			indexCel2i1.setBorder(PdfPCell.NO_BORDER);
			bodyTable.addCell(indexCelli1);
			
			
			document.add(bodyTable);
	        
	        
	        
	        //insertCell(table, rs.getString(1), Element.ALIGN_LEFT, font6,0);

	        // Add a new page if needed
	        document.newPage();

	        // Service Header Table
	        PdfPTable sheaderTable = new PdfPTable(1);
	        sheaderTable.setWidthPercentage(100f);
	        sheaderTable.setSpacingBefore(2f);

	        PdfPCell sheadCell = new PdfPCell();
	        Paragraph titleSubTxt=new Paragraph("PART - 'B'",font3);
	        //sheadCell.addElement(new Paragraph("PART 'B'", font2));
	        titleSubTxt.setAlignment(Element.ALIGN_CENTER);
	        sheadCell .addElement(titleSubTxt);
	        sheadCell.setBorder(PdfPCell.NO_BORDER);
	        sheaderTable.addCell(sheadCell);
	        
	        insertMainTBLCell(sheaderTable, "1.","","Security Deposit Register","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Item No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Folio No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","S D Vol. No.	","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "2.","","Service Connection Register ","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "","","Item No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Folio No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Vol.No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "3.","","Agreement Register","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "","","Item No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Folio No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Vol.No.","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "4.","","A.M.G. Calender","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "","","Item No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","Folio No.","", Element.ALIGN_LEFT, txtfont);
	        
insertMainTBLCell(sheaderTable, "5.","","Consumer Ledger","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "","","Item No.","", Element.ALIGN_LEFT, txtfont);
	        insertMainTBLCell(sheaderTable, "","","S.C. No.","", Element.ALIGN_LEFT, txtfont);
	        
	        insertMainTBLCell(sheaderTable, "","","Ledger Folio Opened to entries completed Meter Card Opened","", Element.ALIGN_LEFT, txtfont);
	        
	        document.add(sheaderTable);

	    } catch (Exception e) {
	        // Handle the exception
	        e.printStackTrace();
	        throw new ServletException("Error generating PDF", e);
	    } finally {
	        // Ensure the document is closed
	        if (document.isOpen()) {
	            document.close();
	        }
	    }
	}

	void insertCell(PdfPTable table, String text, int halign, Font font, int bgcolor) {
	    PdfPCell cell = new PdfPCell(new Paragraph(text, font));
	    cell.setBorderColor(BaseColor.BLACK);
	    cell.setPadding(8);
	    if (bgcolor == 1) {
	        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    }
	    cell.setHorizontalAlignment(halign);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    table.addCell(cell);
	}
	
	void insertTBLCell(PdfPTable table, String text, int halign, Font font, int bgcolor) {
	    PdfPCell cell = new PdfPCell(new Paragraph(text, font));
	    cell.setBorderColor(BaseColor.BLACK);
	    cell.setPadding(3);
	    if (bgcolor == 1) {
	        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    }
	    cell.setHorizontalAlignment(halign);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    table.addCell(cell);
	}
	
	void insertMainTBLCell(PdfPTable table, String index, String subx, String text,String desc, int halign, Font font) {
		PdfPCell indexCel = new PdfPCell();
		Paragraph indexText=new Paragraph(index,font);
		indexText.setAlignment(Element.ALIGN_LEFT);
		indexCel.addElement(indexText);
		indexCel.setBorder(PdfPCell.NO_BORDER);
		table.addCell(indexCel);
		
		PdfPCell indexCel2 = new PdfPCell();
		Paragraph indexText2=new Paragraph(subx,font);
		indexText2.setAlignment(Element.ALIGN_LEFT);
		//indexText2.setSpacingBefore(2f);
		indexCel2.addElement(indexText2);
		indexCel2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(indexCel2);
		
		PdfPCell indexCel3 = new PdfPCell();
		Paragraph indexText3=new Paragraph(text,font);
		indexText3.setAlignment(Element.ALIGN_LEFT);
		//indexCel310.setColspan(2);
		indexCel3.addElement(indexText3);
		indexCel3.setBorder(PdfPCell.NO_BORDER);
		table.addCell(indexCel3);
		
		PdfPCell indexCel4 = new PdfPCell();
		Paragraph indexText4=new Paragraph(desc,font);
		indexText4.setAlignment(Element.ALIGN_LEFT);
		indexCel4.addElement(indexText4);
		indexCel4.setBorder(PdfPCell.NO_BORDER);
		table.addCell(indexCel4);
		
	}

	
	@GetMapping("/ltmapplicationstatus/{pagetype}/{menuId}")
	public String LTMApplicationStatus(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		model.addAttribute("pageTitle", "LTM Registration Application Status");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		//model.addAttribute("docslist", docsList);
		return "nsts/ltmapplicationstatus";
	}
	
	@RequestMapping(value = "/getLTMDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getLTMDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request) throws SQLException, Exception {
		//System.out.println("Registration ID..."+regId);
		//System.out.println(nstsDAO.getRegData(regId));
		//System.out.println("context path..."+request.getContextPath());
		//System.out.println("request path..."+request.getServletContext().getRealPath(""));
		//System.out.println("real path message resource..."+messageSource.getMessage("sas.realpath", null, Locale.ENGLISH));
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getLTMRegData(regId, realPath, pagetype));
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getLTMRegistrationDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getLTMRegistrationDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request) throws SQLException, Exception {
		System.out.println("Registration ID..."+regId);
		//System.out.println(nstsDAO.getRegData(regId));
		//System.out.println("context path..."+request.getContextPath());
		//System.out.println("request path..."+request.getServletContext().getRealPath(""));
		//System.out.println("real path message resource..."+messageSource.getMessage("sas.realpath", null, Locale.ENGLISH));
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getLTMRegData(regId, realPath, pagetype));
		//return nstsDAO.getRegData(regId);
	}
	
	@GetMapping("/ltmaddconnections/{pagetype}/{menuId}")
	public String LtmAddConnections(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <AddConnections> addConsList = nstsDAO.getAddConnectionList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Pending for Add Connections");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", addConsList);
		return "nsts/addconnections";
	}
	
	@RequestMapping(value = "/getAddConDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getAddConDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request) throws SQLException, Exception {
		System.out.println("Registration ID..."+regId);
		//System.out.println(nstsDAO.getRegData(regId));
		//System.out.println("context path..."+request.getContextPath());
		//System.out.println("request path..."+request.getServletContext().getRealPath(""));
		//System.out.println("real path message resource..."+messageSource.getMessage("sas.realpath", null, Locale.ENGLISH));
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getAddConData(regId, realPath, pagetype));
		//return nstsDAO.getRegData(regId);
	}
	
	
	@GetMapping("/ltmsendotptoconsumer/{pagetype}/{menuId}")
	public String LtmSendOTPtoConsumer(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <LTMSendOTPtoConsumer> addConsList = nstsDAO.getOTPtoConsumer((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Sending OTP to Consumer");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", addConsList);
		return "nsts/ltmsendotpconsumer";
	}
	
	@RequestMapping(value = "/getLTMDocOtpApproval/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getLTMDocOtpApproval(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getLTMDocOtpApproval(regId, realPath, pagetype,  (User)session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	
	@RequestMapping(value = "/sendSMStoLTMLineman/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> sendSMStoLTMLineman(@PathVariable String pagetype, @RequestParam String regid, @RequestParam String empid, @RequestParam String lmphone,
		HttpServletRequest request, HttpSession session, @ModelAttribute("sendsms") LTMsmsSendToLineman sendsms, Model model) throws SQLException, Exception {
		//, @RequestParam("meterno") String meterno
		String meterno="";
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result = nstsDAO.sendSMStoLTMConsumer(regid, empid, lmphone, (User) session.getAttribute("userData"));
		//System.out.println("result>>>"+result);
		//List <LTMSendOTPtoConsumer> addConsList = nstsDAO.getOTPtoConsumer((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Sending OTP to Consumer");
		//model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		//model.addAttribute("docslist", addConsList);
		//return "nsts/ltmsendotpconsumer";
		//return ResponseEntity.ok().body(nstsDAO.getLTMDocOtpApproval(regid, realPath, pagetype,  (User)session.getAttribute("userData")));
		return ResponseEntity.ok().body(result);
	}
	
	@GetMapping("/ltmtestreport/{pagetype}/{menuId}")
	public String LtmTestReport(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <LTMtestReport> ltmList = nstsDAO.getTestReportInfo((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "LTM Test Report Generation");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("ltmlist", ltmList);
		return "nsts/ltmtestreport";
	}
	
	@RequestMapping(value = "/getTestReport/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getTestReport(@RequestParam("regid") String regId, @RequestParam("meterno") String meterno, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		return ResponseEntity.ok().body(nstsDAO.getLTMTestReport(regId, meterno, realPath, pagetype,  (User)session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	@GetMapping("/ltmotpupdation/{pagetype}/{menuId}")
	public String LtmOtpUpdation(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <LTMSendOTPtoConsumer> otpUpdationList = nstsDAO.getOTPUpdationList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "LTM OTP Updation");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", otpUpdationList);
		return "nsts/ltmotpupdation";
	}
	
	
	@PostMapping("/uploadTestReport")
	public String uploadTestReport(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("uploadtestreport") UploadTestReportModel utr,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=nstsDAO.uploadTestReport((UploadTestReportModel) utr,(User) session.getAttribute("userData"), realPath);
		   redirectAttributes.addFlashAttribute("result",result);
		   System.out.println(result);
		   return "redirect:/nsts/ltmotpupdation?type=casetype";
	}
	
	@GetMapping("/ltmtobereleased/{pagetype}/{menuId}")
	public String LtmTobeRelesed(@PathVariable int menuId, @PathVariable String pagetype, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <LTMSendOTPtoConsumer> tobereleasedList = nstsDAO.getToBeReleasedList((User) session.getAttribute("userData"));
		model.addAttribute("pageTitle", "LTM to be Released");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("docslist", tobereleasedList);
		return "nsts/ltmtoberelesed";
	}
	
	@RequestMapping(value = "/getLTMtobeReleasedDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getLTMtobeReleasedDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder lineMenList = new StringBuilder();
		StringBuilder areaList = new StringBuilder();
		StringBuilder FeederList = new StringBuilder();
		HashMap<String,String> areacode = new HashMap<>();
		HashMap<String,String> linemen = new HashMap<>();
		HashMap<String,String> feeders = new HashMap<>();
		String amount="", amt_tobe_paid="", amt_paid="";
			//Area List
			areacode = nstsDAO.getAreaList((User)session.getAttribute("userData"));
			areaList.append("<option value='x'>Choose Area</option>");
			
			areacode.forEach((key, value) -> {
				areaList.append("<option value='" + key + "'>" + value + "</option>");
			});
			//Linemen List
			linemen = nstsDAO.getLinemenList((User)session.getAttribute("userData"));
			lineMenList.append("<option value='x'>Choose Lineman</option>");
	
			linemen.forEach((key, value) -> {
				lineMenList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			//Feeder List
			feeders = nstsDAO.getFeederList((User)session.getAttribute("userData"));
			FeederList.append("<option value='x'>Choose Feeder</option>");
			
			feeders.forEach((key, value) -> {
				FeederList.append("<option value='" + key + "'>" + value + "</option>");
			});
		
			amount=nstsDAO.getAmountDetails(regId);
			//System.out.println("amount>>>>>>>>"+amount);
			String[] regAmt = amount.split("@@");
			amt_tobe_paid=regAmt[0];
			amt_paid=regAmt[1];
			
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getLTMtobeReleasedDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData"))+"@@@"+lineMenList+"@@@"+areaList+"@@@"+FeederList+"@@@"+amt_tobe_paid+"@@@"+amt_paid+"@@@"+nstsDAO.getFlatDetails(regId, (User)session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getStructureCode", method = RequestMethod.POST)
	public ResponseEntity<?> getStructureCode(@RequestParam("feedercode") String feedercode, 
			HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder structureList = new StringBuilder();
		HashMap<String,String> structure = new HashMap<>();
		
			structure = nstsDAO.getStructureList((User)session.getAttribute("userData"),feedercode);
			structureList.append("<option value='x'>Choose Structure</option>");
	
			structure.forEach((key, value) -> {
				structureList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			return ResponseEntity.ok().body(structureList);
		
	}
	
	@GetMapping("/agltobereleasedlist/{menuId}")
	public String AGLTobeRelesedList(@PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <AGLRegistrations> agltobereleasedList = nstsDAO.getAglToBeReleasedList((User) session.getAttribute("userData"));
		model.addAttribute("pageTitle", "AGL to be Released List");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("agllist", agltobereleasedList);
		return "nsts/agltoberelesed";
	}
	
	@RequestMapping(value = "/getAGLtobeReleasedDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getAGLtobeReleasedDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder lineMenList = new StringBuilder();
		StringBuilder FeederList = new StringBuilder();
		HashMap<String,String> linemen = new HashMap<>();
		HashMap<String,String> feeders = new HashMap<>();
		String scno_seq="",xbox_val="";
		int xibox=0,xjbox=0;
			
			//Linemen List
			linemen = nstsDAO.getLinemenList((User)session.getAttribute("userData"));
			lineMenList.append("<option value='x'>Choose Lineman</option>");
	
			linemen.forEach((key, value) -> {
				lineMenList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			//Feeder List
			feeders = nstsDAO.getFeederList((User)session.getAttribute("userData"));
			FeederList.append("<option value='x'>Choose Feeder</option>");
			
			feeders.forEach((key, value) -> {
				FeederList.append("<option value='" + key + "'>" + value + "</option>");
			});
		
			xbox_val=nstsDAO.getServiceNoBoxDetails((User)session.getAttribute("userData"));
			String[] regAmt = xbox_val.split("@@");
			xibox=Integer.parseInt(regAmt[0]);
			xjbox=Integer.parseInt(regAmt[1]);
			
			scno_seq=nstsDAO.getSequenceNumber();
			
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getAGLtobeReleasedDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData"))+"@@@"+lineMenList+"@@@"+FeederList+"@@@"+scno_seq+"@@@"+xibox+"@@@"+xjbox);
		//return nstsDAO.getRegData(regId);
	}
	
	@PostMapping("/aglTobeReleased")
	public String AglTobeReleased(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("agltobrReleased") AglTobeReleasedModel atr,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		//String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String error_msg="",available_flag="";
		String client_ip=request.getRemoteAddr();
		String check_scno = nstsDAO.getCheckServiceAvailable((AglTobeReleasedModel) atr,(User) session.getAttribute("userData"));
		String[] chk_data = check_scno.split("@@");
		error_msg=chk_data[0];
		available_flag=chk_data[1];
		
		if("0".equals(available_flag)){
			String result=nstsDAO.updateAglTobeReleased((AglTobeReleasedModel) atr,(User) session.getAttribute("userData"), client_ip);
			redirectAttributes.addFlashAttribute("result",result);
			//System.out.println(result);
		}
		return "redirect:/nsts/ltmotpupdation?type=casetype";
	}
	
	@RequestMapping(value = "/getNscTobeReleasedDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getNscTobeReleasedDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder lineMenList = new StringBuilder();
		StringBuilder FeederList = new StringBuilder();
		HashMap<String,String> linemen = new HashMap<>();
		HashMap<String,String> feeders = new HashMap<>();
		String scno_seq="",xbox_val="";
		int xibox=0,xjbox=0;
			
			//Linemen List
			linemen = nstsDAO.getLinemenList((User)session.getAttribute("userData"));
			lineMenList.append("<option value='x'>Choose Lineman</option>");
	
			linemen.forEach((key, value) -> {
				lineMenList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			//Feeder List
			feeders = nstsDAO.getFeederList((User)session.getAttribute("userData"));
			FeederList.append("<option value='x'>Choose Feeder</option>");
			
			feeders.forEach((key, value) -> {
				FeederList.append("<option value='" + key + "'>" + value + "</option>");
			});
		
			xbox_val=nstsDAO.getServiceNoBoxDetails((User)session.getAttribute("userData"));
			String[] regAmt = xbox_val.split("@@");
			xibox=Integer.parseInt(regAmt[0]);
			xjbox=Integer.parseInt(regAmt[1]);
			
			scno_seq=nstsDAO.getSequenceNumber();
			
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getNscTobeReleasedDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData"))+"@@@"+lineMenList+"@@@"+FeederList+"@@@"+scno_seq+"@@@"+xibox+"@@@"+xjbox);
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getNsTestReportUploadDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getNsTestReportUploadDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder lineMenList = new StringBuilder();
		StringBuilder FeederList = new StringBuilder();
		HashMap<String,String> linemen = new HashMap<>();
		HashMap<String,String> feeders = new HashMap<>();
		String scno_seq="",xbox_val="";
		int xibox=0,xjbox=0;
			
			xbox_val=nstsDAO.getServiceNoBoxDetails((User)session.getAttribute("userData"));
			String[] regAmt = xbox_val.split("@@");
			xibox=Integer.parseInt(regAmt[0]);
			xjbox=Integer.parseInt(regAmt[1]);
			
			scno_seq=nstsDAO.getSequenceNumber();
			
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(nstsDAO.getNsTestReportUploadDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData"))+"@@@"+lineMenList+"@@@"+FeederList+"@@@"+scno_seq+"@@@"+xibox+"@@@"+xjbox);
		//return nstsDAO.getRegData(regId);
	}
	/*private void show(List<NewRegistrations> regList) {
		regList.forEach(System.out::println);
	}*/
	
	@PostMapping("/uploadNRTestReport")
	public String uploadNRTestReport(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("testreportupload") UploadTestReportNewModel utr,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=nstsDAO.uploadNRTestReport((UploadTestReportNewModel) utr,(User) session.getAttribute("userData"), realPath);
		   redirectAttributes.addFlashAttribute("result",result);
		   System.out.println(result);
		   return "redirect:/nsts/nscstobereleased/vd/1";
		   
	}
}


