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
        // pagingType:"full_numbers",
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
    // $('#subject-table').dataTable({
    //     columnDefs: [{
    //         orderable: false,
    //         className: 'select-checkbox select-checkbox-all',
    //         targets: 0
    //     }],
    //     select: {
    //         style: 'multi',
    //         selector: 'td:first-child'
    //     }
    // });
    $(".select2-single").select2({
        theme:"bootstrap4",
        width:"100%",
        dropdownCssClass: "f-13"
        // containerCssClass:":all:"
    });
});
var OnEditSubject = (id) => {
    $.ajax({
        url: "/dashboard/subject/findOne/" + id,
        dataType: "json",
        method: "GET",
        success: (obj) => {
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
        var formData = {
            "subjectCode": subject_code,
            "subjectName": subject_name,
            "fee": fee,
            "slot": slot,
            "semesterId": semester_id,
            "majorId": major_id
        }
        $.ajax({
            url: "/dashboard/subject/post",
            contentType: "application/json",
            method: "POST",
            data: JSON.stringify(formData),
            success: (data) => {
                alert("Tạo thành công");
                setTimeout(()=>{
                    location.reload();
                },2000);
            },
            error:()=>{
                alert("Thất bại");
            }
        });
    }
}

var OnUpdateSubject = () => {
    if ($("#edit-form").valid()) {
        var id = $("#edit_id").val();
        var subject_code = $("#edit_subject_code").val();
        var subject_name = $("#edit_subject_name").val();
        var fee = $("#edit_fee").val().replace(/,/g,'');
        console.log(fee);
        var slot = $("#edit_slot").val();
        var semester_id = $("#edit_semester_id").val();
        var major_id = $("#edit_major_id").val();
        var formData = {
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
            data: JSON.stringify(formData)
            , success: (data) => {
                location.reload();
            }, error: (data) => {
                console.log("error");
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