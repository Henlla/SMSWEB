$(()=>{
    let data = JSON.parse($('#scheduleList').val())
    $('#week').select2()
    console.log(data)
    let shift =""
    for(let i of data){
        switch(i.dayOfWeek){
            case "MONDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_monday').html(formatDate)
                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r4_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r6_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }
                break;
            case "TUESDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_tuesday').html(formatDate)

                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r4_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r6_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }
                break;
            case "WEDNESDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_wednesday').html(formatDate)
                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r4_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r6_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }
                break;
            case "THURSDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_thursday').html(formatDate)
                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r4_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r6_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }
                break;
            case "FRIDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_friday').html(formatDate)
                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                    }else{
                        $('.r4_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                    }else{
                        $('.r6_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                    }
                }
                break;
            case "SATURDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_saturday').html(formatDate)
                if(shift==="M"){
                    if(i.slot==1){
                        $('.r1_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r2_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="A"){
                    if(i.slot==1){
                        $('.r3_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r4_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }else if(shift==="E"){
                    if(i.slot==1){
                        $('.r5_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }else{
                        $('.r6_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                    }
                }
                break;
            case "SUNDAY":
                arrDate = i.date.split("-")
                formatDate = arrDate[2] + "/"+arrDate[1]
                shift = i.shift.substring(0,1)
                $('.date_sunday').html(formatDate)
                    if(shift==="M"){
                        if(i.slot==1){
                            $('.r1_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }else{
                            $('.r2_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }
                    }else if(shift==="A"){
                        if(i.slot==1){
                            $('.r3_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }else{
                            $('.r4_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }
                    }else if(shift==="E"){
                        if(i.slot==1){
                            $('.r5_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }else{
                            $('.r6_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                        }
                    }
                    break;
        }
    }
    
})

let OnChangeWeek = ()=>{
    let week = $('#week').val()
    let classCode =$('#classCode').val()
    let data = new FormData()
    data.append("week",week)
    data.append("classCode",classCode)
    $.ajax({
        url:"/teacher/viewTeachingScheduleByWeek",
        data :data,
        method: "POST",
        contentType: false,
        cache: false,
        processData: false,
        success:(res)=>{
            let arrDate = []
            let formatDate = ""
            if(res === "token expired"){
                    Swal.fire({
                        title: 'Out of login version, please login again',
                        showDenyButton: false,
                        showCancelButton: false,
                        confirmButtonText: 'Ok',
                    }).then((result) => {
                        if (result.isConfirmed) {
                            location.href = "/login";
                        }
                    })
            }else if(res ==="error"){
                Swal.fire(
                    "",
                    "No data for this week",
                    "error"
                )
            }else{
                $('.r1_1').html('')
                $('.r1_2').html('')
                $('.r1_3').html('')
                $('.r1_4').html('')
                $('.r1_5').html('')
                $('.r1_6').html('')
                $('.r1_7').html('')
                
                $('.r2_1').html('')
                $('.r2_2').html('')
                $('.r2_3').html('')
                $('.r2_4').html('')
                $('.r2_5').html('')
                $('.r2_6').html('')
                $('.r2_7').html('')

                $('.r3_1').html('')
                $('.r3_2').html('')
                $('.r3_3').html('')
                $('.r3_4').html('')
                $('.r3_5').html('')
                $('.r3_6').html('')
                $('.r3_7').html('')


                $('.r4_1').html('')
                $('.r4_2').html('')
                $('.r4_3').html('')
                $('.r4_4').html('')
                $('.r4_5').html('')
                $('.r4_6').html('')
                $('.r4_7').html('')


                $('.r5_1').html('')
                $('.r5_2').html('')
                $('.r5_3').html('')
                $('.r5_4').html('')
                $('.r5_5').html('')
                $('.r5_6').html('')
                $('.r5_7').html('')

                $('.r6_1').html('')
                $('.r6_2').html('')
                $('.r6_3').html('')
                $('.r6_4').html('')
                $('.r6_5').html('')
                $('.r6_6').html('')
                $('.r6_7').html('')
                
                $('.date_monday').html('')
                $('.date_tuesday').html('')
                $('.date_wednesday').html('')
                $('.date_thursday').html('')
                $('.date_friday').html('')
                $('.date_saturday').html('')
                $('.date_sunday').html('')
                let shift =""
                for(let i of res){
                    switch(i.dayOfWeek){
                        case "MONDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_monday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r4_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r6_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }
                            break;
                        case "TUESDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_tuesday').html(formatDate)

                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r4_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r6_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }
                            break;
                        case "WEDNESDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_wednesday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r4_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r6_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }
                            break;
                        case "THURSDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_thursday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r4_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r6_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }
                            break;
                        case "FRIDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_friday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }else{
                                    $('.r4_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }else{
                                    $('.r6_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }
                            }
                            break;
                        case "SATURDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_saturday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r2_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r4_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }else{
                                    $('.r6_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectName}</span><span>Class:${i.classCode}</span><span>Room:${i.roomCode}</span></div>`)
                                }
                            }
                            break;
                        case "SUNDAY":
                            arrDate = i.date.split("-")
                            formatDate = arrDate[2] + "/"+arrDate[1]
                            shift = i.shift.substring(0,1)
                            $('.date_sunday').html(formatDate)
                            if(shift==="M"){
                                if(i.slot==1){
                                    $('.r1_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }else{
                                    $('.r2_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }
                            }else if(shift==="A"){
                                if(i.slot==1){
                                    $('.r3_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }else{
                                    $('.r4_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }
                            }else if(shift==="E"){
                                if(i.slot==1){
                                    $('.r5_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }else{
                                    $('.r6_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center">${i.subject.subjectName}</div>`)
                                }
                            }
                            break;
                    }
                }
            }
                    
                    
        },error:(err)=>{

        }
    })
}