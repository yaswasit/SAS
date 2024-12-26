package com.tsspdcl.sas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;  
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.tsspdcl.sas.common.CommonUtils;
import com.tsspdcl.sas.dao.nsts.CctsDAO;
import com.tsspdcl.sas.dao.nsts.NstsDAO;
import com.tsspdcl.sas.dto.UserDto;
import com.tsspdcl.sas.entity.User;
import com.tsspdcl.sas.service.UserService;


@Controller
@SessionAttributes("userData")
public class AuthController {
	
	
	@ModelAttribute("userData") 
    public User createUserData() { 
        return new User(); 
    } 

	@Autowired   
	private MessageSource messageSource;  
	
	NstsDAO nstsDAO = new NstsDAO();
	CctsDAO cctsDAO = new CctsDAO();
	
	private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
	
    @GetMapping("/")
    public String index(){
    	return "signin";
    }
    
    @GetMapping("/login")
    /*public String home(){
    	return "signin";
    }*/
    public String home(@RequestParam(value = "invalid", defaultValue = "false") boolean invalidSession,
    		@RequestParam(value = "error", defaultValue = "false") boolean errorMsg,
    		final Model model){
    	System.out.println("Error..."+errorMsg);
    	System.out.println("invalidSession..."+invalidSession);
    	
    	if(errorMsg) {
    		model.addAttribute("error", "Invalid Username or Password");
    	}
    	if(invalidSession) {
    		model.addAttribute("invalidSession", "You already have an active Session. We do not allow multiple active Sessions");
    	}
    	return "signin";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByUser(user.getUsername());
        if (existing != null) {
            result.rejectValue("username", null, "There is already an account registered with that Username");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request, HttpSession session){
        //List<UserDto> users = userService.findAllUsers();
    	//String redirectURL = request.getContextPath();
    	//System.out.println("Path..."+redirectURL);
    	Collection<? extends GrantedAuthority> authorities;
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
    	authorities = auth.getAuthorities();
        String myRole = authorities.toArray()[0].toString();
        //System.out.println("My Role..."+myRole);
        //System.out.println("Username: " + auth.getName());
        model.addAttribute("pageTitle","Dashboard");
    	//System.out.println("User Session Dashboard..."+session.getAttribute("userData"));
        model.addAttribute("userData", session.getAttribute("userData"));
    	
        /*if (myRole.equals("ADMIN") || myRole.equals("AE") || myRole.equals("AAE") || myRole.equals("ADE")) {
        	return "sasportal";
        } */
        return "dashboard";
    }
    
    @GetMapping("/userdashboard")
    public String userdashboard(@RequestParam("menuId") int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
    	CommonUtils methodUtils = new CommonUtils();
        //System.out.println("report id..."+menuId);
    	model.addAttribute("menuId", menuId);
    	//System.out.println("dashboard title..."+methodUtils.dashboardTitle(menuId));
    	//System.out.println("*********"+messageSource.getMessage(methodUtils.dashboardTitle(menuId), null, Locale.ENGLISH));
    	HashMap<String,String> reginfo = nstsDAO.getDashboardInfo(menuId, (User)session.getAttribute("userData"));
    	String barchartinfo = nstsDAO.getDashboardBarChartInfo(menuId, (User)session.getAttribute("userData"));
    	String[] myArray = barchartinfo.split("@@");
    	
    	model.addAttribute("pageTitle", messageSource.getMessage(methodUtils.dashboardTitle(menuId), null, Locale.ENGLISH));
        model.addAttribute("userData", session.getAttribute("userData"));
        model.addAttribute("reginfo", reginfo);
        model.addAttribute("labels", myArray[0]);
        model.addAttribute("nr_data", myArray[1]);
        model.addAttribute("ltm_data", myArray[2]);
        
    	return "dashboard";
    }
    
    @GetMapping("/ccuserdashboard")
    public String ccuserdashboard(@RequestParam("menuId") int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
    	CommonUtils methodUtils = new CommonUtils();
        //System.out.println("report id..."+menuId);
    	model.addAttribute("menuId", menuId);
    	HashMap<String,String> ccinfo = cctsDAO.pendingComplaintsList(menuId, (User)session.getAttribute("userData"));
    	HashMap<String,String> reginfo = nstsDAO.getDashboardInfo(menuId, (User)session.getAttribute("userData"));
    	String barchartinfo = nstsDAO.getDashboardBarChartInfo(menuId, (User)session.getAttribute("userData"));
    	String[] myArray = barchartinfo.split("@@");
    	
    	model.addAttribute("pageTitle", messageSource.getMessage(methodUtils.dashboardTitle(menuId), null, Locale.ENGLISH));
        model.addAttribute("userData", session.getAttribute("userData"));
        model.addAttribute("reginfo", reginfo);
        model.addAttribute("ccinfo", ccinfo);
        model.addAttribute("labels", myArray[0]);
        model.addAttribute("nr_data", myArray[1]);
        model.addAttribute("ltm_data", myArray[2]);
        
    	return "ccdashboard";
    }
    
    @GetMapping("/mmdashboard")
    public String mmudashboard(@RequestParam("menuId") int menuId, Model model, HttpServletRequest request, HttpSession session) throws SQLException, Exception{
    	CommonUtils methodUtils = new CommonUtils();
        //System.out.println("report id..."+menuId);
    	model.addAttribute("menuId", menuId);
    	HashMap<String,String> ccinfo = cctsDAO.pendingComplaintsList(menuId, (User)session.getAttribute("userData"));
    	HashMap<String,String> reginfo = nstsDAO.getDashboardInfo(menuId, (User)session.getAttribute("userData"));
    	String barchartinfo = nstsDAO.getDashboardBarChartInfo(menuId, (User)session.getAttribute("userData"));
    	String[] myArray = barchartinfo.split("@@");
    	
    	model.addAttribute("pageTitle", messageSource.getMessage(methodUtils.dashboardTitle(menuId), null, Locale.ENGLISH));
        model.addAttribute("userData", session.getAttribute("userData"));
        model.addAttribute("reginfo", reginfo);
        model.addAttribute("ccinfo", ccinfo);
        model.addAttribute("labels", myArray[0]);
        model.addAttribute("nr_data", myArray[1]);
        model.addAttribute("ltm_data", myArray[2]);
        
    	return "mmdashboard";
    }
    
    @GetMapping("/sasportal")
    public String sasportal(Model model, HttpServletRequest request, HttpSession session){
        //List<UserDto> users = userService.findAllUsers();
    	System.out.println("User Session SASPortal..."+session.getAttribute("userData"));
        model.addAttribute("userData", session.getAttribute("userData"));
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
    	//authorities = auth.getAuthorities();
        //String myRole = authorities.toArray()[0].toString();
        //System.out.println("My Role..."+myRole);
        System.out.println("Username: " + auth.getName()); 
        return "sasportal";
    }

    @GetMapping("/signout")
    public String signout(HttpSession session){
    	System.out.println("Successfully Logged Out");
    	session.invalidate();
    	//session.getAttribute("userData");
    	return "signout";
    }
    
    @GetMapping("/sessionout")
    public String sessionout(@RequestParam(value = "expired", defaultValue = "false") boolean expireSession,
    		final Model model){
    	//System.out.println("invalidSession..."+invalidSession);
    	System.out.println("expireSession..."+expireSession);
    	/*if(invalidSession) {
    		model.addAttribute("invalidSession", "You already have an active Session. We do not allow multiple active Sessions");
    	}*/
    	if(expireSession) {
    		model.addAttribute("expireSession", "Your Session time has Expired. Please Re-login");
    	}
    	return "sessionout";
    }
    
    @RequestMapping("/getusers")
    @ResponseBody
	public List<String> getUserContaining(@RequestParam(value = "term", required = false, defaultValue = "") String term) {
    	List<String> allUsers = new ArrayList<String>();
    	
    	try {
    		List<User> users = userService.findBySasusernameContainingIgnoreCase(term);
    		//System.out.println("Filter Users..."+users);
    		for(User user: users) {
    			allUsers.add(user.getSasusername().toString());
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return new ArrayList<String>();
    	}
    	//System.out.println(allUsers);
    	return allUsers;
	}
    
    /*@GetMapping("/logout")  
    public String logout(HttpServletRequest request, HttpServletResponse response) {  
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();  
        if (auth != null){      
           new SecurityContextLogoutHandler().logout(request, response, auth);  
        }  
        return "redirect:/signout";  
    } */
    
    /*@GetMapping("/getusers/{str}")
	public List<User> getUserContaining(@PathVariable(name = "str", required = false) String uname) {
    	return userService.findBySasusernameContainingIgnoreCase(uname);
		//model.addAttribute("usersList", users);
	}*/
    
    /*
    @RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	        System.out.println("status code..."+statusCode);
	    
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {
	            return "public/error/404";
	        }
	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return "public/error/500";
	        }
	    }
	    return "error";
	}*/
}
