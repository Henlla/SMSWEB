let addSubject = function () {
    let selectSubject = $("#mark_select_subject");
    selectSubject.empty();
    $.ajax({
        url: "/teacher/class/get-all-subject/"+$("#classId").val(),
        method: "GET",
        cache: false,
        processData: false,
        contentType: false,
        success: (response) => {
            let parse = JSON.parse(response);
            if(parse.length == 0){
                $('<option value="">'+'This class has no subject'+'</option>').appendTo(selectSubject);
            }else {
                $('<option value="">'+'--Select--'+'</option>').appendTo(selectSubject);
                parse.forEach(p => {
                    $('<option value="'+ p.id +'">'+ p.subjectName +'</option>').appendTo(selectSubject);
                })
            }

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
let check_input_mark = function (){
    $(".check_numberic_0_100").each(function (){
        let data = $(this).val();
        if (/^0$|^[1-9]{1}$|^[1-9]{1}[0-9]{1}$|^100$/.test(data) == false){
            if(/^0[1-9]{1}$/.test(data)){
                $(this).val(data.toString().slice(1));
            }
            $(this).val('').focus();
        }
    })
}
let check_input_mark_not_null = function (){
    var isValid = true;
    $("input[class='check_numberic_0_100']").each(function (){
        let data = $(this).val();
        if(data == null || data == '') isValid = false
    })
    return isValid;
}
function check_numberic_0_100(e){
    let val = e.target.value;
    const reg = new RegExp('^0$|^[1-9]{1}$|^[1-9]{1}[0-9]{1}$|^100$');
    if (!reg.test(val)){
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Mark must in range[0-100]',
            showDenyButton: false,
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Ok',
        });
        check_input_mark();
    }
}

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
    $("#mark_list").click(function (e) {
        $(this).val('');
    })
    $("#mark_list").change(function (e){

        $("#mark_select_subject").val('');
        //Clear Database
        $("#mark_table tbody").empty();

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
            dataArray = Sheet1;
            console.log(Sheet1);
            for (let i = 0; i < dataArray.length; i++) {
                var studentId = dataArray[i]['Student Code'];
                var subjectId = dataArray[i]['Subject Code'];
                var fullName = dataArray[i]['Student Name'];
                var subjectName = dataArray[i]['Subject Name'];
                var asmMark =dataArray[i]['ASM mark'];
                var objMark = dataArray[i]['OBJ mark'];
                $(`<tr>
                        <td style="display:none;">
                            <input style="border: none" type="text" value="${studentId}" hidden readonly/>
                        </td><td style="display:none;">
                            <input style="border: none" type="text" value="${subjectId}" hidden readonly/>
                        </td><td>
                            <input style="border: none" type="text" value="${fullName}" readonly/>
                        </td><td>
                            <input style="border: none" type="text" value="${subjectName}" readonly/>
                        </td><td>
                            <input class="check_numberic_0_100" value="${asmMark == null || asmMark == 'null' || asmMark == undefined ? '': asmMark}" onchange="check_numberic_0_100(event)" type="number" min="0" max="100" />
                        </td><td>
                            <input class="check_numberic_0_100" value="${objMark == null || objMark == 'null' || objMark == undefined ? '': objMark}" onchange="check_numberic_0_100(event)" type="number" min="0" max="100" />
                        </td>
                    </tr>`).appendTo("#mark_table tbody");
            }

            check_input_mark();
        };
    })

    $("#form_import_mark").submit(function (event) {
        event.preventDefault();

        //var file = $("#mark_list").get(0).files[0];
        var data = new FormData();
        data.append("classId",$("#classId").val())
        data.append("teacherId",$("#teacherId").val())

        //Convert dataTable to blod(file)
        var wb = XLSX.utils.table_to_book(document.getElementById('mark_table'),{sheet:'MARK_LIST'});
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

    //download student list
    $("#export_student_excel").click(function (event){
        event.preventDefault();

        //Convert dataTable to blod(file)
        var wb = XLSX.utils.table_to_book(document.getElementById('student-table'),{sheet:'STUDENT_LIST'});
        XLSX.writeFile(wb, "Student_list_"+$("#title_class_code").html()+".xlsx")
    })

    let selectSubject = $("#mark_select_subject");
    selectSubject.change(function (event) {
        $("#mark_list").val('');
        //Clear Database
        $("#mark_table tbody").empty();

        if(selectSubject.val() != '' && selectSubject != null){
            var data = new FormData();
            data.append("subjectId", selectSubject.val());
            data.append("classId", $('#classId').val());
            $.ajax({
                url: "/teacher/class/get-student-list",
                method: "POST",
                data:data,
                cache: false,
                processData: false,
                contentType: false,
                enctype: "multipart/form-data",
                success: (response) => {
                    console.log(JSON.parse(response));
                    dataArray = JSON.parse(response);
                    for (let i = 0; i < dataArray.length; i++) {
                        $(`<tr>
                            <td style="display:none;">
                                <input style="border: none" type="text" value="${dataArray[i].studentCode}" hidden readonly/>
                            </td><td style="display:none;">
                                <input style="border: none" type="text" value="${dataArray[i].subjectCode}" hidden readonly/>
                            </td><td>
                                <input style="border: none" type="text" value="${dataArray[i].fullName}" readonly/>
                            </td><td>
                                <input style="border: none" type="text" value="${dataArray[i].subjectName}" readonly/>
                            </td><td>
                                <input class="check_numberic_0_100" value="${dataArray[i].asmMark == null || dataArray[i].asmMark == 'null' || dataArray[i].asmMark == undefined ? '': dataArray[i].asmMark}" onchange="check_numberic_0_100(event)" type="number" min="0" max="100" />
                            </td><td>
                                <input class="check_numberic_0_100" value="${dataArray[i].objMark == null || dataArray[i].objMark == 'null' || dataArray[i].objMark == undefined ? '': dataArray[i].objMark}" onchange="check_numberic_0_100(event)" type="number" min="0" max="100" />
                            </td>
                        </tr>`).appendTo("#mark_table tbody");
                    }
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
        }else {
            $('#mark_table tbody').empty();
        }
    });

    let formInputMark = $('#form_input_mark');
    formInputMark.click(function (event) {
        event.preventDefault();

        if(check_input_mark_not_null()){
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
                    $("#mark_table tbody tr td input").each(function () {
                        $(this).replaceWith($(this).val());
                    });

                    var jsonRowsTable= '';
                    let array = $("#mark_table").DataTable({
                        destroy: true,
                        scroll:true,
                        paging: false,
                        search: false,
                        "searching": false
                    }).rows().data().toArray();

                    if ( $.fn.DataTable.isDataTable('#mark_table') ) {
                        $('#mark_table').DataTable().destroy();
                    }
                    $('#mark_table tbody').empty();
                    console.log(array)
                    jsonRowsTable+='[';
                    for (let index = 0; index < array.length; index++) {
                        jsonRowsTable += `{
                            "studentCode":"${array[index][0]}",
                            "subjectCode":"${array[index][1]}",
                            "fullName":"${array[index][2]}",
                            "subjectName":"${array[index][3]}",
                            "asmMark":${array[index][4]},
                            "objMark":${array[index][5]}
                            }`;
                        if (index != array.length - 1){
                            jsonRowsTable += ','
                        }
                    }
                    jsonRowsTable+=']';
                    console.log(jsonRowsTable);

                    var data = new FormData();
                    data.append("teacherId",$("#teacherId").val());
                    data.append("classId",$("#classId").val());
                    data.append("mark_list",jsonRowsTable);

                    $.ajax({
                        url: "/teacher/class/input-mark",
                        method: "POST",
                        data:data,
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
                            });
                            $('#mark_table tbody').empty();
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
        }else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'please field all mark',
                showDenyButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Ok',
            })
        }
    })

    //Download teamplate
    $("#download_template").click(function (event) {
        event.preventDefault();
        let selectSubject = $("#mark_select_subject");
        if(selectSubject.val() == "" || selectSubject.val() == null){
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Please select subject',
                showDenyButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Ok',
            })
        }else {
            window.location.href = "/teacher/class/download_template/"+$("#classId").val()+"/"+selectSubject.val();
        }
    })
    
});
