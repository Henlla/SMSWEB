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
function getOverviewlist () {
    let data = new FormData();
    let date = $("#over_view_date").val();
    let department = $("#department").val();

    if (date == "" || date == null){
        data.append("date",formatDate(Date.now()));
    }else data.append("date",formatDate(date));

    if (department == "" || department == null){
        data.append("departmentId","1");
    }else data.append("departmentId",department);
    if ( $.fn.DataTable.isDataTable('#table_view_room') ) {
        $('#table_view_room').DataTable().destroy();
    }
    $.ajax({
        url: "/dashboard/view_room_active",
        method: "POST",
        data:data,
        cache: false,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        success: (response) => {
            $("#tbody-view-room").empty();
            let parseData = JSON.parse(response);
            console.log(parseData);
            if(parseData.length >0){

                var totalRow = parseData.length + 1;
                let table = document.getElementById("tbody-view-room");
                for(var room of parseData){

                    console.log(room)
                    if (room.roomClass != null){
                        let row = table.insertRow();
                        row.classList.add("row");

                        var cell1 = row.insertCell();
                        cell1.classList.add("col-1")
                        cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;


                        let clazz1 = room.roomClass.filter(item => /^M\d{1}$/.test(item.shift))[0]
                        var cell2 = row.insertCell();
                        cell2.classList.add("col")
                        if(clazz1 != null || clazz1 != undefined){
                            let schedule1 = new Object();
                            schedule1 = {
                                classCode: clazz1.classCode,
                                teacherCard: clazz1.teacher.teacherCard,
                                teacherName: clazz1.teacher.profileByProfileId.firstName + " " + clazz1.teacher.profileByProfileId.lastName,
                                subjectName: clazz1.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                time: clazz1.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                            };
                            cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule1.classCode}</span>
                                <span class="col-12">${schedule1.teacherName}</span>
                                <span class="col-12">${schedule1.subjectName}</span>
                                <span class="col-12">${schedule1.time}</span>
                            </div>`;
                        }else {
                            cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;
                        }

                        let clazz2 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]
                        var cell3 = row.insertCell();
                        cell3.classList.add("col")
                        if(clazz2 != null || clazz2 != undefined){
                            let schedule2 = new Object();
                            schedule2 = {
                                classCode: clazz2.classCode,
                                teacherCard: clazz2.teacher.teacherCard,
                                teacherName: clazz2.teacher.profileByProfileId.firstName + " " + clazz2.teacher.profileByProfileId.lastName,
                                subjectName: clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                time: clazz2.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                            };
                            cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule2.classCode}</span>
                                <span class="col-12">${schedule2.teacherName}</span>
                                <span class="col-12">${schedule2.subjectName}</span
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                        }else {
                            cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                        }

                        let clazz3 = room.roomClass.filter(item => /^E\d{1}$/.test(item.shift))[0]

                        var cell4 = row.insertCell();
                        cell4.classList.add("col")

                        if(clazz3 != null || clazz3 != undefined){
                            let schedule3 = new Object();
                            schedule3 = {
                                classCode: clazz3.classCode,
                                teacherCard: clazz3.teacher.teacherCard,
                                teacherName: clazz3.teacher.profileByProfileId.firstName + " " + clazz3.teacher.profileByProfileId.lastName,
                                subjectName: clazz3.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                time: clazz3.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                            };
                            cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule3.classCode}</span>
                                <span class="col-12">${schedule3.teacherName}</span>
                                <span class="col-12">${schedule3.subjectName}</span
                                <span class="col-12">${schedule3.time}</span>
                            </div>`;
                        }else {
                            cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                        }
                    }else {
                        let row = table.insertRow();
                        row.classList.add("row");

                        var cell1 = row.insertCell();
                        cell1.classList.add("col-1")
                        cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;

                        var cell2 = row.insertCell();
                        cell2.classList.add("col")
                        cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;

                        var cell3 = row.insertCell();
                        cell3.classList.add("col")
                        cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;

                        var cell4 = row.insertCell();
                        cell4.classList.add("col")
                        cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                    }
                }
            }
            else{
                $("#table_view_room").DataTable({
                    destroy: true,
                    paging: false,
                    scrollY: true,
                    scrollCollapse: true
                });
            }
            $("#table_view_room").DataTable({
                destroy: true,
                paging: false,
                scrollY: true,
                scrollCollapse: true
            });
        },
        error: (error) => {
            $("#table_view_room").DataTable({
                destroy: true,
                paging: false,
                scrollY: true,
                scrollCollapse: true
            });
        }
    })
}
$(() => {
    getOverviewlist ();
    $("#over_view_date").change(function () {
        let data = new FormData();
        let date = $("#over_view_date").val();
        let department = $("#department").val();

        if (date == "" || date == null){
            data.append("date",formatDate(Date.now()));
        }else data.append("date",formatDate(date));

        if (department == "" || department == null){
            data.append("departmentId","1");
        }else data.append("departmentId",department);
        if ( $.fn.DataTable.isDataTable('#table_view_room') ) {
            $('#table_view_room').DataTable().destroy();
        }
        $.ajax({
            url: "/dashboard/view_room_active",
            method: "POST",
            data:data,
            cache: false,
            processData: false,
            contentType: false,
            enctype: "multipart/form-data",
            success: (response) => {
                $("#tbody-view-room").empty();
                let parseData = JSON.parse(response);
                console.log(parseData);
                if(parseData.length >0){

                    var totalRow = parseData.length + 1;
                    let table = document.getElementById("tbody-view-room");
                    for(var room of parseData){

                        console.log(room)
                        if (room.roomClass != null){
                            let row = table.insertRow();
                            row.classList.add("row");

                            var cell1 = row.insertCell();
                            cell1.classList.add("col-1")
                            cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;


                            let clazz1 = room.roomClass.filter(item => /^M\d{1}$/.test(item.shift))[0]
                            var cell2 = row.insertCell();
                            cell2.classList.add("col")
                            if(clazz1 != null || clazz1 != undefined){
                                let schedule1 = new Object();
                                schedule1 = {
                                    classCode: clazz1.classCode,
                                    teacherCard: clazz1.teacher.teacherCard,
                                    teacherName: clazz1.teacher.profileByProfileId.firstName + " " + clazz1.teacher.profileByProfileId.lastName,
                                    subjectName: clazz1.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz1.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule1.classCode}</span>
                                <span class="col-12">${schedule1.teacherName}</span>
                                <span class="col-12">${schedule1.subjectName}</span>
                                <span class="col-12">${schedule1.time}</span>
                            </div>`;
                            }else {
                                cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;
                            }

                            let clazz2 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]
                            var cell3 = row.insertCell();
                            cell3.classList.add("col")
                            if(clazz2 != null || clazz2 != undefined){
                                let schedule2 = new Object();
                                schedule2 = {
                                    classCode: clazz2.classCode,
                                    teacherCard: clazz2.teacher.teacherCard,
                                    teacherName: clazz2.teacher.profileByProfileId.firstName + " " + clazz2.teacher.profileByProfileId.lastName,
                                    subjectName: clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz2.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule2.classCode}</span>
                                <span class="col-12">${schedule2.teacherName}</span>
                                <span class="col-12">${schedule2.subjectName}</span
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                            }else {
                                cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                            }

                            let clazz3 = room.roomClass.filter(item => /^E\d{1}$/.test(item.shift))[0]

                            var cell4 = row.insertCell();
                            cell4.classList.add("col")

                            if(clazz3 != null || clazz3 != undefined){
                                let schedule3 = new Object();
                                schedule3 = {
                                    classCode: clazz3.classCode,
                                    teacherCard: clazz3.teacher.teacherCard,
                                    teacherName: clazz3.teacher.profileByProfileId.firstName + " " + clazz3.teacher.profileByProfileId.lastName,
                                    subjectName: clazz3.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz3.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule3.classCode}</span>
                                <span class="col-12">${schedule3.teacherName}</span>
                                <span class="col-12">${schedule3.subjectName}</span
                                <span class="col-12">${schedule3.time}</span>
                            </div>`;
                            }else {
                                cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                            }
                        }else {
                            let row = table.insertRow();
                            row.classList.add("row");

                            var cell1 = row.insertCell();
                            cell1.classList.add("col-1")
                            cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;

                            var cell2 = row.insertCell();
                            cell2.classList.add("col")
                            cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;

                            var cell3 = row.insertCell();
                            cell3.classList.add("col")
                            cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;

                            var cell4 = row.insertCell();
                            cell4.classList.add("col")
                            cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                        }
                    }
                }
                else{
                    $("#table_view_room").DataTable({
                        destroy: true,
                        paging: false,
                        scrollY: true,
                        scrollCollapse: true
                    });
                }
                $("#table_view_room").DataTable({
                    destroy: true,
                    paging: false,
                    scrollY: true,
                    scrollCollapse: true
                });
            },
            error: (error) => {
                $("#table_view_room").DataTable({
                    destroy: true,
                    paging: false,
                    scrollY: true,
                    scrollCollapse: true
                });
            }
        })
    });
    $("#department").change(function () {
        let data = new FormData();
        let date = $("#over_view_date").val();
        let department = $("#department").val();

        if (date == "" || date == null){
            data.append("date",formatDate(Date.now()));
        }else data.append("date",formatDate(date));

        if (department == "" || department == null){
            data.append("departmentId","1");
        }else data.append("departmentId",department);
        if ( $.fn.DataTable.isDataTable('#table_view_room') ) {
            $('#table_view_room').DataTable().destroy();
        }
        $.ajax({
            url: "/dashboard/view_room_active",
            method: "POST",
            data:data,
            cache: false,
            processData: false,
            contentType: false,
            enctype: "multipart/form-data",
            success: (response) => {
                $("#tbody-view-room").empty();
                let parseData = JSON.parse(response);
                console.log(parseData);
                if(parseData.length >0){

                    var totalRow = parseData.length + 1;
                    let table = document.getElementById("tbody-view-room");
                    for(var room of parseData){

                        console.log(room)
                        if (room.roomClass != null){
                            let row = table.insertRow();
                            row.classList.add("row");

                            var cell1 = row.insertCell();
                            cell1.classList.add("col-1")
                            cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;


                            let clazz1 = room.roomClass.filter(item => /^M\d{1}$/.test(item.shift))[0]
                            var cell2 = row.insertCell();
                            cell2.classList.add("col")
                            if(clazz1 != null || clazz1 != undefined){
                                let schedule1 = new Object();
                                schedule1 = {
                                    classCode: clazz1.classCode,
                                    teacherCard: clazz1.teacher.teacherCard,
                                    teacherName: clazz1.teacher.profileByProfileId.firstName + " " + clazz1.teacher.profileByProfileId.lastName,
                                    subjectName: clazz1.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz1.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule1.classCode}</span>
                                <span class="col-12">${schedule1.teacherName}</span>
                                <span class="col-12">${schedule1.subjectName}</span>
                                <span class="col-12">${schedule1.time}</span>
                            </div>`;
                            }else {
                                cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;
                            }

                            let clazz2 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]
                            var cell3 = row.insertCell();
                            cell3.classList.add("col")
                            if(clazz2 != null || clazz2 != undefined){
                                let schedule2 = new Object();
                                schedule2 = {
                                    classCode: clazz2.classCode,
                                    teacherCard: clazz2.teacher.teacherCard,
                                    teacherName: clazz2.teacher.profileByProfileId.firstName + " " + clazz2.teacher.profileByProfileId.lastName,
                                    subjectName: clazz2.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz2.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule2.classCode}</span>
                                <span class="col-12">${schedule2.teacherName}</span>
                                <span class="col-12">${schedule2.subjectName}</span
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                            }else {
                                cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;
                            }

                            let clazz3 = room.roomClass.filter(item => /^A\d{1}$/.test(item.shift))[0]

                            var cell4 = row.insertCell();
                            cell4.classList.add("col")

                            if(clazz3 != null || clazz3 != undefined){
                                let schedule3 = new Object();
                                schedule3 = {
                                    classCode: clazz3.classCode,
                                    teacherCard: clazz3.teacher.teacherCard,
                                    teacherName: clazz3.teacher.profileByProfileId.firstName + " " + clazz3.teacher.profileByProfileId.lastName,
                                    subjectName: clazz3.schedulesById[0].scheduleDetailsById[0].subjectBySubjectId.subjectName,
                                    time: clazz3.schedulesById[0].scheduleDetailsById.length == 2 ? "8:00 - 12:00" : "8:00 - 10:00"
                                };
                                cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-secondary text-md col-12">${schedule3.classCode}</span>
                                <span class="col-12">${schedule3.teacherName}</span>
                                <span class="col-12">${schedule3.subjectName}</span
                                <span class="col-12">${schedule3.time}</span>
                            </div>`;
                            }else {
                                cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                            }
                        }else {
                            let row = table.insertRow();
                            row.classList.add("row");

                            var cell1 = row.insertCell();
                            cell1.classList.add("col-1")
                            cell1.innerHTML =`<div class="row text-center">
                                <span class="col text-center text-lg text-bold mt-2">${room.roomCode}</span>
                            </div>`;

                            var cell2 = row.insertCell();
                            cell2.classList.add("col")
                            cell2.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">8:00 - 12:00</span>
                            </div>`;

                            var cell3 = row.insertCell();
                            cell3.classList.add("col")
                            cell3.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">13:30 - 17:30</span>
                            </div>`;

                            var cell4 = row.insertCell();
                            cell4.classList.add("col")
                            cell4.innerHTML = `<div class="row text-center">
                                <span class="badge badge-success text-md col-12">Available</span>
                                <span class="col-12 mt-4">17:30 - 21:30</span>
                            </div>`;
                        }
                    }
                }
                else{
                    $("#table_view_room").DataTable({
                        destroy: true,
                        paging: false,
                        scrollY: true,
                        scrollCollapse: true
                    });
                }
                $("#table_view_room").DataTable({
                    destroy: true,
                    paging: false,
                    scrollY: true,
                    scrollCollapse: true
                });
            },
            error: (error) => {
                $("#table_view_room").DataTable({
                    destroy: true,
                    paging: false,
                    scrollY: true,
                    scrollCollapse: true
                });
            }
        })
    });
})