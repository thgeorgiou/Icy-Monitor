// Plot data
var data_temp = [], values_temp = [];
var data_load = [], values_load = [];
// Misc
var loop = 0;
var coreCount;
var options = {series: {lines: { show: true }},
				grid: { hoverable: true, autoHighlight: false },
				yaxis: { min: 0, max: 100 },
				xaxis: { min: 0, max: 130 }};

$(function () {
	// Setup CPU graphs
	$.ajax({
		url:      'data?type=graph',
		method:   'GET',
		dataType: 'json',
		success:  function(json) {
			coreCount = json.Temp.length;
			for (var i = 0; i < coreCount; i++) {
				values_temp[i] = [];
				values_load[i] = [];
				data_temp[i] = {data: values_temp[i], label: "Core " + (i + 1)};
				data_load[i] = {data: values_load[i], label: "Core " + (i + 1)};
			}
		}
	});
	
	$.plot($("#placeholder_temp"), data_temp, options);
	$.plot($("#placeholder_load"), data_load, options);
	
	var legends_temp = $("#placeholder_temp .legendLabel");
	legends_temp.each(function () {
		// fix the widths so they don't jump around
		$(this).css('width', $(this).width());
	});
	
	var legends_load = $("#placeholder_temp .legendLabel");
	legends_load.each(function () {
		// fix the widths so they don't jump around
		$(this).css('width', $(this).width());
	});
	
	// Setup disk table
	$.ajax({
		url:      'data?type=disks',
		method:   'GET',
		dataType: 'json',
		success:  function(json) {
			diskCount = json.length;
			var table = document.getElementById("tbl_disks");
			for (var i = 0; i < diskCount; i++) {
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				
				for (cellID = 0; cellID < 5; cellID++) {
					var cell = row.insertCell(cellID);
				}
				
				var r = i + 1;
				table.rows[r].cells[0].innerHTML = json[i].Name;
				table.rows[r].cells[1].innerHTML = json[i].Label;
				table.rows[r].cells[2].innerHTML = json[i].Format;
				table.rows[r].cells[3].innerHTML = json[i].Size;
				table.rows[r].cells[4].innerHTML = json[i].Free;
			}
		}
	});
	
	// Setup processes table
		$.ajax({
		url:      'data?type=processes',
		method:   'GET',
		dataType: 'json',
		success:  function(json) {
			processCount = json.Processes.length;
			var table = document.getElementById("tbl_processes");
			for (var i = 0; i < processCount; i++) {
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				
				for (cellID = 0; cellID < 2; cellID++) {
					var cell = row.insertCell(cellID);
				}
				
				var r = i + 1;
				table.rows[r].cells[0].innerHTML = json.Processes[i].Name;
				table.rows[r].cells[1].innerHTML = json.Processes[i].Mem;
			}
		}
	});
});

fetchdata();
function fetchdata() {
	$.ajax({
		url:      'data?type=graph',
		method:   'GET',
		dataType: 'json',
		success:  function(json) {
			for (i = 0; i < coreCount; i++) {
				var temp = json.Temp[i];
				var load = json.Load[i];
				values_temp[i].push([loop, temp]);
				data_temp[i].label = "Core " + (i + 1) + " (" + temp + "\u00B0 C)";
				values_load[i].push([loop, load]);
				data_load[i].label = "Core " + (i + 1) + " (" + load + "%)";
			}
			loop++;
			$.plot($("#placeholder_temp"), data_temp, options);
			$.plot($("#placeholder_load"), data_load, options);
			
			// Scroll graph
			if(loop > 100) {
				options.xaxis.min = loop - 100;
				options.xaxis.max = loop + 30;
			}
		}
	});
	setTimeout(fetchdata, 1000); // REFRESH RATE
}