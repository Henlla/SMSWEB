$(()=>{
    $('.timetable').hide()
    $('#week').select2({
        theme:'bootstrap4'
    })
})


var OnDetailSchedule = ()=>{
    let classId = $('#classId').val()
    let semester = $('#semesterSchedule').val()
    let data = new FormData()
    data.append("classId",classId)
    data.append("semester",semester)
    $.ajax({
        url:"/student/getScheduleDetails",
        data:data,
        method:"POST",
        contentType: false,
        cache: false,
        processData: false,
        success:(res)=>{
            let shift = $('#shift').val().substring(0, 1)
            let arrTime = []
            if(shift==='M'){
                arrTime.push('8:00-10:00')
                arrTime.push('10:00-12:00')
            }else if(shift==='A'){
                arrTime.push('12:30-15:30')
                arrTime.push('15:30-17:30')
            }else{
                arrTime.push('17:30-19:30')
                arrTime.push('19:30-21:30')
            }
            if (res !== '') {
                $('.cbx_week').show()
                $('.timetable').hide()
                $('#schedule_table').show()
                const list = Object.values(res);
                let table = document.getElementById("schedule_table")
                $('#schedule_table tbody').remove()
                let tbody = document.createElement("tbody")
                for (let i of list) {
                    const tr = document.createElement("tr")

                    const td0_1 = document.createElement("td")
                    td0_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td0_2 = document.createElement("td")
                    td0_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    const td1_1 = document.createElement("td")
                    td1_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td1_2 = document.createElement("td")
                    td1_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    const td2_1 = document.createElement("td")
                    td2_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td2_2 = document.createElement("td")
                    td2_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    const td3_1 = document.createElement("td")
                    td3_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td3_2 = document.createElement("td")
                    td3_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    const td4_1 = document.createElement("td")
                    td4_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td4_2 = document.createElement("td")
                    td4_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    const td5_1 = document.createElement("td")
                    td5_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td5_2 = document.createElement("td")
                    td5_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    // const td6_1 = document.createElement("td")
                    // td6_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    // const td6_2 = document.createElement("td")
                    // td6_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`

                    for (let j of i.list) {
                        switch (j.dayOfWeek) {
                            case "MONDAY":
                                if (j.slot === 1) {
                                    td0_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td0_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            case "TUESDAY":
                                if (j.slot === 1) {
                                    td1_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td1_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            case "WEDNESDAY":
                                if (j.slot === 1) {
                                    td2_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td2_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            case "THURSDAY":
                                if (j.slot === 1) {
                                    td3_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td3_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            case "FRIDAY":
                                if (j.slot === 1) {
                                    td4_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td4_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            case "SATURDAY":
                                if (j.slot === 1) {
                                    td5_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                } else {
                                    td5_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                      <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                                    break;
                                }
                            // case "SUNDAY":
                            //     if (j.slot === 1) {
                            //         td6_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                            //           <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                            //           <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                            //                   onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                            //           <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                            //         break;
                            //     } else {
                            //         td6_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                            //           <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                            //           <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                            //                   onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                            //           <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                            //         break;
                            //     }
                            case "1":
                                if (j.slot === 1) {
                                    td0_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td0_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            case "2":
                                if (j.slot === 1) {
                                    td1_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td1_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            case "3":
                                if (j.slot === 1) {
                                    td2_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td2_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            case "4":
                                if (j.slot === 1) {
                                    td3_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td3_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            case "5":
                                if (j.slot === 1) {
                                    td4_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td4_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            case "6":
                                if (j.slot === 1) {
                                    td5_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                } else {
                                    td5_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                    break;
                                }
                            // case "7":
                            //     if (j.slot === 1) {
                            //         td6_1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                            //         break;
                            //     } else {
                            //         td6_2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                            //         break;
                            //     }
                        }
                    }
                    tr.appendChild(td0_1)
                    tr.appendChild(td0_2)
                    tr.appendChild(td1_1)
                    tr.appendChild(td1_2)
                    tr.appendChild(td2_1)
                    tr.appendChild(td2_2)
                    tr.appendChild(td3_1)
                    tr.appendChild(td3_2)
                    tr.appendChild(td4_1)
                    tr.appendChild(td4_2)
                    tr.appendChild(td5_1)
                    tr.appendChild(td5_2)
                    // tr.appendChild(td6_1)
                    // tr.appendChild(td6_2)
                    tbody.appendChild(tr)
                    table.appendChild(tbody)
                    $('.btn_update_date').css('display', 'none')

                }

            } else {
                Swal.fire(
                    "",
                    "No data for semseter " + $('#semesterSchedule').val(),
                    "error"
                )
            }

        }, error: (xhr, status, error) => {
            var err = eval("(" + xhr.responseText + ")");
            $("#student_update").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
                Swal.fire({
                    title: 'Out of login version, please login again',
                    showDenyButton: false,
                    showCancelButton: false,
                    confirmButtonText: 'Ok',
                }).then((result) => {
                    if (result.isConfirmed) {
                        location.href = "/dashboard/login";
                    }
                })
            } else {
                toastr.success('View schedule failed')
            }
        }
    })
}

var OnChangeWeek = ()=>{
    let week = $('#week').val()
    let classId = $('#classId').val()
    let semester = $('#semesterSchedule').val()
    let data = new FormData()
    data.append("week",week)
    data.append("classId",classId)
    data.append("semester",semester)

    $.ajax({
        url:"/student/viewScheduleByWeek",
        data:data,
        method: "POST",
        contentType: false,
        cache: false,
        processData: false,
        success:(res)=>{
                console.log(res)
                if(res == ''){
                    Swal.fire(
                        "",
                        "No data for this week",
                        "error"
                    )
                }else{
                    $('#schedule_table').hide()
                    $('.timetable').show()
                    let shift = $('#shift').val().substring(0,1)
                    let arrDate = []
                    let formatDate = ""
                    for(let i of res){
                        switch(i.dayOfWeek){
                            case "MONDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_monday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_1').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_1').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_1').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_1').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_1').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "TUESDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_tuesday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_2').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_2').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_2').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_2').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_2').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "WEDNESDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_wednesday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_3').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_3').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_3').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_3').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_3').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "THURSDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_thursday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_4').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_4').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_4').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_4').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_4').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "FRIDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_friday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_5').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_5').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_5').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "SATURDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_saturday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r2_6').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_6').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r4_6').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_6').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }else{
                                        $('.r6_6').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                    }
                                }
                                break;
                            case "SUNDAY":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_sunday').html(formatDate)
                                    if(shift==="M"){
                                        if(i.slot==1){
                                            $('.r1_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }else{
                                            $('.r2_7').html(` <div class="accent-orange-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }
                                    }else if(shift==="A"){
                                        if(i.slot==1){
                                            $('.r3_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }else{
                                            $('.r4_7').html(` <div class="accent-green-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }
                                    }else if(shift==="E"){
                                        if(i.slot==1){
                                            $('.r5_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }else{
                                            $('.r6_7').html(` <div class="accent-cyan-gradient" style="display:flex;justify-content:center;align-items:center;flex-direction: column"><span>${i.subject.subjectCode}</span><span>Room: ${$("#classRoom").val()}</span></div>`)
                                        }
                                    }
                                    break;
                            case "1":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_monday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_1').html(``)
                                    }else{
                                        $('.r2_1').html(``)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_1').html(``)
                                    }else{
                                        $('.r4_1').html(``)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_1').html(``)
                                    }else{
                                        $('.r6_1').html(``)
                                    }
                                }
                                break;
                            case "2":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_tuesday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_2').html(` `)
                                    }else{
                                        $('.r2_2').html(` `)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_2').html(` `)
                                    }else{
                                        $('.r4_2').html(` `)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_2').html(` `)
                                    }else{
                                        $('.r6_2').html(` `)
                                    }
                                }
                                break;
                            case "3":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_wednesday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_3').html(` `)
                                    }else{
                                        $('.r2_3').html(` `)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_3').html(` `)
                                    }else{
                                        $('.r4_3').html(` `)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_3').html(``)
                                    }else{
                                        $('.r6_3').html(` `)
                                    }
                                }
                                break;
                            case "4":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_thursday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_4').html(` `)
                                    }else{
                                        $('.r2_4').html(` `)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_4').html(` `)
                                    }else{
                                        $('.r4_4').html(` `)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_4').html(``)
                                    }else{
                                        $('.r6_4').html(` `)
                                    }
                                }
                                break;
                            case "5":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_friday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_5').html(``)
                                    }else{
                                        $('.r2_5').html(` `)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_5').html(` `)
                                    }else{
                                        $('.r4_5').html(` `)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_5').html(` `)
                                    }else{
                                        $('.r6_5').html(` `)
                                    }
                                }
                                break;
                            case "6":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_saturday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_6').html(` `)
                                    }else{
                                        $('.r2_6').html(` `)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_6').html(` `)
                                    }else{
                                        $('.r4_6').html(``)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_6').html(` `)
                                    }else{
                                        $('.r6_6').html(` `)
                                    }
                                }
                                break;
                            case "7":
                                arrDate = i.date.split("-")
                                formatDate = arrDate[2] + "/"+arrDate[1]
                                $('.date_sunday').html(formatDate)
                                if(shift==="M"){
                                    if(i.slot==1){
                                        $('.r1_7').html(` `)
                                    }else{
                                        $('.r2_7').html(``)
                                    }
                                }else if(shift==="A"){
                                    if(i.slot==1){
                                        $('.r3_7').html(` `)
                                    }else{
                                        $('.r4_7').html(` `)
                                    }
                                }else if(shift==="E"){
                                    if(i.slot==1){
                                        $('.r5_7').html(` `)
                                    }else{
                                        $('.r6_7').html(` `)
                                    }
                                }
                                break;
                        }
                    }
                }
        }, error: (xhr, status, error) => {
            var err = eval("(" + xhr.responseText + ")");
            $("#student_update").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
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
            } else {
                toastr.success('View schedule by week failed')
            }
        }
    })

}