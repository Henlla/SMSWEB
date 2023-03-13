$(document).ready(function () {
    $('#reservationdate_u').datetimepicker({
        format: 'DD/MM/YYYY'
    });
    $("#class-table").DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
        scrollCollapse: true,
        scrollY: '600px',
        "language": {
            "decimal": "",
            "emptyTable": "Don't have any record",
            "info": "",
            "infoEmpty": "",
            "infoFiltered": "",
            "infoPostFix": "",
            "thousands": ",",
            "lengthMenu": "Show _MENU_ record",
            "loadingRecords": "Searching...",
            "processing": "",
            "search": "Search:",
            "zeroRecords": "Don't find any record",
            "paginate": {
                "first": "First page",
                "last": "Last page",
                "next": "Next page",
                "previous": "Previous page"
            },
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            }
        },initComplete: function () {
            count = 0;
            this.api().columns([2]).every(function (i) {
                var title = this.header();
                //replace spaces with dashes
                title = $(title).html().replace(/[\W]/g, '');
                var column = this;
                var select = $('<select id="' + title + '" class="form-control form-control-sm select2" style="width: 50%" ></select>')
                    .appendTo($('#select-teacher'))
                    .on('change', function () {
                        //Get the "text" property from each selected data
                        //regex escape the value and store in array
                        var data = $.map($(this).select2('data'),
                            function (value, key) {
                                return value.text ? '^' + $.fn.dataTable.util.escapeRegex(value.text) + '$' : null;
                            });
                        //if no data selected use ""
                        if (data.length === 0) {
                            data = [""];
                        }
                        //join array into string with regex or (|)
                        var val = data.join('|');
                        //search for the option(s) selected
                        column
                            .search(val ? val : '', true, false)
                            .draw();
                    });

                column.data().unique().sort().each(function (d, j) {
                    select.append('<option value="' + d + '">' + d + '</option>');
                });
                //use column title as selector and placeholder
                $('#' + title).select2({
                    closeOnSelect: true,
                    placeholder: "- All -",
                    allowClear: true,
                    width: 'resolve',
                });

                //initially clear select otherwise first option is selected
                $('.select2').val(null).trigger('change');
            });
        },
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width': '350px', 'display': 'inline-block'}
    );
});

var OnUpdate = (id) => {
    $.ajax({
        url: "/dashboard/class/class-update/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#class_code').val(data.classCode)
            $('#class_teacher').val(data.teacher.profileByProfileId.firstName + ' ' + data.teacher.profileByProfileId.lastName)
            $('#class_major').val(data.major.majorName)
            $('#class_student_count').val(data.studentClassById.length)
            $('#class_student_limit').val(data.limitStudent)
            $('#class_create_date').val(data.startDate)
            $('#class_status').val(data.classStatus)

            $("#class_details").modal("show");
        },
        error: (data) => {
            console.log(data);
        }
    });
}
