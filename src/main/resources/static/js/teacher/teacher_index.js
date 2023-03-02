$(()=>{
    $('#reservationdate_u').datetimepicker({
        format: 'DD/MM/YYYY'
    });

    $('.select2').select2({
        theme: 'bootstrap4'
    });
    $('#student-table').DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
        scrollY: '350px',
        scrollCollapse: true,
        pagingType:"full_numbers",
        columnDefs: [{
            orderable: false,
            targets: 1
        }],
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
        }
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width': '350px', 'display': 'inline-block'}
    );

    $('#reset_password').on('click', () => {
        Confirm('Đặt lại mật khẩu', 'Có chắc chắn muốn đặt lại mật khẩu?', 'Có', 'Không')
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
            var data = new FormData();
            data.append("id", $('#accountId').val());
            data.append("email", $('#email').val());
            $('#spinner-div').show()
            $.ajax({
                url: "/dashboard/teacher/reset_password",
                method: "POST",
                cache: false,
                processData: false,
                contentType: false,
                data: data,
                success: (data) => {
                    console.log(data)
                },
                complete: () => {
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
    const province = document.getElementById("province_u")
    const district = document.getElementById("district_u")
    const wards = document.getElementById("ward_u")

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
});
var OnDetails = (id) => {
    $.ajax({
        url: "/dashboard/teacher/teacher_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#tc_image').attr('src', data.teacher.profileByProfileId.avartarUrl)
            $('#tc_fullName').html(data.teacher.profileByProfileId.firstName + ' ' + data.teacher.profileByProfileId.lastName)
            $('#tc_dob').html(data.teacher.profileByProfileId.dob)
            $('#tc_phone').html(data.teacher.profileByProfileId.phone)
            $('#tc_email').html(data.teacher.profileByProfileId.email)
            $('#tc_sex').html(data.teacher.profileByProfileId.sex)
            $('#tc_identityId').html(data.teacher.profileByProfileId.identityCard)
            if (data.classses.length === 0) {
                $('#tc_class').html("Chưa có")
            } else {
                for (var classes of data.classses) {
                    $('#tc_class').html(classes.classCode)
                }
            }
            $('#tc_address').html(data.teacher.profileByProfileId.address + ' , ' + data.teacher.profileByProfileId.wardByWardId.name + ' , ' + data.teacher.profileByProfileId.districtByDistrictId.name + ' , ' + data.teacher.profileByProfileId.profileProvince.name)
            $('#accountId').val(data.teacher.profileByProfileId.accountByAccountId.id)
            $('#email').val(data.teacher.profileByProfileId.email)
            $("#teacher_details").modal("show");
        }, error: (data) => {
            console.log(data);
        }
    });
}
var OnUpdate = (id) => {
    const province = document.getElementById("province_u")
    const district = document.getElementById("district_u")
    const wards = document.getElementById("ward_u")
    $.ajax({
        url: "/dashboard/teacher/teacher_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#st_image_u').attr('src', data.profileByProfileId.avartarUrl)
            $('#img').val(data.profileByProfileId.avartarUrl)
            $('#firstName_u').val(data.profileByProfileId.firstName)
            $('#lastName_u').val(data.profileByProfileId.lastName)
            $('#dob_u').val(data.profileByProfileId.dob)
            $('#phone_u').val(data.profileByProfileId.phone)
            $('#email_u').val(data.profileByProfileId.email)
            $('#address_u').val(data.profileByProfileId.address)
            $('#identityCard_u').val(data.profileByProfileId.identityCard)
            $('#profileId_u').val(data.profileByProfileId.id)
            $('#accountId_u').val(data.profileByProfileId.accountId)

            const province_id = data.profileByProfileId.profileProvince.id

            const district_id = data.profileByProfileId.districtByDistrictId.id

            const ward_id = data.profileByProfileId.wardByWardId.id

            console.log(province_id, district_id, ward_id)

            $("#province_u").val(province_id).trigger('change');

            $.ajax({
                url: "http://localhost:8080/api/districts/",
                method: "GET",
                contentType: "application/json",
                data: {province: province_id},
                success: (res) => {
                    for (var dis of res) {
                        district.options[district.options.length] = new Option(dis.name, dis.id);
                    }
                    $("#district_u option[value='" + district_id + "']").prop("selected", true);
                    $.ajax({
                        url: "http://localhost:8080/api/wards/",
                        method: "GET",
                        contentType: "application/json",
                        data: {province: province_id, district: district_id},
                        success: (res) => {
                            for (const ward of res) {
                                wards.options[wards.options.length] = new Option(ward.name, ward.id);
                            }
                            $("#ward_u option[value='" + ward_id + "']").prop("selected", true);
                        }
                    })
                }
            })

            $("input[name=sex][value=" + data.profileByProfileId.sex + "]").prop('checked', true);

            $("#teacher_update").modal("show");
        }, error: (data) => {
            console.log(data);
        }
    });
}
var OnUpdateSubmit = () =>{
    const sex = document.getElementsByName("sex")
    var profileId = $('#profileId_u').val()
    var accountId = $('#accountId_u').val()
    var provinceId = $('#province_u').val()
    var districtId = $('#district_u').val()
    var wardId = $('#ward_u').val()
    var firstName = $('#firstName_u').val()
    var lastName = $('#lastName_u').val()
    var dob = $('#dob_u').val()
    var phone = $('#phone_u').val()
    var address = $('#address_u').val()
    var email = $('#email_u').val()
    var identityCard = $('#identityCard_u').val()
    let formData = new FormData();
    var sexValue = ""
    for(i = 0; i < sex.length; i++) {
        if(sex[i].checked){
            sexValue = sex[i].value
        }
    }
    var profile = {
        "id":profileId,
        "firstName" : firstName,
        "lastName" : lastName,
        "dob" : dob,
        "provinceId":provinceId,
        "districtId":districtId,
        "wardId":wardId,
        "address":address,
        "phone":phone,
        "email":email,
        "identityCard" : identityCard,
        "sex":sexValue,
        "accountId" : accountId
    }
    formData.append('profile',JSON.stringify(profile))
    $('#spinner-divT').show();
    $.ajax({
        url:"/dashboard/teacher/teacher_update",
        method:"POST",
        data:formData,
        cache : false,
        processData: false,
        contentType: false,
        success:(result)=>{
            console.log(result)
            $("#student_update").modal("hide");
            $('#spinner-divT').hide();
            location.reload();
            toastr.success('Cập nhật giáo viên thành công')
        },
        error:(e)=>{
            toastr.error('Thất bại')
            $('#spinner-divT').hide();
        }
    })
}
const imageProfile = document.getElementById('st_image_u')
function selectFile(){
    // alert("click")
    $('#fileAvatar').trigger('click');
}
function previewImage(){
    imageProfile.src=URL.createObjectURL(event.target.files[0]);
    $('#btn-function').show()
    $('#btn-updateImg').hide()
}

var CancelUpdateImg = () =>{
    $('#btn-function').hide()
    $('#btn-updateImg').show()
    $('#st_image_u').attr('src',$('#img').val())
}

var OnUpdateImg = () => {

    ConfirmImg('Thay đổi hình ảnh', 'Có chắc chắn muốn thay đổi hình ảnh?', 'Có', 'Không')
}
function ConfirmImg(title, msg, $true, $false) { /*change*/
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
        var avatarUrl = document.getElementById("fileAvatar")
        var profileId = $('#profileId_u').val()
        let formData = new FormData();
        formData.append('file', avatarUrl.files[0]);
        formData.append('id', profileId)
        $('#spinner-divT').show()
        $.ajax({
            url:"/dashboard/teacher/changeImg",
            method:"POST",
            enctype: 'multipart/form-data',
            data:formData,
            cache : false,
            processData: false,
            contentType: false,
            success : (data)=>{
                $('#spinner-divT').hide()
                location.reload();
                toastr.success('Thay đổi hình ảnh thành công')
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
