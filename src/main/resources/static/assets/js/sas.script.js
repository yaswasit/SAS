$(document).ready(function(){
	$("#consumerDetails").hide();	
	//alert($("#catcd").val());	
	$('#password').bind("paste",function(e) {
    	e.preventDefault();
  	});
  	
  	$("#releasedNRForm").submit(function (event) {
        event.preventDefault();
		alert("Realeased NR Form");
    });
  	
	$.ui.autocomplete.prototype._renderItem = function (ul, item) {
    	var t = String(item.value).replace(
    		new RegExp(this.term, "gi"),
    		"<strong>$&</strong>");
    	return $("<li></li>").
    	data("item.autocomplete", item).
    		append("<div>" + t + "</div>").
    		appendTo(ul);
  	};
	
    $( "#username").autocomplete({
		delay: 300,
		source: "getusers",
      	minLength: 3,
		selectFirst: true,
		highlightFirst: true
    });
    
    
    /*$('.docslist').click(function(){
		alert("hai");
    	
    	$tr =$(this).closest('tr');
        var data= $tr.children("td").map(function(){
        	return $(this).text();
        }).get();
            
        alert(data[2]);
    });*/
    
    /*$("#docsList").on('click', '#btnShow', function () {
    
        alert('clicked');
    });*/
});

function getNRInfo(url){
	//alert(url);
	$.ajax({
    	type: 'POST',
       	url: url,
    	data: "regid="+$("#regId").val(),
        success: function(result)
        {
			$("#nrStatusBody").hide();
			$("#consumerDetails").show();
        	$("#registrationDetails").html(result);
        }
	});        	
}

function getDDDetails(regId){ 
	$.ajax({
    	type: 'POST',
       	url: "/SAS/nsts/getDDDetails",
    	data: "regid="+regId,
        success: function(result)
        {
        	//alert("result>>"+result);
			$("#dd_span").html(result);
        }
	}); 
}

function getDocsInfo(regId, url, pageTitle){
	//alert(pageTitle);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	$("#actionType").empty();
	$("#actionType").append("<option value='x' disabled selected>Choose one</option>");
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert(result);
			$("#newreglist").hide();
			$("#consumerDetails").show();
        	$("#registrationDetails").html(result);
        	//alert($("#catcd").val());
        	//alert($("#estreq").val());
        	var catcd = $("#catcd").val();
        	var estreq = $("#estreq").val();
        	
        	$("#reason").hide();
			$("#documents").hide();
			$("#remarks").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
			$("#udcoslid").hide();
			$("#udcoslYes").hide();
			$("#udcoslNo").hide();
			$("#StatusEst").hide();
			$("#ukscno1_div").hide();
			$("#ukscno2_div").hide();
			$("#ukscno3_div").hide();
			$("#ukscno4_div").hide();
			$("#aeaction_id").hide();
        	if(pageTitle=="Assign To Lineman"){
        		$.ajax({
            		type: 'POST',
               		url: "/SAS/nsts/getLinemenList",
               		data: "empid=null",
            		success: function(result) {
        				//alert(result);
        				$("#linemenlist").html(result);
        			}	
        		});	
        		$("#lineman").show();
        		$("#btnSubmit").show();
        		$("#action_id").hide();
        	}else if(pageTitle=="Field Verification"){
        		$("#aeaction_id").show();
        		$("#udcoslid").hide();
        		$("#lineman").hide();
        		$("#btnSubmit").show();
        		$("#action_id").hide();
        	}else{
        		$("#aeaction_id").hide();
        		$("#lineman").hide();
        		$("#btnSubmit").hide();
        	}
        	
			$("#actionType").append("<option value='8'>Verification Successful</option>");
			$("#actionType").append("<option value='0'>Missing Documents (Send link to consumer)</option>");
			$("#actionType").append("<option value='9'>Invalid Documents</option>");
			$("#actionType").append("<option value='6'>Reject (Application)</option>");
			/*if(!(catcd=="8"||catcd=="5"||estreq=="Y")){    	
	        	$("#actionType").append("<option value='4'>Forward for Meter Issue</option>");
	        	$("#actionType").append("<option value='1'>Estimation Required (Move to SAP)</option>");
	        	$("#actionType").append("<option value='0'>Missing Documents (Send link to consumer)</option>");
				$("#actionType").append("<option value='6'>Reject (Application)</option>");
				$("#actionType").append("<option value='3'>UDC Service Existing in the Premises</option>");
				$("#actionType").append("<option value='7'>Others (Site/Consumer Not Ready)</option>");
			} else if (catcd=="5") {
				$("#actionType").append("<option value='1'>Estimation Required (Move to SAP)</option>");
				$("#actionType").append("<option value='3'>UDC Service Existing in the Premises</option>");
				$("#actionType").append("<option value='7'>Others (Site/Consumer Not Ready)</option>");
			} else if (catcd=="8"||estreq=="Y") {
				$("#actionType").append("<option value='1'>Estimation Required (Move to SAP)</option>");
	        	$("#actionType").append("<option value='0'>Missing Documents (Send link to consumer)</option>");
				$("#actionType").append("<option value='6'>Reject (Application)</option>");
				$("#actionType").append("<option value='3'>UDC Service Existing in the Premises</option>");
				$("#actionType").append("<option value='7'>Others (Site/Consumer Not Ready)</option>");
			}	*/	
		}
   	})
}

