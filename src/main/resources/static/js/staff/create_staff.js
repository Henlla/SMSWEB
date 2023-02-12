
$(()=>{

    const province = document.getElementById("province")
    const district = document.getElementById("district")
    const wards = document.getElementById("ward")

    const sex = document.getElementsByName("sex")

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
        var avatarUrl = document.getElementById("avatar")
        let formData = new FormData();
        var identityCard = $('#identityCard').val()
        var sexValue = ""
        for(i = 0; i < sex.length; i++) {
            if(sex[i].checked){
                sexValue = sex[i].value
            }
        }
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
            "sex":sexValue,
            "accountId" : ""
        }

        formData.append('file', avatarUrl.files[0]);
        formData.append('profile',JSON.stringify(profile))
        formData.append('roleId', $('#role').val())
        $.validator.addMethod("valueNotEquals", function(value, element, arg){
            return arg !== value;
        }, "Value must not equal arg.");
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
                province: {
                    valueNotEquals: ""
                },
                district: {
                    valueNotEquals: ""
                },
                ward: {
                    valueNotEquals: ""
                },
                address:{
                    required: true
                },
                role:{
                    valueNotEquals: ""
                }

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
                province: {
                    valueNotEquals: "Vui lòng chọn tỉnh/thành phố "
                },
                district: {
                    valueNotEquals: "Vui lòng chọn quận/huyện "
                },
                ward: {
                    valueNotEquals: "Vui lòng chọn xã/thị trấn "
                },
                address:{
                    required: "Vui lòng nhập địa chỉ"
                },
                role: {
                    valueNotEquals: "Vui lòng chọn chức vụ "
                },
            },
        })
        if(avatarUrl.files.length===0){
            $('.errorAvatar').css("display","block")
        }else{
            if($('#form-create').valid()){
                $('#spinner-div').show()
                $.ajax({
                    url:"/dashboard/staff/create-staff",
                    method:"POST",
                    enctype: 'multipart/form-data',
                    data:formData,
                    cache : false,
                    processData: false,
                    contentType: false,
                    success:(result)=>{
                        console.log(result)
                        $('#province').val("").change()
                        $('#district').val("").change()
                        $('#ward').val("").change()
                        $('#first_name').val("")
                        $('#role').val("")
                        $('#last_name').val("")
                        $('#dob').val("")
                        $('#phone').val("")
                        $('#address').val("")
                        $('#email').val("")
                        $('#major').val("").change()
                        $('#avatar').val("")
                        $('#identityCard').val("")
                        $('.icon-cancel_image').css("display","none")
                        $('.icon-choose_image').css("display","block")
                        $('.background-choose_image').attr('src','/img/avatar.png').css("filter","blur(3px)")
                        toastr.success('Tạo nhân viên thành công')
                        $('#spinner-div').hide();
                    },
                    error:(e)=>{
                        console.log(e)
                        toastr.error('Thất bại !')
                        $('#spinner-div').hide();
                    }
                })
            }

        }
    })
})

function selectRole(){
    console.log($('#role').val())
    if($('#role').val() != ""){
        $('#role-error').html("")
    }
}
function selectProvince(){
    console.log($('#province').val())
    if($('#province').val() != ""){
        $('#province-error').html("")
    }
}

function selectDistrict(){
    console.log($('#district').val())
    if($('#district').val() != ""){
        $('#district-error').html("")
    }
}

function selectWard(){
    console.log($('#ward').val())
    if($('#ward').val() != ""){
        $('#ward-error').html("")
    }
}