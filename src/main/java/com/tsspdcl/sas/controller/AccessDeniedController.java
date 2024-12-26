package com.tsspdcl.sas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

	 @GetMapping("/403")
	 public String page403() {
		 return "public/error/403";
	 }
}
