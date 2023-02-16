


$(() => {

    CKEDITOR.replace('editor');
    var isActive = false
    if($('#isActive').prop('checked') ===true){
        isActive =true
    }else{
        isActive =false
    }
    console.log(isActive)
    $('#isActive').on('click',()=>{
        if($('#isActive').prop('checked') ===true){
            isActive =true
        }else{
            isActive =false
        }
        console.log('after '+isActive)
    })


    $('#btn_create_news').on('click', (e) => {
        e.preventDefault()
        console.log(CKEDITOR.instances.editor.getData())
        var avatarUrl = document.getElementById("avatar")
        var title = $('#title').val()
        var sub_title = $('#sub_title').val()
        let formData = new FormData();

        var news = {
            'title':title,
            'sub_title':sub_title,
            'content':CKEDITOR.instances.editor.getData(),
            'post_date':"",
            'thumbnailUrl':'',
            'thumbnailPath':'',
            'isActive':isActive
        }
        formData.append('file', avatarUrl.files[0]);
        formData.append('news', JSON.stringify(news))

        console.log(isActive)
        if (avatarUrl.files.length === 0) {
            $('.errorAvatar').css("display", "block")
        } else {
            $('#spinner-div').show()
            $.ajax({
                url: "/dashboard/news/create_news",
                method: "POST",
                enctype: 'multipart/form-data',
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: (result) => {
                    console.log(result)
                    toastr.success('Tạo sinh tin tức')
                    $('#spinner-div').hide();
                },
                error: (e) => {
                    toastr.error('Thất bại')
                    $('#spinner-div').hide();
                }
            })
        }
    })
    $('.icon-choose_image').on('click', function () {
        $('#avatar').trigger('click');
    });

    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('.background-choose_image').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]);
        }
    }

    $("#avatar").change(function () {
        $('.icon-choose_image').css("display", "none")
        $('.icon-cancel_image').css("display", "block")
        $('.errorAvatar').css("display", "none")
        console.log(this.value)
        readURL(this);
    });


    // custom confirm
    $('.icon-cancel_image').on('click', function () {
        Confirm('Hủy hình ảnh', 'Có chắc chắn muốn hủy hình ảnh?', 'Hủy', 'Không')


    })

    function Confirm(title, msg, $true, $false) { /*change*/
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
            $('.icon-choose_image').css("display","block")
            $('.icon-cancel_image').css("display","none")
            $('.background-choose_image').attr('src','/img/avatar.png')
            $('#avatar').val("")
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
})







