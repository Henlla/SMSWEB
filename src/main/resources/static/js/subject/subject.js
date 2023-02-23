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
                required: true
            },
            create_slot_validate: {
                required: true
            }
        },
        messages: {
            create_subject_code_validate: {
                required: "Vui lòng nhập mã môn học"
            },
            create_subject_name_validate: {
                required: "Vui lòng nhập tên môn học"
            },
            create_fee_validate: {
                required: "Vui lòng nhập học phí"
            },
            create_slot_validate: {
                required: "Vui lòng nhập số tiết học"
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
                required: true
            },
            edit_slot_validate: {
                required: true
            }
        },
        messages: {
            edit_subject_code_validate: {
                required: "Vui lòng nhập mã môn học"
            },
            edit_subject_name_validate: {
                required: "Vui lòng nhập tên môn học"
            },
            edit_fee_validate: {
                required: "Vui lòng nhập học phí"
            },
            edit_slot_validate: {
                required: "Vui lòng nhập số tiết học"
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
            "emptyTable": "Không có dữ liệu",
            "info": "",
            "infoEmpty": "",
            "infoFiltered": "",
            "infoPostFix": "",
            "thousands": ",",
            "lengthMenu": "Hiển thị _MENU_ dữ liệu",
            "loadingRecords": "Đang tìm...",
            "processing": "",
            "search": "Tìm kiếm:",
            "zeroRecords": "Không tìm thấy dữ liệu",
            "paginate": {
                "first": "Trang đầu",
                "last": "Trang cuối",
                "next": "Trang kế tiếp",
                "previous": "Trang trước"
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
        success: () => {

        },
        error: () => {

        }
    });
    alert("ok");
}

var OnEditSubject = (id) => {
    $.ajax({
        url: "/dashboard/subject/findOne/" + id,
        dataType: "json",
        method: "GET",
        success: (obj) => {
            console.log(obj);
            var formatFee = obj.fee.toLocaleString('en-US', {
                valute: "currency"
            });
            $("#edit_id").val(obj.id);
            $("#edit_subject_code").val(obj.subjectCode);
            $("#edit_subject_name").val(obj.subjectName);
            $("#edit_fee").val(formatFee);
            $("#edit_slot").val(obj.slot);
            $("#edit_semester_id").val(obj.semesterId).trigger("change");
            $("#edit_major_id").val(obj.majorId).trigger("change");
            $("#subject-edit-modal").modal("show");
        },
        error: (data) => {
            if (data.toLowerCase() === "token expired") {
                alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                setTimeout(() => {
                    location.href = "/dashboard/login";
                }, 2000);
            } else {
                alert("Tìm thất bại");
            }
        }
    });
}

var OnCreateSubject = () => {
    if ($("#create-form").valid()) {
        var subject_code = $("#create_subject_code").val();
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
                alert("Tạo thành công");
                setTimeout(() => {
                    location.reload();
                }, 2000);
            },
            error: (data) => {
                if (data.toLowerCase() === "token expired") {
                    alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                    setTimeout(() => {
                        location.href = "/dashboard/login";
                    }, 2000);
                } else {
                    alert("Tạo mới thất bại");
                }
            }
        });
    }
}

var OnUpdateSubject = () => {
    if ($("#edit-form").valid()) {
        var id = $("#edit_id").val();
        var subject_code = $("#edit_subject_code").val();
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
            dataType: "json",
            contentType: "application/json",
            method: "post",
            data: JSON.stringify(subject)
            , success: (data) => {
                alert("Cập nhật thành công");
                setTimeout(() => {
                    location.reload();
                }, 2000);
            }, error: (data) => {
                if (data.toLowerCase() === "token expired") {
                    alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                    setTimeout(() => {
                        location.href = "/dashboard/login";
                    }, 2000);
                } else {
                    alert("Cập nhật thất bại");
                }
            }
        });
    }
}

var OnDeleteSubject = (id) =>{
    var isConfirm = confirm("Bạn muốn xóa dữ liệu ?");
    if(isConfirm){
        $.ajax({
            url: "/dashboard/subject/delete/" + id,
            method:"GET",
            success : ()=>{
                alert("Xóa thành công");
                setTimeout(()=>{
                    location.reload();
                },2000)
            },
            error : (data) =>{
                if(data.responseText.toLowerCase() === "token expired"){
                    alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                    setTimeout(()=>{
                        location.href = "/dashboard/login";
                    })
                }else{
                    alert("Xóa thất bại");
                }
            }
        });
    }
}

var OnFormatCurrency = (obj) => {
    if (obj.id === "create_fee") {
        var fee = $("#create_fee").val();
        $("#create_fee").val(fee.replace(/\D/g, '')
            .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
    } else {
        var fee = $("#edit_fee").val();
        $("#edit_fee").val(fee.replace(/\D/g, '')
            .replace(/\B(?=(\d{3})+(?!\d))/g, ','));
    }
}
