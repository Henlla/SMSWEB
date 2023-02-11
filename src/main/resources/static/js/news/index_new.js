$(()=>{
    CKEDITOR.replace('editor');
    $("#new-table").DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
        scrollCollapse: true,
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
        },
    })
})

function Confirm(title, msg, $true, $false,isActive,id) { /*change*/
    var $content =  "<div class='dialog-ovelay'>" +
        "<div class='dialog'><header>" +
        " <h3> " + title + " </h3> " +
        "<i class='fa fa-close'></i>" +
        "</header>" +
        "<div class='dialog-msg'>" +
        " <p> " + msg + " </p> " +
        "</div>" +
        "<footer>" +
        "<div class='controls'>" +
        " <button class='button button-danger doAction'>" + $true + "</button> " +
        " <button class='button button-default cancelAction'>" + $false + "</button> " +
        "</div>" +
        "</footer>" +
        "</div>" +
        "</div>";
    $('body').prepend($content);
    $('.doAction').click(function () {
        let status = "";
        if(isActive){
            status = !isActive
        }else{
            status = !isActive
        }
        let formData = new FormData()
        formData.append('isActive',JSON.stringify(status))
        $.ajax({
            url:"/dashboard/news/change_active/"+id,
            method:"POST",
            data : formData,
            cache: false,
            processData: false,
            contentType: false,
            success:(result)=>{
                location.reload();
            },
            error:()=>{

            }
        })
        $(this).parents('.dialog-ovelay').fadeOut(500, function () {
            $(this).remove();
        })
    });
    $('.cancelAction, .fa-close').click(function () {
        if(isActive){
            $('#isActive'+id).prop("checked",true)
        }else{
            $('#isActive'+id).prop("checked",false)
        }
        $(this).parents('.dialog-ovelay').fadeOut(500, function () {
            $(this).remove();
        });
    });

}

var OnActive = (id,isActive)=>{
   if(isActive){
       Confirm('Hiển thị', 'Có chắc chắn muốn ẩn tin tức này không ?', 'Có', 'Không',isActive,id)
   }else{
       Confirm('Hiển thị', 'Có chắc chắn muốn hiển thị tin tức này không ?', 'Có', 'Không',isActive,id)
   }
}

var OnDetails = (id)=>{
    $.ajax({
        url: "/dashboard/news/new_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            $('#thumbnail').attr('src', data.thumbnailUrl)
            $('#title').html(data.title)
            $('#sub_title').html(data.sub_title)
            $('#post_date').html(data.post_date)
            $('#content').html(data.content)
            $("#news_details").modal("show");
        }, error: (data) => {

        }
    });
}

var OnUpdate = (id)=>{
    $.ajax({
        url: "/dashboard/news/new_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            $('#thumbnail_u').attr('src', data.thumbnailUrl)
            $('#news_id').val(data.id)
            $('#img').val(data.thumbnailUrl)
            $('#img_path').val(data.thumbnailPath)
            $('#title_u').val(data.title)
            $('#sub_title_u').val(data.sub_title)
            $('#post_date_u').val(data.post_date)
            if(data.isActive){
                $('#isActive_u').prop("checked",true)
            }else{
                $('#isActive_u').prop("checked",false)
            }
            CKEDITOR.instances.editor.setData(data.content)
            $("#news_update").modal("show");
        }, error: (data) => {

        }
    });
}

var OnUpdateSubmit = () =>{
    var isActive = false
    if($('#isActive').prop('checked') ===true){
        isActive =true
    }else{
        isActive =false
    }
    var title = $('#title_u').val()
    var sub_title = $('#sub_title_u').val()
    var post_date = $('#post_date_u').val()
    let formData = new FormData();

    var news = {
        'id':$('#news_id').val(),
        'title':title,
        'sub_title':sub_title,
        'content':CKEDITOR.instances.editor.getData(),
        'thumbnailUrl':$('#img').val(),
        'thumbnailPath':$('#img_path').val(),
        'post_date':post_date,
        'isActive':isActive
    }

    formData.append('news',JSON.stringify(news))
    $.ajax({
        url: "/dashboard/news/update_new",
        method: "POST",
        enctype: 'multipart/form-data',
        data: formData,
        cache: false,
        processData: false,
        contentType: false,
        success: (result) => {
            location.reload();
        },
        error: (e) => {
            toastr.error('Thất bại')
        }
    })

}

function selectFile() {
    // alert("click")
    $('#fileAvatar').trigger('click');
}
const thumbnailImage = document.getElementById('thumbnail_u')
function previewImage() {
    thumbnailImage.src = URL.createObjectURL(event.target.files[0]);
    $('#btn-function').show()
    $('#btn-updateImg').hide()
}

var CancelUpdateImg = () => {
    $('#btn-function').hide()
    $('#btn-updateImg').show()
    $('#thumbnail_u').attr('src', $('#img').val())
}

var OnUpdateImg = () => {

    ConfirmImg('Thay đổi hình ảnh', 'Có chắc chắn muốn thay đổi hình ảnh?', 'Có', 'Không')
}

function ConfirmImg(title, msg, $true, $false) { /*change*/
    var $content = "<div class='dialog-ovelay'>" +
        "<div class='dialog'><header>" +
        " <h3> " + title + " </h3> " +
        "<i class='fa fa-close'></i>" +
        "</header>" +
        "<div class='dialog-msg'>" +
        " <p> " + msg + " </p> " +
        "</div>" +
        "<footer>" +
        "<div class='controls'>" +
        " <button class='button button-danger doAction'>" + $true + "</button> " +
        " <button class='button button-default cancelAction'>" + $false + "</button> " +
        "</div>" +
        "</footer>" +
        "</div>" +
        "</div>";
    $('body').prepend($content);
    $('.doAction').click(function () {
        var thumbnail = document.getElementById("fileAvatar")
        var news_id = $('#news_id').val()
        let formData = new FormData();
        formData.append('file', thumbnail.files[0]);
        formData.append('news_id', news_id)
        $.ajax({
            url: "/dashboard/news/changeImg",
            method: "POST",
            enctype: 'multipart/form-data',
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            success: (data) => {
                location.reload();
            }
        })
        $(this).parents('.dialog-ovelay').fadeOut(500, function () {
            $(this).remove();
        })
    });
    $('.cancelAction, .fa-close').click(function () {
        $(this).parents('.dialog-ovelay').fadeOut(500, function () {
            $(this).remove();
        });
    });
}