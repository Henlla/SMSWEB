$(()=>{
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
        let data = new FormData()
        data.append("roomCode",roomCode)

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
                    Swal.fire(
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
    let data = new FormData()
    data.append("shift",shift)

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


