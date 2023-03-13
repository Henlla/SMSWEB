$(() => {
    $(".select2").select2({
        theme: "bootstrap4",
        dropdownCssClass: "f-13"
    });

    $("#listStudent").DataTable({
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

    $("#listEditStudent").DataTable({
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

});

const DataTable = (response) => {
    let html = "";
    $("#listStudent").DataTable({
        data: response,
        paging: false,
        searching: false,
        info: false,
        autoWidth: false,
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
                data: "classStudentByStudent",
                render: function (data, type, row, index) {
                    html = '<img style="width: 50px;height: 50px" src="' + data.studentByProfile.avartarUrl + '"/>'
                    return html;
                }
            },
            {
                data: "classStudentByStudent",
                render: function (data, type, row, index) {
                    return data.studentByProfile.firstName + " " + data.studentByProfile.lastName;
                }
            },
            {
                data: "studentId",
                render: function (data, type, row, index) {
                    html = '<input type="radio" style="width: 20px;height: 20px" checked name="attend' + data + '" id="attend' + data + '" value="Present"/>'
                    return html;
                }
            },
            {
                data: "studentId",
                render: function (data, type, row, index) {
                    html = '<input type="radio" style="width: 20px;height: 20px" name="attend' + data + '" id="attend' + data + '" value="Absent"/>'
                    return html;
                }
            },
            {
                data: "studentId",
                width: "250px",
                render: function (data, type, row, index) {
                    html = '<textarea placeholder="Ghi chú..." style="resize: none" name="note' + data + '" id="note' + data + '" cols="30" rows="2" class="form-control"></textarea>'
                    return html;
                }
            },
            {
                data: "studentId",
                render: function (data) {
                    html = '<input type="radio" style="width: 20px;height: 20px" checked name="attendId' + data + '" id="attendId' + data + '" value=""/>'
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

const DataTableEdit = (response) => {
    let html = "";
    $("#listEditStudent").DataTable({
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

const OnSubmitAttendance = () => {
    Swal.fire({
        title: 'Do you want to send this ?',
        text: "Check carefully before update!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: "Cancel",
        confirmButtonText: 'Confirm!'
    }).then((result) => {
        if (result.isConfirmed) {
            let table = $("#listStudent").DataTable();
            let data = table.rows().data().toArray();
            let formData = [];
            let classId = $("#classId").val().split("-");
            let date = FormatHelper("DATE", new Date(classId[2] + "-" + classId[3] + "-" + classId[4]), "yyyy-mm-dd")
            for (const item of data) {
                let checked = $("#attend" + item.studentId + ":checked").val();
                let note = $("#note" + item.studentId).val();
                let attendanceModel = {
                    "student_id": item.studentId,
                    "note": note,
                    "status": checked,
                    "slot": classId[1]
                }
                formData.push(attendanceModel);
            }
            $.ajax({
                url: "/teacher/attendance/submitAttendance",
                method: "POST",
                data: {
                    "attendModel": JSON.stringify(formData),
                    "classId": classId[0],
                    "date": date,
                    "slot": classId[1]
                },
                success: (data) => {
                    toastr.success("Điểm danh thành công");
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                },
                error: (data) => {
                    if (data.responseText.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'Hết phiên đăng nhập vui lòng đăng nhập lại',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Đồng ý',
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
};

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
            let table = $("#listEditStudent").DataTable();
            let data = table.rows().data().toArray();
            let formData = [];
            let classId = $("#classId").val().split("-");
            let dateFormat = FormatHelper("DATE", new Date(classId[2] + "-" + classId[3] + "-" + classId[4]), "yyyy-mm-dd");
            for (const item of data) {
                let checked = $("#attend" + item.student_id + ":checked").val();
                let note = $("#note" + item.student_id).val();
                let attendanceModel = {
                    "attendance_id": item.attendance_id,
                    "student_id": item.student_id,
                    "note": note,
                    "status": checked,
                    "slot": classId[1]
                }
                formData.push(attendanceModel);
            }
            $.ajax({
                url: "/teacher/attendance/updateAttendance",
                method: "POST",
                data: {
                    "attendModel": JSON.stringify(formData),
                    "classId": classId[0],
                    "date": dateFormat,
                    "slot": classId[1]
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

const OnTakeAttendance = (data) => {
    $("#classId").val(data);
    let classId = data.split("-");
    $.ajax({
        url: "/teacher/attendance/listStudentInClass/" + classId[0],
        method: "GET",
        success: (response) => {
            $("#attendance-modal").modal("show");
            let table;
            if ($.fn.dataTable.isDataTable('#listStudent')) {
                table = $('#listStudent').DataTable();
                table.clear().draw();
                table.destroy();
                DataTable(response);
            } else {
                DataTable(response);
            }
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

const OnEditAttendance = (obj) => {
    $("#classId").val(obj);
    let classId = obj.split("-");
    let dateFormat = FormatHelper("DATE", new Date(classId[2] + "-" + classId[3] + "-" + classId[4]), "yyyy-mm-dd");
    $.ajax({
        url: "/teacher/attendance/findAttendanceByDate",
        method: "POST",
        data: {
            "date": dateFormat,
            "classId": classId[0],
            "slot": classId[1]
        },
        success: (response) => {
            let table;
            $("#edit-attendance-modal").modal("show");
            if ($.fn.dataTable.isDataTable('#listEditStudent')) {
                table = $('#listEditStudent').DataTable();
                table.clear().draw();
                table.destroy();
                DataTableEdit(response);
            } else {
                DataTableEdit(response);
            }
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
                if ($.fn.dataTable.isDataTable('#listEditStudent')) {
                    table = $('#listEditStudent').DataTable();
                    table.clear().draw();
                }
            }
        }
    });
}
