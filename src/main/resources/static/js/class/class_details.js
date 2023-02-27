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
            toastr.error('Tạo thời khóa biểu thành công')
            $('#create_schedule').modal("hide")
            $('#spinner-div').hide();
        }
    })
}

$(document).ready(function () {
    $('#semesterSchedule').on('change', function () {
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
                console.log(res)
                let shift = $('#shift').val().substring(0, 1)
                if(res !== ''){
                    var time = ''
                    // console.log(shift)
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
                    // let tbody = document.getElementById("timetable")
                    let table = document.getElementById("schedule_table")
                    $('#schedule_table tbody').remove()
                    let tbody = document.createElement("tbody")


                    for (let i = 0; i < arr.length; i++) {
                        const tr = document.createElement("tr")
                        console.log(arr[i])
                        const td0 = document.createElement("td")
                        const td1 = document.createElement("td")
                        const td2 = document.createElement("td")
                        const td3 = document.createElement("td")
                        const td4 = document.createElement("td")
                        const td5 = document.createElement("td")
                        const td6 = document.createElement("td")
                        for (let j = 0; j < arr[i].list.length; j++) {
                            console.log(arr[i].list[j])
                            switch (arr[i].list[j].dayOfWeek) {
                                case "MONDAY":
                                    td0.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div>
                                        <div class="font-size13 text-light-gray">${time}</div>
                                        `
                                    break;
                                case "TUESDAY":
                                    td1.innerHTML =
                                        `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div><div class="font-size13 text-light-gray">${time}</div>`
                                    break;
                                case "WEDNESDAY":
                                    td2.innerHTML =
                                        `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div><div class="font-size13 text-light-gray">${time}</div>`
                                    break;
                                case "THURSDAY":
                                    td3.innerHTML =
                                        `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div><div class="font-size13 text-light-gray">${time}</div>`
                                    break;
                                case "FRIDAY":
                                    td4.innerHTML =
                                        `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div><div class="font-size13 text-light-gray">${time}</div>`

                                    break;
                                case "SATURDAY":
                                    td5.innerHTML =
                                        `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${arr[i].list[j].subject.subjectCode}</span>
                                    <div class="margin-10px-top font-size14">${arr[i].list[j].date}</div><div class="font-size13 text-light-gray">${time}</div>`

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
                    }
                }else{
                    Swal.fire(
                        "",
                        "Chưa có thời khóa biểu kỳ "+$('#semesterSchedule').val(),
                        "error"
                    )
                }


            }, error: (er) => {

            }
        })
    });
});