function getUDCEntryDetails(udc){
	if(udc=="YES"){
		$("#udcoslYes").show();
		$("#remarks").show();
		$("#btnSubmit").show();
		$("#udcoslNo").hide();
	} else if(udc=="NO"){
		$("#udcoslNo").show();
		$("#udcoslYes").hide();
		$("#remarks").hide();
		$("#btnSubmit").show();
	} else{
		$("#udcoslYes").hide();
		$("#remarks").hide();
		$("#btnSubmit").hide();
		$("#udcoslNo").hide();
	}
}

function getServiceDetails(service){
	if(service=="Estimated Required"){
		$("#StatusEst").show();
	} else if(service=="Service Release"){
		$("#StatusEst").hide();
	} else{
		$("#StatusEst").hide();
	}
}

function getStatusDetails(status){
	if(status=="Pannel Board"){
		$("#ukscno1_div").show();
		$("#ukscno2_div").show();
		$("#ukscno3_div").show();
		$("#ukscno4_div").show();
	}
	else{
		$("#ukscno1_div").hide();
		$("#ukscno2_div").hide();
		$("#ukscno3_div").hide();
		$("#ukscno4_div").hide();
	}
}


function getAction(){
	
	if($("#actionType").val()=="0") {
		$("#reason").hide();
		$("#docType").empty();
		//$("#documents").empty();
		$("#docType").append("<option value='x' selected>Choose one</option>");
		$("#docType").append("<option value='1'>Documents Not Uploaded</option>");
		
		$("#documents").hide();
		$("#remarks").show();
		$("#btnSubmit").show();
		$("#lineman").hide();
		$("#scno").hide();
	} else if($("#actionType").val()=="3") {
		$("#reason").hide();
		$("#documents").hide();
		$("#remarks").show();
		$("#btnSubmit").show();
		$("#lineman").hide();
		$("#scno").show();
	} else if($("#actionType").val()=="4") {
		$("#lmphone").val("");
		$("#reason").hide();
		$("#documents").hide();
		$("#remarks").hide();
		$("#btnSubmit").show();
		$("#lineman").show();
		$("#scno").hide();
		
		$.ajax({
    		type: 'POST',
       		url: "/SAS/nsts/getLinemenList",
       		data: "empid=null",
    		success: function(result) {
				//alert(result);
				$("#linemenlist").html(result);
			}	
		});	
		
	} else if($("#actionType").val()=="6") {
		$("#reason").hide();
		$("#documents").hide();
		$("#remarks").show();
		$("#btnSubmit").show();
		$("#lineman").hide();
		$("#scno").hide();
	} else {
		$("#reason").hide();
		$("#documents").hide();
		$("#remarks").hide();
		$("#btnSubmit").show();
		$("#lineman").hide();
		$("#scno").hide();
	}
}

function getLinemanPhone() {
	//alert("hi");	
	//alert($("#linemenlist").val());
	$.ajax({
    	type: 'POST',
       	url: "/SAS/nsts/getLinemenList",
    	data: "empid="+$("#linemenlist").val(),
        success: function(result) {
			//alert(result)
			$("#lmphone").val(result);
		}
	});				
} 

