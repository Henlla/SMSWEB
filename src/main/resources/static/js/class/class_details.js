var OnCreateSchedule = (classId, majorId, shift) => {
    console.log(classId, majorId);
    let semester = $('#semester').val()
    let startDate = $('#startDate').val()
    let teacherId = $('#teacherId_v').val()
    let data = new FormData();
    data.append("semester", semester)
    data.append("startDate", startDate)
    data.append("classId", classId)
    data.append("majorId", majorId)
    data.append("shift", shift)
    data.append("teacher_id", teacherId)
    $.ajax({
        url: "/dashboard/class/create_schedule",
        data: data,
        method: "POST",
        cache: false,
        processData: false,
        contentType: false,
        success: (res) => {
            if (res === "error") {
                Swal.fire(
                    "",
                    `Schedule for semester ${semester} already exist !`,
                    "error"
                )
            }else if(res ==="error schedule"){
                Swal.fire(
                    "",
                    `You have to create schedule ${semester - 1} before !`,
                    "error"
                )
            } else if (res === "error date") {
                Swal.fire(
                    "",
                    `The start date must be greater than the date of the previous term's schedule !`,
                    "error"
                )
            } else {
                Swal.fire(
                    "",
                    `Create schedule for semester ${semester} success !`,
                    "success"
                )

                $('#create_schedule').modal("hide")
                $('#spinner-div').hide();
            }


        }, error: (e) => {
            toastr.error('Create schedule failed')

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
    $('#btn_create_schedule').hide()
    $('#btn_submitChangeDate').hide()
    $('#btn_submitTeacher').hide()

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
            msgError.html('Please choose date to change')
        } else {
            console.log($('#classId').val())
            console.log($('#semesterSchedule').val())
            let data = new FormData()
            data.append('currenDate', i_newDate.val())
            data.append('classId', $('#classId').val())
            data.append('semester', $('#semesterSchedule').val())
            data.append("slot", $('#slot').val())
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
                            "This slot was exists, please choose another ",
                            "error"
                        )
                    } else {
                        let data2 = new FormData()
                        data2.append("schedule_details_id", $('#schedule_details_id').val())
                        data2.append("newDate", i_newDate.val())
                        data2.append("slot", $('#slot').val())
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
                                    $('#btn_update_schedule').html('Change')
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
                                    toastr.success('View fail')
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
                        toastr.success('View fail')
                    }
                }
            })
        }
    })

    let flag = true;
    $('#btn_update_schedule').on('click', () => {
        if (flag === true) {
            $('#btn_update_schedule').html('Cancel edit')
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
            var studentCard = $("#inputStudentCard").val();
            if (availablePlace < 1) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Number of student is maximum',
                    showDenyButton: false,
                    confirmButtonColor: '#3085d6',
                    confirmButtonText: 'Ok',
                })
            } else {
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
                        text: 'This student was having in this class',
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
                                    html: 'Do you want to add this student: ' + response.studentByProfile.firstName + ' ' + response.studentByProfile.lastName +
                                        '<br> Birth day: ' + response.studentByProfile.dob,
                                    showCancelButton: true,
                                    showDenyButton: false,
                                    confirmButtonColor: '#3085d6',
                                    cancelButtonColor: '#d33',
                                    cancelButtonText: 'Cancel',
                                    confirmButtonText: 'Ok',
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
                                                    title: 'Add success',
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
                                                    title: 'Add fail',
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
                                    text: 'Student is not exists',
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
                text: 'Number of student is maximum',
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
                        required: "Please choose list of student"
                    },
                },
            })
            if ($('#form_import_student_file').valid()) {
                var data = new FormData(document.querySelector('#form_import_student_file'))
                data.append("classCode", $("#classCode").val())
                data.append("availablePlace", $("#availablePlace").val())
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
                        console.log(error)
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: error.responseJSON.message,
                            showDenyButton: false,
                            confirmButtonColor: '#3085d6',
                            confirmButtonText: 'Ok',
                        })
                    },
                });
            }
        }
    });

    $('#teacher_change').on('change', () => {
        let card = $('#teacher_change').val()
        let shift = $('#shift').val()
        let data = new FormData()
        data.append("card", card)
        data.append("shift", shift)
        $.ajax({
            url: `/dashboard/class/checkTeacherChange`,
            method: "POST",
            data: data,
            contentType: false,
            cache: false,
            processData: false,
            success: (res) => {
                console.log(res)
                if (res.toLowerCase() === "success") {
                    Swal.fire(
                        "",
                        `You can swap this teacher !`,
                        "success"
                    )
                    $('#btn_submitTeacher').show()
                } else {
                    Swal.fire(
                        "",
                        `This time the teacher has a class !`,
                        "error"
                    )
                    $('#btn_submitTeacher').hide()
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
                    toastr.success('View fail')
                }
            }

        })
    })

    $('#btn_submitTeacher').on('click', () => {
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
                data.append("classId", $('#classId').val())
                let teacherCard = $('#teacher_change').val()
                data.append("teacherCard", teacherCard)
                $.ajax({
                    url: "/dashboard/class/change_teacher",
                    method: "POST",
                    cache: false,
                    data: data,
                    processData: false,
                    contentType: false,
                    success: (res) => {
                        console.log(res)
                        Swal.fire({
                            title: 'Change teacher success',
                            showDenyButton: false,
                            showCancelButton: false,
                            icon: 'success',
                            confirmButtonText: 'Ok',
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.reload();
                            }
                        })
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
                            toastr.success('View fail')
                        }
                    }

                })
            }
        })
    })

    $('#btnSendSchedule').on('click', () => {
        let data = new FormData();
        data.append("file", $("#fileSchedule").get(0).files[0])
        data.append("classId", $('#classId').val())
        $.ajax({
            url: "/dashboard/class/sendSchedule",
            method: "POST",
            enctype: 'multipart/form-data',
            cache: false,
            processData: false,
            contentType: false,
            data: data,
            success: () => {
                Swal.fire(
                    "",
                    "Send time table success",
                    "success"
                )
                $('#send_schedule').modal("hide")
            }, error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
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
                    Swal.fire(
                        'Send fail',
                        'error'
                    )
                }
            }

        })
    })

    $('#teacher_schedule_change').on('change', () => {
        let date_change = $('#date_change').val()
        let slot = $('#slot_I').val()
        let shift = $('#shift').val()
        let teacherCode = $('#teacher_schedule_change').val()
        let data = new FormData()
        data.append("date_change", date_change)
        data.append("slot", slot)
        data.append("teacherCode", teacherCode)
        data.append("shift", shift)
        if (teacherCode == "") {
            $('#btn_submitTeacher_schedule').attr("disabled", false);
        }

        $.ajax({
            url: "/dashboard/class/checkTeacherScheduleDetailsChange",
            method: "POST",
            data: data,
            contentType: false,
            cache: false,
            processData: false,
            success: (res) => {
                if (res.toLowerCase() === "success") {
                    Swal.fire(
                        "",
                        `You can swap this teacher !`,
                        "success"
                    )
                    $('#btn_submitTeacher_schedule').attr("disabled", false);
                } else {
                    Swal.fire(
                        "",
                        `${date_change} - slot ${slot} this teacher the teacher has a lesson !`,
                        "error"
                    )
                    $('#btn_submitTeacher_schedule').attr("disabled", true);
                }
            }, error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
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
                    Swal.fire(
                        'Change fail',
                        'error'
                    )
                }
            }

        })
    })

    $('#btn_submitTeacher_schedule').on('click', () => {
        let schedule_detail_id = $('#schedule_detail_id').val()
        let teacher_card = $('#teacher_schedule_change').val()
        let data = new FormData()
        data.append("schedule_detail_id", schedule_detail_id)
        data.append("teacher_card", teacher_card)

        $.ajax({
            url: "/dashboard/class/changeTeacherInScheduleDetail",
            method: "POST",
            data: data,
            contentType: false,
            cache: false,
            processData: false,
            success: (res) => {
                if (res.toLowerCase() === "success") {
                    Swal.fire(
                        'Change teacher success',
                        'success'
                    )
                    $('#change_teacher_schedule').modal('hide')
                    OnChangeSemesterSchedule();
                } else {
                    Swal.fire(
                        'Change teacher failed',
                        'error'
                    )
                }
            }, error: (xhr, status, error) => {
                var err = eval("(" + xhr.responseText + ")");
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
                    Swal.fire(
                        'Send fail',
                        'error'
                    )
                }
            }
        })

    })

    $('#startDate').on('change', () => {
        console.log($('#startDate').val())
        console.log(isFutureDate($('#startDate').val()))
        if (!isFutureDate($('#startDate').val())) {
            $('#btn_create_schedule').hide()
            $('.error-choose_day').html('Cannot choose day is past . Try again!')
        } else {
            $('#btn_create_schedule').show()
            $('.error-choose_day').html('')
        }
    })

    $('.newDate').on('change', () => {
        if (!isFutureDate($('.newDate').val())) {
            $('#btn_submitChangeDate').hide()
            $('.error-choose_day').html('Cannot choose day is past . Try again!')
        } else {
            $('#btn_submitChangeDate').show()
            $('.error-choose_day').html('')
        }
    })

});

