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
            toastr.success('Create success')
            $('#create_schedule').modal("hide")
            $('#spinner-div').hide();
        }, error: (e) => {
            toastr.error('Create fail')
            $('#create_schedule').modal("hide")
            $('#spinner-div').hide();
        }
    })
}

$(document).ready(function () {
    $('#newDate_input').datetimepicker({
        format: 'DD/MM/YYYY',
    });
    $('#day_default_input').datetimepicker({
        format: 'DD/MM/YYYY',
    });
    $('#btn_update_schedule').css('display', 'none')
    $('#btnDownSchedule').hide()
    $("#student-table").DataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, -1], [5, 10, 20, 'All']],
        scrollCollapse: true,
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
        }, initComplete: function () {
            count = 0;
            this.api().columns([4]).every(function (i) {
                var title = this.header();
                //replace spaces with dashes
                title = $(title).html().replace(/[\W]/g, '');
                var column = this;
                var select = $('<select id="' + title + '" class="form-control form-control-sm select2" style="width: 50%" ></select>')
                    .appendTo($('#select-major'))
                    .on('change', function () {
                        //Get the "text" property from each selected data
                        //regex escape the value and store in array
                        var data = $.map($(this).select2('data'),
                            function (value, key) {
                                return value.text ? '^' + $.fn.dataTable.util.escapeRegex(value.text) + '$' : null;
                            });
                        //if no data selected use ""
                        if (data.length === 0) {
                            data = [""];
                        }
                        //join array into string with regex or (|)
                        var val = data.join('|');
                        //search for the option(s) selected
                        column
                            .search(val ? val : '', true, false)
                            .draw();
                    });

                column.data().unique().sort().each(function (d, j) {
                    select.append('<option value="' + d + '">' + d + '</option>');
                });
                //use column title as selector and placeholder
                $('#' + title).select2({
                    closeOnSelect: true,
                    placeholder: "- All -",
                    allowClear: true,
                    width: 'resolve',
                });

                //initially clear select otherwise first option is selected
                $('.select2').val(null).trigger('change');
            });
        },
    })

    let i_newDate = $('.newDate')
    let msgError = $('#msg_error')
    msgError.css('display', 'none')


    i_newDate.on('focus', () => {
        msgError.css('display', 'none')
    })

    $('#btn_submitChangeDate').on('click', () => {
        if (i_newDate.val() === '') {
            msgError.css('display', 'block')
            msgError.html('Please choose date')
        } else {
            console.log($('#classId').val())
            console.log($('#semesterSchedule').val())
            let data = new FormData()
            data.append('currenDate', i_newDate.val())
            data.append('classId', $('#classId').val())
            data.append('semester', $('#semesterSchedule').val())
            $.ajax({
                url: "/dashboard/class/changeDateSchedule",
                method: "POST",
                data: data,
                contentType: false,
                cache: false,
                processData: false,
                success: (res) => {
                    console.log(res)
                    if (res.toLowerCase() === "error") {
                        Swal.fire(
                            "",
                            "The date was have, please choose again ",
                            "error"
                        )
                    } else {
                        let data2 = new FormData()
                        data2.append("schedule_details_id", $('#schedule_details_id').val())
                        data2.append("newDate", i_newDate.val())
                        $.ajax({
                            url: "/dashboard/class/updateDateChangeSchedule",
                            method: "POST",
                            data: data2,
                            contentType: false,
                            cache: false,
                            processData: false,
                            success: (res) => {
                                if (res.toLowerCase() === "error") {
                                    Swal.fire(
                                        "",
                                        "Change fail",
                                        "error"
                                    )
                                } else {
                                    $('#schedule_update').modal("hide")
                                    Swal.fire(
                                        "",
                                        "Change success",
                                        "success"
                                    )
                                    $('#btn_update_schedule').html('Edit')
                                    OnChangeSemesterSchedule();
                                }
                            }, error: (xhr, status, error) => {
                                var err = eval("(" + xhr.responseText + ")");
                                $("#student_update").modal("hide");
                                console.log(err)
                                if (err.message.toLowerCase() === "token expired") {
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
                                } else {
                                    toastr.success('View detail fail')
                                }
                            }
                        })
                    }
                }, error: (xhr, status, error) => {
                    var err = eval("(" + xhr.responseText + ")");
                    $("#student_update").modal("hide");
                    console.log(err)
                    if (err.message.toLowerCase() === "token expired") {
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
                    } else {
                        toastr.success('View detail fail')
                    }
                }
            })
        }
    })

    let flag = true;
    $('#btn_update_schedule').on('click', () => {
        if (flag === true) {
            $('#btn_update_schedule').html('Cancel')
            $('.btn_update_date').css('display', 'block')
            flag = !flag;
        } else {
            $('#btn_update_schedule').html('Edit')
            $('.btn_update_date').css('display', 'none')
            flag = !flag;
        }
    })

    $("#form_add_student").submit(function (event) {
        event.preventDefault();
        var studentCard = $("#inputStudentCard").val();
        if (availablePlace < 1) {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Student in class was maximum',
                showDenyButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Ok',
            })
        } else {
            $('#form_add_student').validate({
                rules: {
                    inputStudentCard: {
                        required: true
                    }
                },
                messages: {
                    inputStudentCard: {
                        required: "Please enter student card"
                    }
                },
            })
            if ($('#form_add_student').valid()) {

                const dataTable = $("#student-table").DataTable();
                var listStudent = new Array()
                const regExp = /<(?:"[^"]*"['"]*|'[^']*'['"]*|[^'">])+>/g;
                for (let index = 0; index < dataTable.rows().data().length; index++) {
                    listStudent.push(dataTable.cell(index, 3).data().replace(regExp, ''));
                }

                if (listStudent.filter(item => item == studentCard).length > 0) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: "Don't need add student in this class",
                        showDenyButton: false,
                        confirmButtonColor: '#3085d6',
                        confirmButtonText: 'Ok',
                    })
                } else {
                    $.ajax({
                        url: "/dashboard/class/get-student-by-card/" + studentCard,
                        method: "GET",
                        contentType: false,
                        cache: false,
                        processData: false,
                        success: (response) => {
                            console.log(response);
                            if (response != null && response != "") {
                                response = JSON.parse(response);
                                Swal.fire({
                                    icon: 'question',
                                    title: 'Warning',
                                    html: 'Bạn có muốn thêm sinh viên: ' + response.studentByProfile.firstName + ' ' + response.studentByProfile.lastName +
                                        '<br> ngày sinh: ' + response.studentByProfile.dob,
                                    showCancelButton: true,
                                    showDenyButton: false,
                                    confirmButtonColor: '#3085d6',
                                    cancelButtonColor: '#d33',
                                    cancelButtonText: 'Cancel',
                                    confirmButtonText: 'Confirm',
                                }).then((result) => {
                                    if (result.isConfirmed) {
                                        let data = new FormData()
                                        data.append("studentId", response.id);
                                        data.append("classId", $("#classId").val());
                                        $.ajax({
                                            url: "/dashboard/class/add-student-to-class",
                                            method: "POST",
                                            data: data,
                                            contentType: false,
                                            cache: false,
                                            processData: false,
                                            success: (response) => {
                                                Swal.fire({
                                                    icon: 'success',
                                                    title: 'Create success',
                                                    showDenyButton: false,
                                                    showCancelButton: false,
                                                    confirmButtonText: 'Ok',
                                                }).then((result) => {
                                                    location.reload();
                                                })
                                            },
                                            error: (e) => {
                                                Swal.fire({
                                                    icon: 'error',
                                                    title: 'Create fail',
                                                    showDenyButton: false,
                                                    showCancelButton: false,
                                                    confirmButtonText: 'Ok',
                                                })
                                            }
                                        });
                                    }
                                })
                            } else {
                                Swal.fire({
                                    title: 'Error',
                                    text: 'Student not found',
                                    icon: 'error',
                                    showDenyButton: false,
                                    showCancelButton: false,
                                    confirmButtonText: 'Ok',
                                    timer: 2000
                                })
                            }
                        },
                        error: (e) => {
                            Swal.fire({
                                title: 'Error',
                                text: e.messages,
                                icon: 'error',
                                showDenyButton: false,
                                showCancelButton: false,
                                confirmButtonText: 'Ok',
                                timer: 2000
                            })
                        }
                    });
                }
            }
        }
    });

    $("#form_import_student_file").submit(function (event) {
        event.preventDefault();
        if (availablePlace < 1) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'The student was maximum',
                showDenyButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Ok',
            })
        } else {
            $('#form_import_student_file').validate({
                rules: {
                    studentList: {
                        required: true
                    }
                },
                messages: {
                    studentList: {
                        required: "Please choose list student"
                    },
                },
            })
            if ($('#form_import_student_file').valid()) {
                var data = new FormData(document.querySelector('#form_import_student_file'))
                data.append("classCode", classCode)
                data.append("availablePlace", availablePlace)

                $.ajax({
                    url: "/dashboard/class/import-student-excel",
                    method: "POST",
                    data: data,
                    cache: false,
                    processData: false,
                    contentType: false,
                    enctype: "multipart/form-data",
                    success: (response) => {
                        response = JSON.parse(response);
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: response.message,
                            showDenyButton: false,
                            confirmButtonColor: '#3085d6',
                            confirmButtonText: 'Ok',
                        }).then((result) => {
                            location.reload();
                        });
                    },
                    error: (error) => {
                        Swal.fire({
                            icon: 'error',
                            title: 'Fail',
                            text: error.responseJSON.message,
                            showDenyButton: false,
                            confirmButtonColor: '#3085d6',
                            confirmButtonText: 'Ok',
                        })
                    },
                });
            }
        }
    })
    $('#teacher_change').on('change', () => {
        let card = $('#teacher_change').val()
        $.ajax({
            url: `/dashboard/class/getTeacherByCard/${card}`,
            method: "GET",
            success: (res) => {
                console.log(res)
                if (res.id == null) {
                    Swal.fire({
                        title: "Don't find teacher",
                        showDenyButton: false,
                        showCancelButton: false,
                        confirmButtonText: 'Ok',
                    })
                }else {
                    let data = res.teacherCard + " (" + res.profileByProfileId.firstName + " "+res.profileByProfileId.lastName +")"
                    $('#teacher_change').val(data)
                }
            }, error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
                $("#student_update").modal("hide");
                console.log(err)
                if (err.message.toLowerCase() === "token expired") {
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
                } else {
                    toastr.success('View detail fail')
                }
            }

        })
    })
    $('#btn_submitTeacher').on('click',()=>{
        Swal.fire({
            title: '',
            text: "Are you sure to change teacher ?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Change'
        }).then((result) => {
            if (result.isConfirmed) {
                let data = new FormData()
                data.append("classId",$('#classId').val())
                let teacherCard = $('#teacher_change').val().split(" ")
                data.append("teacherCard",teacherCard[0])
                $.ajax({
                    url:"/dashboard/class/change_teacher",
                    method:"POST",
                    cache: false,
                    data:data,
                    processData: false,
                    contentType: false,
                    success:(res)=>{
                        console.log(res)
                        Swal.fire({
                            title: 'Change teacher success',
                            showDenyButton: false,
                            showCancelButton: false,
                            confirmButtonText: 'Ok',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.reload();
                            }
                        })
                    },error: (xhr, status, error) => {
                        var err = eval("(" + xhr.responseText + ")");
                        $("#student_update").modal("hide");
                        console.log(err)
                        if (err.message.toLowerCase() === "token expired") {
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
                        } else {
                            toastr.success('View detail fail')
                        }
                    }
                })
            }
        })
    })
    $('#btnSendSchedule').on('click',()=>{
        let data = new FormData();
        data.append("file",$("#fileSchedule").get(0).files[0])
        data.append("classId",$('#classId').val())
        $.ajax({
            url:"/dashboard/class/sendSchedule",
            method:"POST",
            enctype: 'multipart/form-data',
            cache: false,
            processData: false,
            contentType: false,
            data: data,
            success: () => {
                Swal.fire(
                    'Send time table success',
                    'success'
                )
                $('#modal_import_file').modal("hide")
            }, error:(xhr, status, error)=>{
                var err = eval("(" + xhr.responseText + ")");
                if (err.message.toLowerCase() === "token expired") {
                    $('#spinner-divI').hide()
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
                    Swal.fire(
                        'Import fail',
                        'error'
                    )
                }
            }

        })
    })

});

