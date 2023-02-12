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

    $("#create-form").validate({
        rules: {
            create_major_code_validate: {
                required: true
            },
            create_major_name_validate: {
                required: true
            }
        }, messages: {
            create_major_code_validate: {
                required: "Vui lòng nhập mã ngành"
            },
            create_major_name_validate: {
                required: "Vui lòng nhập tên ngành"
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
            }
        }, messages: {
            edit_major_code_validate: {
                required: "Vui lòng nhập mã ngành"
            },
            edit_major_name_validate: {
                required: "Vui lòng nhập tên ngành"
            }
        }
    });

    $("#major-table").DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
        scrollCollapse: true,
        scrollY: '300px',
        // pagingType:"full_numbers",
        "language": {
            "decimal":        "",
            "emptyTable":     "Không có dữ liệu",
            "info":           "",
            "infoEmpty":      "",
            "infoFiltered":   "",
            "infoPostFix":    "",
            "thousands":      ",",
            "lengthMenu":     "Hiển thị _MENU_ dữ liệu",
            "loadingRecords": "Đang tìm...",
            "processing":     "",
            "search":         "Tìm kiếm:",
            "zeroRecords":    "Không tìm thấy dữ liệu",
            "paginate": {
                "first":      "Trang đầu",
                "last":       "Trang cuối",
                "next":       "Trang kế tiếp",
                "previous":   "Trang trước"
            },
            "aria": {
                "sortAscending":  ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            }
        }
    });
});

var OnCreateMajor = () => {
    if ($("#create-form").valid()) {
        var major_code = $("#create_major_code").val();
        var major_name = $("#create_major_name").val();
        var formData = {
            "majorCode": major_code,
            "majorName": major_name
        }
        $.ajax({
            url: "/dashboard/major/save",
            contentType: "application/json",
            dataType: "json",
            method: "post",
            data: JSON.stringify(formData),
            success: (data) => {
                location.reload();
            }
        });
    }
}

var OnEditMajor = (id) => {
    $.ajax({
        url: "/dashboard/major/findOne/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            $("#edit_major_id").val(data.id);
            $("#edit_major_code").val(data.majorCode);
            $("#edit_major_name").val(data.majorName);
            $("#edit-major-modal").modal("show");
        }, error: (data) => {
            alert("Thất bại");
        }
    });
}

var OnUpdateMajor = () => {
    var formData = {
        "id": $("#edit_major_id").val(),
        "majorCode": $("#edit_major_code").val(),
        "majorName": $("#edit_major_name").val(),
    }
    $.ajax({
        url: "/major/update",
        dataType: "json",
        contentType:"application/json",
        method:"POST",
        data:JSON.stringify(formData),
        success: () => {
            alert("Tạo thành công");
            setTimeout(()=>{
                location.reload();
            },2000);
        }
    });
}

var OnClickImport = () =>{
    $("#fileUpload").trigger("click");
}

var OnSaveExcelData = () =>{
    var file = $("#fileUpload").get(0).files[0];
    var formData = new FormData();
    formData.append("file",file);
    $.ajax({
        url:"/dashboard/major/import-excel",
        data:formData,
        method:"POST",
        processData:false,
        contentType:false,
        enctype:"multipart/form-data",
        success:()=>{
            toastr.success("Đỗ dữ liệu thành công");
            setTimeout(()=>{
                location.reload();
            },1500);
        }
    });
}