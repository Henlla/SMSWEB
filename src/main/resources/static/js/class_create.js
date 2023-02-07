
$(()=>{
    $('#majorId').change(function (){
        if ($('#majorId').val()!= ""){
            var classList;
            const d = new Date();
            var day = d.getDate();
            if (day.toString().length < 2){day = "0"+day;}
            var month = d.getMonth();
            if (month.toString().length < 2){month = "0"+month;}
            var year = d.getFullYear().toString().slice(2,4);

            var major = $('#majorId option[value='+$('#majorId').val()+']').text();;
            const regex = /[^A-Z]/g;

            fetch("http://localhost:8080/dashboard/classList")
                .then(response =>
                    response.json()
                        .then(data => ({
                        data: data,
                        status: response.status
                    })
                ).then(async res => {
                    for(var increaseNumber = 1;increaseNumber < 20;increaseNumber++){
                        if (increaseNumber.toString().length < 2){
                            increaseNumber = "0"+increaseNumber;
                        }
                        var classCode =major.replace(regex, '')+"."+day+"."+month+"."+year+"."+increaseNumber;
                        if (res.data.filter( c => c.classCode == classCode).length == 0){
                            $("#classCode").val(classCode);
                            break;
                        }
                    }
                }));
        }
    });

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
            "limitStudent" : limitStudent,
        }
        var formData = new FormData();
        formData.append('newClass',JSON.stringify(newClass))

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
                }
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
                }
            },
        })
        if($('#form-create').valid()){
            $('#spinner-div').show()
            $.ajax({
                url:"/dashboard/class-create",
                method:"POST",
                data:formData,
                cache : false,
                processData: false,
                contentType: false,
                success:(result)=>{
                    $('#teacherId').val("").change()
                    $('#majorId').val("").change()
                    $('#classCode').val("")
                    $('#class_code_first').val("")
                    toastr.success('Tạo lớp học thành công')
                    $('#spinner-div').hide();
                },
                error:(e)=>{
                    toastr.error('Tạo lớp học thất bại')
                    $('#spinner-div').hide();
                },
            })
        }

    })
})
