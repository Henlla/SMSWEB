$(()=>{
    $.ajax({
        url: "/student/get-marks-list",
        method: "GET",
        success: (response) => {
            let parse = JSON.parse(response);
            if(parse == null || parse == ""){
                $("#listMark").DataTable({
                    data: parse,
                    columns:[
                        {title:'Student Name'},
                        {title:'Subject Name'},
                        {title:'ASM Mark'},
                        {title:'OBJ Mark'},
                        {title:'Status'}
                    ]
                })
            }else {
                $("#listMark").DataTable({
                    data: parse,
                    columns:[
                        {
                            title:'Student Name',
                            data:'fullName'
                        }, {
                            title:'Subject Name',
                            data:'subjectName'
                        }, {
                            title:'ASM Mark',
                            data:'asmMark'
                        }, {
                            title:'OBJ Mark',
                            data:'objMark'
                        }, {
                            title:'Status',
                            data:function(row, type, set) {
                                if (typeof (row.asmMark) != "number" && typeof (row.objMark) != "number") {
                                    return "Not started";
                                }
                                if ((typeof (row.asmMark) != "number" && typeof (row.objMark) == "number") || (row.asmMark < 40 || row.objMark < 40) || (typeof (row.objMark) != "number" && typeof (row.asmMark) == "number")) {
                                    return "Not passed";
                                }
                                return "Passed"
                            }
                        }
                    ],
                    columnDefs: [ {
                        targets: 4,
                        createdCell: function (td, cellData, rowData, row, col) {
                            if ( cellData =='Passed' ) {
                                $(td).addClass('text-success')
                            }else if ( cellData =='Not passed' ) {
                                $(td).addClass('text-warning')
                            }
                        }
                    } ]
                })
            }
        },
        error: (error) => {

        }
    })
})