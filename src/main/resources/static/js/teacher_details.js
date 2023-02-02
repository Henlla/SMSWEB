$(document).ready(function () {
    $('#reset_password').on('click',()=>{
        Confirm('Đặt lại mật khẩu', 'Có chắc chắn muốn đặt lại mật khẩu?', 'Có', 'Không')
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
            var data = new FormData();
            data.append("id",$('#accountId').val());
            data.append("email",$('#email').val());
            $('#spinner-div').show()
            $.ajax({
                url:"/dashboard/teacher/reset_password",
                method:"POST",
                cache : false,
                processData: false,
                contentType: false,
                data:data,
                success:(data)=>{
                    console.log(data)
                },
                complete:()=>{
                    toastr.success('Đặt lại mật khẩu thành công')
                    $('#spinner-div').hide()
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
})