package com.tsspdcl.sas.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SmsMsg {
	public static String sendMsg(String number, String msg)
	  {
	    String inputLine = "";
	    try
	    {
	      String url = "http://sms6.rmlconnect.net:8080/bulksms/bulksms?username=TSSPDCTRAN6&password=CscSAsa6&type=0&dlr=1&destination=" + number + "&source=TGSPDC&message=" + URLEncoder.encode(new StringBuilder(String.valueOf(msg)).append("-TGSPDCL").toString(), "UTF-8");
	      URL hp = new URL(url);

	      URLConnection hpCon = hp.openConnection();
	      hpCon.setReadTimeout(300000);

	      BufferedReader in = new BufferedReader(new InputStreamReader(hpCon.getInputStream()));

	      inputLine = in.readLine();

	      in.close();
	      try
	      {
	    	  SMSlog.InsertSMSLog(number, msg, "TSSP-spdcsc", inputLine);
	      }
	      catch (Exception ex)
	      {
	        System.out.println(ex.getMessage());
	      }
	    }
	    catch (Exception e1)
	    {
	      e1.printStackTrace();
	      System.out.println(e1.getMessage());
	    }
	    return inputLine;
	  }

	  public static String sendMsg(String number, String msg, String user, String pwd)
	    throws Exception
	  {
	    String url = "http://sms6.rmlconnect.net:8080/bulksms/bulksms?username=" + user + "&password=" + pwd + "&type=0&dlr=1&destination=" + number + "&source=TSSPDC&message=" + URLEncoder.encode(new StringBuilder(String.valueOf(msg)).append("-TSSPDCL").toString(), "UTF-8");
	    URL hp = new URL(url);
	    URLConnection hpCon = hp.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(hpCon.getInputStream()));
	    String inputLine = in.readLine();
	    in.close();
	    try
	    {
	    	SMSlog.InsertSMSLog(number, msg, user, inputLine);
	    }
	    catch (Exception ex)
	    {
	      System.out.println(ex.getMessage());
	    }
	    return inputLine;
	  }
}
