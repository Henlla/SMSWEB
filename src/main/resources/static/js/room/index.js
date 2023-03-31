$(()=>{
    $("#room-table").DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
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
        }
    })
    $("#room_create-form").validate({
        rules: {
            room_code_validate: {
                required: true
            },
        },
        messages: {
            room_code_validate: {
                required: "Please enter room code"
            },
        },
    });

    $('#btn_create_room').on('click',()=>{
        let roomCode = $('#room_code').val()
        let department = $('#department').val()
        let data = new FormData()

        let room = {
            "roomCode":roomCode,
            "departmentId":department
        }

        data.append("room",JSON.stringify(room))

        if ($("#room_create-form").valid()) {
            $.ajax({
                url:"/dashboard/room/create_room",
                method:"POST",
                data:data,
                cache : false,
                processData: false,
                contentType: false,
                success:(res)=>{
                    console.log(res)
                    location.reload();
                    Swal.fire(
                        "",
                        'Create room success',
                        'success'
                    )
                    $('#room_create').modal('hide')
                }
            })
        }
    })
})

let OnClickViewRoom = (id)=>{
    console.log(id)
    $('#room_view').modal('show')

    let data= new FormData()
    data.append("id",id)

    $.ajax({
        url:"/dashboard/room/details",
        method:"POST",
        data:data,
        cache : false,
        processData: false,
        contentType: false,
        success:(res)=>{
            $('.m0').html(`<span style="color:green">Available</span>`)
            $('.a0').html(`<span style="color:green">Available</span>`)
            $('.e0').html(`<span style="color:green">Available</span>`)
            $('.m1').html(`<span style="color:green">Available</span>`)
            $('.a1').html(`<span style="color:green">Available</span>`)
            $('.e1').html(`<span style="color:green">Available</span>`)
            for(var i of res){
                if(i.shift ==="M0"){
                    $('.m0').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }else if(i.shift ==="A0"){
                    $('.a0').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }else if(i.shift ==="E0"){
                    $('.e0').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }else if(i.shift ==="M1"){
                    $('.m1').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }else if(i.shift ==="A1"){
                    $('.a1').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }else if(i.shift ==="E1"){
                    $('.e1').html(`<span>${i.classCode} - ${i.teacher.profileByProfileId.firstName} ${i.teacher.profileByProfileId.lastName}</span>`)
                }

            }
        }
    })
}

let OnChangeShiftCheckRoom = ()=>{
    let shift = $('#shift').val()
    let department = $('#department_id').val()
    console.log(department)
    let data = new FormData()
    data.append("shift",shift)
    data.append("department_id",department)

    $.ajax({
        url:"/dashboard/room/checkShiftRoom",
        method:"POST",
        data:data,
        cache : false,
        processData: false,
        contentType: false,
        success:(res)=>{
            console.log(res)
            const dataDiv = document.getElementsByClassName("data")[0]
            dataDiv.innerHTML = '';
            for(let i of res){
                const row = document.createElement("div")
                row.classList.add("row")
                const col1 = document.createElement("div")
                col1.classList.add("col-6")
                const col2 = document.createElement("div")
                col2.classList.add("col-6")

                col1.innerHTML = `<span><i class="fas fa-dot-circle"></i> ${i.roomCode}</span>`
                col2.innerHTML = `<span style="color:green">Available</span>`

                row.append(col1)
                row.append(col2)
                dataDiv.appendChild(row)
            }
        }
    })
}

let OnChangeDepartment = ()=>{
    $('.list-shift').show()
    $('#shift').val("")
    const dataDiv = document.getElementsByClassName("data")[0]
    dataDiv.innerHTML = '';
}


