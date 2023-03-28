$(() => {
    $(".select2").select2({
        theme: "bootstrap4"
    });

    $('#date').datetimepicker({
        format: "DD/MM/YYYY",
        maxDate: moment().subtract(3, "days")
    });

    $("#date_time").val(moment().subtract(3, "days").format("DD/MM/YYYY"));

    $(".class_select2").select2({
        theme: "bootstrap4",
        ajax: {
            url: "/dashboard/attendance/findScheduleDetailByDate",
            method: "GET",
            delay:250,
            data: function (params) {
                let query = {
                    searchTerm: params.term,
                    date: $("#date_time").val()
                }
                return query;
            }, processResults: function (response) {
                let dataArray = [];
                for (const dataKey of response) {
                    let data1 = {
                        "id": dataKey.id,
                        "text": dataKey.classCode
                    }
                    dataArray.push(data1);
                }
                return {
                    results: dataArray
                };
            }, error: (data) => {
                if (data.responseText.toLowerCase() === "token expired") {
                    Swal.fire({
                        title: 'End of login session please login again',
                        showDenyButton: false,
                        showCancelButton: false,
                        confirmButtonText: 'Confirm',
                    }).then((result) => {
                        if (result.isConfirmed) {
                            location.href = "/dashboard/login";
                        }
                    });
                } else {
                    toastr.error(data.responseText);
                }
            }
        }
    });

    $("#class_select").removeClass("d-none");

    $("#attendance_table").DataTable({
        paging: false,
        searching: false,
        info: false,
        columnDefs: [
            {
                "targets": [0, 1, 2, 3, 4, 5],
                "className": "text-center",
            },
            {
                "targets": [1, 3, 4, 5],
                "orderable": false,
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Avatar"},
            {title: "Student name"},
            {title: "Present"},
            {title: "Absent"},
            {title: "Note"},
            {title: "Attendance", visible: false}
        ],
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
        }
    });

    $("#date").on("change.datetimepicker", () => {
        $("#btnOnsubmit").addClass("d-none")
        $("#slot").val("").trigger("change");
        $("#class").empty();
        let table = $('#attendance_table').DataTable();
        table.clear().draw();
        let date = $("#date_time").val();
        $("#class_select").removeClass("d-none");
        $(".class_select2").select2({
            theme: "bootstrap4",
            ajax: {
                url: "/dashboard/attendance/findScheduleDetailByDate",
                method: "GET",
                data: function (params) {
                    let query = {
                        search: params.term,
                        date: date
                    }
                    return query;
                }, processResults: function (response) {
                    let dataArray = [];
                    for (const dataKey of response) {
                        let data1 = {
                            "id": dataKey.id,
                            "text": dataKey.classCode
                        }
                        dataArray.push(data1);
                    }
                    return {
                        results: dataArray
                    };
                }, error: (data) => {
                    if (data.responseText.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'End of login session please login again',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Confirm',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.href = "/dashboard/login";
                            }
                        });
                    } else {
                        toastr.error(data.responseText);
                    }
                }
            }
        });
    });

    $("#class").on("change", () => {
        $("#btnOnsubmit").addClass("d-none")
        $("#slot").val("").trigger("change");
        let table = $('#attendance_table').DataTable();
        table.clear().draw();
        $("#slot_select").removeClass("d-none");
    });

    $("#slot_select").on("change", () => {
        let classId = $("#class").val();
        let date = $("#date_time").val().split("/");
        let formatDate = date[2] + "-" + date[1] + "-" + date[0];
        let slot = $("#slot").val();
        if (slot != "") {
            $.ajax({
                url: "/dashboard/attendance/findAttendanceByDate",
                method: "POST",
                data: {
                    classId: classId,
                    date: formatDate,
                    slot: slot
                },
                success: (response) => {
                    console.log(response);
                    if ($.fn.dataTable.isDataTable('#attendance_table')) {
                        let table = $('#attendance_table').DataTable();
                        table.clear().draw();
                        table.destroy();
                        DataTableEdit(response);
                    } else {
                        DataTableEdit(response);
                    }
                    $("#btnOnsubmit").removeClass("d-none");
                }, error: (data) => {
                    if (data.responseText.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'End of login session please login again',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Confirm',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.href = "/dashboard/login";
                            }
                        });
                    } else {
                        toastr.error(data.responseText);
                    }
                }
            });
        }else{
            let table = $('#attendance_table').DataTable();
            table.clear().draw();
            $("#btnOnsubmit").addClass("d-none");
        }
    });

});