var OnImportListStudent = () => {
    swal.showLoading();
    var file = $("#studentList").get(0).files[0];
    var formData = new FormData();
    var availablePlace = $("#availablePlace").val();
    var classId = $("#classId").val();
    formData.append("file", file);
    formData.append("classId", classId);
    formData.append("availablePlace", availablePlace);
    $.ajax({
        url: "/dashboard/class/import-excel-student",
        method: "POST",
        data: formData,
        contentType: false,
        processData: false,
        enctype: "multipart/file",
        beforeSend: () => {
            $("#class_import_student_file").modal("hide");
            var sweet_loader = `<div id="spinner-divI">
                            <div style="overflow: hidden" class="spinner-border m-0 text-primary" role="status"></div>
                        </div>`
            Swal.fire({
                title: 'Importing student',
                html: sweet_loader,// add html attribute if you want or remove
                allowOutsideClick: false,
                showConfirmButton: false,
                customClass: "swal-height",
            });
        }, success: (response) => {
            setTimeout(() => {
                Swal.close();
                toastr.success("Import student success");
                setTimeout(()=>{
                    location.reload();
                },1500)
            }, 1500);
        }, error: (data) => {
            if (data.responseText.toLowerCase() === "token expired") {
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
                setTimeout(() => {
                    Swal.close();
                    toastr.error(data.responseText);
                }, 1500);
            }
        }
    });
}

