package com.tsspdcl.sas.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import com.tsspdcl.sas.dao.nsts.MmDAO;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.entity.nsts.AglTobeReleasedModel;
import com.tsspdcl.sas.entity.nsts.UploadTestReportModel;
import com.tsspdcl.sas.entry.mm.AreaList;
import com.tsspdcl.sas.entry.mm.LinemanList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentAutoList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentModel;
import com.tsspdcl.sas.entry.mm.MeterAllotmentServicesList;
import com.tsspdcl.sas.entry.mm.MeterAllotmentStuckupModel;
import com.tsspdcl.sas.entry.mm.MeterMakeList;
import com.tsspdcl.sas.entry.mm.MeterStatusList;
import com.tsspdcl.sas.entry.mm.StuckupDataList;

@Controller
@RequestMapping("/mm")
public class MMController {
	
	@Autowired   
	private MessageSource messageSource; 
	
		
	MmDAO mmDAO = new MmDAO();
	
	@GetMapping("/burntmeterallotment/{pagetype}/{menuId}")
	public String BurntMeterAllotment(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <AreaList> areaList = mmDAO.mmAreaList((User) session.getAttribute("userData"));
		
		model.addAttribute("pageTitle", "Burnt Meter Allotment");
		model.addAttribute("menuId", menuId);
		model.addAttribute("areaList", areaList);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/burntmeterallotment";
	}
	
	@GetMapping("/getBurntDetails/{pagetype}")
	public String getBurntDetails(@RequestParam("area") String area, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <MeterAllotmentServicesList> masList = mmDAO.allotmentServiceList((User) session.getAttribute("userData"), area);
		model.addAttribute("pageTitle", "Meter Allotment for Services");
		model.addAttribute("menuId", 3);
		model.addAttribute("area", area);
		model.addAttribute("masList", masList);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/burntmeterallotmententry";
	}
	
	
	@PostMapping("/InsertBurntMeterAllotment")
	public String submitFormData(
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam Map<String, String> params
	        /*@RequestParam(value = "chk", required = false) List<String> selectedRows,
	        @RequestParam(value = "CTSCNO", required = false) List<String> serviceNos,
	        @RequestParam(value = "CTCAT", required = false) List<String> cats,
	        @RequestParam(value = "CTCTRLD", required = false) List<String> loads,
	        @RequestParam(value = "CTMTRPHASE", required = false) List<String> phases,
	        @RequestParam(value = "CTMTREXPDT", required = false) List<String> expDates,
	        @RequestParam(value = "approv", required = false) List<String> approvs,
	        @RequestParam(value = "CTMobile", required = false) List<String> mobiles*/) {
			
		//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@rows=="+rows);
		HashMap<String, String> formData = new HashMap<>();
		for (String key : params.keySet()) {
			 System.out.println("Key: " + key + ", Value: " + params.get(key));
			 formData.put(key, params.get(key));
        }
		mmDAO.saveServiceData(formData, rows);
	    /*if (selectedRows != null && !selectedRows.isEmpty()) {
	        for (int i = 0; i < selectedRows.size(); i++) {
	            String serviceNo = serviceNos.get(i);
	            String cat = cats.get(i);
	            String load = loads.get(i);
	            String phase = phases.get(i);
	            String expDate = expDates.get(i);
	            String approv = approvs.get(i);
	            String mobile = mobiles.get(i);
System.out.println("========================================================================================================================----------mobile="+mobile);
	            // Using service to save the data
	            mmDAO.saveServiceData(serviceNo, cat, load, phase, expDate, approv, mobile);
	        }
	    }*/
	    
	    return "redirect:/mm/burntmeterallotmententry"; // Redirect to a confirmation page
	}
	
	@GetMapping("/meterallotment/{pagetype}/{menuId}")
	public String MeterAllotment(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <AreaList> areaList = mmDAO.mmAreaList((User) session.getAttribute("userData"));
		
		List <MeterStatusList> meterStatusList = mmDAO.meterStatusList((User) session.getAttribute("userData"));
		
		model.addAttribute("pageTitle", "Meter Allotment");
		model.addAttribute("menuId", menuId);
		model.addAttribute("areaList", areaList);
		model.addAttribute("msList", meterStatusList);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/meterallotment";
	}
	
