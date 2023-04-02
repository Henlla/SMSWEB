$(document).ready(function () {
    $('#reservationdate_u').datetimepicker({
        format: 'DD/MM/YYYY'
    });
    var groupColumn = 1;
    var count = 0;
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
            this.api().columns([1]).every(function (i) {
                var title = this.header();
                //replace spaces with dashes
                title = $(title).html().replace(/[\W]/g, '');
                var column = this;
                var select = $('<select id="' + title + '" class="form-control form-control-sm select2" style="width: 50%" ></select>')
                    .appendTo($('#select-department'))
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
        columnDefs: [{ visible: false, targets: groupColumn }],
        order: [[groupColumn, 'asc']],
        displayLength: 25,
        drawCallback: function (settings) {
            var api = this.api();
            var rows = api.rows({ page: 'current' }).nodes();
            var last = null;

            api
                .column(groupColumn, { page: 'current' })
                .data()
                .each(function (group, i) {
                    if (last !== group) {
                        $(rows)
                            .eq(i)
                            .before('<tr class="group group_'+count+' expand" onclick="groupItem('+count+')"><td class="text-bold text-lg text-uppercase" colspan="7">' + group + '</td></tr>');
                        last = group;
                        count++;
                    }
                });
            count = 0;
        },
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width': '350px', 'display': 'inline-block'}
    );

    // var group = $("tr[class='group']");
    // group.click((event)=>{
    //     console.log(event.target);
    // })
});
 var groupItem = function (param) {
     var class_list = $( ".group_"+param ).attr('class').split(' ');
     console.log(class_list);
     var bol = class_list.includes('expand');
     console.log(bol)
     if (bol){
         $( ".group_"+param )
             .nextUntil( ".group_"+(param+1) ).addClass("d-none");
         $( ".group_"+param ).removeClass('expand')
     }else {
         $( ".group_"+param )
             .nextUntil( ".group_"+(param+1) ).removeClass("d-none");
         $( ".group_"+param ).addClass('expand')
     }

 }

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
