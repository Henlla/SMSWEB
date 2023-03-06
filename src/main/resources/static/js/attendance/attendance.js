$(() => {
    $(".select2").select2({
        theme: "bootstrap4",
    });
    $("#attendance_date").datetimepicker({
        format: "DD/MM/YYYY"
    });
    $("#listStudent").DataTable({
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
            {title: "STT"},
            {title: "Avatar"},
            {title: "Tên sinh viên"},
            {title: "Có mặt"},
            {title: "Vắng mặt"},
            {title: "Ghi chú"},
            {title: "Attendance", visible: false}
        ]
    });
});

const OnSubmitAttendance = () => {
    let table = $("#listStudent").DataTable();
    let data = table.rows().data().toArray();
    if (data.length > 0) {
        let formData = [];
        let classId = $("#classList").val();
        let date = FormatHelper("DATE", new Date(), "yyyy-mm-dd")
        for (const item of data) {
            let checked = $("#attend" + item.studentId + ":checked").val();
            let note = $("#note" + item.studentId).val();
            let attendanceModel = {
                "student_id": item.studentId,
                "note": note,
                "status": checked,
            }
            formData.push(attendanceModel);
        }
        $.ajax({
            url: "/teacher/attendance/submitAttendance",
            method: "POST",
            data: {
                "attendModel": JSON.stringify(formData),
                "classId": classId,
                "date": date
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
                    toastr.error("Điểm danh thất bại");
                }
            }
        });
    } else {
        $("#btnOnsubmit").addClass("d-none");
    }
};

const OnChangeClass = () => {
    let classId = $("#classList").val();
    if (classId !== "") {
        $.ajax({
            url: "/teacher/attendance/listStudentInClass/" + classId,
            method: "GET",
            success: (response) => {
                let html = "";
                let table;
                if ($.fn.dataTable.isDataTable('#listStudent')) {
                    table = $('#listStudent').DataTable();
                    table.clear().draw();
                    table.destroy();
                }
                table = $("#listStudent").DataTable({
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
                                html = '<input type="radio" style="width: 20px;height: 20px" name="attend' + data + '" id="attend' + data + '" value="Present"/>'
                                return html;
                            }
                        },
                        {
                            data: "studentId",
                            render: function (data, type, row, index) {
                                html = '<input type="radio" style="width: 20px;height: 20px" checked name="attend' + data + '" id="attend' + data + '" value="Absent"/>'
                                return html;
                            }
                        },
                        {
                            data: "studentId",
                            render: function (data) {
                                html = '<textarea placeholder="Ghi chú..." style="resize: none" name="note' + data + '" id="note' + data + '" cols="30" rows="3" class="form-control"></textarea>'
                                return html;
                            }
                        },
                        {
                            data: "studentId",
                            render: function (data) {
                                html = '<input type="radio" style="width: 20px;height: 20px" checked name="attendId' + data + '" id="attendId' + data + '" value=""/>'
                                return html;
                            },
                            visible:false
                        }
                    ]
                });
                $("#btnOnsubmit").removeClass("d-none");
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
                    toastr.error("Không tìm thấy dữ liệu");
                }
            }
        });
    } else {
        let table;
        $("#btnOnsubmit").addClass("d-none");
        if ($.fn.dataTable.isDataTable('#listStudent')) {
            table = $('#listStudent').DataTable();
            table.clear().draw();
        }
    }
};

