$(()=>{
    $('#btn_changePassword').on('click',()=>{
        $('#change_password').modal('show')
    })

    $('#oldPass').on('focus',()=>{
        $('#mess_oldPass').html('')
    });

    $("#change_password").on("hidden.bs.modal",()=>{
        $("#oldPass").val("");
        $("#newPass").val("");
    });

    $('#submitChange').on('click',(e)=>{
        e.preventDefault()
        jQuery.validator.addMethod("notEqualTo",
            function(value, element, param) {
                var notEqual = true;
                value = $.trim(value);
                for (i = 0; i < param.length; i++) {
                    if (value == $.trim($(param[i]).val())) { notEqual = false; }
                }
                return this.optional(element) || notEqual;
            },
            "Please enter cannot same old password."
            );
        $('#form_change_password').validate({
            rules:{
                oldPass:{
                    required:true,
                    minlength: 8

                },newPass:{
                    required:true,
                    minlength: 8,
                    notEqualTo: ['#oldPass']
                }
            },messages:{
                oldPass:{
                    required:"Please enter old password",
                    minlength: "Enter atleast 8 characters"
                },newPass:{
                    required:"Please enter new password",
                    minlength: "Enter atleast 8 characters",
                }
            }
        })

        if($('#form_change_password').valid()){
            let oldPassword = $('#oldPass').val()
            let newPassword = $('#newPass').val()
            let data = new FormData()
            data.append("oldPass",oldPassword)
            data.append("newPass",newPassword)
            $.ajax({
                url:"/student/change_password/"+$('#accountId').val(),
                method:"POST",
                data:data,
                contentType: false,
                cache: false,
                processData: false,
                success:(res)=>{
                    console.log(res)
                    if(res === "error"){
                        $('#mess_oldPass').html('Old password isn\'t correct.Try again !')
                    }else{
                        Swal.fire(
                            "",
                            "Change password success",
                            "success"
                        )
                        $('#change_password').modal('hide')
                    }
                },error:(err)=>{
                    console.log(err)
                   
                }
            })
        }
    })
})