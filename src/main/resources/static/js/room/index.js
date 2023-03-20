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
                }
            })
        }
    })
})


