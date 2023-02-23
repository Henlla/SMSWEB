var FormatHelper = (type, value, formatType) => {
    switch (type.toUpperCase()) {
        case "DATE":
            var date = value.getDate() < 10 ? "0" + value.getDate() : value.getDate();
            var month = value.getMonth() + 1 < 10 ? "0" + (parseInt(value.getMonth()) + 1): value.getMonth() + 1;
            var year = value.getFullYear();
            if (formatType.toLowerCase() === "dd/mm/yyyy") {
                return date + "/" + month + "/" + year;
            } else if (formatType.toLowerCase() === "mm/dd/yyyy") {
                return month + "/" + date + "/" + year;
            } else if (formatType.toLowerCase() === "yyyymmdd") {
                return year + month + date;
            } else if (formatType.toLowerCase() === "yyyy/mm/dd") {
                return year + "/" + month + "/" + date;
            } else {
                console.error("Ngày sai định dạng");
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
