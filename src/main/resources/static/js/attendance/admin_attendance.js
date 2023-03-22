$(() => {
    $("#date_time").val(null);
    $(".select2").select2({
        theme:"bootstrap4"
    })
    $('#date').datetimepicker({
        format: "DD/MM/YYYY",
        maxDate: new Date()
    });
    $("#attendance_table").DataTable({
        paging: false,
        searching: false,
        info: false,
        columnDefs: [
            {
                "targets": [0, 1, 2, 3, 4, 5],
                "className": "text-center",
            },
            {
                "targets": [1, 3, 4, 5],
                "orderable": false,
            }
        ],
        columns: [
            {title: "STT"},
            {title: "Avatar"},
            {title: "Student name"},
            {title: "Present"},
            {title: "Absent"},
            {title: "Note"},
            {title: "Attendance", visible: false}
        ],
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
        }
    });

    $("#date").on("change.datetimepicker", () => {
        let date = $("#date_time").val();
        $("#class_select").removeClass("d-none");
        // $(".class_select2").empty().trigger("change");
        $(".class_select2").select2({
            theme: "bootstrap4",
            ajax: {
                url: "/dashboard/attendance/findScheduleDetailByDate",
                method: "GET",
                data: function (params){
                    let query = {
                        search: params.term,
                        date:date
                    }
                    return query;
                },processResults: function (response) {
                    let dataArray = [];
                    for (const dataKey of response) {
                        let data1 = {
                            "id" : dataKey.id,
                            "text":dataKey.classCode
                        }
                        dataArray.push(data1);
                    }
                    return {
                        results: dataArray
                    };
                },error : (data)=>{
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
                        toastr.error(data.responseText);
                    }
                }
            }
        });
    });
    $("#class").on("change",()=>{
        $("#slot_select").removeClass("d-none");
    });
    $("#slot_select").on("change", () =>{
        let date = $("#date_time").val();
        let classId = $("#class").val();
        let slot = $("#slot").val();
        $.ajax({
            url:"/dashboard/attendance/findAttendanceByDate",
            method:"POST",
            data:{
                date: date,
                classId:classId,
                slot: slot
            },
            success:(response)=>{
                console.log(response);
            },error : (data)=>{
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
                    toastr.error(data.responseText);
                }
            }
        })
    })
})