function getReject(){
	if($("#docType").val()=="1") {
		$("#docslist").empty();
		//$("#docslist").append("<option value='x' selected>Choose one/Multiple</option>");
		$("#documents").show();
		//alert("hai");
		$.ajax({
    		type: 'POST',
       		url: "/SAS/nsts/getRejectedDocsList",
    		success: function(result) {
				//alert(result);
				$("#docslist").html(result);
			}	
		});	
	}	
	else
		$("#documents").hide();
}
function viewDocument(url){
	//alert("http://10.10.101.37:9999/SAS/downloads/"+url);
	window.open("http://10.10.101.7:8085/SAS/downloads/"+url)	
}
/*function getSAPSanctionLetter(regId) {
	
}*/

function getLTMInfo(url){
	//alert(url);
	if($("#regId").val().length==0){
		alert("Enter LTM Number..");
		$("#regId").focus();
		return false;
	}
	$.ajax({
    	type: 'POST',
       	url: url,
    	data: "regid="+$("#regId").val(),
        success: function(result)
        {
			$("#nrStatusBody").hide();
			$("#consumerDetails").show();
        	$("#registrationDetails").html(result);
        }
	});        	
}

function showList(){
	$("#newreglist").show();
		
	$("#consumerDetails").hide();
}

function sendLTMTestOTP(){
	if($("#actionType").val()==""){
		alert("Select Action Type..");
		$("#actionType").focus();
		return false;
	}
	if($("#linemenlist").val()=="x"){
		alert("Select Lineman..");
		$("#linemenlist").focus();
		return false;
	}
	if($("#lmphone").val().length==0){
		alert("Enter "+$("#linemenlist").val()+" Lineman Phone Number..");
		$("#lmphone").focus();
		return false;
	}
	 var regex = /^(0|91)?[6-9][0-9]{9}$/;
     if (regex.test($("#lmphone").val())) {
         $("#lblError").css("visibility", "hidden");
     } else {
    	 alert("Enter Valid Phone Number..");
    	 $("#lmphone").focus();
 		 return false;
     }
	//$("#btn-otp").disable();
	$.ajax({
		  method: "POST",
		  url: "/SAS/nsts/sendSMStoLTMLineman/otpc",
		  //url:"{{route('/sendSMStoLTMLineman/ltm')}}",
		  data: {
			 regid: $("#reg_Id").val(),
			 empid: $("#linemenlist").val(),
			 lmphone: $("#lmphone").val(),
			 meterno: $("#meterno").val()
		  },
		  traditional: true,
		  success: function(result) {
		    console.log(result);
		    if(result=="YES")
		    	$("#sms_form").html("<div class='alert alert-success alert-bdleft-success'><i class='fe fe-check-square me-2'></i>SMS Send Successfully</div>");
		    else
		    	$("#sms_form").html("<div class='alert alert-danger alert-bdleft-danger'><i class='fe fe-x-circle me-2'></i>Error while sending SMS..! Try Again..</div>");
		  }
		});
	/*$.ajax({
    	type: 'POST',
    	 url: "/sendSMStoLTMLineman/otpc",
       	data: "regid="+$("#reg_Id").val(),
       	//data: { regid: $("#regId").val(), phoneno: $("#lmphone").val() },
        success: function(result)
        {
        	alert("result::"+result);
			//$("#nrStatusBody").hide();
			//$("#consumerDetails").show();
        	//$("#registrationDetails").html(result);
        }
	});*/
	
}

function getAddConDetails(regId, url){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert(result);
			$("#newreglist").hide();
			$("#consumerDetails").show();
        	$("#registrationDetails").html(result);
        	//alert($("#catcd").val());
        	//alert($("#estreq").val());
        	var catcd = $("#catcd").val();
        	var estreq = $("#estreq").val();
        	
        	$("#reason").hide();
			$("#documents").hide();
			$("#remarks").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
        			
		}
   	})
}

