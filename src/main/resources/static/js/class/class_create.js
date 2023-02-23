
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
        if (selectMajor.val()!= "" && selectShift.val()!= "" && selectDayOfWeek.val() != ""){

            var yearSlice = year.toString().slice(2,4);

            var major = $('#majorId option[value='+selectMajor.val()+']').text();;
            const regex = /[^A-Z]/g;
            var classCode =major.replace(regex, '')+"."+selectShift.val()+selectDayOfWeek.val()+"."+day+"."+month+"."+yearSlice+".";

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

    $('#btn_create_class').on('click',function (e){
        e.preventDefault();

        var classCode = $('#classCode').val();
        var majorId = $('#majorId').val()
        var teacherId = $('#teacherId').val()
        var limitStudent = $('#limitStudent').val()

        var newClass = {
            "classCode" : classCode,
            "majorId" : majorId,
            "teacherId" : teacherId,
            "shift" : selectShift.val()+selectDayOfWeek.val(),
            "limitStudent" : limitStudent,
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
                    required:"Vui lòng nhập Mã lớp học"
                },
                majorId : {
                    required:"Vui lòng chọn nghành học"
                },
                teacherId: {
                    required: "Vui lòng chọn Giáo viên chủ nhiệm"
                },
                shift: {
                    required: "Vui lòng chọn thời gian học trong ngày"
                },
                dayOfWeek: {
                    required: "Vui lòng chọn ngày học trong tuần"
                },
                startDate: {
                    required: "Vui lòng chọn ngày bắt đầu học",
                    date: "Ngày định dạng mm/dd/yyyy",
                    dateGreaterThan: "Ngày bắt đầu học không thể là ngày trong quá khứ !!"
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
                    console.log(result);
                    result = JSON.parse(result);
                    console.log(result);
                    if (result.status == "success"){
                        $('#teacherId').val("").change();
                        $('#majorId').val("").change();
                        $('#classCode').val("");
                        selectDayOfWeek.val("").change();
                        selectShift.val("").change();
                        toastr.success('Tạo lớp học thành công')
                        if(result.message != null){
                            toastr.warning(result.message)
                        }
                        $('#spinner-div').hide();
                    }else {
                        //toastr.error('Tạo lớp học thất bại')
                        $('#spinner-div').hide();
                    }
                },
                error:(e)=>{
                    console.log(e)
                    toastr.error('Tạo lớp học thất bại')
                    $('#spinner-div').hide();
                },
            })
        }

    })
})
