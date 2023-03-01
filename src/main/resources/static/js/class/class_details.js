var OnCreateSchedule = (classId, majorId, shift) => {
    console.log(classId, majorId);
    let semester = $('#semester').val()
    let startDate = $('#startDate').val()
    let data = new FormData();
    data.append("semester", semester)
    data.append("startDate", startDate)
    data.append("classId", classId)
    data.append("majorId", majorId)
    data.append("shift", shift)
    $.ajax({
        url: "/dashboard/class/create_schedule",
        data: data,
        method: "POST",
        cache: false,
        processData: false,
        contentType: false,
        success: (res) => {
            toastr.success('Tạo thời khóa biểu thành công')
            $('#create_schedule').modal("hide")
            $('#spinner-div').hide();
        }, error: (e) => {
            toastr.error('Tạo thời khóa biểu thất bại')
            $('#create_schedule').modal("hide")
            $('#spinner-div').hide();
        }
    })
}

$(document).ready(function () {
    $('#btn_update_schedule').css('display','none')
    // $('#semesterSchedule').on('change', function () {
    //
    // });



    let i_newDate = $('.newDate')
    let msgError =  $('#msg_error')
    msgError.css('display','none')


    i_newDate.on('focus',()=>{
        msgError.css('display','none')
    })



    $('#btn_submitChangeDate').on('click',()=>{
        if(i_newDate.val()===''){
            msgError.css('display','block')
            msgError.html('Vui lòng chọn ngày thay đổi')
        }else{
            console.log($('#classId').val())
            console.log($('#semesterSchedule').val())
            let data = new FormData()
            data.append('currenDate',i_newDate.val())
            data.append('classId',$('#classId').val())
            data.append('semester',$('#semesterSchedule').val())
            $.ajax({
                url:"/dashboard/class/changeDateSchedule",
                method:"POST",
                data:data,
                contentType: false,
                cache: false,
                processData: false,
                success:(res)=>{
                    console.log(res)
                    if(res.toLowerCase() ==="error"){
                        Swal.fire(
                            "",
                            "Ngày thay đổi đã tồn tại , Vui lòng chọn lại ",
                            "error"
                        )
                    }else {
                        let data2 = new FormData()
                        data2.append("schedule_details_id",$('#schedule_details_id').val())
                        data2.append("newDate",i_newDate.val())
                        $.ajax({
                            url:"/dashboard/class/updateDateChangeSchedule",
                            method:"POST",
                            data:data2,
                            contentType: false,
                            cache: false,
                            processData: false,
                            success:(res)=>{
                                if(res.toLowerCase() ==="error"){
                                    Swal.fire(
                                        "",
                                        "Thay đổi thất bại",
                                        "error"
                                    )
                                }else{
                                    $('#schedule_update').modal("hide")
                                    Swal.fire(
                                        "",
                                        "Thay đổi ngày thành công",
                                        "success"
                                    )
                                    $('#btn_update_schedule').html('Chỉnh sửa')
                                    OnChangeSemesterSchedule();
                                }
                            },error:(xhr, status, error)=>{
                                var err = eval("(" + xhr.responseText + ")");
                                $("#student_update").modal("hide");
                                console.log(err)
                                if (err.message.toLowerCase() === "token expired") {
                                    Swal.fire({
                                        title: 'Hết phiên đăng nhập vui lòng đăng nhập lại',
                                        showDenyButton: false,
                                        showCancelButton: false,
                                        confirmButtonText: 'Đồng ý',
                                    }).then((result) => {
                                        if (result.isConfirmed) {
                                            location.href = "/dashboard/login";
                                        }
                                    })
                                }else{
                                    toastr.success('Xem thông tin thất bại')
                                }
                            }
                        })
                    }
                },error:(xhr, status, error)=>{
                    var err = eval("(" + xhr.responseText + ")");
                    $("#student_update").modal("hide");
                    console.log(err)
                    if (err.message.toLowerCase() === "token expired") {
                        Swal.fire({
                            title: 'Hết phiên đăng nhập vui lòng đăng nhập lại',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Đồng ý',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.href = "/dashboard/login";
                            }
                        })
                    }else{
                        toastr.success('Xem thông tin thất bại')
                    }
                }
            })
        }
    })



    let flag = true;
    $('#btn_update_schedule').on('click',()=>{
        if(flag===true){
            $('#btn_update_schedule').html('Hủy chỉnh sửa')
            $('.btn_update_date').css('display','block')
            flag = !flag;
        }else{
            $('#btn_update_schedule').html('Chỉnh sửa')
            $('.btn_update_date').css('display','none')
            flag = !flag;
        }
    })
});

