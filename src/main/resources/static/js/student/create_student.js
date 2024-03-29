$(()=>{
    $('#dob_calendar').datetimepicker({
        format: 'DD/MM/YYYY'
    });
    $('.select2').select2({
        theme: 'bootstrap4'
    });

    $('#dob_calendar').on('change.datetimepicker',(e)=>{
        // var formatedValue = e.date.format(e.date._f);
        let date = new Date(e.date)
        let age = _calculateAge(date)
        console.log(date);
        if (age <= 18){
            $('#dob-error').show()
            $('#dob-error').html('Age must be greater than 18 ')
        }else{
            $('#dob-error').hide()
            $('#dob-error').html('')
        }
    })

    $('#phone').on('change',()=>{
        // console.log()
        let isPhone = regexPhone($('#phone').val())
        if(isPhone){
            $('#phone-error').hide()
            $('#phone-error').html('')
        }else{
            $('#phone-error').show()
            $('#phone-error').html('Please enter incorrect phone')
        }
    })

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
            if(GetExtension(input.files[0].name) === "png" ||
                        GetExtension(input.files[0].name) ==="jpg" ||
                        GetExtension(input.files[0].name) ==="jpeg") {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('.background-choose_image').attr('src', e.target.result);
                }
                reader.readAsDataURL(input.files[0]);
                return true;
            }else{
                return false;
            }
        }
    }

    $("#avatar").change(function(){
        if(readURL(this)){
            $('.icon-choose_image').css("display","none")
            $('.icon-cancel_image').css("display","block")
            $('.background-choose_image').css("filter","blur(0px)")
            $('.errorAvatar').css("display","none")
        }else{
            $('.errorAvatar').css("display","block")
            $('.errorAvatar').html("Please choose image (png,jpg,jpeg)")
        }
    });


    // custom confirm
   $('.icon-cancel_image').on('click',function (){
       $('.background-choose_image').css("filter","blur(3px)")
       Confirm('Cancel image', 'Are you sure cancel image?', 'Ok', 'Cancel')


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
        formData.append('majorId',majorId)

        //method rule
        $.validator.addMethod("valueNotEquals", function(value, element, arg){
            return arg !== value;
        }, "Value must not equal arg.");

        $.validator.addMethod("checkAge", function(value, element){
            var dateParts = value.split("/");
            // month is 0-based, that's why we need dataParts[1] - 1
            var date = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
            console.log(value)
            let age = _calculateAge(date)
            console.log(age)
            return age >= 18;
        }, "Age must be greater than 18");

        $.validator.addMethod("checkPhoneNumber", function(value, element){
            return regexPhone(value);
        }, "Please enter incorrect phone");

        $.validator.addMethod("checkIdentityCard", function(value, element){
            return isCheck;
        }, "Please enter incorrect phone");

        $('#form-create').validate({
            rules: {
                first_name: {

                    required: true
                },
                last_name: {
                    required: true
                },
                phone: {
                    checkPhoneNumber:true,
                    required: true
                }
                ,dob: {
                    checkAge:true,
                    required: true
                },
                email: {
                    required: true,
                    email:true
                },
                identityCard: {
                    checkIdentityCard:true,
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
                major:{
                    valueNotEquals: ""

                }
                },
            messages:{
                first_name : {
                    required:"Please enter first name"
                },
                last_name : {
                    required:"Please enter last name"
                },
                phone: {
                    checkPhoneNumber: "Please enter incorrect phone",
                    required: "Please enter phone numbers "
                }, dob: {
                    checkAge:"Age must be greater than 18",
                    required: "Please enter date of birth "
                },
                email: {
                    required: "Please enter email ",
                    email:"Email wrong format xxxx@xxx.xxx"
                },
                identityCard: {
                    checkIdentityCard:"Identity card is exist",
                    required: "Please enter identity card "
                },
                province: {
                    valueNotEquals: "Please enter province "
                },
                district: {
                    valueNotEquals: "Please enter district "
                },
                ward: {
                    valueNotEquals: "Please enter ward "
                },
                address:{
                    required: "Please enter address"
                },
                major: {
                    valueNotEquals: "Please enter major "
                }
            },
        })
            if(avatarUrl.files.length===0){
                $('.errorAvatar').css("display","block")
                $('.errorAvatar').html("Please choose image")
            }else{
                if($('#form-create').valid()){
                    $('#spinner-div').show()
                    $.ajax({
                        url:"/dashboard/student/create-student",
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
                            toastr.success('Create success')
                            $('#spinner-div').hide();
                        },
                        error:(xhr, status, error)=>{
                            var err = eval("(" + xhr.responseText + ")");
                            console.log(err)
                            if (err.message.toLowerCase() === "token expired") {
                                $('#spinner-div').hide();
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
                                toastr.error('Create fail')
                            }
                        }
                    })
            }

        }
    })

})

let isCheck = false

let CheckIdentityCard= ()=>{

    let data = new FormData()
    data.append("identityCard",$('#identityCard').val())

    $.ajax({
        url:"/dashboard/student/checkIdentityCard",
        method:"POST",
        data:data,
        cache : false,
        processData: false,
        contentType: false,
        success:(res)=>{
            if(res.toLowerCase() === "success"){
                isCheck = true
                console.log(isCheck)
                $('#identityCard-error').hide()
                $('#identityCard-error').html("")
            }else{
                $('#identityCard-error').show()
                $('#identityCard-error').html("Identity card is exist !")
            }
        }

    })
}





function selectMajor(){
    console.log($('#major').val())
   if($('#major').val() != ""){
       $('#major-error').html("")
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


