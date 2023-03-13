$(() => {
    $(".select2").select2({
        theme: "bootstrap4",
        dropdownCssClass: "f-13",
    });

    $("#listAttendance").DataTable({
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
                "targets": [1, 2, 3, 4],
                "orderable": false,
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Date"},
            {title: "Slot"},
            {title: "Teacher"},
            {title: "Class"},
            {title: "Status"},
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

var DataTable = (response) => {
    $("#listAttendance").DataTable({
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
                "targets": [2, 3, 4],
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
                data: "date",
                render: function (data, type, row, index) {
                    let formatDate = data.split("-");
                    return formatDate[2] + "/" + formatDate[1] + "/" + formatDate[0];
                }
            },
            {
                data: "slot",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: "teacher_name",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: "class_name",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: "status",
                render: function (data, type, row, index) {
                    if (data === "1") {
                        data = `<span class="text-success">Present</span>`;
                    } else if (data === "0") {
                        data = `<span class="text-danger">Absent</span>`;
                    } else {
                        data = `<span>Future</span>`;
                    }
                    return data;
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

var OnChangeSubject = () => {
    let subjectId = $("#listSubject :selected").val();
    let studentId = $("#currentStudentId").val();
    if (subjectId !== "") {
        $.ajax({
            url: "/student/attendance/findAttendance",
            method: "POST",
            data: {
                "subject_id": subjectId,
                "student_id": studentId
            },
            success: (response) => {
                let table;
                if ($.fn.dataTable.isDataTable('#listAttendance')) {
                    table = $('#listAttendance').DataTable();
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
                    if ($.fn.dataTable.isDataTable('#listAttendance')) {
                        table = $('#listAttendance').DataTable();
                        table.clear().draw();
                    }
                    toastr.error(data.responseText);
                }
            }
        });
    } else {
        let table;
        if ($.fn.dataTable.isDataTable('#listAttendance')) {
            table = $('#listAttendance').DataTable();
            table.clear().draw();
        }
    }
}

var OnViewAttendance = (subjectId) => {
    let studentId = $("#currentStudentId").val();
    $.ajax({
        url: "/student/attendance/findAttendance",
        method: "POST",
        data: {
            "subject_id": subjectId,
            "student_id": studentId
        },
        success: (response) => {
            let table;
            $("#student-attendance-model").modal("show");
            if ($.fn.dataTable.isDataTable('#listAttendance')) {
                table = $('#listAttendance').DataTable();
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
                if ($.fn.dataTable.isDataTable('#listAttendance')) {
                    table = $('#listAttendance').DataTable();
                    table.clear().draw();
                }
                toastr.error(data.responseText);
            }
        }
    });
}
