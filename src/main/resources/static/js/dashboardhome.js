function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}
let getOverviewlist = function (inputDate) {
    let data = new FormData();
    if (inputDate == "" || inputDate == null){
        data.append("date",formatDate(Date.now()));
    }else data.append("date",formatDate(inputDate));
    $.ajax({
        url: "/dashboard/view_room_active",
        method: "POST",
        data:data,
        cache: false,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        success: (response) => {
            $("#thead-view-room").empty();
            $("#tbody-view-room").empty();
            let parseData = JSON.parse(response);
            if(parseData.length >0){
                $(`<th class="col-1 text-black-50"></th>`).appendTo("#thead-view-room")
                parseData.forEach((room) =>{
                    $(`<th class="col text-bold text-lg text-center align-middle">${room.roomCode}</th>`).appendTo("#thead-view-room")
                })

                var totalCol = parseData.length + 1;
                var totalRow = 4;
                let table = document.getElementById("tbody-view-room");
                let row1 = table.insertRow();
                row1.classList.add("row");
                let row2 = table.insertRow();
                row2.classList.add("row");
                let row3 = table.insertRow();
                row3.classList.add("row");

                var cell1_1 = row1.insertCell();
                cell1_1.classList.add("col-1")
                cell1_1.innerHTML = "Morning";
                var cell1_2 = row2.insertCell();
                cell1_2.classList.add("col-1")
                cell1_2.innerHTML = "Afternoon";
                var cell1_3 = row3.insertCell();
                cell1_3.classList.add("col-1")
                cell1_3.innerHTML = "Evening";
                for (var i = 0 ; i< totalCol - 1 ; i++){
                    let room = parseData[i];
                    console.log(room)
                    let clazz1 = room.roomClass.filter(item => /^M\d{1}$/.test(item.shift))[0]
                    let schedule1 = new Object();
                    if(clazz1 != null || clazz1 != undefined){
                        schedule1 = {
                            classCode: clazz1.classCode,
                            teacherCard: clazz1.teacher.teacherCard,
                            teacherName: clazz1.teacher.profileByProfileId.firstName + " " + clazz1.teacher.profileByProfileId.lastName,
                            subjectName: clazz1.schedulesById[i].scheduleDetailsById[i].subjectBySubjectId.subjectName,
                            time: clazz1.schedulesById[0].scheduleDetailsById.length == 2 ? "8h - 12h" : "8h - 10h"
                        };
                    }else {
                        schedule1 = {
                            classCode: "",
                            teacherCard: "",
                            teacherName: "",
                            subjectName: "",
                            time: ""
                        };
                    }

                    let clazz2 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]
                    let schedule2 = new Object();
                    if(clazz2 != null || clazz2 != undefined){
                        schedule2 = {
                            classCode: clazz2.classCode,
                            teacherCard: clazz2.teacher.teacherCard,
                            teacherName: clazz2.teacher.profileByProfileId.firstName + " " + clazz2.teacher.profileByProfileId.lastName,
                            subjectName: clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName == null ?
                                "": clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                            time: clazz2.schedulesById[0].scheduleDetailsById.length == 2 ? "13h30 - 17h30" : "13h - 15h30"
                        };
                    }else {
                        schedule2 = {
                            classCode: "",
                            teacherCard: "",
                            teacherName: "",
                            subjectName: "",
                            time: ""
                        };
                    }

                    let clazz3 = room.roomClass.filter(item => /^E\d{1}$/.test(item.shift))[0]
                    let schedule3 = new Object();
                    if(clazz3 != null || clazz3 != undefined){
                        schedule3 = {
                            classCode: clazz3.classCode,
                            teacherCard: clazz3.teacher.teacherCard,
                            teacherName: clazz3.teacher.profileByProfileId.firstName + " " + clazz3.teacher.profileByProfileId.lastName,
                            subjectName: clazz3.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                            time: clazz3.schedulesById[0].scheduleDetailsById.length == 2 ? "17h30 - 21h" : "17h30 - 19h30"
                        };
                    }else {
                        schedule3 = {
                            classCode: "",
                            teacherCard: "",
                            teacherName: "",
                            subjectName: "",
                            time: ""
                        };
                    }
                    var cell2_1 = row1.insertCell();
                    cell2_1.classList.add("col");
                    cell2_1.classList.add("row");
                    cell2_1.classList.add("align-middle");
                    cell2_1.classList.add("text-center");
                    cell2_1.classList.add("text-md");
                    cell2_1.classList.add("text-bold");
                    cell2_1.innerHTML =`<div class="row">
                    <span class="badge badge-info text-md col-12">${schedule1.classCode}</span>
                    <span class="col-12">${schedule1.teacherName}</span>
                    <span class="col-12">${schedule1.subjectName}</span>
                    <span class="col-12">${schedule1.time}</span>
                </div>`;
                    var cell2_2 = row2.insertCell();
                    cell2_2.classList.add("col");
                    cell2_2.classList.add("row");
                    cell2_2.classList.add("align-middle");
                    cell2_2.classList.add("text-center");
                    cell2_2.classList.add("text-md");
                    cell2_2.classList.add("text-bold");
                    cell2_2.innerHTML = `<div class="row">
                            <span class="badge badge-info text-md col-12">${schedule2.classCode}</span>
                            <span class="col-12">${schedule2.teacherName}</span>
                            <span class="col-12">${schedule2.subjectName}</span>
                            <span class="col-12">${schedule2.time}</span>
                        </div>`;
                    var cell2_3 = row3.insertCell();
                    cell2_3.classList.add("col");
                    cell2_3.classList.add("row");
                    cell2_3.classList.add("align-middle");
                    cell2_3.classList.add("text-center");
                    cell2_3.classList.add("text-md");
                    cell2_3.classList.add("text-bold");
                    cell2_3.innerHTML = `
                        <div class="row">
                            <span class="badge badge-info text-md col-12">${schedule3.classCode}</span>
                            <span class="col-12">${schedule3.teacherName}</span>
                            <span class="col-12">${schedule3.subjectName}</span>
                            <span class="col-12">${schedule3.time}</span>
                        </div>`;
                }
            }
            else{
                $(`<th class="col">No Class active</th>`).appendTo("#thead-view-room")
            }
        },
        error: (error) => {
            $("#listMark").DataTable({
                columns:[
                    {title:'Student Name'},
                    {title:'Subject Name'},
                    {title:'ASM Mark'},
                    {title:'OBJ Mark'},
                    {title:'Status'}
                ]
            })
        }
    })
}
$(()=>{
    getOverviewlist();
    let overViewDate = $("#over_view_date");
    overViewDate.change(function (inputDate) {
        let data = new FormData();
        if (overViewDate.val() == "" || overViewDate.val() == null){
            data.append("date",formatDate(Date.now()));
        }else data.append("date",formatDate(overViewDate.val()));
        $.ajax({
            url: "/dashboard/view_room_active",
            method: "POST",
            data:data,
            cache: false,
            processData: false,
            contentType: false,
            enctype: "multipart/form-data",
            success: (response) => {
                console.log(response);
                $("#thead-view-room").empty();
                $("#tbody-view-room").empty();
                let parseData = JSON.parse(response);
                console.log(parseData);
                if(parseData.length >0){
                    $(`<th class="col-1 text-black-50"></th>`).appendTo("#thead-view-room")
                    parseData.forEach((room) =>{
                        $(`<th class="col text-bold text-lg text-center align-middle">${room.roomCode}</th>`).appendTo("#thead-view-room")
                    })

                    var totalCol = parseData.length + 1;
                    var totalRow = 4;
                    let table = document.getElementById("tbody-view-room");
                    let row1 = table.insertRow();
                    row1.classList.add("row");
                    let row2 = table.insertRow();
                    row2.classList.add("row");
                    let row3 = table.insertRow();
                    row3.classList.add("row");

                    var cell1_1 = row1.insertCell();
                    cell1_1.classList.add("col-1")
                    cell1_1.innerHTML = "Morning";
                    var cell1_2 = row2.insertCell();
                    cell1_2.classList.add("col-1")
                    cell1_2.innerHTML = "Afternoon";
                    var cell1_3 = row3.insertCell();
                    cell1_3.classList.add("col-1")
                    cell1_3.innerHTML = "Evening";
                    for (var i = 0 ; i< totalCol - 1 ; i++){
                        let room = parseData[i];
                        console.log(room)
                        let clazz1 = room.roomClass.filter(item => /^M\d{1}$/.test(item.shift))[0]
                        let schedule1 = new Object();
                        if(clazz1 != null || clazz1 != undefined){
                            schedule1 = {
                                classCode: clazz1.classCode,
                                teacherCard: clazz1.teacher.teacherCard,
                                teacherName: clazz1.teacher.profileByProfileId.firstName + " " + clazz1.teacher.profileByProfileId.lastName,
                                subjectName: clazz1.schedulesById[i].scheduleDetailsById[i].subjectBySubjectId.subjectName,
                                time: clazz1.schedulesById[0].scheduleDetailsById.length == 2 ? "8h - 12h" : "8h - 10h"
                            };
                        }else {
                            schedule1 = {
                                classCode: "",
                                teacherCard: "",
                                teacherName: "",
                                subjectName: "",
                                time: ""
                            };
                        }

                        let clazz2 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]
                        let schedule2 = new Object();
                        if(clazz2 != null || clazz2 != undefined){
                            schedule2 = {
                                classCode: clazz2.classCode,
                                teacherCard: clazz2.teacher.teacherCard,
                                teacherName: clazz2.teacher.profileByProfileId.firstName + " " + clazz2.teacher.profileByProfileId.lastName,
                                subjectName: clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName == null ?
                                    "": clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                time: clazz2.schedulesById[0].scheduleDetailsById.length == 2 ? "13h30 - 17h30" : "13h - 15h30"
                            };
                        }else {
                            schedule2 = {
                                classCode: "",
                                teacherCard: "",
                                teacherName: "",
                                subjectName: "",
                                time: ""
                            };
                        }

                        let clazz3 = room.roomClass.filter(item => /^E\d{1}$/.test(item.shift))[0]
                        let schedule3 = new Object();
                        if(clazz3 != null || clazz3 != undefined){
                            schedule3 = {
                                classCode: clazz3.classCode,
                                teacherCard: clazz3.teacher.teacherCard,
                                teacherName: clazz3.teacher.profileByProfileId.firstName + " " + clazz3.teacher.profileByProfileId.lastName,
                                subjectName: clazz3.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                time: clazz3.schedulesById[0].scheduleDetailsById.length == 2 ? "17h30 - 21h" : "17h30 - 19h30"
                            };
                        }else {
                            schedule3 = {
                                classCode: "",
                                teacherCard: "",
                                teacherName: "",
                                subjectName: "",
                                time: ""
                            };
                        }
                        var cell2_1 = row1.insertCell();
                        cell2_1.classList.add("col");
                        cell2_1.classList.add("row");
                        cell2_1.classList.add("align-middle");
                        cell2_1.classList.add("text-center");
                        cell2_1.classList.add("text-md");
                        cell2_1.classList.add("text-bold");
                        cell2_1.innerHTML =`<div class="row">
                    <span class="badge badge-info text-md col-12">${schedule1.classCode}</span>
                    <span class="col-12">${schedule1.teacherName}</span>
                    <span class="col-12">${schedule1.subjectName}</span>
                    <span class="col-12">${schedule1.time}</span>
                </div>`;
                        var cell2_2 = row2.insertCell();
                        cell2_2.classList.add("col");
                        cell2_2.classList.add("row");
                        cell2_2.classList.add("align-middle");
                        cell2_2.classList.add("text-center");
                        cell2_2.classList.add("text-md");
                        cell2_2.classList.add("text-bold");
                        cell2_2.innerHTML = `<div class="row">
                            <span class="badge badge-info text-md col-12">${schedule2.classCode}</span>
                            <span class="col-12">${schedule2.teacherName}</span>
                            <span class="col-12">${schedule2.subjectName}</span>
                            <span class="col-12">${schedule2.time}</span>
                        </div>`;
                        var cell2_3 = row3.insertCell();
                        cell2_3.classList.add("col");
                        cell2_3.classList.add("row");
                        cell2_3.classList.add("align-middle");
                        cell2_3.classList.add("text-center");
                        cell2_3.classList.add("text-md");
                        cell2_3.classList.add("text-bold");
                        cell2_3.innerHTML = `
                        <div class="row">
                            <span class="badge badge-info text-md col-12">${schedule3.classCode}</span>
                            <span class="col-12">${schedule3.teacherName}</span>
                            <span class="col-12">${schedule3.subjectName}</span>
                            <span class="col-12">${schedule3.time}</span>
                        </div>`;
                    }
                }
                else{
                    $(`<th class="col">No Class active</th>`).appendTo("#thead-view-room")
                }
            },
            error: (error) => {
                $("#listMark").DataTable({
                    columns:[
                        {title:'Student Name'},
                        {title:'Subject Name'},
                        {title:'ASM Mark'},
                        {title:'OBJ Mark'},
                        {title:'Status'}
                    ]
                })
            }
        })
    });
})