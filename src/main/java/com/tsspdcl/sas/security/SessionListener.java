package com.tsspdcl.sas.security;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	private int session_exp_seconds=3600;
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
	    System.out.println("session created");
	    event.getSession().setMaxInactiveInterval(session_exp_seconds);
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	    System.out.println("session destroyed");
	}
}