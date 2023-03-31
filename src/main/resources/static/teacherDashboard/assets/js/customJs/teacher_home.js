$(()=>{
    let dataJSON = JSON.parse($('#listAttendanceCurrenMonth').val())
    // console.log(data)
    OnSetDataAttendanceTracking(dataJSON)
    $('#month').on('change',()=>{
       let teacherId = $('#teacherId').val()
        let month = $('#month').val()
        let data = new FormData()
        data.append("teacherId",teacherId)
        data.append("month",month)
        $.ajax({
            url:"/teacher/getAttendanceTrackingByMonth",
            data:data,
            method:"POST",
            cache : false,
            processData: false,
            contentType: false,
            success:(res)=>{
                OnSetDataAttendanceTracking(res)
            },error:(err)=>{

            }
        })
    })

    let listChart = JSON.parse($('#chartList').val())
    // let finalList = listChart.map(i=>Date.parse(i.x))
    // console.log(finalList)
    OnSetChart(listChart)
})

let OnSetDataAttendanceTracking = (data)=>{
    let table = document.getElementById("table_attendance_tracking")
    $('#table_attendance_tracking tbody').remove()
    $('#table_attendance_tracking thead').remove()

    let tbody = document.createElement("tbody")
    let thead = document.createElement("thead")

    //head
    let tr_head = document.createElement("tr")
    thead.appendChild(tr_head)
    let th_default = document.createElement("th")
    th_default.innerHTML="Days"
    tr_head.appendChild(th_default)

    //body
    let tr_body = document.createElement("tr")
    tbody.appendChild(tr_body)
    let td_default= document.createElement("td")
    tr_body.appendChild(td_default)

    let sum = 0;

    for (let i of data){
        // console.log(i.date.split('-')[2])
        let th = document.createElement("th")
        th.innerHTML=`${i.date.split('-')[2]}`
        tr_head.appendChild(th)
        let td = document.createElement("td")
        if(i.count===0){
            td.innerHTML=`<span style="color:red;margin-left: 10px">x</span>`
        }else{
            let total = i.count * 2
            sum += total
            td.innerHTML= `<span style="color:green;font-weight: bold;margin-left: 10px">${total}</span>`
        }
        tr_body.appendChild(td)
    }
    // console.log(thead)
    // console.log(tbody)
    // console.log(table)
    $('.total').html(`Total : ${sum} hours`)
    table.appendChild(thead)
    table.appendChild(tbody)
}

let OnSetChart=(res)=>{
    const data = {
        // labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        datasets: [{
            label: 'Attendance tracking',
            data: res,
            backgroundColor: [
                'rgba(255, 26, 104, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)',
                'rgba(0, 0, 0, 0.2)'
            ],
            borderColor: [
                'rgba(255, 26, 104, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)',
                'rgba(0, 0, 0, 1)'
            ],
            borderWidth: 1
        }]
    };

    // config
    const config = {
        type: 'bar',
        data,
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    };

    // render init block
    const myChart = new Chart(
        document.getElementById('myChart'),
        config
    );

    // Instantly assign Chart.js version
    const chartVersion = document.getElementById('chartVersion');
    chartVersion.innerText = Chart.version;
}