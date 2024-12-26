function index2() {

	/* Chartjs (#sales-summary) */
	var myCanvas = document.getElementById("sales-summary");
	myCanvas.height = "300";
	var myChart = new Chart(myCanvas, {
		type: 'bar',
		data: {
			labels: ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"],
			datasets: [{
				label: 'Sales',
				data: [27, 16, 27, 22, 14, 18, 27, 21, 14, 27, 20, 27],
				backgroundColor: myVarVal,
				borderWidth: 1,
				hoverBackgroundColor: myVarVal,
				hoverBorderWidth: 0,
				borderColor: myVarVal,
				hoverBorderColor: myVarVal,  
				barPercentage: 0.15,
			}, {

				label: 'Profits',
				data: [44, 24, 39, 30, 31, 32, 39, 28, 24, 39, 31, 39],
				backgroundColor: hexToRgba(myVarVal, 0.2) ||'#9fa8e0',
				borderWidth: 1,
				hoverBackgroundColor: hexToRgba(myVarVal, 0.2) || '#9fa8e0',
				hoverBorderWidth: 0,
				borderColor: hexToRgba(myVarVal, 0.2) || '#9fa8e0',
				hoverBorderColor: hexToRgba(myVarVal, 0.2) ||'#9fa8e0',
				barPercentage: 0.15,
			},

			]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			plugins:{
				layout: {
					padding: {
						left: 0,
						right: 0,
						top: 0,
						bottom: 0
					}
				},
				tooltips: {
					enabled: false,
				},
				legend: {
					display: true,
				},
				elements: {
					point: {
						radius: 0,
					}
				},

			},
			scales: {
				y: {
					gridLines: {
						display: true,
						drawBorder: false,
						zeroLineColor: 'rgba(142, 156, 173,0.1)',
						color: "rgba(142, 156, 173,0.1)",
					},
					scaleLabel: {
						display: false,
					},
					ticks: {
						beginAtZero: true,
						stepSize: 10,
						max: 50,
						fontColor: "#8492a6",
						fontFamily: 'Hind Siliguri',
					},
				},
				x: {
					barPercentage: 0.15,
					barValueSpacing: 3,
					barDatasetSpacing: 3,
					barRadius: 5,
					stacked: true,
					ticks: {
						beginAtZero: true,
						fontColor: "#8492a6",
						fontFamily: 'Hind Siliguri',
					},
					gridLines: {
						color: "rgba(142, 156, 173,0.1)",
						display: false
					},

				}
			},
		}
	});
	/* Chartjs (#sales-summary) closed */



};
 function morrisFn(){

	// index CHART COLOR dOTS
	document.querySelector('.bg-primary-light-1').style.background = hexToRgba(myVarVal, 0.7) ;
	document.querySelector('.bg-primary-light-2').style.background = hexToRgba(myVarVal, 0.5) ;
	document.querySelector('.bg-primary-light-3').style.background = hexToRgba(myVarVal, 0.2) ;

	 	/*Morris chart */
	new Morris.Donut({

		element: 'revenuemorrischart',
		data: [
			{ label: "clients", value: 15 },
			{ label: "sales", value: 42 },
			{ label: "shares", value: 20 },
			{ label: "profits", value: 23 }
		],
		colors: [hexToRgba(myVarVal, 0.7) || "#7886d3", myVarVal, hexToRgba(myVarVal, 0.2) || "#d8dcf3", hexToRgba(myVarVal, 0.5) || "#9fa8e0"],
		labelColor: '#77778e',
		resize: true,
	});

	if (document.querySelectorAll('#revenuemorrischart svg').length >= 2) {
		let svgs = document.querySelectorAll('#revenuemorrischart svg')

		for (var i = 0; i <= svgs.length - 1; i++) {
			if (i == 0) {

			}
			else {
				svgs[i].remove()
			}
		}
	}
 }

/*Data Table */
$('#recentorders').DataTable({
	"order": [[1, "asc"]],
	"language": {
		searchPlaceholder: 'Search...',
		sSearch: '',
	}
});
//______Select2 
$('.select2').select2({
	minimumResultsForSearch: Infinity
});

