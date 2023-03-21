$(document).ready(function () {
    $("#calendar-doctor").simpleCalendar({
        fixedStartDay: 0,
        disableEmptyDetails: true,
        events: [{
            startDate: new Date(new Date().setHours(new Date().getHours() + 24)).toDateString(),
            endDate: new Date(new Date().setHours(new Date().getHours() + 25)).toISOString(),
            summary: getSchedule(new Date()+1)
        }, {
            startDate: new Date(new Date().setHours(new Date().getHours() - new Date().getHours() - 12, 0)).toISOString(),
            endDate: new Date(new Date().setHours(new Date().getHours() - new Date().getHours() - 11)).getTime(),
            summary: getSchedule(new Date()-1)
        }, {
            startDate: new Date(new Date().setHours(new Date().getHours() - 48)).toISOString(),
            endDate: new Date(new Date().setHours(new Date().getHours() - 24)).getTime(),
            summary: getSchedule(new Date()-2)
        }],
    });
});

let getSchedule=(startDate)=>{
    var date = new Date(startDate)
    console.log(date)
    return "Hello"
}