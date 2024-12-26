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

function call4BurntEntry(url){
	if($("#area").val() == ''){
    	alert("Select Area..",'error');
    	$("#area").focus();
		return false;
    }
	$("#areaname").val($("#area option:selected").text());
	//$("#areaname").val()= $("#area option:selected").text();//document.f1.area.options[document.f1.area.selectedIndex].text;
	$("#btn-search").attr('disabled', true);	
	document.f1.method="GET";
	//document.f1.action=url+"?area="+$("#area").val();
	document.f1.action=url;
	document.f1.submit();
}

function saveAll(){
	var count=$("#rows").val();
	var noOfRows=0;
	var mflag;
	
	for(var c=1;c<=count;c++)
	{	
		if($("#chk" + c).prop("checked")==true)
		{
			if($("#approv"+c).val()=="")
			{
				alert("Please select Meter Burnt in the field");
				$("#approv"+c).focus();
				return false;
			}
			else if($("#approv"+c).val()=="N")
			{
				alert("Please Change the status in ERO");
				$("#approv"+c).focus();
				return false;
			}else if($("#CTMobile"+c).val()=="")
			{
				if($("#CTMobile"+c).val()=="")
				{
					alert("Please enter Mobile No.");
					$("#CTMobile"+c).focus();
					return false;
				}
			}
			document.getElementById("chk"+c).value="1";
			//$("#chk"+c).val()="1";
			noOfRows=1;
		}
	}
	if(noOfRows==0)
	{
		alert("Select atleast One Service");
		return false;
	}
	document.f1.method="post";
	document.f1.action="/SAS/mm/InsertBurntMeterAllotment";
	document.f1.submit();
}

function setMessage()
{ //alert("Hii");
	document.getElementById('BurntOutRow').style.display="none";
	document.getElementById('AddLoadRow').style.display="none";
	if ($("#meterStatus").val() == "11"){
		document.getElementById('BurntOutRow').style.display="";
		document.getElementById('AddLoadRow').style.display="none";
		document.getElementById('StuckUpRow').style.display="none";
	}else if ($("#meterStatus").val() == "02"){
		document.getElementById('BurntOutRow').style.display="none";
		document.getElementById('AddLoadRow').style.display="none";
		document.getElementById('StuckUpRow').style.display="";
	}else if ($("#meterStatus").val() == "18") {
		document.getElementById('BurntOutRow').style.display="none";
		document.getElementById('AddLoadRow').style.display="";
		document.getElementById('StuckUpRow').style.display="none";
	}
}

function call4AllotmentEntry(url){
	
	if($("#meterStatus").val() == ''){
    	alert("Select Meter Status..",'error');
    	$("#meterStatus").focus();
		return false;
    }
	$("#areaname").val($("#area option:selected").text());
	$("#mtrstatus_name").val($("#meterStatus option:selected").text());
		
	//$("#btn-search").attr('disabled', true);
	document.f1.method="GET";
	if($("#meterStatus").val()=="00")
	{
		//document.f1.action="MeterAllotmentEntryForNewServices.jsp";
	}
	else if($("#meterStatus").val()=="11") 
	{
		if($("#burntOutStatus").val()=="CC")
		{ alert("111");
			//document.f1.action="MeterAllotmentEntry_Burntload_Auto.jsp";
			document.f1.action="/SAS/mm/meterAllotmentEntryBurntloadAuto";
		}
		else if($("#burntOutStatus").val()=="BX")
		{
			//document.f1.action="MeterAllotmentNEntry_Auto.jsp";
		document.f1.action="/SAS/mm/MeterAllotmentEntryAuto";
		}
		else
		{
			//document.f1.action="MeterAllotmentEntry_Auto.jsp";
			document.f1.action="/SAS/mm/MeterAllotmentEntryAuto";
		}
	}
	else if($("#meterStatus").val()=="02") 
	{	
		if($("#stuckUpStatus").val()=="CC")
		{ //alert("----");
			document.f1.action="/SAS/mm/MeterAllotmentEntryStuckupAuto";
		}
		
		else
		{   //alert("222");
			//document.f1.action="MeterAllotmentEntry_Auto";
			document.f1.action="/SAS/mm/MeterAllotmentEntryAuto";
		}
	}
	else if($("#meterStatus").val()=="18")
	{
		if($("#AddLoadStatus").val()=="1"){alert("222");
			document.f1.action="MeterAllotmentEntry_Addload.jsp";
		}else
			//document.f1.action="MeterAllotmentNEntry.jsp";
			document.f1.action="/SAS/mm/MeterAllotmentNEntry";
	}
	else if($("#meterStatus").val()=="26")
	{alert("333");
		document.f1.action="MeterAllotmentEntry_NetMeter1.jsp";
		
	}
	else if($("#meterStatus").val()=="03" || $("#meterStatus").val()=="99" || $("#meterStatus").val()=="15" || $("#meterStatus").val()=="16" || $("#meterStatus").val()=="17" ||  $("#meterStatus").val()=="19" || $("#meterStatus").val()=="20"|| $("#meterStatus").val()=="21" || $("#meterStatus").val()=="23"|| $("#meterStatus").val()=="24" || $("#meterStatus").val()=="25"  || $("#meterStatus").val()=="27")
	{
		alert("Please enter IR Meter Changes Done in Old month in Old High quality/Non IR Metes Replaced Entry.");
		document.f1.action="/SAS/mm/MeterAllotmentNEntry";
	}
	else if($("meterStatus").val()=="18")
	{alert("444");
		document.f1.action="MeterAllotmentEntry_Addload.jsp";
	}
	else
	{
		//document.f1.action="MeterAllotmentEntry_Auto.jsp";
		document.f1.action="/SAS/mm/MeterAllotmentEntryAuto";
	}
	document.f1.submit();
		
}

