$(() => {
    $('#dob_calendar_u').datetimepicker({
        format: 'DD/MM/YYYY'
    });

    $('.select2').select2({
        theme: 'bootstrap4'
    });
    $("#student-table").DataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollCollapse: true,
        scrollY: '600px',
        "language": {
            "decimal": "",
            "emptyTable": "Don't have any record",
            "info": "",
            "infoEmpty": "",
            "infoFiltered": "",
            "infoPostFix": "",
            "thousands": ",",
            "lengthMenu": "Show _MENU_ record",
            "loadingRecords": "Searching...",
            "processing": "",
            "search": "Search:",
            "zeroRecords": "Don't find any record",
            "paginate": {
                "first": "First page",
                "last": "Last page",
                "next": "Next page",
                "previous": "Previous page"
            },
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            }
        }, initComplete: function () {
            count = 0;
            this.api().columns([4]).every(function (i) {
                var title = this.header();
                //replace spaces with dashes
                title = $(title).html().replace(/[\W]/g, '');
                var column = this;
                var select = $('<select id="' + title + '" class="form-control form-control-sm select2" style="width: 50%" ></select>')
                    .appendTo($('#select-major'))
                    .on('change', function () {
                        //Get the "text" property from each selected data
                        //regex escape the value and store in array
                        var data = $.map($(this).select2('data'),
                            function (value, key) {
                                return value.text ? '^' + $.fn.dataTable.util.escapeRegex(value.text) + '$' : null;
                            });
                        //if no data selected use ""
                        if (data.length === 0) {
                            data = [""];
                        }
                        //join array into string with regex or (|)
                        var val = data.join('|');
                        //search for the option(s) selected
                        column
                            .search(val ? val : '', true, false)
                            .draw();
                    });

                column.data().unique().sort().each(function (d, j) {
                    select.append('<option value="' + d + '">' + d + '</option>');
                });
                //use column title as selector and placeholder
                $('#' + title).select2({
                    closeOnSelect: true,
                    placeholder: "- All -",
                    allowClear: true,
                    width: 'resolve',
                });

                //initially clear select otherwise first option is selected
                $('.select2').val(null).trigger('change');
            });
        },
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width': '350px', 'display': 'inline-block'}
    );
    $('#reset_password').on('click', () => {
        Confirm('Rest password', 'Do you want rest password?', 'Confirm', 'Cancel')
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
            $('#spinner-divI').show()
            $.ajax({
                url: "/dashboard/student/reset_password",
                method: "POST",
                cache: false,
                processData: false,
                contentType: false,
                data: data,
                success: (data) => {
                    console.log(data)
                    toastr.success('Reset password success')
                    $("#student_details").modal("hide");

                    $('#spinner-divI').hide()
                }, error: (xhr, status, error) => {
                    var err = eval("(" + xhr.responseText + ")");
                    console.log(err)
                    $('#spinner-divI').hide()
                    if (err.message.toLowerCase() === "token expired") {
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
                    } else {
                        alert("Create fail");
                    }
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

    province.onchange = function () {
        var provinceId = this.value;
        district.length = 1;
        wards.length = 1;
        if (provinceId != "") {
            console.log(this.value)
            $.ajax({
                url: "http://localhost:8080/api/districts/",
                method: "GET",
                contentType: "application/json",
                data: {province: provinceId},
                success: (res) => {
                    for (var dis of res) {
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
                                url: "http://localhost:8080/api/wards/",
                                method: "GET",
                                contentType: "application/json",
                                data: {province: provinceId, district: this.value},
                                success: (res) => {
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
        url: "/dashboard/student/student_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#st_image').attr('src', data.student.studentByProfile.avartarUrl)
            $('#st_fullName').html(data.student.studentByProfile.firstName + ' ' + data.student.studentByProfile.lastName)
            $('#st_card').html(data.student.studentCard)
            $('#st_dob').html(data.student.studentByProfile.dob)
            $('#st_phone').html(data.student.studentByProfile.phone)
            $('#st_email').html(data.student.studentByProfile.email)
            $('#st_sex').html(data.student.studentByProfile.sex)
            let classCode = "";
            for (var major of data.student.majorStudentsById) {
                $('#st_major').html(major.majorByMajorId.majorCode)
                $('#st_apartment').html(major.majorByMajorId.apartmentByApartmentId.apartmentCode)
            }
            if (data.classes.length === 0) {
                $('#st_class').html('None')
            } else {
                for (var classes of data.classes) {
                    classCode += classes.classCode + " "
                }
                $('#st_class').html(classCode)
            }
            $('#st_identityId').html(data.student.studentByProfile.identityCard)
            if (data.student.studentByProfile.profileProvince != null && data.student.studentByProfile.districtByDistrictId != null && data.student.studentByProfile.wardByWardId != null){
                $('#st_address').html(data.student.studentByProfile.address + ' , ' + data.student.studentByProfile.wardByWardId.name + ' , ' + data.student.studentByProfile.districtByDistrictId.name + ' , ' + data.student.studentByProfile.profileProvince.name)
            }else{
                $('#st_address').html("")
            }

            $('#accountId').val(data.student.studentByProfile.accountByAccountId.id)
            $('#email').val(data.student.studentByProfile.email)
            $("#student_details").modal("show");

        }, error: (xhr, status, error) => {
            var err = eval("(" + xhr.responseText + ")");
            $("#student_details").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
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
            } else {
                toastr.success("Can't get detail")
            }
        }
    });
}
var OnUpdate = (id) => {
    const province = document.getElementById("province_u")
    const district = document.getElementById("district_u")
    const wards = document.getElementById("ward_u")
    $.ajax({
        url: "/dashboard/student/student_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#st_image_u').attr('src', data.student.studentByProfile.avartarUrl)
            $('#img').val(data.student.studentByProfile.avartarUrl)
            $('#firstName_u').val(data.student.studentByProfile.firstName)
            $('#lastName_u').val(data.student.studentByProfile.lastName)
            $('#studentCard_u').val(data.student.studentCard)
            $('#dob_u').val(data.student.studentByProfile.dob)
            $('#phone_u').val(data.student.studentByProfile.phone)
            $('#email_u').val(data.student.studentByProfile.email)
            $('#address_u').val(data.student.studentByProfile.address)
            $('#identityCard_u').val(data.student.studentByProfile.identityCard)
            $('#profileId_u').val(data.student.studentByProfile.id)
            $('#accountId_u').val(data.student.studentByProfile.accountId)

            if (data.student.studentByProfile.profileProvince != null && data.student.studentByProfile.districtByDistrictId != null && data.student.studentByProfile.wardByWardId != null) {
                const province_id = data.student.studentByProfile.profileProvince.id

                const district_id = data.student.studentByProfile.districtByDistrictId.id
                const ward_id = data.student.studentByProfile.wardByWardId.id
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
                        $("#district_u").val(district_id).trigger('change');
                        $.ajax({
                            url: "http://localhost:8080/api/wards/",
                            method: "GET",
                            contentType: "application/json",
                            data: {province: province_id, district: district_id},
                            success: (res) => {
                                for (const ward of res) {
                                    wards.options[wards.options.length] = new Option(ward.name, ward.id);
                                }
                                $("#ward_u").val(ward_id).trigger('change');
                            }
                        })
                    }
                })
            } else {
                $("#province_u").val(null).trigger('change');
                $("#district_u").val(null).trigger('change');
                $("#ward_u").val(null).trigger('change');
            }

            $("input[name=sex][value=" + data.student.studentByProfile.sex + "]").prop('checked', true);

            $("#student_update").modal("show");
        }, error: (xhr, status, error) => {
            var err = eval("(" + xhr.responseText + ")");
            $("#student_update").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
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
            } else {
                toastr.success('Get detail fail')
            }
        }
    });
}

var OnUpdateSubmit = () => {
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
    for (i = 0; i < sex.length; i++) {
        if (sex[i].checked) {
            sexValue = sex[i].value
        }
    }
    var profile = {
        "id": profileId,
        "firstName": firstName,
        "lastName": lastName,
        "dob": dob,
        "provinceId": provinceId,
        "districtId": districtId,
        "wardId": wardId,
        "address": address,
        "phone": phone,
        "email": email,
        "identityCard": identityCard,
        "sex": sexValue,
        "accountId": accountId
    }
    formData.append('profile', JSON.stringify(profile))
    $('#spinner-divI').show();

    //method rule
    $.validator.addMethod("valueNotEquals", function (value, element, arg) {
        return arg !== value;
    }, "Value must not equal arg.");

    $.validator.addMethod("checkAge", function (value, element) {
        let date = new Date(value)
        let age = _calculateAge(date)
        return age >= 18;
    }, "Age must be greater than 18");

    $.validator.addMethod("checkPhoneNumber", function (value, element) {
        return regexPhone(value);
    }, "Please enter incorrect phone");

    $('#form_student_update').validate({
        rules: {
            firstName_u: {
                required: true
            },
            lastName_u: {
                required: true
            },
            phone_u: {
                checkPhoneNumber: true,
                required: true
            }
            , dob_u: {
                checkAge: true,
                required: true
            },
            email_u: {
                required: true,
                email: true
            },
            identityCard_u: {
                required: true
            },
            province_u: {
                valueNotEquals: ""
            },
            district_u: {
                valueNotEquals: ""
            },
            ward_u: {
                valueNotEquals: ""
            },
            address_u: {
                required: true
            },
        },
        messages: {
            firstName_u: {
                required: "Please enter first name"
            },
            lastName_u: {
                required: "Please enter last name"
            },
            phone_u: {
                checkPhoneNumber: "Please enter incorrect phone",
                required: "Please enter phone numbers "
            }, dob_u: {
                checkAge: "Age must be greater than 18",
                required: "Please enter date of birth "
            },
            email_u: {
                required: "Please enter email ",
                email: "Email wrong format xxxx@xxx.xxx"
            },
            identityCard_u: {
                required: "Please enter identity card "
            },
            province_u: {
                valueNotEquals: "Please enter province "
            },
            district_u: {
                valueNotEquals: "Please enter district "
            },
            ward_u: {
                valueNotEquals: "Please enter ward "
            },
            address_u: {
                required: "Please enter address"
            },
        },
    })
    if ($('#form_student_update').valid()) {
        $.ajax({
            url: "/dashboard/student/student_update",
            method: "POST",
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            success: (result) => {
                console.log(result)
                $("#student_update").modal("hide");
                $('#spinner-divI').hide();
                location.reload();
                toastr.success('Cập nhật sinh viên thành công')
            },
            error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
                console.log(err)
                if (err.message.toLowerCase() === "token expired") {
                    $('#spinner-divI').hide();
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
                } else {
                    toastr.success('Update fail')
                }
            }
        })
    }
}
const imageProfile = document.getElementById('st_image_u')

function selectFile() {
    // alert("click")
    $('#fileAvatar').trigger('click');
}

function previewImage() {
    imageProfile.src = URL.createObjectURL(event.target.files[0]);
    $('#btn-function').show()
    $('#btn-updateImg').hide()
}

var CancelUpdateImg = () => {
    $('#btn-function').hide()
    $('#btn-updateImg').show()
    $('#st_image_u').attr('src', $('#img').val())
}

var OnUpdateImg = () => {

    ConfirmImg('Change picture', 'Are you sure change picture?', 'Confirm', 'Cancel')
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
        var avatarUrl = document.getElementById("fileAvatar")
        var profileId = $('#profileId_u').val()
        let formData = new FormData();
        formData.append('file', avatarUrl.files[0]);
        formData.append('id', profileId)
        $('#spinner-divI_2').show()
        $.ajax({
            url: "/dashboard/student/changeImg",
            method: "POST",
            enctype: 'multipart/form-data',
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            success: (data) => {
                $('#spinner-divI_2').hide()
                location.reload();
                toastr.success('Change success')
            }, error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
                console.log(err)
                if (err.message.toLowerCase() === "token expired") {
                    $('#spinner-divI_2').hide()
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
                } else {
                    alert("Change fail");
                }
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

const OnChooseFile = () => {
    $("#fileStudent").trigger("click");
}

const OnImportStudent = () => {
    let file = $("#fileStudent").get(0).files[0];
    if (file !== undefined) {
        let formData = new FormData();
        formData.append("file", file);
        $.ajax({
            url: "/dashboard/student/import-excel",
            data: formData,
            method: "POST",
            processData: false,
            contentType: false,
            enctype: "multipart/form-data",
            beforeSend: () => {
                $("#class_import_student_file").modal("hide");
                var sweet_loader = `<div id="spinner-divI_3">
                                        <div class="spinner-border m-0 text-primary" role="status"></div>
                                    </div>`
                Swal.fire({
                    title: 'Importing student',
                    html: sweet_loader,// add html attribute if you want or remove
                    allowOutsideClick: false,
                    showConfirmButton: false,
                    customClass: "swal-height",
                });
            },
            success: (data) => {
                setTimeout(()=>{
                    Swal.close();
                    toastr.success(data);
                },1500)
                setTimeout(() => {
                    location.reload();
                }, 1500);
            },
            error: (data) => {
                if (data.responseText.toLowerCase() === "token expired") {
                    Swal.close();
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
                } else {
                    Swal.close();
                    toastr.error(data.responseText);
                }
            }
        });
    }
}
