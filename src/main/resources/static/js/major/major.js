$(() => {
    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-top-right",
        "preventDuplicates": true,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "3000",
        "extendedTimeOut": "600",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    $(".apartment_select").select2({
        theme: "bootstrap4",
        placeholder: "Choose major",
        ajax: {
            url: "/dashboard/major/findApartment",
            method: "GET",
            processResults: function (response) {
                let dataArray = [];
                for (const dataKey of response) {
                    let data = {
                        "id": dataKey.id,
                        "text": dataKey.apartmentCode
                    }
                    dataArray.push(data);
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

    $("#create-form").validate({
        rules: {
            create_major_code_validate: {
                required: true
            },
            create_major_name_validate: {
                required: true
            },
            create_apartment_validate:{
                required:true
            }
        }, messages: {
            create_major_code_validate: {
                required: "Please enter major code"
            },
            create_major_name_validate: {
                required: "Please enter major name"
            },
            create_apartment_validate:{
                required:"Please choose major"
            }
        }
    });

    $("#edit-form").validate({
        rules: {
            edit_major_code_validate: {
                required: true
            },
            edit_major_name_validate: {
                required: true
            },
            edit_apartment_validate:{
                required:true
            }
        }, messages: {
            edit_major_code_validate: {
                required: "Please enter major code"
            },
            edit_major_name_validate: {
                required: "Please enter major name"
            },
            edit_apartment_validate:{
                required:"Please choose major"
            }
        }
    });

    $("#major-table").DataTable({
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

    $("#create-major-modal").on("hidden.bs.modal",function(){
        $(this).find('#create-form')[0].reset();
        $("#apartment").val(null).trigger("change");
    })
});

var OnCreateMajor = () => {
    if ($("#create-form").valid()) {
        var major_code = $("#create_major_code").val();
        var major_name = $("#create_major_name").val();
        var apartment_id = $("#apartment").val();
        var major = {
            "majorCode": major_code,
            "majorName": major_name,
            "apartmentId" : apartment_id
        }
        $.ajax({
            url: "/dashboard/major/save",
            contentType: "application/json",
            method: "post",
            data: JSON.stringify(major),
            success: (data) => {
                toastr.success("Create success");
                $("#create-major-modal").modal("hide");
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
}

var OnEditMajor = (id) => {
    $.ajax({
        url: "/dashboard/major/findOne/" + id,
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            $("#edit-major-modal").modal("show");
            $("#edit_major_id").val(data.id);
            $("#edit_major_code").val(data.majorCode);
            $("#edit_major_name").val(data.majorName);
            $("#edit_apartment").val(data.apartmentId).trigger("change");
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

var OnUpdateMajor = () => {
    var major = {
        "id": $("#edit_major_id").val(),
        "majorCode": $("#edit_major_code").val(),
        "majorName": $("#edit_major_name").val(),
        "apartmentId" : $("#edit_apartment").val()
    }
    $.ajax({
        url: "/dashboard/major/update",
        contentType: "application/json",
        method: "POST",
        data: JSON.stringify(major),
        success: () => {
            toastr.success("Update success");
            $("#edit-major-modal").modal("hide");
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

var OnClickImport = () => {
    $("#fileUpload").trigger("click");
}

var OnDeleteMajor = (id) => {
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
                url: "/dashboard/major/delete/" + id,
                method: "GET",
                success: () => {
                    toastr.success("Delete success");
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
    });
}

var OnSaveExcelData = () => {
    var file = $("#fileUpload").get(0).files[0];
    if (file !== undefined) {
        var formData = new FormData();
        formData.append("file", file);
        $.ajax({
            url: "/dashboard/major/import-excel",
            data: formData,
            method: "POST",
            processData: false,
            contentType: false,
            enctype: "multipart/form-data",
            success: (data) => {
                toastr.success(data);
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
