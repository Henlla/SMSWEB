let OnCreateDepartment = () => {
    let code = $('#create_department_code').val()
    let name = $('#create_department_name').val()
    let address = $('#create_department_address').val()
    let phone = $('#create_department_phone').val()

    let department = {
        "departmentCode": code,
        "departmentName": name,
        "address": address,
        "phone": phone
    }

    let data = new FormData()
    data.append("department",JSON.stringify(department))

    $('#create-form').validate({
        rules: {
            create_department_code: {
                required: true
            },
            create_department_name: {
                required: true
            },
            create_department_address: {
                required: true
            }
            ,create_department_phone: {
                required: true
            }
        },
        messages:{
            create_department_code : {
                required:"Please enter department code"
            },
            create_department_name : {
                required:"Please enter department name"
            },
            create_department_address: {
                required: "Please enter department address "
            }, create_department_phone: {
                required: "Please enter department phone "
            }
        },
    })
    if($('#create-form').valid()){

        $.ajax({
            url:"/dashboard/department/create_department",
            method:"POST",
            data:data,
            cache : false,
            processData: false,
            contentType: false,
            success:(res)=>{
                location.reload();
                Swal.fire(
                    "",
                    'Create department success',
                    'success'
                )
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


let OnDepartmentMajor = (id) =>{
    $.ajax({
        url:`/dashboard/department/find/${id}`,
        method: "GET",
        success:(res)=>{
            console.log(res)
            $('#edit-department-modal').modal('show')
            $('#edit_department_id').val(res.id)
            $('#edit_department_code').val(res.departmentCode)
            $('#edit_department_name').val(res.departmentName)
            $('#edit_address').val(res.address)
            $('#edit_phone').val(res.phone)
        }
    })
}

let OnUpdateDepartment = ()=>{
    let code = $('#edit_department_code').val()
    let id = $('#edit_department_id').val()
    let name = $('#edit_department_name').val()
    let address = $('#edit_address').val()
    let phone = $('#edit_phone').val()

    let department = {
        "id":id,
        "departmentCode": code,
        "departmentName": name,
        "address": address,
        "phone": phone
    }

    let data = new FormData()
    data.append("department",JSON.stringify(department))

    $('#edit-form').validate({
        rules: {
            edit_department_code: {
                required: true
            },
            edit_department_name: {
                required: true
            },
            edit_address: {
                required: true
            }
            ,edit_phone: {
                required: true
            }
        },
        messages:{
            edit_department_code : {
                required:"Please enter department code"
            },
            edit_department_name : {
                required:"Please enter department name"
            },
            edit_address: {
                required: "Please enter department address "
            }, edit_phone: {
                required: "Please enter department phone "
            }
        },
    })
    if($('#edit-form').valid()){
        $.ajax({
            url:"/dashboard/department/update",
            method:"POST",
            data:data,
            cache : false,
            processData: false,
            contentType: false,
            success:(res)=>{
                location.reload();
                Swal.fire(
                    "",
                    'Update department success',
                    'success'
                )
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