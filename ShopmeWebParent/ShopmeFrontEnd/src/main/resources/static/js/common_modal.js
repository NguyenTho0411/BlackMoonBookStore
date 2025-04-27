function showModalDialog(title, message) {
  $("#modalTitle").text(title);
  $("#modalBody").text(message);
  var myModal = new bootstrap.Modal(document.getElementById('modalDialog'));
  myModal.show();
}

function showErrorModal(message) {
  showModalDialog("Error", message);
}

function showWarningModal(message) {
  showModalDialog("Warning", message);
}