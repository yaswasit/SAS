package com.tsspdcl.sas.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tsspdcl.sas.dao.nsts.CctsDAO;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.entity.nsts.LTMSendOTPtoConsumer;
import com.tsspdcl.sas.entity.nsts.UploadTestReportModel;
import com.tsspdcl.sas.entry.ccts.BurntConmplaint;
import com.tsspdcl.sas.entry.ccts.CategoryChangeList;
import com.tsspdcl.sas.entry.ccts.MRTtesting;
//import com.tsspdcl.sas.entry.ccts.MeterComplaintsInsert;
import com.tsspdcl.sas.entry.ccts.QualityCheckList;
import com.tsspdcl.sas.entry.ccts.UploadPowerSupplyModel;

@Controller
@RequestMapping("/ccts")
public class ConsCompController {
	
	@Autowired   
	private MessageSource messageSource;  
	
	CctsDAO cctsDAO = new CctsDAO();
	
	@GetMapping("/ccapplicationstatus/{pagetype}/{menuId}")
	public String CCApplicationStatus(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		model.addAttribute("pageTitle", "Complaint Status");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "ccts/ccapplicationstatus";
	}
	
	@RequestMapping(value = "/getComplaintDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getComplaintDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		System.out.println("Registration ID......................."+regId);
		//System.out.println(nstsDAO.getRegData(regId));
		//System.out.println("context path..."+request.getContextPath());
		//System.out.println("request path..."+request.getServletContext().getRealPath(""));
		//System.out.println("real path message resource..."+messageSource.getMessage("sas.realpath", null, Locale.ENGLISH));
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(cctsDAO.getRegData(regId, realPath, pagetype, (User) session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	@GetMapping("/ccqualityofpowersupply/{pagetype}/{menuId}")
	public String QualityOfPowerSupply(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <QualityCheckList> qualityCheckList = cctsDAO.ccQualityOfPowerSupply((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Quality of power supply");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", qualityCheckList);
		return "ccts/qualityCheckList";
	}
	
	@GetMapping("/ccrestorationofpowersupply/{pagetype}/{menuId}")
	public String RestorationOfPowerSupply(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <QualityCheckList> qualityCheckList = cctsDAO.ccRestorationOfPowerSupply((User) session.getAttribute("userData"));
		System.out.println("RestorationCheckList..."+qualityCheckList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Restoration of power supply");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", qualityCheckList);
		return "ccts/qualityCheckList";
	}
	
	@RequestMapping(value = "/getPowerSupplyDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getLTMDocOtpApproval(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(cctsDAO.getPowerSupplyDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	@PostMapping("/uploadQPS")
	public String uploadTestReport(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("uploadpowersupply") UploadPowerSupplyModel utr,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=cctsDAO.uploadPowerSupply((UploadPowerSupplyModel) utr,(User) session.getAttribute("userData"), realPath);
		   redirectAttributes.addFlashAttribute("result",result);
		   //System.out.println(result);
		   return "redirect:/ccts/qualityCheckList?type=casetype";
	}
	
	@GetMapping("/cccategorychange/{pagetype}/{menuId}")
	public String ccCategoryChange(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <CategoryChangeList> catChangeList = cctsDAO.ccCategoryChangeList((User) session.getAttribute("userData"));
		model.addAttribute("pageTitle", "Category Change Test report Upload");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("cclist", catChangeList);
		return "ccts/categorychangelist";
	}
	
	@RequestMapping(value = "/getCatChangeDetails/{pagetype}", method = RequestMethod.POST)
	public ResponseEntity<?> getCatChangeDetails(@RequestParam("regid") String regId, @PathVariable String pagetype,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String doclist="";
		//System.out.println("real path..."+realPath);
		return ResponseEntity.ok().body(cctsDAO.getPowerSupplyDetails(regId, realPath, pagetype,  (User)session.getAttribute("userData")));
		//return nstsDAO.getRegData(regId);
	}
	
	@RequestMapping(value = "/getDocumentsList", method = RequestMethod.POST)
	public ResponseEntity<?> getDocumentsList(@RequestParam("empid") String empid, 
			HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		StringBuilder docList = new StringBuilder();
		HashMap<String,String> docs = new HashMap<>();
			docs = cctsDAO.getDocumentsList((User)session.getAttribute("userData"));
			docList.append("<option value='x'>Choose Documents</option>");
	
			docs.forEach((key, value) -> {
				docList.append("<option value='" + key + "'>" + value + "</option>");
			});
			
			return ResponseEntity.ok().body(docList);
	}
	
	@PostMapping("/uploadCatChange")
	public String uploadCatChange(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("uploadpowersupply") UploadPowerSupplyModel utr,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String ipaddr=(String)session.getAttribute("ipaddr");
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=cctsDAO.uploadCatChange((UploadPowerSupplyModel) utr,(User) session.getAttribute("userData"), realPath, ipaddr);
		   redirectAttributes.addFlashAttribute("result",result);
		   //System.out.println(result); /ccts/cccategorychange/catc/${menuId}
		   return "redirect:/ccts/cccategorychange/catc/2";
	}
	
	@GetMapping("/ccmetercomplaints/{pagetype}/{menuId}")
	public String MeterComplaintsList(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <QualityCheckList> meterComplaintsList = cctsDAO.ccMeterComplaintsList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Meter Complaints");
		model.addAttribute("subpageTitle", "");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", meterComplaintsList);
		return "ccts/meterComplaintsList";
	}
	
	@GetMapping("/ccburntlist/{pagetype}/{menuId}")
	public String BurntComplaintsList(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <QualityCheckList> burntComplaintsList = cctsDAO.ccBurntComplaintsList((User) session.getAttribute("userData"));
		
		HashMap<String,String> mpinfo = cctsDAO.meterPendingComplaints(menuId, (User)session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Meter Burnt Complaints");
		model.addAttribute("subpageTitle", "");
		model.addAttribute("mpinfo", mpinfo);
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", burntComplaintsList);
		return "ccts/burntComplaintsList";
	}
	
	@GetMapping("/ccMRTtestNoticeDownload/{pagetype}/{menuId}")
	public String ccMRTtestNoticeDownload(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <MRTtesting> mrtTestNoticeList = cctsDAO.ccMRTtestNoticeDownloadList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "MRT Testing Notice Download");
		model.addAttribute("subpageTitle", "Testing");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", mrtTestNoticeList);
		return "ccts/mrtTestingNoticeDownload";
	}
	
	@GetMapping("/ccStuckupList/{pagetype}/{menuId}")
	public String ccStuckupList(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <MRTtesting> mrtTestNoticeList = cctsDAO.ccStuckupList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "Meter Stuckup Complaint List");
		model.addAttribute("subpageTitle", "");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", mrtTestNoticeList);
		return "ccts/stuckupComplaintsList";
	}
	
	@GetMapping("/ccAErequestDate/{pagetype}/{menuId}")
	public String ccAErequestDate(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		//List <MRTtesting> mrtTestNoticeList = cctsDAO.ccStuckupList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "AE Request Date");
		model.addAttribute("subpageTitle", "Testing");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		//model.addAttribute("qclist", mrtTestNoticeList);
		return "ccts/aerequestdate";
	}
	
	@GetMapping("/ccMrtTestforbillRevision/{pagetype}/{menuId}")
	public String ccMrtTestforbillRevision(@PathVariable String pagetype, @PathVariable int menuId, 
			Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <QualityCheckList> mrtTestList = cctsDAO.ccTestforBillRevisionList((User) session.getAttribute("userData"));
		//System.out.println("docsList..."+docsList);
		//show(newRegList);
		model.addAttribute("pageTitle", "MRT Test Report");
		model.addAttribute("subpageTitle", "Testing");
		model.addAttribute("menuId", menuId);
		model.addAttribute("userData", session.getAttribute("userData"));
		model.addAttribute("qclist", mrtTestList);
		return "ccts/testreportforbillrevisionlist";
	}
	
	@PostMapping("/submitComplaints")
    public String submitComplaints(@RequestParam("rows") int rows,
                                     HttpSession session,HttpServletRequest request) throws SQLException, Exception {
       // List<MeterComplaintsInsert> burntList = new ArrayList<>();
        List<BurntConmplaint> burntList = new ArrayList<>();
        for(int i=1;i<=rows;i++) {
        	//System.out.println(rows+"#########################"+request.getParameter("chk"+i)+"###"+request.getParameter("burnt"+i));
        	if(request.getParameter("chk"+i)!=null) {
        		BurntConmplaint burnt = new BurntConmplaint();
     		   	//System.out.println("#########"+request.getParameter("perptname"+i));
     		   	burnt.setComplaint_number(request.getParameter("complaint_number"+i));
     		   	burnt.setBurntStatus(request.getParameter("burntStatus"+i));
     		   	burntList.add(burnt);
        	}
        }
        //System.out.println("@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@#@##@"+burntList.size());
        String result = cctsDAO.burntComplaintUpdate((User) session.getAttribute("userData"), burntList);
        // Uncomment to implement your logic
        // complaintService.saveComplaints(complaintsToSave);
        return "redirect:/ccts/ccburntlist/mc/2";
        //return "redirect:/ccts/ccburntlist/mc/2"; // Ensure this URL is valid
    }
}
