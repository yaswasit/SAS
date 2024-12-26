jQuery(function() {
	$('.select2').select2({
		placeholder: 'Choose one',
		searchInputPlaceholder: 'Search',
		width: '100%'
	});

	$('.selectMultiple').select2({
		placeholder: 'Choose one/Multiple',
		searchInputPlaceholder: 'Search',
		width: '100%'
	});

	$('.select2-no-search').select2({
		minimumResultsForSearch: Infinity,
		placeholder: 'Choose one',
		width: '100%'
	});

	function formatState(state) {
		if (!state.id) { return state.text; }
		var $state = $(
			'<span><img src="../../assets/plugins/flag-icon-css/flags/4x3/' + state.element.value.toLowerCase() +
			'.svg" class="img-flag" /> ' +
			state.text + '</span>'
		);
		return $state;
	};

	$(".select2-flag-search").select2({
		templateResult: formatState,
		templateSelection: formatState,
		escapeMarkup: function (m) { return m; }
	});

	$('.select2').on('click', () => {
		let selectField = document.querySelectorAll('.select2-search__field')
		selectField.forEach((element, index) => {
			element.focus();
		})
	});

});
