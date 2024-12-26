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

function showList(){
	$("#newreglist").show();
		
	$("#consumerDetails").hide();
}

function getCTName(option){
	if(option=="1")
		$("#field_title_span").html("Registration Number");
	else if(option=="2")
		$("#field_title_span").html("Service Number");
	else{
		$("#field_title_span").html("Registration Number");
		alert("Select Complaint Number Type");
		$("#type").focus();
		return false;
	}
}

function getCCInfo(url){
	alert(url);
	if($("#regId").val().length==0){
		alert("Enter Registration / Service Number");
		$("#regId").focus();
		return false;
	}
	alert("ajax");
	$.ajax({
    	type: 'POST',
       	url: url,
    	data: "regid="+$("#regId").val(),
        success: function(result)
        {alert("result>>"+result);
			$("#nrStatusBody").hide();
			$("#consumerDetails").show();
        	$("#registrationDetails").html(result);
        }
	});        	
}

function getDocsInfo(regId, url){
	//alert(regId);
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
        	
			if(!(catcd=="8"||catcd=="5"||estreq=="Y")){    	
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
			}		
		}
   	})
}

function getPowerSupplyDetails(regId, url, title, catid){
	//alert("@@@");
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			$("#registrationDetails").html(result);
			        	        	
        	$("#action_div").hide();
			$("#phone_div").hide();
			$("#btnSubmit").hide();
			$("#lineman").hide();
			$("#scno").hide();
        }
   	})
}

function uploadPowerSupply(){
	var test_report = document.getElementById("qps_doc");
    var Receiptfile = test_report.files;           
    var ReceiptSize = Receiptfile.size;    
    const ReceiptKb = ReceiptSize / 1024;
    if(test_report.value == ''){
    	alert("Upload .pdf file..",'error');
		return false
    }
    if(ReceiptKb > 100){
		alert("Maximum File Size allowed 100KB only",'error');	
		return false
	} 
    
    if($("#remarks").val()==""){
		alert("Enter Remarks..");
		$("#remarks").focus();
		return false;
	}
	
	$("#btnsubmit").attr('disabled', true);		
	document.f1.method="POST";
	document.f1.enctype="multipart/form-data";
	document.f1.action="uploadQPS";
	document.f1.submit();
}

function getCatChangeDetails(regId, url, title, catid){
	//alert("@@@"+regId+"--"+url+"--"+title+"--"+catid);
	$.ajax({
    	type: 'POST',
       	url: url+"?regid="+regId,
    	//data: "regid="+regId,
        success: function(result)
        {
			$("#reg_Id").val(regId);
        	$("#newreglist").hide();
			$("#consumerDetails").show();
			
			$("#registrationDetails").html(result);
			        	        	
        	if($("#docflag").val()=="N"){
        		$.ajax({
            		type: 'POST',
               		url: "/SAS/ccts/getDocumentsList",
               		data: "empid=null",
            		success: function(result) {
        				//alert(result);
        				$("#doc_reason").html(result);
        			}	
        		});	
        		$("#doc_div").show();
        		$("#forward_span2").show();
        		$("#status_div").hide();
        	} else{
        		$("#doc_div").hide();
        		$("#forward_span2").hide();
        		$("#status_div").show();
        	}
			$("#forward_span1").hide();
			//$("#forward_span2").hide();
        }
   	})
}

function getTable(obj,catid){
	//alert($("#amtbepd").val());
	
	if(obj=="ERO"){
		if(parseInt($("#amtbepd").val())>parseInt($("#amtpd").val()))
		{
			alert("Paid amount should not be less than to be paid amount");
			$("#status").val()="x";
			return false;
		}
		$("#forward_span1").show();
		$("#forward_span2").show();
		$("#btnsubmit").show();
	} else if(obj=="rejected"){
		$("#forward_span1").hide();
		$("#forward_span2").show();
		$("#remarks_flag").html("Reason for Rejected");
		$("#btnsubmit").show();
	} else{
		$("#forward_span1").hide();
		$("#forward_span2").hide();
		$("#btnsubmit").hide();
	}
}

/*function uploadcatchange(){
	if($("#status").val=="ERO"){
		var test_report = document.getElementById("test_report");
	    var Receiptfile = test_report.files;           
	    var ReceiptSize = Receiptfile.size;    
	    const ReceiptKb = ReceiptSize / 1024;
	    if(test_report.value == ''){
	    	alert("Upload .pdf file..",'error');
			return false;
	    }
	    if(ReceiptKb > 100){
			alert("Maximum File Size allowed 100KB only",'error');	
			return false;
		} 
	    
	    if($("#remarks").val().length==0){
			alert("Enter Remarks..");
			$("#remarks").focus();
			return false;
		}
	} else{
		if($("#remarks").val().length==0){
			alert("Enter Reason for Reject..");
			$("#remarks").focus();
			return false;
		}
	}
	alert("*****");
	$("#btnsubmit").attr('disabled', true);		
	document.f1.method="POST";
	document.f1.enctype="multipart/form-data";
	document.f1.action="uploadCatChange";
	document.f1.submit();
}*/

function uploadcatchange(){
    if($("#status").val() === "ERO"){
        var test_report = document.getElementById("test_report");
        var Receiptfile = test_report.files[0]; // Access the first file if uploaded
        
        if(test_report.value == ''){
            alert("Please upload a .pdf file.");
            return false;
        }
        
        var ReceiptSize = Receiptfile.size;    
        const ReceiptKb = ReceiptSize / 1024;
        
        if(ReceiptKb > 100){
            alert("Maximum File Size allowed is 100KB only.");
            return false;
        }
        
        if($("#remarks").val().trim().length == 0){
            alert("Please enter remarks.");
            $("#remarks").focus();
            return false;
        }
    } else {
        if($("#remarks").val().trim().length == 0){
            alert("Please enter the reason for rejection.");
            $("#remarks").focus();
            return false;
        }
    }
    
    if($("#docflag").val()=="N"){
    	if($("#doc_reason").val()==""){
    		alert("Please Select Document Type");
    		$("#doc_reason").focus();
            return false;
    	}
    }
    
    //alert("*****"+$("#doc_reason").val());

    // Disable the submit button to prevent multiple submissions
    $("#btnsubmit").attr('disabled', true);

    // Set form action to append the relative path to the base URL
    const baseUrl = "http://localhost:8085/SAS/ccts/";
    //document.f1.action = baseUrl + "uploadCatChange"; // Full URL for form submission

    document.f1.method = "POST";
    document.f1.enctype = "multipart/form-data";
    //document.f1.submit();
}

function meterBurntComplaint(){
	document.f1.method = "POST";
	document.f1.action="/SAS/ccts/submitComplaints?rows="+$("#rows").val();
	document.f1.submit();
}

function downloadNotice(){
	if($("#dispatchno").val().trim().length == 0){
        alert("Please enter Dispatch Number.");
        $("#dispatchno").focus();
        return false;
    }
	if($("#Time_test").val().trim().length == 0){
        alert("Please enter Date and time.");
        $("#Time_test").focus();
        return false;
    }
	document.f1.method = "POST";
	//document.f1.action="/SAS/ccts/submitComplaints?rows="+$("#rows").val();
	document.f1.submit();
}