var OnUpdateDate = (id, date, slot) => {
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
            // console.log(res)
            $('#btn_update_schedule').css('display', 'block')
            $('#btnDownSchedule').show()
            $('#btnDownSchedule').attr('href', `/dashboard/class/export_schedule/${classId}&${semester}`)
            // console.log(res)
            let shift = $('#shift').val().substring(0, 1)
            let arrTime = []
            if (shift === 'M') {
                arrTime.push('8:00-10:00')
                arrTime.push('10:00-12:00')
            } else if (shift === 'A') {
                arrTime.push('12:30-15:30')
                arrTime.push('15:30-17:30')
            } else {
                arrTime.push('17:30-19:30')
                arrTime.push('19:30-21:30')
            }
            if (res !== '') {
                const list = Object.values(res);
                console.log(list)
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
                                    if (isFutureDate(j.date)) {
                                        td0_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white f-12">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size13" style="display: flex;justify-content: center;">${j.date}
                                          <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size12 tb_teacher"  style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="tb_time font-size12">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td0_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white f-12">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}</div>
                                      <div class="font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}</div>
                                      <div style="font-size: 13px" class="font-size12">${arrTime[j.slot - 1]}</div>`

                                    }
                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td0_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size13" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td0_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}</div>
                                     <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size12  tb_time">${arrTime[j.slot - 1]}</div>`

                                    }
                                    break;
                                }
                            case "TUESDAY":
                                if (j.slot === 1) {
                                    if (isFutureDate(j.date)) {
                                        td1_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td1_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_toime">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td1_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td1_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                }
                            case "WEDNESDAY":
                                if (j.slot === 1) {
                                    if (isFutureDate(j.date)) {
                                        td2_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td2_1.innerHTML = `<span class=" tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td2_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td2_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                }
                            case "THURSDAY":
                                if (j.slot === 1) {
                                    if (isFutureDate(j.date)) {
                                        td3_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td3_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                     </div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td3_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td3_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                }
                            case "FRIDAY":
                                if (j.slot === 1) {
                                    if (isFutureDate(j.date)) {
                                        td4_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a>
                                     </div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                              <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a>
                                              </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td4_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                     </div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                              </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td4_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td4_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                              </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                }
                            case "SATURDAY":
                                if (j.slot === 1) {
                                    if (isFutureDate(j.date)) {
                                        td5_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td5_1.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }

                                    break;
                                } else {
                                    if (isFutureDate(j.date)) {
                                        td5_2.innerHTML = `<span class="tb_code bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                                              onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                                              <div class="tb_teacher font-size12" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                                              onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    } else {
                                        td5_2.innerHTML = `<span class="tb_teacher bg-sky padding-5px-tb padding-5px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                                      <div class="tb_date margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                                      </div>
                                              <div class="font-size12 tb_teacher" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName + ' ' + j.teacher.profileByProfileId.lastName}
                                               </div>
                                      <div class="font-size13 tb_time">${arrTime[j.slot - 1]}</div>`

                                    }
                                    break;
                                }
                            // case "SUNDAY":
                            //     if (j.slot === 1) {
                            //         td6_1.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                            //           <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                            //           <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                            //                   onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                            //                   <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName+' '+j.teacher.profileByProfileId.lastName}
                            //                    <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}" data-shift="${$('#shift').val()}"
                            //                   onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
                            //           <div class="font-size13 text-light-gray">${arrTime[j.slot - 1]}</div>`
                            //         break;
                            //     } else {
                            //         td6_2.innerHTML = `<span class="bg-sky padding-5px-tb padding-15px-lr border-radius-5 margin-10px-bottom text-white font-size16 xs-font-size13">${j.subject.subjectCode}</span>
                            //           <div class="margin-10px-top font-size14" style="display: flex;justify-content: center;">${j.date}
                            //           <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"
                            //                   onclick="OnUpdateDate(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'))" class="fas fa-pencil-alt"></i></a></div>
                            //           <div class="font-size13 text-light-gray" style="display: flex;justify-content: center">${j.teacher.profileByProfileId.firstName+' '+j.teacher.profileByProfileId.lastName}
                            //                    <a class="ml-1 btn_update_date"><i data-id="${j.id}" data-date="${j.date}" data-slot="${j.slot}"  data-shift="${$('#shift').val()}"
                            //                   onclick="OnChangeTeacherScheduleDetails(this.getAttribute('data-id'),this.getAttribute('data-date'),this.getAttribute('data-slot'),this.getAttribute('data-shift'))" class="fas fa-pencil-alt"></i></a></div>
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
                    "Don't have time table for semester " + $('#semesterSchedule').val(),
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
                toastr.success('View fail')
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

var OnChangeClassStudent = (studentId, studentName) => {
    console.log(studentId)
    $('#studentId').val(studentId)
    $('.studentName').html(studentName)
    $('.classCode').html($('#classCode').val())
    $('#change_class_student').modal('show')
}

var OnCheckStudentInClass = () => {
    let studentId = $('#studentId').val()
    let data = new FormData()
    data.append("studentId", studentId)
    data.append("classId", $('#classList').val())
    $.ajax({
        url: "/dashboard/class/checkStudentInClass",
        method: "POST",
        data: data,
        contentType: false,
        cache: false,
        processData: false,
        success: (res) => {
            if (res.toLowerCase() == "error") {
                Swal.fire(
                    "",
                    "This student already exists in the class",
                    "error"
                )
            } else {
                Swal.fire(
                    "",
                    "You can change this student's class",
                    "success"
                )
                $('#btn_change_class_student').show()
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
                toastr.success('View fail')
            }
        }
    })
}

var OnSubmitChangeClassStudent = () => {
    let studentId = $('#studentId').val()
    let classIdChange = $('#classList').val()
    let classIdCurrent = $('#classId').val()
    let data = new FormData()
    data.append("studentId", studentId)
    data.append("classIdChange", classIdChange)
    data.append("classIdCurrent", classIdCurrent)

    $.ajax({
        url: "/dashboard/class/changeClassForStudent",
        data: data,
        method: "POST",
        contentType: false,
        cache: false,
        processData: false,
        success: (res) => {
            Swal.fire(
                "",
                "Change class for student success",
                "success"
            )
            $('#btn_change_class_student').hide()
        }, error: (err) => {
        }

    })
}

let OnChangeTeacherScheduleDetails = (id, date, slot, shift) => {
    $('#change_teacher_schedule').modal('show')
    $('#date').html(date)
    $('#date_change').val(date)
    $('#schedule_detail_id').val(id)
    $('#slot_2').html(slot)
    $('#slot_I').val(slot)
}

let OnchangeRoom = () => {
    $("#room_change").empty();
    $('#change_room').modal('show')
    let data = new FormData();

    data.append("shift", $("#shift").val());
    $.ajax({
        url: "/dashboard/class/getAvailableRoom",
        method: "POST",
        data: data,
        cache: false,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        success: (response) => {
            let parse = JSON.parse(response);
            if (parse.length == 0) {
                parse.forEach(item => {
                    $(`
                    <option value="">--No room available--</option>
                `).appendTo("#room_change");
                });
            } else {
                $(`
                    <option value="">--Select Room--</option>
                `).appendTo("#room_change");
                parse.forEach(item => {
                    $(`
                    <option value="${item.id}">${item.roomCode}</option>
                `).appendTo("#room_change");
                });
            }
        },
        error: (error) => {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.responseJSON.message,
                showDenyButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Ok',
            })
        }
    })
}

