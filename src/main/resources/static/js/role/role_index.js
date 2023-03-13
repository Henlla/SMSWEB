$(()=>{
    $("#role-table").dataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollY: '350px',
        scrollCollapse: true,
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
                required: "Please enter role code"
            },
            role_description_validate: {
                required: "Please enter role description"
            }
        },
    });
})

var OnCreateRole = () => {
    if ($("#role_create-form").valid()) {
        var role_code = $("#role_code_create").val();
        var role_description = $("#role_description_create").val();
        var formData = {
            "roleName": role_code,
            "roleDescription": role_description,
        }
        $.ajax({
            url: "/dashboard/role/create_role",
            contentType: "application/json",
            method: "POST",
            data: JSON.stringify(formData),
            success: (data) => {
                location.reload();
            }
        });
    }
}
