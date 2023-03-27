$(() => {
    $("#create-form").validate({
        rules: {
            create_subject_code_validate: {
                required: true
            },
            create_subject_name_validate: {
                required: true
            },
            create_fee_validate: {
                required: true,
            },
            create_slot_validate: {
                required: true,
                min: 8,
                max: 15
            },
            create_course_validate: {
                required: true
            }
        },
        messages: {
            create_subject_code_validate: {
                required: "Please enter subject code"
            },
            create_subject_name_validate: {
                required: "Please enter subject name"
            },
            create_fee_validate: {
                required: "Please enter fee",
            },
            create_slot_validate: {
                required: "Please enter number of slot",
                min: "Slot must be greater than 8",
                max: "Slot must be letter than 15"
            },
            create_course_validate: {
                required: "Please choose course"
            }
        },
    });

    $("#edit-form").validate({
        rules: {
            edit_subject_code_validate: {
                required: true
            },
            edit_subject_name_validate: {
                required: true
            },
            edit_fee_validate: {
                required: true,
            },
            edit_slot_validate: {
                required: true,
                min: 8,
                max: 15
            }
        },
        messages: {
            edit_subject_code_validate: {
                required: "Please enter subject code"
            },
            edit_subject_name_validate: {
                required: "Please enter subject name"
            },
            edit_fee_validate: {
                required: "Please enter fee",
                min: "Slot must be greater than 8",
                max: "Slot must be letter than 15"

            },
            edit_slot_validate: {
                required: "Please enter number of slot"
            }
        },
    });

    $("#subject-table").dataTable({
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

    $(".select2-single").select2({
        theme: "bootstrap4",
        width: "100%",
        dropdownCssClass: "f-13"
    });

    $("#create_major_id").on("change", function () {
        let code = $("#create_major_id").select2('data')[0].text.split("-")[0];
        $("#create_apartment_code").text(code + "-")
    });
    $("#edit_major_id").on("change", function () {
        let code = $("#edit_major_id").select2('data')[0].text.split("-")[0];
        $("#edit_apartment_code").text(code + "-")
    });
});

var OnClickImport = () => {
    $("#fileUpload").trigger("click");
}

var OnSaveExcelData = () => {
    var file = $("#fileUpload").get(0).files[0];
    var formData = new FormData();
    formData.append("file", file);
    $.ajax({
        url: "/dashboard/subject/importExcelData",
        data: formData,
        enctype: "multipart/form-data",
        method: "POST",
        contentType: false,
        processData: false,
        success: (data) => {
            toastr.success(data);
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

var OnEditSubject = (id) => {
    $.ajax({
        url: "/dashboard/subject/findOne/" + id,
        dataType: "json",
        method: "GET",
        success: (obj) => {
            var formatFee = obj.fee.toLocaleString('en-US', {
                valute: "currency"
            });
            var code = obj.subjectCode.split("-");
            $("#edit_id").val(obj.id);
            $("#edit_subject_code").val(code[1]);
            $("#edit_subject_name").val(obj.subjectName);
            $("#edit_fee").val(formatFee);
            $("#edit_slot").val(obj.slot);
            $("#edit_semester_id").val(obj.semesterId).trigger("change");
            $("#edit_major_id").val(obj.majorId).trigger("change");
            $("#edit_apartment_code").text(code[0] + "-");
            $("#subject-edit-modal").modal("show");
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

var OnCreateSubject = () => {
    if ($("#create-form").valid()) {
        var subject_code = $("#create_apartment_code").text() + $("#create_subject_code").val();
        var subject_name = $("#create_subject_name").val();
        var fee = $("#create_fee").val().replace(/,/g, '');
        var slot = $("#create_slot").val();
        var semester_id = $("#create_semester_id").val();
        var major_id = $("#create_major_id").val();
        var subject = {
            "subjectCode": subject_code,
            "subjectName": subject_name,
            "fee": fee,
            "slot": slot,
            "semesterId": semester_id,
            "majorId": major_id
        }
        $.ajax({
            url: "/dashboard/subject/save",
            contentType: "application/json",
            method: "POST",
            data: JSON.stringify(subject),
            success: (data) => {
                toastr.success("Create success");
                $("#subject-modal").modal("hide");
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
    }
}

var OnUpdateSubject = () => {
    if ($("#edit-form").valid()) {
        var id = $("#edit_id").val();
        var subject_code = $("#edit_apartment_code").text() + $("#edit_subject_code").val();
        var subject_name = $("#edit_subject_name").val();
        var fee = $("#edit_fee").val().replace(/,/g, '');
        var slot = $("#edit_slot").val();
        var semester_id = $("#edit_semester_id").val();
        var major_id = $("#edit_major_id").val();
        var subject = {
            "id": id,
            "subjectCode": subject_code,
            "subjectName": subject_name,
            "fee": fee,
            "slot": slot,
            "semesterId": semester_id,
            "majorId": major_id
        };
        $.ajax({
            url: "/dashboard/subject/update",
            contentType: "application/json",
            method: "post",
            data: JSON.stringify(subject)
            , success: (data) => {
                toastr.success("Update success");
                $("#subject-edit-modal").modal("hide");
                setTimeout(() => {
                    location.reload();
                }, 2000);
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
    }
}

var OnDeleteSubject = (id) => {
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
                url: "/dashboard/subject/delete/" + id,
                method: "GET",
                success: () => {
                    toastr.success("Delete success");
                    setTimeout(() => {
                        location.reload();
                    }, 2000)
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

var OnFormatCurrency = (obj) => {
    if (obj.id === "create_fee") {
        var fee = $("#create_fee").val();
        if (fee.replaceAll(",", "") > 10000000) {
            fee = "9999999"
            $("#create_fee").val(fee.replace(/\D/g, '')
                .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
        } else {
            $("#create_fee").val(fee.replace(/\D/g, '')
                .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
        }
    } else {
        var fee = $("#edit_fee").val();
        if (fee.replaceAll(",", "") > 10000000) {
            fee = "9999999"
            $("#edit_fee").val(fee.replace(/\D/g, '')
                .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
        } else {
            $("#edit_fee").val(fee.replace(/\D/g, '')
                .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
        }
    }
}
