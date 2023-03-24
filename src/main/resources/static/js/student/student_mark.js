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
                                    return `<span class="badge badge-secondary">Not started</span>`;
                                }
                                if ((typeof (row.asmMark) != "number" && typeof (row.objMark) == "number") || (row.asmMark < 40 || row.objMark < 40) || (typeof (row.objMark) != "number" && typeof (row.asmMark) == "number")) {
                                    return `<span class="badge badge-warning">Not passed</span>`;
                                }
                                return `<span class="badge badge-success">Passed</span>`;
                            }
                        }
                    ],
                    columnDefs: [ {
                        targets: [0,1,2,3,4],
                        createdCell: function (td, cellData, rowData, row, col) {
                            $(td).addClass('text-md')
                        }
                    } ]
                })
            }
        },
        error: (error) => {
            $("#listMark").DataTable({
                columns:[
                    {title:'Student Name'},
                    {title:'Subject Name'},
                    {title:'ASM Mark'},
                    {title:'OBJ Mark'},
                    {title:'Status'}
                ]
            })
        }
    })
})