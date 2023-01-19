$(document).ready(function() {

    var dropbox = document.getElementById("dropbox"),
        fileElem = document.getElementById("fileElem"),
        fileSelect = document.getElementById("fileSelect");

    fileSelect.addEventListener("click", function(e) {
        if (fileElem) {
            fileElem.click();
        }
    }, false);

    dropbox.addEventListener("dragenter", dragenter, false);
    dropbox.addEventListener("dragover", dragover, false);
    dropbox.addEventListener("drop", drop, false);
});

function dragenter(e) {
    e.stopPropagation();
    e.preventDefault();
}

function dragover(e) {
    e.stopPropagation();
    e.preventDefault();
}

function drop(e) {
    e.stopPropagation();
    e.preventDefault();
    var dt = e.dataTransfer;
    var files = dt.files;
    handleFiles(files);
}

function handleFiles(files) {
    var file = files[0];
    var imageType = /^image\//;
    if (!imageType.test(file.type)) {
        alert("NOT AN IMAGE");
        return;
    }
    var img = document.createElement("img");
    img.classList.add("obj");
    img.file = file;
    img.height = $('#dropbox').height();
    img.width = $('#dropbox').width();
    $(dropbox).addClass('hidden');
    $(preview).removeClass('hidden');
    preview.appendChild(img);
    var reader = new FileReader();
    reader.onload = (function(aImg) {
        return function(e) {
            aImg.src = e.target.result;
        };
    })(img);
    reader.readAsDataURL(file)
}