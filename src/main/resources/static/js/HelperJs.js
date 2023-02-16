var FormatHelper = (type, value, formatType) => {
    switch (type.toLowerCase()) {
        case "DATE":
            break;
        case "TIME":
            break;
        default:
            alert("Không tìm thấy case cho type " + type);
            break;
    }
}
var GetExtension = (fileName) =>{
    return fileName.split('.').pop();
}
