function index() {
	'use strict'
	/* Apex Chart Start*/
	var options = {
		series: [{
			name: 'Net Profit',
			type: 'column',
			data: [22, 34, 56, 37, 35, 21, 34, 60, 78, 56, 53, 89],
		}, {
			name: 'Sales',
			type: 'column',
			data: [42, 50, 70, 57, 55, 58, 43, 80, 54, 23, 34, 77],
		}, {
			name: 'Total',
			type: 'line',
			data: [25, 36, 58, 39, 38, 25, 37, 62, 56, 25, 37, 79],
		}],
		chart: {
			height: 300,
			foreColor: 'rgba(142, 156, 173, 0.9)',
		},
		stroke: {
			width: [0, 2, 4],
			curve: "smooth"
		},
		grid: {
			borderColor: 'transparent',
		},
		colors: [myVarVal || "#4d65d9", "#d7d7d9", "#e4e7ed"],
		plotOptions: {
			bar: {
				endingShape: 'rounded',
				horizontal: false,
				columnWidth: '30%',
			},
		},
		dataLabels: {
			enabled: false,
		},
		legend: {
			show: true,
			position: 'top',
			labels: {
				color: 'rgba(142, 156, 173, 0.9)'
			},
			fontFamily: 'Hind Siliguri',
		},
		stroke: {
			show: true,
			width: 4,
			colors: ['transparent']
		},
		yaxis: {
			title: {
				style: {
					color: '#adb5be',
					fontSize: '14px',
					fontFamily: 'Hind Siliguri',
					fontWeight: 600,
					cssClass: 'apexcharts-yaxis-label',
				},
			},
			labels: {
				rotate: -90,
				style: {
					fontFamily: 'Hind Siliguri',
					cssClass: 'summaryyaxis',
				}
			}
		},
		xaxis: {
			type: 'month',
			categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
			axisBorder: {
				show: true,
				color: 'rgba(119, 119, 142, 0.05)',
				offsetX: 0,
				offsetY: 0,
			},
			axisTicks: {
				show: true,
				borderType: 'solid',
				color: 'rgba(119, 119, 142, 0.05)',
				width: 6,
				offsetX: 0,
				offsetY: 0
			},
			labels: {
				rotate: -90,
				style: {
					fontFamily: 'Hind Siliguri',
					cssClass: 'summaryxaxis',
				}
			}
		},
		markers: {
			size: 0
		}
	};

	document.getElementById('salessummary').innerHTML = ''
	var chart = new ApexCharts(document.querySelector("#salessummary"), options);
	chart.render();

	/* Apex Chart End*/

	// circle 1
	$('#circle1').circleProgress({
		value: 0.7,
		size: 60,
		fill: {
			color: ["#ff9b21"]
		}
	})
		.on('circle-animation-progress', function (event, progress) {
			$(this).find('strong').html(Math.round(70 * progress) + '<i>%</i>');
		});

	// circle 2
	$('#circle2').circleProgress({
		value: 0.85,
		size: 60,
		fill: {
			color: ["#19b159"]
		}
	})
		.on('circle-animation-progress', function (event, progress) {
			$(this).find('strong').html(Math.round(85 * progress) + '<i>%</i>');
		});

	// circle 3
	$('#circle3').circleProgress({
		value: 0.85,
		size: 60,
		fill: {
			color: ["#01b8ff"]
		}
	})
		.on('circle-animation-progress', function (event, progress) {
			$(this).find('strong').html(Math.round(90 * progress) + '<i>%</i>');
		});

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
