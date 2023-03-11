$(() => {

    CKEDITOR.replace('editor');
    var isActive = false
    if ($('#isActive').prop('checked') === true) {
        isActive = true
    } else {
        isActive = false
    }
    console.log(isActive)
    $('#isActive').on('click', () => {
        if ($('#isActive').prop('checked') === true) {
            isActive = true
        } else {
            isActive = false
        }
        console.log('after ' + isActive)
    })


    $('#btn_create_news').on('click', (e) => {
        e.preventDefault()
        console.log(CKEDITOR.instances.editor.getData())
        var avatarUrl = document.getElementById("avatar")
        var title = $('#title').val()
        var sub_title = $('#sub_title').val()
        let formData = new FormData();

        var news = {
            'title': title,
            'sub_title': sub_title,
            'content': CKEDITOR.instances.editor.getData(),
            'post_date': "",
            'thumbnailUrl': '',
            'thumbnailPath': '',
            'isActive': isActive
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
                    toastr.success('Create success')
                    $('#spinner-div').hide();
                },
                error: (e) => {
                    toastr.error('Create fail')
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
            if (GetExtension(input.files[0].name) === "png" ||
                GetExtension(input.files[0].name) === "jpg" ||
                GetExtension(input.files[0].name) === "jpeg") {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('.background-choose_image').attr('src', e.target.result);
                }
                reader.readAsDataURL(input.files[0]);
                return true;
            } else {
                return false;
            }
        }
    }

    $("#avatar").change(function () {
        if (readURL(this)) {
            $('.icon-choose_image').css("display", "none")
            $('.icon-cancel_image').css("display", "block")
            $('.background-choose_image').css("filter", "blur(0px)")
            $('.errorAvatar').css("display", "none")
        } else {
            $('.errorAvatar').css("display", "block")
            $('.errorAvatar').html("Please choose image file (png,jpg,jpeg)")
        }
    });


    // custom confirm
    $('.icon-cancel_image').on('click', function () {
        Confirm('Cancel image', 'Are you sure to cancel?', 'Ok', 'Cancel')


    })

    function Confirm(title, msg, $true, $false) { /*change*/
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
            $('.icon-choose_image').css("display", "block")
            $('.icon-cancel_image').css("display", "none")
            $('.background-choose_image').attr('src', '/img/blank.png')
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

    function readURL(input) {
        if (input.files && input.files[0]) {
            if (GetExtension(input.files[0].name) === "docx") {
                var reader = new FileReader();
                reader.onload = function (e) {
                }
                reader.readAsDataURL(input.files[0]);
                return true;
            } else {
                return false;
            }
        }
    }

    $("#file_import").change(function () {
        if (readURL(this)) {

        } else {
            Swal.fire('File incorrect !')
            this.value = ''
        }
    });
})


var OnImportFile = () => {
    $('#modal_import_file').modal("show")
}

var OnImportFileSubmit = ()=>{
    let data = new FormData();
    data.append("file",$("#file_import").get(0).files[0])
    $.ajax({
        url: "/dashboard/news/import_file_excel",
        enctype: 'multipart/form-data',
        method: "POST",
        cache: false,
        processData: false,
        contentType: false,
        data: data,
        success: () => {
            Swal.fire(
                'Import success',
                'success'
            )
            $('#modal_import_file').modal("hide")
        }, error:(xhr, status, error)=>{
            var err = eval("(" + xhr.responseText + ")");
            if (err.message.toLowerCase() === "token expired") {
                $('#spinner-divI').hide()
                Swal.fire({
                    title: 'End of login session please login again',
                    showDenyButton: false,
                    showCancelButton: false,
                    confirmButtonText: 'Confirm',
                }).then((result) => {
                    if (result.isConfirmed) {
                        location.href = "/dashboard/login";
                    }
                });
            }else{
                Swal.fire(
                    'Import fail',
                    'error'
                )
            }
        }
    });
}







