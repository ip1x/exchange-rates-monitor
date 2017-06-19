$(document).ready(function () {
    getChartRates();
    getTableRates();
    buildDatePicker();
});

var exchangeRates;

function filterDates() {
    var startDate = $('#startDatePicker input').val();
    var endDate = $('#endDatePicker input').val();
    filterChartRates(startDate, endDate);
    filterTableRates(startDate, endDate)

}

function filterChartRates(startDate, endDate) {
    $.ajax({
        type: "GET",
        data: {
            startDate: startDate,
            endDate: endDate
        },
        url: "/rates/filter/chart",
        success: function (data) {
            buildChart(data);
        },
        dataType: 'json'
    });
}

function filterTableRates(startDate, endDate) {
    $.ajax({
        type: "GET",
        data: {
            startDate: startDate,
            endDate: endDate
        },
        url: "/rates/filter/table",
        success: function (data) {
            buildTable(data)
        },
        error: function (msg) {
            $.notify(msg.responseText,
                {position: "right"}
            );

        },
        dataType: 'json'
    });
}

function downloadRatesInExcel() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            //this.response is what you're looking for
            console.log(this.response, typeof this.response);
            var link = document.getElementById('download_link');
            var url = window.URL || window.webkitURL;
            link.href = url.createObjectURL(this.response);
            link.download = 'excel-rates.xls';
            link.click();
        }
    };
    xhr.open('POST', '/rates/excel');
    xhr.responseType = 'blob';
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(JSON.stringify(exchangeRates));
}

function executeRatesUpdating() {
    var startDate = $('#startDatePicker input').val();
    var endDate = $('#endDatePicker input').val();
    $.ajax({
        type: "PUT",
        data: {
            startDate: startDate,
            endDate: endDate
        },
        url: "/rates/update",
        success: function (data) {
            if (startDate && endDate) {
                filterChartRates(startDate, endDate);
                filterTableRates(startDate, endDate);
            } else {
                getChartRates();
                getTableRates();
            }
        },
        error: function (msg) {
            $.notify(msg.responseText,
                {position: "right"}
            );

        }
    });
}

function getChartRates() {
    $.ajax({
        type: "GET",
        url: "/rates/chart",
        success: function (data) {
            buildChart(data);
        },
        dataType: 'json'
    });
}

function getTableRates() {
    $.ajax({
        type: "GET",
        url: "/rates/table",
        success: function (data) {
            buildTable(data);
        },
        dataType: 'json'
    });
}

function buildChart(data) {
    $('#rateChart').empty();
    var ctx = document.getElementById('rateChart').getContext('2d');

    var dates = [];
    var rates = [];
    for (let x of data) {
        rates.push(x.rate);
        dates.push(x.date);
    }

    var chart = new Chart(ctx, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: dates,
            datasets: [{
                label: 'Rate',
                backgroundColor: 'rgba(0,153,51,0.2)',
                borderColor: '#00802b',
                data: rates,
            }]
        },

        // Configuration options go here
        options: {}
    });
}

function buildTable(data) {
    exchangeRates = data;
    var table = $('#rateTable tbody');
    table.empty();
    if (data.length < 1) {
        table.append('<td colspan="2">No data for this period</td>');
        return;
    }
    var mergedRawNumber = 1;
    var rateTd;
    for (var i = data.length - 1; i > 0; i--) {
        if (data[i].rate === data[i - 1].rate) {
            mergedRawNumber++;
            rateTd = '';
        } else {
            rateTd = '<td rowspan=' + mergedRawNumber + '>' + data[i].rate + '</td>';
            mergedRawNumber = 1;
        }
        var row = '<tr><td>' + data[i].date + '</td>' + rateTd + '</tr>';
        table.prepend(row);
    }
    table.prepend('<tr><td>' + data[0].date + '</td>'
        + '<td rowspan=' + mergedRawNumber + '>' + data[i].rate + '</td></tr>');

}

function buildDatePicker() {
    $('#rateFilters .input-group.date input').datepicker({
        autoclose: true,
        format: 'yyyy-mm-dd'
    });

    $('#rateFilters .input-group.date input').on('show', function (e) {
        console.debug('show', e.date, $(this).data('stickyDate'));

        if (e.date) {
            $(this).data('stickyDate', e.date);
        }
        else {
            $(this).data('stickyDate', null);
        }
    });

    $('#rateFilters .input-group.date input').on('hide', function (e) {
        console.debug('hide', e.date, $(this).data('stickyDate'));
        var stickyDate = $(this).data('stickyDate');

        if (!e.date && stickyDate) {
            console.debug('restore stickyDate', stickyDate);
            $(this).datepicker('setDate', stickyDate);
            $(this).data('stickyDate', null);
        }
    });
}