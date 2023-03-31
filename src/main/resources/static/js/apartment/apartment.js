$(() => {
    OnGetApartment();
    $("#create-form").validate({
        rules: {
            create_apartment_code_validate: {
                required: true
            },
            create_apartment_name_validate: {
                required: true
            }
        }, messages: {
            create_apartment_code_validate: {
                required: "Please enter major code"
            },
            create_apartment_name_validate: {
                required: "Please enter major name"
            }
        }
    });
});

const DataTable = (response) => {
    $("#apartmentTable").DataTable({
        info: false,
        data: response,
        columnDefs: [
            {targets:[1,2],orderable:false}
        ],
        columns: [
            {
                title: "STT",
                render: function (data, type, row, index) {
                    return index.row + 1;
                }
            },
            {
                title: "Major Code",
                data: "apartmentCode",
                render: function (data, type, row, index) {
                    return data;
                }
            },
            {
                title: "Major Name",
                data: "apartmentName",
                render: function (data, type, row, index) {
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

const OnGetApartment = () => {
    $.ajax({
        url: "/dashboard/apartment/findAllApartment",
        method: "GET",
        success: (response) => {
            console.log(response);
            if ($.fn.dataTable.isDataTable('#apartmentTable')) {
                table = $('#apartmentTable').DataTable();
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
                        location.href = "/dashboard/login";
                    }
                });
            } else {
                toastr.error(data.responseText);
            }
        }
    })
}

const OnCreateApartment = () => {
    if($("#create-form").valid()){
        let code = $("#create_major_code").val();
        let name = $("#create_major_name").val();
        let data = {
            "apartmentCode": code,
            "apartmentName": name
        }
        $.ajax({
            url: "/dashboard/apartment/createApartment",
            contentType: "application/json",
            method: "POST",
            data: JSON.stringify(data),
            success: (response) => {
                toastr.success("Create success");
                setTimeout(() => {
                    location.href = "/dashboard/apartment/index";
                }, 1500)
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