$(()=>{
    $('#apartment').select2({
        theme:'bootstrap4'
    })
    $("#department-table").dataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollCollapse: true,
        scrollY: '300px',
        columnDefs: [
            {
                "targets": [0, 1, 2, 3, 4],
                "className": "text-center"
            },
            {
                "targets": [1, 2, 4],
                "orderable": false,
            }
        ],
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
        }
    });

})


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

let OnDetails = (id)=>{
    $('#view_department_modal').modal()
    $('#department').val(id)
}

let OnGetClass=()=>{
    let department_id = $('#department').val()
    let apartment_id = $('#apartment').val()
    let data = new FormData()
    data.append("departmentId",department_id)
    data.append("apartmentId",apartment_id)

    $.ajax({
        url:"/dashboard/department/viewDepartment",
        method:"POST",
        data:data,
        cache : false,
        processData: false,
        contentType: false,
        success:(res)=>{
            console.log(res)
            let container = document.getElementsByClassName("data")[0]
            container.innerHTML=""
            let row = document.createElement("div")
            row.classList.add("row")
            container.appendChild(row)

            for (let i of res){
                let col = document.createElement("div")
                col.classList.add("col-6")
                col.innerHTML = `<span><i class="fas fa-dot-circle"></i> <a href='/dashboard/class/class-details/${i.classCode}'>${i.classCode}</a></span>`
                row.appendChild(col)
            }

        }
    })
}