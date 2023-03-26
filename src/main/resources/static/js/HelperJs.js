var FormatHelper = (type, value, formatType) => {
    switch (type.toUpperCase()) {
        case "DATE":
            var date = value.getDate() < 10 ? "0" + value.getDate() : value.getDate();
            var month = value.getMonth() + 1 < 10 ? "0" + (parseInt(value.getMonth()) + 1) : value.getMonth() + 1;
            var year = value.getFullYear();
            switch (formatType.toLowerCase()) {
                case "dd/mm/yyyy":
                    return date + "/" + month + "/" + year;
                case "mm/dd/yyyy":
                    return month + "/" + date + "/" + year;
                case "yyyymmdd":
                    return year + month + date;
                case "yyyy/mm/dd":
                    return year + "/" + month + "/" + date;
                case "dd/mm/yy":
                    return date + "/" + month + "/" + year.toLocaleDateString('en', {year: '2-digit'});
                case "dd-mm-yyyy":
                    return date + "-" + month + "-" + year;
                case "mm-dd-yyyy":
                    return month + "-" + date + "-" + year;
                case "yyyy-mm-dd":
                    return year + "-" + month + "-" + date;
                default:
                    console.error("Ngày sai định dạng");
                    break;
            }
            break;
        case "TIME":
            break;
        default:
            alert("Không tìm thấy case cho type " + type);
            break;
    }
}

var GetExtension = (fileName) => {
    return fileName.split('.').pop();
}

// 1. Chuyển đổi image sang base64 string
var toBase64 = (file) => new Promise((resolve, reject) => {
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result.replace("data:", "").replace(/^.+,/, ""));
    reader.onerror = error => reject(error);
});

// 2. Chuyển đổi base64 string sang image
async function base64ToImage(base64String) {
    var base64Response = await fetch(`data:image/png;base64,${base64String}`);
    return base64Response;
}

// Chuyển đổi base64 sang word
async function base64ToWord(base64String) {
    var base64Response = await fetch(`data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,${base64String}`);
    return base64Response;
}

var urltoFile = (url, mimeType) => {
    return (fetch(url)
            .then(function (res) {
                return res.arrayBuffer();
            })
            .then(function (buf) {
                return new File([buf], {type: mimeType});
            })
    );
}

function _calculateAge(birthday) { // birthday is a date
    var ageDifMs = Date.now() - birthday.getTime();
    var ageDate = new Date(ageDifMs); // miliseconds from epoch
    return Math.abs(ageDate.getUTCFullYear() - 1970);
}

let regexPhone = (phone) => {
    const regexPhoneNumber = /(0[3|5|7|8|9])+([0-9]{8})\b/g;
    return phone.match(regexPhoneNumber) ? true : false;

}
