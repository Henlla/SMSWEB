
$(()=>{

    $('#majorId').change(function (){
        if ($('#majorId').val()!= ""){

            const d = new Date();
            let day = d.getDate();
            if (day < 10){
                day = "0"+day;
            }
            let month = d.getMonth();
            if (month < 10){
                day = "0"+month;
            }

            let year = d.getFullYear().toString().slice(2,4);

            var major = $('#majorId option[value='+$('#majorId').val()+']').text();;
            const regex = /[^A-Z]/g;
            var classCode =major.replace(regex, '')+"."+day+"."+month+"."+year+".";
            $('#class_code_first').val(classCode)
        }
    });

    $('#btn_create_class').on('click',function (e){
        e.preventDefault()
        classNumber = $('#classCode').val();
        if ( classNumber < 10) classNumber = "0"+classNumber;

        var classCode = $('#class_code_first').val() + classNumber;
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
                        console.log(result)
                        console.log("success")

                        $('#teacherId').val("").change()
                        $('#majorId').val("").change()
                        $('#classCode').val("")
                        $('#class_code_first').val("")
                        toastr.success('Tạo lớp học thành công')
                        $('#spinner-div').hide();
                    },
                    error:(e)=>{
                        console.log(e)
                        console.log("failed")
                        toastr.success('Tạo lớp học thất bại')
                        $('#spinner-div').hide();
                    },
                })
            }

    })

})
