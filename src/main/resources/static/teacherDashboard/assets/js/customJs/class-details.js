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

    $('#myTable').on( 'dblclick', 'tbody td:not(:first-child)', function (e) {
        var data = this.innerText;
        this.innerText = "";
        $('<input type="text" value="'+ data +'"/>').appendTo(this);
        var strLength = data.length * 2;

        $(this).children("input:first").focus();
        $(this).children("input:first")[0].setSelectionRange(strLength, strLength);
    } );
    $('#myTable tbody tr td input').keyup(function(e){
        if(e.keyCode == 13)
        {
            let input = $("#myTable tbody tr td input")
            let td = $("#myTable tbody tr td:has(input)")
            var  data = input.val();

            td.children().remove();
            td.addClass("text-info").html(data);
        }
    });
    $("#myTable").click( function (e){
        let input = $("#myTable tbody tr td input")
        let td = $("#myTable tbody tr td:has(input)")
        var  data = input.val();

        td.children().remove();
        td.addClass("text-info").html(data);
    })

    $("#mark_list").change(function (e){
        // Đọc file Excel
        var file = $("#mark_list").get(0).files[0];
        var reader = new FileReader();
        reader.readAsArrayBuffer(file);
        reader.onload = function(e) {
            var data = new Uint8Array(reader.result);
            var workbook = XLSX.read(data, {type: 'array'});
            var sheet_name_list = workbook.SheetNames;
            var Sheet1 = XLSX.utils.sheet_to_json(workbook.Sheets[sheet_name_list[0]], {range:0});

            // Hiển thị dữ liệu trong DataTable jQuery
            $('#myTable').DataTable({
                data: Sheet1,
                columns: [
                    {data: "Numbers"},
                    {data: "Student code"},
                    {data: "Student name"},
                    {data: "Subject code"},
                    {data: "ASM mark"},
                    {data: "OBJ mark"}
                ],
            });
        };
    })

    $("#form_import_mark").submit(function (event) {
        event.preventDefault();

        //var file = $("#mark_list").get(0).files[0];
        var data = new FormData();
        data.append("classId",$("#classId").val())
        data.append("teacherId",$("#teacherId").val())

        //Convert dataTable to blod(file)
        var wb = XLSX.utils.table_to_book(document.getElementById('myTable'),{sheet:'MARK_LIST'});
        var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:true, type:'binary'})
        var buf = new ArrayBuffer(wbout.length);
        var view = new Uint8Array(buf);
        for (let i = 0; i < wbout.length; i++) {
            view[i] = wbout.charCodeAt(i) & 0xFF;
        }
        var blod = new Blob([buf], {type:'application/octet-stream'});
        data.append("mark_list",blod);

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
            })

        }

    })
});
