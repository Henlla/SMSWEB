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