const OnUpdateAttendance = () => {
    Swal.fire({
        title: 'Do you want to update this ?',
        text: "Check carefully before update!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: "Cancel",
        confirmButtonText: 'Confirm!'
    }).then((result) => {
        if (result.isConfirmed) {
            let table = $("#attendance_table").DataTable();
            let data = table.rows().data().toArray();
            let formData = [];
            let classId = $("#class").val();
            let date = $("#date_time").val().split("/");
            let formatDate = date[2] + "-" + date[1] + "-" + date[0];
            let slot = $("#slot").val();
            for (const item of data) {
                let checked = $("#attend" + item.student_id + ":checked").val();
                let note = $("#note" + item.student_id).val();
                let attendanceModel = {
                    "attendance_id": item.attendance_id,
                    "student_id": item.student_id,
                    "note": note,
                    "status": checked,
                    "slot": slot
                }
                formData.push(attendanceModel);
            }
            $.ajax({
                url: "/dashboard/attendance/submitAttendance",
                method: "POST",
                data: {
                    "attendModel": JSON.stringify(formData),
                    "classId": classId,
                    "date": formatDate,
                    "slot": slot
                },
                success: (data) => {
                    toastr.success("Update success");
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                },
                error: (data) => {
                    if (data.responseText.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'End of login session please login again',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Confirm',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.href = "/login";
                            }
                        });
                    } else {
                        toastr.error(data.responseText);
                    }
                }
            });
        }
    });
}

const DataTableEdit = (response) => {
    let html = "";
    $("#attendance_table").DataTable({
        autoWidth: false,
        data: response,
        paging: false,
        searching: false,
        info: false,
        columnDefs: [
            {
                "targets": [0, 1, 2, 3, 4],
                "className": "text-center"
            },
            {
                "targets": [1, 3, 4, 5],
                "orderable": false,
            }
        ],
        columns: [
            {
                render: function (data, type, row, index) {
                    return index.row + 1;
                }
            },
            {
                data: "avatar",
                render: function (data, type, row, index) {
                    html = '<img style="width: 50px;height: 50px" src="' + data + '"/>'
                    return html;
                }
            },
            {
                data: "student_name",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: {student_id: "student_id", isPresent: "isPresent"},
                render: function (data, type, row, index) {
                    if (data.isPresent === 1) {
                        html = '<input type="radio"  style="width: 20px;height: 20px" checked name="attend' + data.student_id + '" id="attend' + data.student_id + '" value="Present"/>'
                    } else {
                        html = '<input type="radio"  style="width: 20px;height: 20px" name="attend' + data.student_id + '" id="attend' + data.student_id + '" value="Present"/>'
                    }
                    return html;
                }
            },
            {
                data: {student_id: "student_id", isPresent: "isPresent"},
                render: function (data, type, row, index) {
                    if (data.isPresent === 0) {
                        html = '<input type="radio"  style="width: 20px;height: 20px" checked name="attend' + data.student_id + '" id="attend' + data.student_id + '" value="Absent"/>'
                    } else {
                        html = '<input type="radio"  style="width: 20px;height: 20px" name="attend' + data.student_id + '" id="attend' + data.student_id + '" value="Absent"/>'
                    }
                    return html;
                }
            },
            {
                data: {note: "note", student_id: "student_id"},
                width: "250px",
                render: function (data, type, row, index) {
                    html = '<textarea placeholder="Ghi chú..." style="resize: none" name="note' + data.student_id + '" id="note' + data.student_id + '" cols="30" rows="2" class="form-control">' + data.note + '</textarea>'
                    return html;
                }
            },
            {
                data: {attendance_id: "attendance_id", student_id: "student_id"},
                render: function (data, type, row, index) {
                    html = '<input type="input" name="attendId' + data.student_id + '" id="attendId' + data.student_id + '" value="' + data.attendance_id + '"/>'
                    return html;
                },
                visible: false
            }
        ],
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
        }
    });
}