/*function getPhase(id,load)
{
	var ld=load;
	alert("ld"+ld);
	if(ld.length!=0)
	{
		if(parseInt(ld)<5000)
		{
			document.getElementById("phaseopt"+id).innerHTML="<select name='Phase"+id+"'><option value='1'>1Ph</option>";
		}
		else
		{
			document.getElementById("phaseopt"+id).innerHTML="<select name='Phase"+id+"'><option value='3'>3 Ph</option>";
		}
	}else
	{
		document.getElementById("phaseopt"+id).innerHTML="<select name='Phase"+id+"'><option value=''>-select-</option>";
	}
}*/

function getLTMDetails(regId, url, doc_submit){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert("regId=="+regId);
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			$("#registrationDetails").html(result);
			
			if(doc_submit=="NO"){
				$("#doc_no").show();
				$("#sms_form").hide();
			}else{
				$("#sms_form").show();
				$("#doc_no").hide();
			}
        	        	
        	$("#action_div").hide();
			$("#phone_div").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
        }
   	})
}

function getActionDetails(){
	
	if($("#actionType").val()=="2") {
		$.ajax({
    		type: 'POST',
       		url: "/SAS/nsts/getLinemenList",
       		data: "empid=null",
    		success: function(result) {
				//alert(result);
				$("#linemenlist").html(result);
			}	
		});
		$("#action_div").show();
		$("#phone_div").show();
		$("#btnSubmit").show();
	}else {
		$("#action_div").hide();
		$("#phone_div").hide();
		$("#btnSubmit").hide();
	}
}

function getTestReport(regId, url, id){
	//alert(regId+"--"+url+"--"+id+mtrslno);
	if($("#mtrslno"+id).val()=="0"){
		alert("Select Meter No..");
		$("#mtrslno"+id).focus();
		return false;
	}
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId+"&meterno="+$("#mtrslno"+id).val(),
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert(result);
			$("#newreglist").hide();
			$("#consumerDetails").show();
			$("#cons_name").html(": <b>"+result.builder_name+"</b>");
        	$("#newreglist").hide();
        	$("#ltm_regno_dt").html(regId+" - "+result.registration_on);
        	$("#premises_dno").html(": "+result.adderss1);
        	$("#premises_syno").html(": "+result.adderss1);
        	$("#premises_street").html(": "+result.address2);
        	$("#premises_town").html(": "+result.area_name);
        	$("#paddress").html(": "+result.adderss3);
        	$("#pstreet").html(": "+result.address4);
        	$("#ptown").html(": "+result.area_name2);
        	
        	$("#meter_no").html(result.meter_slno);
        	$("#make").html(result.meter_make);
        	$("#mf").html(result.meter_mf);
        	$("#capacity").html(result.capacity);
        	$("#inl_reading").html(result.initial_reading);
        	
        	$("#action_div").hide();
			$("#phone_div").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
        }
   	})
}

function getLTMOTPDetails(regId, url, doc_submit){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert("regId=="+regId);
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			$("#registrationDetails").html(result);
			
			if(doc_submit=="NO"){
				$("#doc_no").show();
				$("#sms_form").hide();
			}else{
				$("#sms_form").show();
				$("#doc_no").hide();
			}
        	        	
        	$("#action_div").hide();
			$("#phone_div").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
        }
   	})
}

function checkFileimg(id){	
	$("#"+id).prop('class','form-control');
	var Receiptelement = document.getElementById(id);
	var Receiptname = Receiptelement.value;
	var allowedExtensions = /(\.jpg|\.jpeg)$/i;
    if (!allowedExtensions.exec(Receiptname)) {
    	Receiptelement.value = '';
        alert("Upload jpg or jpeg Format only",'error');	
		$("#").prop('class','form-control error');
		return false
    }  
}  

function checkFilepdf(id){
      var docpdf = document.getElementById(id);
     var Receiptfile = docpdf.files;           
     var ReceiptSize = Receiptfile[0].size;    
    const ReceiptKb = ReceiptSize / 1024;
	if(ReceiptKb > 100){
		//docpdf.value = '';
		toastmsg("Maximum File Size allowed 100KB only",'error');	
		$("#id_proof").prop('class','form-control error');
		return false
	} 		        
}