	@GetMapping("/MeterAllotmentNEntry")
	public String MeterAllotmentNEntry(@RequestParam("area") String area,@RequestParam("areaname") String areaname,@RequestParam("meterStatus") String meterStatus,@RequestParam("mtrstatus_name") String mtrstatus_name, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date dateObj= new java.util.Date();
		String date=sdf1.format(dateObj);
		
		List <LinemanList> lmList = mmDAO.mmLinemanList((User) session.getAttribute("userData"));
		
		List <MeterMakeList> mkList = mmDAO.mmMeterMakeList((User) session.getAttribute("userData"));
		
		model.addAttribute("pageTitle", "Meter Allotment for "+mtrstatus_name+" Services");
		model.addAttribute("menuId", 3);
		model.addAttribute("area", area);
		model.addAttribute("areaname", areaname);
		model.addAttribute("meterStatus", meterStatus);
		model.addAttribute("mtrstatus_name", mtrstatus_name);
		model.addAttribute("lmList", lmList);
		model.addAttribute("mkList", mkList);
		model.addAttribute("date", date);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/meterallotmentnentry";
	}
	
	@RequestMapping(value = "/getContent", method = RequestMethod.POST)
	public ResponseEntity<?> getContent(@RequestParam("mmake") String mmake,@RequestParam("ids") String ids, @RequestParam("mphase") String mphase,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		return ResponseEntity.ok().body(mmDAO.getContent(mmake, ids, mphase, realPath, (User) session.getAttribute("userData")));
	}
	
	@RequestMapping(value = "/getSealInfo", method = RequestMethod.POST)
	public ResponseEntity<?> getSealInfo(@RequestParam("mnomake") String mmake,@RequestParam("mmeterno") String meterno, @RequestParam("mphase") String mphase,
		HttpServletRequest request, HttpSession session) throws SQLException, Exception {
		
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		return ResponseEntity.ok().body(mmDAO.getSealInfo(mmake, meterno, mphase, realPath, (User) session.getAttribute("userData")));
	}
	
	@PostMapping("/meterAllotmentInserts")
	public String meterAllotmentInserts(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("meterallotment") MeterAllotmentModel mam,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=mmDAO.meterAllotmentInserts((MeterAllotmentModel) mam,(User) session.getAttribute("userData"), realPath, session);
		   redirectAttributes.addFlashAttribute("result",result);
		   //System.out.println(result);
		   return "redirect:/mm/meterallotmentnentry?type=casetype";
	}
	
	
	@GetMapping("/MeterAllotmentEntryAuto")
	public String MeterAllotmentEntryAuto(@RequestParam("area") String area,@RequestParam("areaname") String areaname,@RequestParam("meterStatus") String meterStatus,@RequestParam("mtrstatus_name") String mtrstatus_name, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date dateObj= new java.util.Date();
		String date=sdf1.format(dateObj);
		
		List <LinemanList> lmList = mmDAO.mmLinemanList((User) session.getAttribute("userData"));
		
		List <MeterMakeList> mkList = mmDAO.mmMeterMakeList((User) session.getAttribute("userData"));
		
		List <MeterAllotmentAutoList> maaList = mmDAO.meterAllotmentAutoList((User) session.getAttribute("userData"), area, meterStatus);
		
		String leddt = mmDAO.getLedDate((User) session.getAttribute("userData"), area, meterStatus);
		
		model.addAttribute("pageTitle", "Meter Allotment for "+mtrstatus_name+" Services");
		model.addAttribute("menuId", 3);
		model.addAttribute("area", area);
		model.addAttribute("areaname", areaname);
		model.addAttribute("meterStatus", meterStatus);
		model.addAttribute("mtrstatus_name", mtrstatus_name);
		model.addAttribute("lmList", lmList);
		model.addAttribute("mkList", mkList);
		model.addAttribute("maaList", maaList);
		model.addAttribute("date", date);
		model.addAttribute("leddt", leddt);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/meterallotmententryauto";
	}
	