$("#attendance_date").on("change.datetimepicker", function () {
    let classId = $("#classList").val();
    let date = $("#attendanceDate").val().split("/");
    let dateFormat = FormatHelper("DATE", new Date(date[2] + "/" + date[1] + "/" + date[0]), "yyyy-mm-dd");
    if (classId !== "") {
        $.ajax({
            url: "/teacher/attendance/findAttendanceByDate",
            method: "POST",
            data: {
                "date": dateFormat,
                "classId": classId
            },
            success: (response) => {
                console.log(response);
                let html = "";
                let table;
                if ($.fn.dataTable.isDataTable('#listStudent')) {
                    table = $('#listStudent').DataTable();
                    table.clear().draw();
                    table.destroy();
                }
                table = $("#listStudent").DataTable({
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
                            render: function (data, type, row, index) {
                                html = '<textarea placeholder="Ghi chú..." style="resize: none" name="note' + data.student_id + '" id="note' + data.student_id + '" cols="30" rows="3" class="form-control">' + data.note + '</textarea>'
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
                    ]
                });
                $("#btnOnsubmit").removeClass("d-none");
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
                    if ($.fn.dataTable.isDataTable('#listStudent')) {
                        table = $('#listStudent').DataTable();
                        table.clear().draw();
                    }
                }
            }
        });
    } else {
        alert("Vui lòng chọn lớp");
        $("#attendanceDate").val(null);
        let table;
        $("#btnOnsubmit").addClass("d-none");
        if ($.fn.dataTable.isDataTable('#listStudent')) {
            table = $('#listStudent').DataTable();
            table.clear().draw();
        }
    }
});

const OnChangeClass2 = () => {
    let classId = $("#classList").val();
    let date = $("#attendanceDate").val().split("/");
    let dateFormat = FormatHelper("DATE", new Date(date[2] + "/" + date[1] + "/" + date[0]), "yyyy-mm-dd");
    if (classId !== "") {
        $.ajax({
            url: "/teacher/attendance/findAttendanceByDate",
            method: "POST",
            data: {
                "date": dateFormat,
                "classId": classId
            },
            success: (response) => {
                console.log(response);
                let html = "";
                let table;
                if ($.fn.dataTable.isDataTable('#listStudent')) {
                    table = $('#listStudent').DataTable();
                    table.clear().draw();
                    table.destroy();
                }
                table = $("#listStudent").DataTable({
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
                            render: function (data, type, row, index) {
                                html = '<textarea placeholder="Ghi chú..." style="resize: none" name="note' + data.student_id + '" id="note' + data.student_id + '" cols="30" rows="3" class="form-control">' + data.note + '</textarea>'
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
                    ]
                });
                $("#btnOnsubmit").removeClass("d-none");
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
                    if ($.fn.dataTable.isDataTable('#listStudent')) {
                        table = $('#listStudent').DataTable();
                        table.clear().draw();
                    }
                }
            }
        });
    } else {
        $("#attendanceDate").val(null);
        let table;
        $("#btnOnsubmit").addClass("d-none");
        if ($.fn.dataTable.isDataTable('#listStudent')) {
            table = $('#listStudent').DataTable();
            table.clear().draw();
        }
    }
}

const OnUpdateAttendance = () => {
    let table = $("#listStudent").DataTable();
    let data = table.rows().data().toArray();
    if (data.length > 0) {
        let formData = [];
        let classId = $("#classList").val();
        let date = $("#attendanceDate").val().split("/");
        let dateFormat = FormatHelper("DATE", new Date(date[2] + "/" + date[1] + "/" + date[0]), "yyyy-mm-dd");
        for (const item of data) {
            let checked = $("#attend" + item.student_id + ":checked").val();
            let note = $("#note" + item.student_id).val();
            let attendanceModel = {
                "attendance_id": item.attendance_id,
                "student_id": item.student_id,
                "note": note,
                "status": checked,
            }
            formData.push(attendanceModel);
        }
        $.ajax({
            url: "/teacher/attendance/updateAttendance",
            method: "POST",
            data: {
                "attendModel": JSON.stringify(formData),
                "classId": classId,
                "date": dateFormat
            },
            success: (data) => {
                toastr.success("Cập nhật thành công");
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
                    toastr.error("Cập nhật thất bại");
                }
            }
        });
    } else {
        $("#btnOnsubmit").addClass("d-none");
    }
}