function uploadTestReport(){
	if($("#otp").val()==""){
		alert("Enter OTP..");
		$("#otp").focus();
		return false;
	}
	var meter_img = document.getElementById("meter_img");
	var Filename = meter_img.value;
	var allowedExtensions = /(\.jpg|\.jpeg)$/i;
    if (!allowedExtensions.exec(Filename)) {
    	meter_img.value = '';
        alert("Upload jpg or jpeg Format only",'error');	
		//$("#meter_img").prop('class','form-control error');
		return false
    }
	
	var test_report = document.getElementById("test_report");
    var Receiptfile = test_report.files;           
    var ReceiptSize = Receiptfile.size;    
    const ReceiptKb = ReceiptSize / 1024;
    if(test_report.value == ''){
    	alert("Upload .pdf file..",'error');	
		//$("#test_report").prop('class','form-control error');
		return false
    }
    if(ReceiptKb > 100){
		//test_report.value = '';
		alert("Maximum File Size allowed 100KB only",'error');	
		//$("#test_report").prop('class','form-control error');
		return false
	} 	
	
	$("#btnsubmit").attr('disabled', true);		
	document.f1.method="POST";
	document.f1.enctype="multipart/form-data";
	document.f1.action="uploadTestReport";
	document.f1.submit();
}

function getLTMtobeReleasedDetails(regId, url, exess_meters){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			//alert("regId=="+regId);
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			const myArray = result.split("@@@");
			//alert(">>>"+myArray[1]);
			
			$("#registrationDetails").html(myArray[0]);
			
			//alert($("#regdt_flag").val());
			
			if(exess_meters=="YES"){
				$("#exess_mtrs").show();
				$("#error_mtrs").hide();
				$("#release_form").hide();
			} else if($("#regdt_flag").val()!="Y"){
				$("#exess_mtrs").hide();
				$("#error_mtrs").show();
				$("#release_form").hide();
			}else{
				$("#release_form").show();
				$("#exess_mtrs").hide();
				$("#error_mtrs").hide();
				
				$("#linemenlist").html(myArray[1]);
				$("#areacode").html(myArray[2]);
				$("#feedercode").html(myArray[3]);
				$("#amt_tobe_div").html(myArray[4]);
				$("#amt_tobe_paid").val(myArray[4]);
				$("#amt_div").html(myArray[5]);
				$("#amt_paid").val(myArray[5]);
				$("#faltdel_div").html(myArray[6]);
				
			}
        	        	
        	$("#action_div").hide();
			$("#phone_div").hide();
			$("#btnSubmit").hide();
			$("#scno").hide();
        }
   	})
}

function getStructureCode(feeder_code) {
	$.ajax({
    	type: 'POST',
       	url: "/SAS/nsts/getStructureCode",
    	data: "feedercode="+feeder_code,
        success: function(result) {
			$("#structcode").html(result);
		}
	});				
} 

function checkStatus(val)
{
	var c=document.getElementById('meters_count').value;
	for(var i=1;i<=c;i++)
	{
		document.getElementById('flatno'+i).checked=val;
	}
	if(val)
		document.getElementById('chkInfo').innerHTML="Deselect All";
	else
		document.getElementById('chkInfo').innerHTML="Select All";
}

function goscno(q) 
{
	document.getElementById(q).value=document.getElementById(q).value.toUpperCase();		
	//alert("hai "+document.getElementById(q).value.length);
	//alert(q);
	if(document.getElementById(q).value.length==$("#xibox").value()) 
	{		
		//alert("Enter valid Service Number");
		document.getElementById(q).focus();
		return false;
	}
}

function checkIBox(q)
{ alert("@@@");
	var a=document.getElementById(q).value;
	if(document.getElementById(q).value.length==0)
	{
		alert("Service Number can't be Left Blank");
		document.getElementById(q).focus();
		return false;
	}
	else
	{
		goscno(q);
	}
}

function getAGLtobeReleasedDetails(regId, url){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			const myArray = result.split("@@@");
			
			$("#registrationDetails").html(myArray[0]);
			$("#linemenlist").html(myArray[1]);
			$("#feedercode").html(myArray[2]);
			$("#ascno").val(myArray[3]);
			$("#xibox").val(myArray[4]);
			$("#xjbox").val(myArray[5]);
			
			$("#release_form").hide();
			$("#rejected_form").hide();
			$("#btn_div").hide();
		}
   	})
}

