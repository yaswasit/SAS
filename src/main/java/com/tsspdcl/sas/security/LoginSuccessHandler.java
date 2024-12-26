package com.tsspdcl.sas.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
 
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	
    	Collection<? extends GrantedAuthority> authorities;
    	
    	//Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
    	authorities = authentication.getAuthorities();
        String myRole = authorities.toArray()[0].toString();
        System.out.println("My Role..."+myRole);
         
        String redirectURL = request.getContextPath();
        
        if (myRole.equals("ADMIN") || myRole.equals("AE") || myRole.equals("AAE") || myRole.equals("ADE")) 
        	redirectURL = "sasportal";
        else
        	redirectURL = "dashboard";
        
        response.sendRedirect(redirectURL);
    }
}
