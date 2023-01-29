
$(()=>{

    const province = document.getElementById("province")
    const district = document.getElementById("district")
    const wards = document.getElementById("ward")


    province.onchange = function (){
        var provinceId = this.value;
        district.length = 1;
        wards.length = 1;
        if(provinceId!=""){
            console.log(this.value)
            $.ajax({
                url:"http://localhost:8080/api/districts/",
                method:"GET",
                contentType: "application/json",
                data: {province : provinceId},
                success : (res)=>{
                    for(var dis of res){
                        // var districtOption = document.createElement("option");
                        // districtOption.value = dis.id;
                        // districtOption.text =dis.name;
                        // district.appendChild(districtOption);
                        district.options[district.options.length] = new Option(dis.name, dis.id);
                    }
                    district.onchange = function () {
                        wards.length = 1;
                        console.log(this.value)
                        if (this.value != "") {
                            $.ajax({
                                url:"http://localhost:8080/api/wards/",
                                method:"GET",
                                contentType: "application/json",
                                data: {province : provinceId,district : this.value},
                                success: (res)=>{
                                    for (const ward of res) {
                                        wards.options[wards.options.length] = new Option(ward.name, ward.id);
                                    }
                                }
                            })
                        }
                    };
                }
            })
        }

    }
    $('.icon-choose_image').on('click', function() {
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

    $("#avatar").change(function(){
        $('.icon-choose_image').css("display","none")
        $('.icon-cancel_image').css("display","block")
        $('.background-choose_image').css("filter","blur(0px)")
        $('.errorAvatar').css("display","none")
        console.log(this.value)
        readURL(this);
    });


    // custom confirm
   $('.icon-cancel_image').on('click',function (){
       $('.background-choose_image').css("filter","blur(3px)")
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


    $('#btn_create_student').on('click',function (e){
        e.preventDefault()
        var provinceId = $('#province').val()
        var districtId = $('#district').val()
        var wardId = $('#ward').val()
        var firstName = $('#first_name').val()
        var lastName = $('#last_name').val()
        var dob = $('#dob').val()
        var phone = $('#phone').val()
        var address = $('#address').val()
        var email = $('#email').val()
        var majorId = $('#major').val()
        var avatarUrl = document.getElementById("avatar")
        let formData = new FormData();
        var identityCard = $('#identityCard').val()
        var profile = {
            "firstName" : firstName,
            "lastName" : lastName,
            "dob" : dob,
            "provinceId":provinceId,
            "districtId":districtId,
            "wardId":wardId,
            "address":address,
            "phone":phone,
            "email":email,
            "avartarUrl":"",
            "avatarPath" : "",
            "identityCard" : identityCard,
            "accountId" : ""
        }
        formData.append('file', avatarUrl.files[0]);
        formData.append('profile',JSON.stringify(profile))
        formData.append('majorId',majorId)

        $('#form-create').validate({
            rules: {
                first_name: {
                    required: true
                },
                last_name: {
                    required: true
                },
                phone: {
                    required: true
                },
                email: {
                    required: true,
                    email:true
                },
                identityCard: {
                    required: true
                },
                // province: {
                //     required: true
                // },
                // district: {
                //     required: true
                // },
                // ward: {
                //     required: true
                // },
                address:{
                    required: true
                },
                // major:{
                //     required:true
                //
                // }
                },
            messages:{
                first_name : {
                    required:"Vui lòng nhập họ sinh viên"
                },
                last_name : {
                    required:"Vui lòng nhập tên sinh viên"
                },
                phone: {
                    required: "Vui lòng nhập số điện thoại "
                },
                email: {
                    required: "Vui lòng nhập email ",
                    email:"Vui lòng nhập đúng email"
                },
                identityCard: {
                    required: "Vui lòng nhập CMND/CCCD "
                },
                // province: {
                //     required: "Vui lòng chọn tỉnh/thành phố "
                // },
                // district: {
                //     required: "Vui lòng chọn quận/huyện "
                // },
                // ward: {
                //     required: "Vui lòng chọn xã/thị trấn "
                // },
                address:{
                    required: "Vui lòng nhập địa chỉ"
                },
                // major: {
                //     required: "Vui lòng chọn nghành học "
                // }
            },
        })
            if(avatarUrl.files.length===0){
                $('.errorAvatar').css("display","block")
            }else{
                if($('#form-create').valid()){
                    $.ajax({
                        url:"/dashboard/create-student",
                        method:"POST",
                        enctype: 'multipart/form-data',
                        dataType: 'json',
                        data:formData,
                        cache : false,
                        processData: false,
                        contentType: false,
                        success:(data)=>{
                            console.log(data)
                            toastr.success('Tạo sinh viên thành công')
                        }
                    })
            }

        }





        console.log("provinceId "+provinceId)
        console.log("districtId "+districtId)
        console.log("wardId "+wardId)
        console.log("firstName "+firstName)
        console.log("lastName "+lastName)
        console.log("dob "+dob)
        console.log("phone "+phone)
        console.log("address "+address)
        console.log("email "+email)
        console.log("majorId "+majorId)
        console.log("avatarUrl "+formData)
    })

})