function getTable(status){
	if(status=="Release"){
		$("#release_form").show();
		if($("#xibox").val()!="0"){
			$("#xibox_divt").show();
			$("#scno1").attr('maxlength',$("#xibox").val());
			$("#scno1").attr('size',$("#xibox").val());
			$("#scno2").attr('maxlength',$("#xjbox").val());
			$("#scno2").attr('size',$("#xjbox").val());
			$("#xibox_divf").hide();
		}else{
			$("#xibox_divt").hide();
			$("#xibox_divf").show();
		}
		$("#rejected_form").hide();
		$("#btn_div").show();
	} else if(status=="Rejected"){
		$("#release_form").hide();
		$("#rejected_form").show();
		$("#btn_div").show();
	} else{
		$("#release_form").hide();
		$("#rejected_form").hide();
		$("#btn_div").hide();
	}
}

function joinScno()
{
	var maxLength=$("#xjbox").val();
	var newStr="";
	var nSpaces=$("#xibox").val();
	var newSpaces="";
	
	//alert(maxLength);
	if($("#scno1").val().length==0)
	{
		alert("Service Number can't be Left Blank");
		$("#scno1").focus();
		return false;
	}
	if($("#scno2").val().length==0)
	{
		alert("Service Number can't be Left Blank");
		$("#scno2").focus();
		return false;
	}
	else
	{
		var txt1 = $("#scno1").val();
		var txt2 = $("#scno2").val();

		 if(parseInt(txt1,10) == 0)
			{
				alert('all chars should not be zero');
				$("#scno1").focus();
				return false;
			}
		 if(parseInt(txt2,10) == 0)
			{
				alert('all chars should not be zero');
				$("#scno2").focus();
				return false;
			}

		if(nSpaces==2)
		{
			if($("#scno1").val().length<nSpaces) 
			{
				for(var i=1;i<=nSpaces-$("#scno1").val().length;i++)
					newSpaces=newSpaces+" ";
				$("#scno1").val()=$("#scno1").val()+newSpaces;
			}
		}
		else
		{
			if($("#scno1").val().length< nSpaces ) 
			{
				for(var i=1;i<=nSpaces - $("#scno1").val().length;i++)
					newSpaces=newSpaces+"0";
				$("#scno1").val()=newSpaces+$("#scno1").val();	
			}
		}
		if(nSpaces!=2)
		{
			
			if($("#xibox").val()==4 && $("#xjbox").val()==6)
			{
				$("#scno1").val()=$("#scno1").val()+"";
			}
			else if($("#xibox").val()==5 && $("#xjbox").val()==5)
			{
				$("#scno1").val()=$("#scno1").val()+"";
			}
			else
			{
				$("#scno1").val()=$("#scno1").val()+" ";
			}
		}

		if($("#scno2").val().length< maxLength ) 
		{
			for(var i=1;i<=maxLength - $("#scno2").val().length;i++)
				newStr=newStr+"0";
			$("#scno2").val()=newStr+$("#scno2").val();	
		}
		
		$("#scno_span").html()="<input type='text' name='txtscno' value='"+$("#scno1").val()+$("#scno2").val()+"' class='form-control' size=13 readonly>";
		return true;
   }
}

function aglcheckIBox()
{
	if($("#scno1").val().length==0)
	{
		alert("Service Number can't be Left Blank");
		$("#scno1").focus();
		return false;
	}
	else
	{
		$("#scno1").focus();
		return true;
	}
}

function goaglscno() {
	$("#scno1").val()=$("#scno1").val().toUpperCase();
		
	if($("#scno1").val().length==$("#xibox").val()) 
	{
		$("#scno2").focus();
	}
}

