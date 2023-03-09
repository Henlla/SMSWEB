
$(()=>{
    $('.select2').select2({
        theme: 'bootstrap4'
    });
    $("#student-table").DataTable({
        pageLength:5,
        lengthMenu:[[5,10,20,-1], [5, 10, 20,'All']],
        "language": {
            "decimal":        "",
            "emptyTable":     "Không có dữ liệu",
            "info":           "",
            "infoEmpty":      "",
            "infoFiltered":   "",
            "infoPostFix":    "",
            "thousands":      ",",
            "lengthMenu":     "Hiển thị _MENU_ dữ liệu",
            "loadingRecords": "Đang tìm...",
            "processing":     "",
            "search":         "Tìm kiếm:",
            "zeroRecords":    "Không tìm thấy dữ liệu",
            "paginate": {
                "first":      "Trang đầu",
                "last":       "Trang cuối",
                "next":       "Trang kế tiếp",
                "previous":   "Trang trước"
            },
            "aria": {
                "sortAscending":  ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            }
        },initComplete: function () {
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
                    console.log(d)
                    console.log(j)
                    select.append('<option value="' + d + '">' + d + '</option>');
                });
                //use column title as selector and placeholder
                $('#' + title).select2({
                    closeOnSelect: true,
                    placeholder: "- Tất cả -",
                    allowClear: true,
                    width: 'resolve',
                });

                //initially clear select otherwise first option is selected
                $('.select2').val(null).trigger('change');
            });
        },
    })

    $('.dataTables_filter input[type="search"]').css(
        {'width': '400px', 'display': 'inline-block'}
    );
});
var OnDetails = (id) => {
    $.ajax({
        url: "/dashboard/student/student_details/" + id,
        dataType: "json",
        contentType: "application/json",
        method: "GET",
        success: (data) => {
            console.log(data)
            $('#st_image').attr('src', data.student.studentByProfile.avartarUrl)
            $('#st_fullName').html(data.student.studentByProfile.firstName + ' ' + data.student.studentByProfile.lastName)
            $('#st_card').html(data.student.studentCard)
            $('#st_dob').html(data.student.studentByProfile.dob)
            $('#st_phone').html(data.student.studentByProfile.phone)
            $('#st_email').html(data.student.studentByProfile.email)
            $('#st_sex').html(data.student.studentByProfile.sex)
            for (var major of data.student.majorStudentsById) {
                $('#st_major').html(major.majorByMajorId.majorName)
            }
            if(data.classes.length ===0){
                $('#st_class').html('Chưa có')
            }else {
                for (var classes of data.classes) {
                    $('#st_class').html(classes.classCode)
                }
            }
            $('#st_identityId').html(data.student.studentByProfile.identityCard)
            $('#st_address').html(data.student.studentByProfile.address + ' , ' + data.student.studentByProfile.wardByWardId.name + ' , ' + data.student.studentByProfile.districtByDistrictId.name + ' , ' + data.student.studentByProfile.profileProvince.name)
            $('#accountId').val(data.student.studentByProfile.accountByAccountId.id)
            $('#email').val(data.student.studentByProfile.email)
            $("#student_details").modal("show");
            // $.ajax({
            //     url: "/dashboard/student/getClassByStudentId/" + id,
            // })
        },  error:(xhr, status, error)=>{
            var err = eval("(" + xhr.responseText + ")");
            $("#student_details").modal("hide");
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
    });
}