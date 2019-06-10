let dropArea = document.querySelector(".file-viewer");

// Associate events
['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
  dropArea.addEventListener(eventName, preventDefaults, false);
  document.body.addEventListener(eventName, preventDefaults, false);
});

// Handle dropped files
dropArea.addEventListener('drop', handleDrop, false);

//
function handleDrop(e) {
  //alert("You dropped a file!");
  var dt = e.dataTransfer;
  var files = dt.files;

  handleFiles(files);
}

function handleFiles(files) {
  files = [...files]
  files.forEach(uploadFile)
}

function uploadFile(file, i) {
  var form = document.querySelector(".file-listing > form");
  var url = form.getAttribute("action");

  var xhr = new XMLHttpRequest();
  var formData = new FormData();
  xhr.open('POST', url, true);
  xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

  formData.append('fileToUpload', file);
  xhr.send(formData);
}

// Prevent defaults
function preventDefaults(e) {
  e.preventDefault();
  e.stopPropagation();
}
