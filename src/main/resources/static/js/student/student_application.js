$(() => {
    $("#listApplication").DataTable({
        searching: false,
        info: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollCollapse: true,
        scrollY: '300px',
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
        }, columnDefs: [
            {
                targets: [2, 3, 4],
                orderable: false
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Send Date"},
            {title: "Note"},
            {title: "Status"},
            {title: "Message"}
        ]
    });
    OnGetApplicationIndex();
});

var DataTable = (response) => {
    $("#listApplication").DataTable({
        data: response,
        searching: false,
        info: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollCollapse: true,
        scrollY: '300px',
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
        }, columnDefs: [
            {
                targets: [2, 3, 4],
                orderable: false
            },
            {
                targets: "_all",
                "className": "text-center"
            }
        ],
        columns: [
            {
                render: function (data, type, row, index) {
                    return index.row + 1;
                }
            },
            {
                data: "sendDate",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: "note",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                data: "status",
                render: function (data, type, row, index) {
                    var html = "";
                    if (data.toLowerCase() === "pending") {
                        html = '<span class="text-warning">' + data + '</span>'
                    } else if (data.toLowerCase() === "discard") {
                        html = '<span class="text-danger">' + data + '</span>'
                    } else {
                        html = '<span class="text-success">' + data + '</span>'
                    }
                    return html;
                }
            },
            {
                data: "responseNote",
                render: function (data, type, row, index) {
                    return data;
                }
            }
        ]
    });
}

var OnGetApplicationIndex = () => {
    $.ajax({
        url: "/student/application/getApplicationByStudent",
        method: "GET",
        success: (response) => {
            if ($.fn.dataTable.isDataTable('#listApplication')) {
                let table = $('#listApplication').DataTable();
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
    })
}

var OnSendApplication = async () => {
    var app_type = $("#appType").val();
    var student_id = $("#student_id").val();
    var note = $("#note").val();
    var app_type_file = $("#app-type-file").get(0).files[0];
    if (app_type !== "") {
        if (note !== "") {
            if ($("#app-type-file").get(0).files.length > 0) {
                var fileExtension = GetExtension(app_type_file.name);
                if (fileExtension === 'docx') {
                    var application = {
                        "sendDate": FormatHelper("date", new Date(), "dd/mm/yyyy"),
                        "note": note,
                        "file": await toBase64(app_type_file),
                        "status": "PENDING",
                        "studentId": student_id,
                        "applicationTypeId": app_type
                    }
                    $.ajax({
                        url: "/student/application/sendApplication",
                        contentType: "application/json",
                        method: "POST",
                        data: JSON.stringify(application),
                        success: () => {
                            toastr.success("Send application success");
                            $("#app-modal").modal("hide");
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
                                        location.href = "/dashboard/login";
                                    }
                                });
                            } else {
                                toastr.error(data.responseText);
                            }
                        }
                    });
                } else {
                    $("#error_file").html("Please choose docx file");
                }
            } else {
                $("#error_file").html("Please choose file to send");
            }
        } else {
            $("#error_note").html("Please enter note");
        }
    } else {
        $("#error_app_type").html("Please choose application type")
    }

}

var OnChangeAppType = () => {
    var appType = $("#appType").val();
    if (appType !== "") {
        $("#error_app_type").html("");
        $("#appTypeDownload").removeClass("d-none");
        $.ajax({
            url: "/student/application/get_one_app_type/" + appType,
            method: "GET",
            contentType: "application/json",
            success: async (data) => {
                $("#appTypeDownload").attr("href", (await base64ToWord(data.file)).url);
                $("#appTypeDownload").attr("download", data.name);
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
                            location.href = "/dashboard/login";
                        }
                    });
                } else {
                    toastr.error("Don't find any records");
                    $("#appType").val("").trigger("change");
                }
            }
        });
    } else {
        $("#error_app_type").html("Please choose application type");
        $("#appTypeDownload").addClass("d-none");
    }
}

var OnChangeValidate = (id) => {
    switch (id) {
        case "note":
            if ($("#note").val() !== "") {
                $("#error_note").html("");
            } else {
                $("#error_note").html("Please enter note");
            }
            break;
    }
}