let btn_submit_change_room = $("#btn_submit_change_room");

btn_submit_change_room.click((event) => {
    event.preventDefault();

    let roomId = $("#room_change").val();
    if (roomId == "" || roomId == null) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Please choose a Room",
            showDenyButton: false,
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Ok',
        })
    } else {
        Swal.fire({
            icon: 'question',
            title: 'Change to this room?',
            showDenyButton: false,
            showCancelButton: true,
            confirmButtonText: 'Yes',
            cancelButtonText: 'No',
        }).then((result) => {
            if (result.isConfirmed) {
                let data = new FormData();
                data.append("classId", $("#classId").val());
                data.append("roomId", roomId);
                $.ajax({
                    url: "/dashboard/class/class-update-room",
                    method: "POST",
                    data: data,
                    cache: false,
                    processData: false,
                    contentType: false,
                    enctype: "multipart/form-data",
                    success: (response) => {
                        Swal.fire({
                            icon: 'success',
                            title: 'Succcess',
                            text: error.responseJSON.message,
                            showDenyButton: false,
                            confirmButtonColor: '#3085d6',
                            confirmButtonText: 'Ok',
                        })
                    },
                    error: (error) => {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: error.responseJSON.message,
                            showDenyButton: false,
                            confirmButtonColor: '#3085d6',
                            confirmButtonText: 'Ok',
                        })
                    }
                })
            }
        });
    }
})

function isFutureDate(value) {
    d_now = new Date();
    d_inp = new Date(value)
    return d_now.getTime() <= d_inp.getTime();
}



