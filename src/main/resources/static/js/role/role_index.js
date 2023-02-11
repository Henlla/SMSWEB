$(()=>{
    $("#role-table").dataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollY: '350px',
        scrollCollapse: true,
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
    $("#role_create-form").validate({
        rules: {
            role_code_validate: {
                required: true
            },
            role_description_validate: {
                required: true
            }
        },
        messages: {
            role_code_validate: {
                required: "Vui lòng nhập mã chức vụ"
            },
            role_description_validate: {
                required: "Vui lòng nhập mô tả chức vụ"
            }
        },
    });
})

var OnCreateRole = () => {
    if ($("#role_create-form").valid()) {
        var role_code = $("#role_code_create").val();
        var role_description = $("#role_description_create").val();
        var formData = {
            "role_code": role_code,
            "role_description": role_description,
        }
        $.ajax({
            url: "/dashboard/roles/create_role",
            contentType: "application/json",
            method: "POST",
            data: JSON.stringify(formData),
            success: (data) => {
                location.reload();
            }
        });
    }
}
