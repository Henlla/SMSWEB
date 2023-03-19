$(document).ready(function () {
    $('.select2').select2({
        theme: 'bootstrap4'
    });
    $('#student-table').DataTable({
        pageLength:5,
        "lengthMenu": [5, 10, 25, 50, 75, 100],
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
                    .appendTo($('#select-major'))
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
                    width: 'resolve'
                });

                //initially clear select otherwise first option is selected
                $('.select2').val(null).trigger('change');
            });
        },
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width':'400px','display':'inline-block'}
    );
    jQuery.validator.addMethod("checkExcelExtension",
        function(value, element, params) {
            if(/^.+xlsx$/.test(value)) return true;
            else return false;
        }, 'Only accept excel file !');

    $("#form_import_mark").submit(function (event) {
        event.preventDefault();
        Swal.fire({
            icon: 'question',
            title: 'Confirm',
            text: "Are you sure confirm this list",
            showCancelButton: true,
            showDenyButton: false,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            cancelButtonText: 'Cancel',
            confirmButtonText: 'Ok',
        }).then((result)=>{
            if(result.isConfirmed){
                var file = $("#mark_list").get(0).files[0];
                var data = new FormData();
                data.append("mark_list", file);
                data.append("classId",$("#classId").val())
                data.append("teacherId",$("#teacherId").val())
                let fileName = data.get("mark_list").name;

                $('#form_import_mark').validate({
                    rules: {
                        mark_list: {
                            required: true,
                            checkExcelExtension: fileName
                        }
                    },
                    messages: {
                        mark_list: {
                            required: "Please choose mark list !",
                            checkExcelExtension:"Only accept excel file !"
                        },
                    },
                })
                if ($('#form_import_mark').valid()) {
                    $.ajax({
                        url: "/teacher/class/import-mark-list",
                        method: "POST",
                        data: data,
                        cache: false,
                        processData: false,
                        contentType: false,
                        enctype: "multipart/form-data",
                        success: (response) => {
                            Swal.fire({
                                icon: 'success',
                                title: 'Success',
                                text: "Import mark list Success",
                                showDenyButton: false,
                                confirmButtonColor: '#3085d6',
                                confirmButtonText: 'Ok',
                            })
                        },
                        error: (error)=>{
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: error.responseJSON.message,
                                showDenyButton: false,
                                confirmButtonColor: '#3085d6',
                                confirmButtonText: 'Ok',
                            })
                        },
                    });
                }
            }
        })
    })
});
