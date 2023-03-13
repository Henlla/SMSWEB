$(()=>{
    var date = new Date();
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    if (month < 10) month = "0" + month;
    if (day < 10) day = "0" + day;
    var today = year + "-" + month + "-" + day

    let inputStartDate = $('#startDate')
    let selectMajor = $('#majorId')
    let selectShift = $('#shift')
    let selectDayOfWeek = $('#dayOfWeek')

    jQuery.validator.addMethod("dateGreaterThan",
        function(value, element, params) {
        $("#inputToday").val(today);
            if (new Date(value) > new Date($(params).val())) {
                return true;
            }
            return false;
        }, 'Must be greater than {0}.');
    let autoFillClassCode = function (){
        if (selectMajor.val()!= "" && selectShift.val()!= "" && selectDayOfWeek.val() != "" && inputStartDate.val() != ""){
            var inputDate = new Date(inputStartDate.val());
            var day = inputDate.getDate() < 10? "0"+inputDate.getDate(): inputDate.getDate().toString();
            var month = (inputDate.getMonth()+1) < 10? "0"+(inputDate.getMonth()+1): (inputDate.getMonth()+1).toString();

            var major = $('#majorId option[value='+selectMajor.val()+']').text();;
            const regex = /[^A-Z]/g;
            var classCode =major.replace(regex, '')+"."+selectShift.val()+selectDayOfWeek.val()+"."+day+month+".";

            $.ajax({
                url: "/dashboard/class/class-searchClasssesByClassCode?classCode="+classCode,
                method: "GET",
                success: (result) => {
                    result =JSON.parse(result);
                    for(var increaseNumber = 1;increaseNumber < 99;increaseNumber++){
                        if (increaseNumber.toString().length < 2){
                            increaseNumber = "0"+increaseNumber;
                        }
                        var temp  = classCode+increaseNumber;
                        if (result.filter( c => c == temp).length == 0){
                            $("#classCode").val(temp);
                            break;
                        }
                    }
                },
                error: (e) => {
                    console.log(e)
                }
            })
        }
    }
    selectShift.change(autoFillClassCode);
    selectDayOfWeek.change(autoFillClassCode);
    selectMajor.change(autoFillClassCode);
    inputStartDate.change(autoFillClassCode);

    $('#btn_create_class').on('click',function (e){
        e.preventDefault();

        var classCode = $('#classCode').val();
        var majorId = $('#majorId').val()
        var teacherId = $('#teacherId').val()
        var limitStudent = $('#limitStudent').val()

        var newClass = {
            "id": "",
            "classCode" : classCode,
            "majorId" : majorId,
            "teacherId" : teacherId,
            "shift" : selectShift.val()+selectDayOfWeek.val(),
            "limitStudent" : limitStudent,
            "startDate": inputStartDate.val()
        }
        var file = $("#studentList").get(0).files[0];
        var formData = new FormData();
        formData.append('newClass',JSON.stringify(newClass))
        formData.append("file", file);

        $('#form-create').validate({
            rules: {
                classCode:{
                    required: true
                },
                majorId: {
                    required: true
                },
                teacherId: {
                    required: true
                },
                shift: {
                    required: true
                },
                dayOfWeek: {
                    required: true
                },
                startDate: {
                    required: true,
                    date: true,
                    dateGreaterThan: "#inputToday"
                },
            },
            messages:{
                classCode : {
                    required:"Please enter class code"
                },
                majorId : {
                    required:"Please enter major"
                },
                teacherId: {
                    required: "Please choose teacher"
                },
                shift: {
                    required: "Please choose timer"
                },
                dayOfWeek: {
                    required: "Please choose day of weeks"
                },
                startDate: {
                    required: "Please enter start date",
                    date: "Date format mm/dd/yyyy",
                    dateGreaterThan: "Start date must be in the future !!"
                },
            },
        })
        if($('#form-create').valid()){
            $('#spinner-div').show()
            $.ajax({
                url:"/dashboard/class/class-create",
                method:"POST",
                data:formData,
                cache : false,
                processData: false,
                contentType: false,
                enctype:"multipart/form-data",
                success:(result)=>{
                    console.log("sussess");
                    result = JSON.parse(result);
                    if (result.status == "success"){
                        $('#teacherId').val("").change();
                        $('#majorId').val("").change();
                        $('#classCode').val("");
                        selectDayOfWeek.val("").change();
                        selectShift.val("").change();
                        toastr.success('Create success')
                        if(result.message != null && result.message != ''){
                            toastr.warning(result.message)
                        }
                        $('#spinner-div').hide();
                    }else {
                        toastr.error('Create fail')
                        $('#spinner-div').hide();
                    }
                },
                error:(xhr, status, error)=>{
                    var err = eval("(" + xhr.responseText + ")");
                    console.log(err)
                    if (err.message.toLowerCase() === "token expired") {
                        $('#spinner-div').hide();
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
                        toastr.error('Create fail')
                    }
                }
            })
        }

    })
})


