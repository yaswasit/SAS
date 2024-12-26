$(function() {
	'use strict'
	//Date range picker
	$('#reservation').daterangepicker();

	// Time Picker
	$('#tpBasic').timepicker();
	$('#tp2').timepicker({
		'scrollDefault': 'now'
	});
	
	$('#tp3').timepicker();
	
	$(document).on('click', '#setTimeButton', function() {
		$('#tp3').timepicker('setTime', new Date());
	});
});