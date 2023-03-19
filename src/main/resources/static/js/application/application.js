$(() => {
    $(".select2bs4").select2({
        theme: "bootstrap4"
    });

    $("#app-type-table").DataTable({
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
        }
    });

    $("#app-table").DataTable({
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
        }
    });

    $("#app-modal").on("hidden.bs.modal", () => {
        $("#appType").val("").trigger("change");
    });
});

var OnChangeStatus = () => {
    var status = $("#edit-status").val();
    if (status === "DISCARD") {
        $("#edit-note").val("Please download the template file then fill all information and send it");
    } else if (status === "APPROVED") {
        $("#edit-note").val("After 7 days go to finance room get the application");
    } else {
        $("#edit-note").val("");
    }
}

var OnDeleteApplicationType = (id) => {
    Swal.fire({
        title: 'Do you want to delete this ?',
        text: "When you confirm data can't recover!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: "Cancel",
        confirmButtonText: 'Confirm!'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: "/dashboard/applicationType/deleteAppType/" + id,
                method: "POST",
                success: (data) => {
                    toastr.success("Delete data success");
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
        }
    });
}

var OnAppDownload = (id) => {
    $.ajax({
        url: "/dashboard/application/get_one_app/" + id,
        method: "GET",
        contentType: "application/json",
        success: async (data) => {
            var downloadLink = document.createElement("a");
            downloadLink.href = (await base64ToWord(data.file)).url;
            $.ajax({
                url: "/dashboard/applicationType/get_one_app_type/" + data.applicationTypeId,
                method: "GET",
                contentType: "application/json",
                success: (data) => {
                    downloadLink.download = data.name;
                    downloadLink.click();
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
}

var OnEditApplication = (id) => {
    $.ajax({
        url: "/dashboard/application/get_one_app/" + id,
        method: "GET",
        contentType: "application/json",
        success: (data) => {
            $("#app-edit-id").val(data.id);
            $("#app-edit-modal").modal("show");
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
}

var OnGetOneApplication = async (id) => {
    return await fetch("/dashboard/application/get_one_app/" + id)
        .then(res => res.json())
        .then((data) => {
            return data
        });
}

var OnUpdateApplication = async () => {
    var id = $("#app-edit-id").val();
    var status = $("#edit-status").val();
    var note = $("#edit-note").val();
    var response_date = FormatHelper("DATE", new Date(), "dd/mm/yyyy");
    let data = await OnGetOneApplication(id);
    var application = {
        "id": id,
        "sendDate": data.sendDate,
        "note": data.note,
        "file": data.file,
        "status": status,
        "studentId": data.studentId,
        "applicationTypeId": data.applicationTypeId,
        "responseNote": note,
        "responseDate": response_date
    }
    $.ajax({
        url: "/dashboard/application/update",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(application),
        success: (data) => {
            toastr.success("Update success");
            $("#app-edit-modal").modal("hide");
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
}

var OnCreateApplicationType = async () => {
    var appTypeName = $("#app-type-name").val();
    var appTypeFile = $("#app-type-file").get(0).files[0];
    var formData = new FormData();
    var base64String = await toBase64(appTypeFile);
    formData.append("name", appTypeName);
    formData.append("file", appTypeFile);
    formData.append("base64String", base64String);
    var fileExtension = GetExtension(appTypeFile.name);
    if (fileExtension === "docx") {
        $.ajax({
            url: "/dashboard/applicationType/save_app_type",
            data: formData,
            method: "POST",
            contentType: false,
            processData: false,
            enctype: "multipart/form-data",
            success: (data) => {
                $("#create-app-type-modal").modal("hide");
                toastr.success("Create success");
                setTimeout(() => {
                    location.reload();
                }, 1500);
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
        $("#error-file").html("Please choose the .docx file");
    }
}

var OnCreateApplication = async () => {
    var app_type = $("#appType").val();
    var student_id = 6;
    var note = $("#note").val();
    var app_type_file = $("#app-type-file").get(0).files[0];
    var application = {
        "sendDate": FormatHelper("date", new Date(), "dd/mm/yyyy"),
        "note": note,
        "file": await toBase64(app_type_file),
        "status": "PENDING",
        "studentId": student_id,
        "applicationTypeId": app_type
    }
    $.ajax({
        url: "/dashboard/application/save_app",
        contentType: "application/json",
        method: "POST",
        data: JSON.stringify(application),
        success: () => {
            toastr.success("Create success");
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
}

var OnViewApplication = async (base64String) => {
    var fileUrl = (await base64ToWord(base64String)).url;
    var file = await urltoFile(fileUrl, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    PreviewWordDoc(file);
    $("#show-document").modal("show");
}

var PreviewWordDoc = (file) => {
    if (file != null) {
        var docxOptions = Object.assign(docx.defaultOptions, {
            useMathMLPolyfill: true
        });
        var container = document.querySelector("#word_container");
        docx.renderAsync(file, container, null, docxOptions);
    }
}