function upperSCNO(obj) { //alert(obj);
	var x = obj.name.substring(4);
	document.getElementById("scno"+(x)).value = document.getElementById("scno"+(x)).value.toUpperCase();
}
function upperCompno(obj) {
	var x = obj.name.substring(4);
	document.getElementById("compno"+(x)).value = document.getElementById("compno"+(x)).value.toUpperCase();
}

var id=null;

function selectmeter2nd(ids)
{
	id=ids;
	document.getElementById('meternos'+id).value='';
	if(document.getElementById('metermake'+ids).value!="")
	{
		$.ajax({
		    type: 'POST',
		    url: "/SAS/mm/getContent",
		    data: {
		        mmake: document.getElementById('metermake' + ids).value,
		        ids: ids,
		        mphase: document.getElementById('ph' + ids).value
		    },
		    success: function(result) {
		        $("#meternos"+id).html(result);
		        //$("#mtrnos"+id).css('width','130px').select2({allowClear:true})
				document.getElementById('meternos'+id).focus();
		    },
		    error: function(xhr, status, error) {
		        console.error("Error in AJAX request: " + error);
		        // Optionally show an error message to the user
		        alert("There was an error processing your request. Please try again.");
		    }
		});
	}
}

function getMeterDetails1(ids)
{
	id=ids;
	$.ajax({
	    type: 'POST',
	    url: "/SAS/mm/getSealInfo",
	    data: {
	    	mnomake: document.getElementById('metermake' + ids).value,
	        mmeterno: document.getElementById('mtrnos'+ids).value,
	        mphase: document.getElementById('ph' + ids).value
	    },
	    success: function(result) {
	        //$("#meternos1").html(result);
	        //alert(result);
	        var str=new Array;
			str=result.split("@");
			//alert(str);
			document.getElementById('MRTseal1'+id).value=str[0];
			document.getElementById('MRTseal2'+id).value=str[1];
			document.getElementById('inireading'+id).value=str[2];
			document.getElementById('inirdg_kvah'+id).value=str[3];
			document.getElementById('mf'+id).value=str[4];
			document.getElementById('capacity'+id).value=str[5];
			document.getElementById('mtrnos'+id).focus();
	    },
	    error: function(xhr, status, error) {
	        console.error("Error in AJAX request: " + error);
	        // Optionally show an error message to the user
	        alert("There was an error processing your request. Please try again.");
	    }
	});
}

