package com.tsspdcl.sas.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageConfig {
	
	@Autowired
	private MessageSource messageSource;
	
	public String getMessage(String key) {
		//System.out.println("Key...."+key);
		//Locale locale = LocaleContextHolder.getLocale();
		//System.out.println(messageSource.getMessage("dashboard.nsts.title", null, Locale.ENGLISH));
		return messageSource.getMessage(key, null, Locale.ENGLISH);
	}
	
	public String getMessage(String key, String [] strArray) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(key, strArray, locale);
	}

}
