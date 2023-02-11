$(() => {
    $("#app-type-table").DataTable({
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

    $("#app-table").DataTable({
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

    $("#app-modal").on("hidden.bs.modal", () => {
        $("#appType").val("").trigger("change");
    });

});

var OnCreateApplicationType = () => {
    var appTypeName = $("#app-type-name").val();
    var appTypeFile = $("#app-type-file").get(0).files[0];
    var formData = new FormData();
    formData.append("name", appTypeName);
    formData.append("file", appTypeFile);
    $.ajax({
        url: "/dashboard/application/save_app_type",
        data: formData,
        method: "POST",
        contentType: false,
        processData: false,
        enctype: "multipart/form-data",
        success: (data) => {
            console.log(data);
            // if(data.toLowerCase() === "token expired"){
            //     alert("Token đã hết hạn vui lòng đăng nhập lại")
            //     setTimeout(()=>{
            //         location.href = "/dashboard/login";
            //     },2000);
            // }else{
            //     $("#create-app-type-modal").modal("hide");
            //     alert("Tạo thành công");
            //     setTimeout(() => {
            //         location.reload();
            //     }, 2000)
            // }
        },
        error:(data)=>{
            console.log('Fail');
        }
    });
}

var OnCreateApplication = () => {

}

var OnValidate = (obj) => {
    console.log(document);
    switch (obj.id) {
        case "app-type-file":
            console.log($("#"+obj.id).val());
            break;
        default:
            alert("Không tìm thấy case cho id " + obj.id);
            break;
    }
}

var OnChangeAppType = () => {
    var appType = $("#appType").val();
    if (appType !== "") {
        $("#downloadButton").removeClass("d-none");
        $.ajax({
            url: "/dashboard/application/get_one_app_type/" + appType,
            method: "GET",
            success: (data) => {
                $("#downloadButton").attr("href", data.data.url);
            },
            error: () => {
                $("#appType").val("").trigger("change");
            }
        });
    } else {
        $("#downloadButton").addClass("d-none");
    }
}