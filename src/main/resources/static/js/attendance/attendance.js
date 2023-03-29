$(() => {
    let date = $("#date_attendance");

    date.val(moment().format("YYYY-MM-DD"));

    OnFindAttendanceByDate(date.val());

    $("#viewTable").DataTable({
        paging: false,
        info: false,
        autoWidth: false,
        columnDefs: [
            {
                "targets": [0, 1, 2],
                "className": "text-center"
            },
            {
                "targets": [1, 2],
                "orderable": false,
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Class"},
            {title: "Action"},
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

    $(".select2").select2({
        theme: "bootstrap4",
        dropdownCssClass: "f-13"
    });

    date.attr("min", moment().subtract(2, "days").format("YYYY-MM-DD"));

    date.attr("max", moment().format("YYYY-MM-DD"));

    date.on("change", () => {
        let date = $("#date_attendance").val();
        OnFindAttendanceByDate(date);
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
            {title: "Student Card"},
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

    $("#listViewStudent").DataTable({
        paging: false,
        searching: false,
        info: false,
        columnDefs: [
            {
                "targets": [0, 1, 2, 3, 4],
                "className": "text-center",
            },
            {
                "targets": [1, 2],
                "orderable": false,
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Avatar"},
            {title: "Student name"},
            {title: "Status"},
            {title: "Note"}
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

const ViewDatable = (response) => {
    $("#viewTable").DataTable({
        paging: false,
        info: false,
        autoWidth: false,
        data: response,
        columnDefs: [
            {
                "targets": [0, 1, 2],
                "className": "text-center"
            },
            {
                "targets": [1, 2],
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
                data: "class_name",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: {class_id: "class_id", isAttendance: "isAttendance", onTime: "onTime"},
                render: function (data, type, row, index) {
                    let html = "";
                    if (data.isAttendance === 0) {
                        if (data.onTime === 0) {
                            html += `<button onclick="OnTakeAttendance('${data.class_id}')" class="btn btn-primary btn-sm"><i class="fas fa-user-edit"></i></button>`
                        } else {
                            html += `<button onclick="OnTakeAttendance('${data.class_id}')" class="btn btn-primary btn-sm"><i class="fas fa-user-edit"></i></button>`
                        }
                    } else {
                        html += `
                            <button class="btn btn-warning btn-sm" onclick="OnEditAttendance('${data.class_id}')"><i class="fas fa-edit"></i></button>
                            <button class="btn btn-info btn-sm" onclick="OnViewAttendance('${data.class_id}')"><i class="fas fa-eye"></i></button>
                        `
                    }
                    return html;
                }
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

const DataTable = (response) => {
    let html = "";
    console.log(response);
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
                    return data.studentCard;
                }
            }
            ,
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

const DataTableView = (response) => {
    let html = "";
    $("#listViewStudent").DataTable({
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
                "targets": [1, 2],
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
                        html = '<span class="text-success">Present</span>'
                    } else {
                        html = '<span class="text-danger">Absent</span>'
                    }
                    return html;
                }
            }
            ,
            {
                data: {note: "note", student_id: "student_id"},
                width: "250px",
                render: function (data, type, row, index) {
                    html = '<textarea readonly placeholder="Ghi chú..." style="resize: none" name="note' + data.student_id + '" id="note' + data.student_id + '" cols="30" rows="2" class="form-control">' + data.note + '</textarea>'
                    return html;
                }
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
        title: 'Do you want to take attendance ?',
        text: "Check carefully before confirm!",
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
                    toastr.success("Take attendance success");
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                },
                error: (data) => {
                    if (data.responseText.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'End of login session, please login again',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Ok',
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

const OnViewAttendance = (obj) => {
    $("#classId").val(obj);
    let classId = obj.split("-");
    let dateFormat = FormatHelper("DATE", new Date(classId[2] + "-" + classId[3] + "-" + classId[4]), "yyyy-mm-dd");
    $.ajax({
        url: "/teacher/attendance/viewAttendance",
        method: "POST",
        data: {
            "date": dateFormat,
            "classId": classId[0],
            "slot": classId[1]
        },
        success: (response) => {
            let table;
            $("#view-attendance-modal").modal("show");
            if ($.fn.dataTable.isDataTable('#listViewStudent')) {
                table = $('#listViewStudent').DataTable();
                table.clear().draw();
                table.destroy();
                DataTableView(response);
            } else {
                DataTableView(response);
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

const OnFindAttendanceByDate = (date) => {
    $.ajax({
        url: "/teacher/attendance/getAttendanceByDate",
        method: "GET",
        data: {
            date: date
        },
        success: (response) => {
            console.log(response);
            if ($.fn.dataTable.isDataTable('#viewTable')) {
                table = $('#viewTable').DataTable();
                table.clear().draw();
                table.destroy();
                ViewDatable(response);
            } else {
                ViewDatable(response);
            }
        }, error: (data) => {
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
    })
}