var OnUpdateDate = (id, date) => {
    // console.log(id)
    // console.log(date)
    $('#schedule_update').modal("show")
    $('.day_default').val(date)
    $('#schedule_details_id').val(id)
}

var OnChangeSemesterSchedule = () => {
    let data = new FormData()
    data.append("semester", $('#semesterSchedule').val())
    data.append("classId", $('#classId').val())
    let classId = $('#classId').val()
    let semester = $('#semesterSchedule').val()
    $.ajax({
        url: `/dashboard/class/getScheduleDetails`,
        data: data,
        method: "POST",
        contentType: false,
        cache: false,
        processData: false,
        success: (res) => {
            $('#btn_update_schedule').css('display', 'block')
            $('#btnDownSchedule').show()
            $('#btnDownSchedule').attr('href',`/dashboard/class/export_schedule/${classId}&${semester}`)
            let shift = $('#shift').val().substring(0, 1)
            if (res !== '') {
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
                // console.log(groups)
                const arr = Object.values(groups)
                // console.log(arr)
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
                    $('.btn_update_date').css('display', 'none')
                }
            } else {
                Swal.fire(
                    "",
                    "Don't have time table in semester " + $('#semesterSchedule').val(),
                    "error"
                )
            }


        }, error: (xhr, status, error) => {
            var err = eval("(" + xhr.responseText + ")");
            $("#student_update").modal("hide");
            console.log(err)
            if (err.message.toLowerCase() === "token expired") {
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
            } else {
                toastr.success('View detail fail')
            }
        }
    })
}

var OnChangeTeacher = (teacherName, id) => {
    console.log(teacherName)
    console.log(id)
    $('#teacher').val(teacherName)
    $('#teacherId').val(id)
    $('#change_teacher').modal('show')
}
