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

var OnAppDownload = (id) => {
    $.ajax({
        url: "/dashboard/application/get_one_app/" + id,
        method: "GET",
        contentType: "application/json",
        success: async (data) => {
            console.log(data);
            var downloadLink = document.createElement("a");
            downloadLink.href = (await base64ToWord(data.file)).url;
            $.ajax({
                url: "/dashboard/application/get_one_app_type/" + data.applicationTypeId,
                method: "GET",
                contentType: "application/json",
                success: (data) => {
                    console.log(data);
                    downloadLink.download = data.name;
                    console.log(downloadLink);
                    downloadLink.click();
                },
                error: (data) => {
                    if (data.toLowerCase() === "token expired") {
                        alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                        setTimeout(() => {
                            location.href = "/dashboard/login";
                        }, 2000);
                    } else {
                        alert("Không tìm thấy dữ liệu");
                        $("#appType").val("").trigger("change");
                    }
                }
            });
        },
        error: (data) => {
            if (data.toLowerCase() === "token expired") {
                alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                setTimeout(() => {
                    location.href = "/dashboard/login";
                }, 2000);
            } else {
                alert("Không tìm thấy dữ liệu");
            }
        }
    });
}

var OnCreateApplicationType = async () => {
    var appTypeName = $("#app-type-name").val();
    var appTypeFile = $("#app-type-file").get(0).files[0];
    var formData = new FormData();
    var base64String = await toBase64(appTypeFile);
    formData.append("name", appTypeName);
    formData.append("file", appTypeFile);
    formData.append("base64String", base64String);
    $.ajax({
        url: "/dashboard/application/save_app_type",
        data: formData,
        method: "POST",
        contentType: false,
        processData: false,
        enctype: "multipart/form-data",
        success: (data) => {
            switch (data.status.toLowerCase()) {
                case "success":
                    $("#create-app-type-modal").modal("hide");
                    alert("Tạo thành công");
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                    break;
                case "wrongtype":
                    $("#error-file").html(data.data);
                    break;
            }
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

var OnCreateApplication = async () => {
    var app_type = $("#appType").val();
    var student_id = 1;
    var note = $("#note").val();
    var app_type_file = $("#app-type-file").get(0).files[0];
    var send_date = new Date();
    var application = {
        "sendDate": send_date.getDate() + "/" + ((send_date.getMonth() + 1) < 10 ? "0" + (send_date.getMonth() + 1) : (send_date.getMonth() + 1)) + "/" + send_date.getFullYear(),
        "note": note,
        "file": await toBase64(app_type_file),
        "status": "pending",
        "studentId": student_id,
        "applicationTypeId": app_type
    }
    $.ajax({
        url: "/dashboard/application/save_app",
        contentType: "application/json",
        processData: false,
        method: "POST",
        data: JSON.stringify(application),
        success: () => {
            alert("Tạo mới thành công");
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

var OnValidate = (obj) => {
    switch (obj.id) {
        case "app-type-file":
            break;
        default:
            alert("Không tìm thấy case cho id " + obj.id);
            break;
    }
}

var OnChangeAppType = () => {
    var appType = $("#appType").val();
    if (appType !== "") {
        $("#appTypeDownload").removeClass("d-none");
        $.ajax({
            url: "/dashboard/application/get_one_app_type/" + appType,
            method: "GET",
            contentType: "application/json",
            success: async (data) => {
                $("#appTypeDownload").attr("href", (await base64ToWord(data.file)).url);
                $("#appTypeDownload").attr("download", data.name);
            }
            ,
            error: (data) => {
                if (data.toLowerCase() === "token expired") {
                    alert("Hết phiên đăng nhập vui lòng đăng nhập lại");
                    setTimeout(() => {
                        location.href = "/dashboard/login";
                    }, 2000);
                } else {
                    alert("Không tìm thấy dữ liệu");
                    $("#appType").val("").trigger("change");
                }
            }
        });
    } else {
        $("#appTypeDownload").addClass("d-none");
    }
}

var OnViewApplication = async (base64String) => {
    var fileUrl = (await base64ToWord(base64String)).url;
    var file = await urltoFile(fileUrl ,"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    PreviewWordDoc(file);
    $("#show-document").modal("show");
}

var PreviewWordDoc = (file) => {
    if (file != null) {
        var docxOptions = Object.assign(docx.defaultOptions, {
            useMathMLPolyfill: true
        });
        var container = document.querySelector("#word_container");
        docx.renderAsync(file, container, null, docxOptions);
    }
}

// 1. Chuyển đổi image sang base64 string
var toBase64 = (file) => new Promise((resolve, reject) => {
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result.replace("data:", "").replace(/^.+,/, ""));
    reader.onerror = error => reject(error);
});

// 2. Chuyển đổi base64 string sang image
async function base64ToImage(base64String) {
    var base64Response = await fetch(`data:image/png;base64,${base64String}`);
    return base64Response;
}

// Chuyển đổi base64 sang word
async function base64ToWord(base64String) {
    var base64Response = await fetch(`data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,${base64String}`);
    return base64Response;
}

var urltoFile = (url, mimeType) =>{
    return (fetch(url)
            .then(function(res){return res.arrayBuffer();})
            .then(function(buf){return new File([buf],{type:mimeType});})
    );
}