	@GetMapping("/MeterAllotmentEntryStuckupAuto")
	public String MeterAllotmentEntryStuckupAuto(@RequestParam("area") String area,@RequestParam("areaname") String areaname,@RequestParam("meterStatus") String meterStatus,@RequestParam("mtrstatus_name") String mtrstatus_name, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date dateObj= new java.util.Date();
		String date=sdf1.format(dateObj);
		
		List <LinemanList> lmList = mmDAO.mmLinemanList((User) session.getAttribute("userData"));
		
		List <MeterMakeList> mkList = mmDAO.mmMeterMakeList((User) session.getAttribute("userData"));
		
		List <MeterAllotmentAutoList> maaList = mmDAO.meterAllotmentStuckupAutoList((User) session.getAttribute("userData"), area, meterStatus);
		
		List <StuckupDataList> sdList = mmDAO.getStuckupDataList((User) session.getAttribute("userData"), area, meterStatus);
		int mtrcount=sdList.size();
		String leddt = mmDAO.getLedDate((User) session.getAttribute("userData"), area, meterStatus);
		
		model.addAttribute("pageTitle", "Meter Allotment for "+mtrstatus_name+" Services");
		model.addAttribute("menuId", 3);
		model.addAttribute("area", area);
		model.addAttribute("areaname", areaname);
		model.addAttribute("meterStatus", meterStatus);
		model.addAttribute("mtrstatus_name", mtrstatus_name);
		model.addAttribute("lmList", lmList);
		model.addAttribute("mkList", mkList);
		model.addAttribute("sdList", sdList);
		model.addAttribute("maaList", maaList);
		model.addAttribute("date", date);
		model.addAttribute("leddt", leddt);
		model.addAttribute("mtrcount", mtrcount);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/meterallotmententrystuckupauto";
	}
	
	@PostMapping("/meterAllotmentStuckupInserts")
	public String meterAllotmentStuckupInserts(Model model,HttpSession session,HttpServletRequest request,@ModelAttribute("meterallotment") MeterAllotmentStuckupModel mam,RedirectAttributes redirectAttributes ) throws SQLException, Exception {
		String realPath = messageSource.getMessage("sas.realpath", null, Locale.ENGLISH);
		String result=mmDAO.meterAllotmentStuckupInserts((MeterAllotmentStuckupModel) mam,(User) session.getAttribute("userData"), realPath, session);
		   redirectAttributes.addFlashAttribute("result",result);
		   //System.out.println(result);
		   return "redirect:/mm/meterallotmentnentry?type=casetype";
	}
	
	@GetMapping("/meterAllotmentEntryBurntloadAuto")
	public String meterAllotmentEntryBurntloadAuto(@RequestParam("area") String area,@RequestParam("areaname") String areaname,@RequestParam("meterStatus") String meterStatus,@RequestParam("mtrstatus_name") String mtrstatus_name, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date dateObj= new java.util.Date();
		String date=sdf1.format(dateObj);
		
		List <LinemanList> lmList = mmDAO.mmLinemanList((User) session.getAttribute("userData"));
		
		List <MeterMakeList> mkList = mmDAO.mmMeterMakeList((User) session.getAttribute("userData"));
		
		List <MeterAllotmentAutoList> maaList = mmDAO.meterAllotmentBurntAutoList((User) session.getAttribute("userData"), area, meterStatus);
		
		List <StuckupDataList> sdList = mmDAO.getStuckupDataList((User) session.getAttribute("userData"), area, meterStatus);
		int mtrcount=sdList.size();
		String leddt = mmDAO.getLedDate((User) session.getAttribute("userData"), area, meterStatus);
		
		model.addAttribute("pageTitle", "Meter Allotment for "+mtrstatus_name+" Services");
		model.addAttribute("menuId", 3);
		model.addAttribute("area", area);
		model.addAttribute("areaname", areaname);
		model.addAttribute("meterStatus", meterStatus);
		model.addAttribute("mtrstatus_name", mtrstatus_name);
		model.addAttribute("lmList", lmList);
		model.addAttribute("mkList", mkList);
		model.addAttribute("sdList", sdList);
		model.addAttribute("maaList", maaList);
		model.addAttribute("date", date);
		model.addAttribute("leddt", leddt);
		model.addAttribute("mtrcount", mtrcount);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/meterallotmententryburntloadauto";
	}
	
	@GetMapping("/cancellation/{pagetype}/{menuId}")
	public String MeterAllotmentCancel(@PathVariable int menuId, @PathVariable String pagetype, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
		
		List <MeterStatusList> msList = mmDAO.meterStatusCancelList((User) session.getAttribute("userData"));
		List <MeterMakeList> mkList = mmDAO.mmMeterMakeTotalList((User) session.getAttribute("userData"));
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@mkList"+mkList);
		//List <MeterStatusList> meterStatusList = mmDAO.meterStatusList((User) session.getAttribute("userData"));
		
		model.addAttribute("pageTitle", "Meter Allotment Cancellation");
		model.addAttribute("menuId", menuId);
		model.addAttribute("mkList", mkList);
		model.addAttribute("msList", msList);
		model.addAttribute("userData", session.getAttribute("userData"));
		return "mm/allotmentcancellationentry";
	}

}