function saveForm(){
	
	for(var x=1; x<=j; x++){
		if($("#meterStatus").val()=="18"){
			if (document.getElementById("compno"+x)!=null)
			{
				if(document.getElementById("compno"+x).value.length==0)
				{
					alert ("Complaint Registration No can't be left Blank");
					document.getElementById("compno"+x).focus();
					return false;
				}
				else if(document.getElementById("compno"+x).value.length==0)
				{
					alert ("Complaint Registration No Should be minimum of 13 characters");
					document.getElementById("compno"+x).focus();
					return false;
				}
			}
			
			if(document.getElementById("scno"+x)!=null)
			{
				document.getElementById("scno"+(x)).value = document.getElementById("scno"+(x)).value.toUpperCase();
				if(document.getElementById("scno"+x).value.length==0)
				{
					alert("Service Number can't be Left Blank");
					document.getElementById("scno"+x).focus();
					return false;
				}
				if(document.getElementById("scno"+x).value.length<8)
				{
					alert("Service Number Should be Minimum of 8 Characters");
					document.getElementById("scno"+x).focus();
					return false;
				}
			}
			
			if(document.getElementById("ph"+x)!=null)
			{
				if(document.getElementById("ph"+x).value.length==0)
				{
					alert("Phase can't be Left Blank");
					document.getElementById("ph"+x).focus();
					return false;
				}
			}

			if(document.getElementById("mtrno"+x)!=null)
			{
				if(document.getElementById("mtrno"+x).value.length==0)
				{
					alert("Meter Number can't be Left Blank");
					document.getElementById("mtrno"+x).focus();
					return false;
				}
				if(!isValidMeter(x))
				{
					return false;
				}
			}

			if(document.getElementById("inireading"+x)!=null)
			{
				if(document.getElementById("inireading"+x).value.length==0)
				{
					alert("Initial Reading can't be Left Blank");
					document.getElementById("inireading"+x).focus();
					return false;
				}
			}

			if(document.getElementById("lineman"+x)!=null)
			{
				if(document.getElementById("lineman"+x).value=="0")
				{
					alert("Select Lineman Name");
					document.getElementById("lineman"+x).focus();
					return false;
				}
			}

			if(document.getElementById("seal1"+x)!=null)
			{
				if(document.getElementById("seal1"+x).value.length==0)
				{
					alert("Seal1 can't be Left Blank");
					document.getElementById("seal1"+x).focus();
					return false;
				}
			}

			if(document.getElementById("seal2"+x)!=null)
			{
				if(document.getElementById("seal2"+x).value.length==0)
				{
					alert("Seal2 can't be Left Blank");
					document.getElementById("seal2"+x).focus();
					return false;
				}
			}
			
			dt1=getDateObject(document.f1.myDate.value,"/");
			dt2=getDateObject(document.getElementById("issuedate"+x).value,"/");
			dt3=getDateObject(document.getElementById("leddate").value,"/");
			
			if(ValidateForm(document.getElementById("issuedate"+x)))
			{
				if(dt2>dt1)
				{
					alert("Issue Date should not exceed Today's Date")
					document.getElementById("issuedate"+x).focus();
					return false;
				}
				else if(dt2<=dt3)
				{
					alert("Issue  Date should  Current month Date")
					document.getElementById("issuedate"+x).focus();
					return false;
				}
			}
		}
		//var seccd=document.getElementById("seccd").value;
		/*document.f1.vector.value=arr;
		document.f1.max.value=j;*/
		//document.f1.btn-search.disabled=true;
		document.f1.method="post";
		document.f1.action="meterAllotmentInserts";
		document.f1.submit();
		
		
	}
}

function saveAll2()
{
	var count=document.f1.rows.value;
	var noOfRows=0;
	var mflag;
	for(var c=1;c<=count;c++)
	{
		if(document.getElementById("chk"+c).checked==true)
		{
			if(document.getElementById("inireading"+c).value=="")
			{
				alert("Enter Initial Reading");
				document.getElementById("inireading"+c).focus();
				return false;
			}
			if(document.getElementById("issuedate"+c).value=="")
			{
				alert("Enter issue date ");
				document.getElementById("issuedate"+c).focus();
				return false;
			}
			if(document.getElementById("lineman"+c).value=="0")
			{
				alert("Select Lineman");
				document.getElementById("lineman"+c).focus();
				return false;
			}
			document.getElementById("chk"+c).value="1";
			noOfRows++;
		
			var dt1=getDateObject(document.f1.myDate.value,"/");
			var dt2=getDateObject(document.getElementById("issuedate"+c).value,"/");
			var dt3=getDateObject(document.getElementById("leddate").value,"/");
			//var dt4=getDateObject(document.getElementById("expdt"+c).value,"/");
		
			if(ValidateForm(document.getElementById("issuedate"+c)))
			{
				if(dt2>dt1)
				{
					alert("Issue Date should not exceed Today's Date")
					document.getElementById("issuedate"+c).focus();
					return false;
				}
				else if(dt2<=dt3)
				{
					alert("Issue  Date should  Current month Date")
					document.getElementById("issuedate"+c).focus();
					return false;
				}
				else if(dt2<=dt3)
				{
					alert("Issue  Date should greater than exception Date")
					document.getElementById("issuedate"+c).focus();
					return false;
				}
			}
		}
	}
	if(noOfRows==0)
	{
		alert("Select atleast One Service");
		return false;
	}
	document.f1.method="post";
	document.f1.action="meterAllotmentStuckupInserts";
	//document.f1.action="InsertMeterAllotment.jsp";
	document.f1.submit();
}