function agl_release_validate()
{
//alert("hello");
   if($("#scno1")!=null)
	{
		return joinScno();
	}
	if($("#pge")=="rapdrp")
	{
			//alert("other");
			if(!(($("#depttype").val()=="G") && ($("#catg").val()=="6" || $("#catg").val()=="5")))
			{

				if($("#structcode").val()=="x")
				{
					alert("Select Structure Code");
					$("#structcode").focus();
					return false;
				}
				else if($("#mrbpole").val().length==0)
				{
					alert("Enter Pole No.");
					$("#mrbpole").focus();
					return false;
				}
			}
		}
	
		if($("#feedercode").val()=='x')
		{
			alert("Select Feeder");
			$("#feedercode").focus();
		}
		else if($("#structcode").val()=='x')
		{
			alert("Select Structure");
			$("#structcode").focus();
		}
		else if($("#cycle").val().length==0)
		{
			alert("Select Cycle");
			$("#cycle").focus();
		}
		
		else if($("#mtrytype").val().length==0)
		{
			alert("Select Meter Type");
			$("#mtrytype").focus();
		}
		else if($("#TCs1").val()<=0)
		{
			alert("Enter TC Seal1");
			$("#TCs1").focus();
		}
		else if($("#TCs2").val().length==0)
		{
			alert("Enter TC Ceal2");
			$("#TCs2").focus();
		}
		else
		{
			joinScno();
			$("#btnsubmit").attr('disabled', true);
			document.f1.method="POST";
			document.f1.action="aglTobeReleased";
			document.f1.submit();
		}
	}


function getNscTobeReleasedInfo(regId, url){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			const myArray = result.split("@@@");
			
			$("#registrationDetails").html(myArray[0]);
			$("#linemenlist").html(myArray[1]);
			$("#feedercode").html(myArray[2]);
			
			$("#release_form").hide();
			$("#rejected_form").hide();
			$("#btn_div").hide();
		}
   	})
}

function getNsTestReportUploadInfo(regId, url){
	//alert(regId);
	//var link = /*[[@{/nsts/getRegistrationDetails}]]*/;
	//alert(url);
	
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			$("#newreglist").hide();
			$("#consumerDetails").show();
			
			const myArray = result.split("@@@");
			
			$("#registrationDetails").html(myArray[0]);
			
			if($("#catid").val()=="3" || $("#catid").val()=="4"){$("#tr_testreport").hide();}
			else{$("#tr_agreement").hide();}
			//$("#btn_div").hide();
		}
   	})
}

function getNRTabInfo(url,page_title){
	//alert("@@@@");
	//url="/SAS/nsts/documentsverification/vd/1";
	document.f2.method="GET";
	document.f2.action=url;
	document.f2.submit();
}

function testReport_Validate(){
	var arrfno=[];
	var catid=$("#catid").val();
	
	if(document.f1.report1.value==""){
		alert("Test Report Should be uploaded");
		document.getElementById("report1").focus();
		return false;
	}
	if(document.f1.report1.value.length!=0)
	{
		var size = parseFloat(report1.files[0].size / (1024 * 1024)).toFixed(2); 
		var str=document.f1.report1.value;
		var arr= new Array();
		arrfno.push(str);
			arr=str.split(".");
			var f5=arr.reverse();	
			if((f5[0]!="pdf") && (f5[0]!="PDF")){
			alert("Please Upload Test Report in .PDF Files Only....");
			return false;
			}

		 if(size > 0.5) {
            alert('Please select test Report size less than 500kb');
			return false;
        }
	}
	if(catid=="3"||catid=="4"){
		if(document.getElementById("agrmnt").value==""){
			alert("Agreement Should be uploaded");
			document.getElementById("agrmnt").focus();
			return false;
		}
		if(document.f1.agrmnt.value.length!=0)
		{
			var size = parseFloat(agrmnt.files[0].size / (1024 * 1024)).toFixed(2); 
			var str=document.f1.agrmnt.value;
			
			var arr= new Array();
			arrfno.push(str);
				arr=str.split(".");
				var f5=arr.reverse();
			
				if((f5[0]!="pdf") && (f5[0]!="PDF")){
				alert("Please Upload Agreement in .PDF Files Only....");
				return false;
				}

			 if(size > 0.5) {
	            alert('Please select Agreement size less than 500kb');
				return false;
	        }
		}
	}
	var regid=document.getElementById('regid').value;
	var scheme=document.getElementById("scheme").value;
	
		$("#btnsubmit").attr('disabled', true);		
		document.f1.method="POST";
		document.f1.enctype="multipart/form-data";
		document.f1.action="uploadNRTestReport";
		document.f1.submit();
		//document.f1.action="TestReport_Upload.jsp?regid="+regid+"&catid="+catid+"&scheme="+scheme+"";
		return true;
}