$(() => {
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
    })
    $("#major-table").dataTable();
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
            url: "/major/post",
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
            console.log(data);
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
            location.reload();
        }
    });
}