var OnUpdateDate = (id,date)=>{
    // console.log(id)
    // console.log(date)
    $('#schedule_update').modal("show")
    $('.day_default').val(date)
    $('#schedule_details_id').val(id)

}

var OnChangeSemesterSchedule = ()=>{
    let data = new FormData()
    data.append("semester", $('#semesterSchedule').val())
    data.append("classId", $('#classId').val())
    $.ajax({
        url: `/dashboard/class/getScheduleDetails`,
        data: data,
        method: "POST",
        contentType: false,
        cache: false,
        processData: false,
        success: (res) => {
            $('#btn_update_schedule').css('display','block')
            let shift = $('#shift').val().substring(0, 1)
            if(res !== ''){
                var time = ''
                // console.log(res)
                switch (shift) {
                    case "M":
                        time = "7h30 - 11h30"
                        break;
                    case "A":
                        time = "13h30 - 17h30"
                        break;
                    case "E":
                        time = "17h30 - 21h30"
                        break;
                }
                const groups = res.dayInWeeks.reduce((groups, {weekOfYear, ...rest}) => {
                    const key = `${weekOfYear}`;
                    groups[key] = groups[key] || {weekOfYear, list: []}
                    groups[key]["list"].push(rest);
                    return groups;
                }, {});
                console.log(groups)
                const arr = Object.values(groups)
                console.log(arr)
                let table = document.getElementById("schedule_table")
                $('#schedule_table tbody').remove()
                let tbody = document.createElement("tbody")

                for (let i = 0; i < arr.length; i++) {
                    const tr = document.createElement("tr")
                    console.log(arr[i])
                    const td0 = document.createElement("td")
                    td0.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td1 = document.createElement("td")
                    td1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td2 = document.createElement("td")
                    td2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td3 = document.createElement("td")
                    td3.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td4 = document.createElement("td")
                    td4.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td5 = document.createElement("td")
                    td5.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    const td6 = document.createElement("td")
                    td6.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                    for (let j = 0; j < arr[i].list.length; j++) {
                        // console.log(arr[i].list[j])
                        switch (arr[i].list[j].dayOfWeek) {
                            case "MONDAY":
                                td0.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                    <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "TUESDAY":
                                td1.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                    <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "WEDNESDAY":
                                td2.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date}
                                     <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                     <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "THURSDAY":
                                td3.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                     <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "FRIDAY":
                                td4.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                     <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "SATURDAY":
                                td5.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                     <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "SUNDAY":
                                td6.innerHTML =
                                    `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${arr[i].list[j].date} 
                                     <a class="ml-1 btn_update_date"><i data-id="${arr[i].list[j].id}" data-date="${arr[i].list[j].date}" 
                                            onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'))" class="fas fa-pencil-alt"></i></a></div>
                                    <div class="font-size13 text-light-gray">${time}</div>`
                                break;
                            case "1":
                                td0.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "2":
                                td1.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "3":
                                td2.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "4":
                                td3.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "5":
                                td4.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "6":
                                td5.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                            case "7":
                                td6.innerHTML = `<div style="display: flex;justify-content: center;height: 70px;align-items: center">X</div>`
                                break;
                        }
                    }
                    tr.appendChild(td0)
                    tr.appendChild(td1)
                    tr.appendChild(td2)
                    tr.appendChild(td3)
                    tr.appendChild(td4)
                    tr.appendChild(td5)
                    tr.appendChild(td6)
                    tbody.appendChild(tr)
                    table.appendChild(tbody)
                    $('.btn_update_date').css('display','none')
                }
            }else{
                Swal.fire(
                    "",
                    "Chưa có thời khóa biểu kỳ "+$('#semesterSchedule').val(),
                    "error"
                )
            }


        },  error:(xhr, status, error)=>{
            var err = eval("(" + xhr.responseText + ")");
            $("#student_update").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
                Swal.fire({
                    title: 'Hết phiên đăng nhập vui lòng đăng nhập lại',
                    showDenyButton: false,
                    showCancelButton: false,
                    confirmButtonText: 'Đồng ý',
                }).then((result) => {
                    if (result.isConfirmed) {
                        location.href = "/dashboard/login";
                    }
                })
            }else{
                toastr.success('Xem thông tin thất bại')
            }
        }
    })
}
