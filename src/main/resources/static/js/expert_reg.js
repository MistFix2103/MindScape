var inputElement = document.querySelector('input[type="file"]');

inputElement.addEventListener('change', function() {
  if (this.files.length > 3) {
    alert('Вы можете загрузить только 3 файла!');
    this.value = '';
    return;
  }

  for (var i = 0; i < this.files.length; i++) {
    var fileName = this.files[i].name;
    var fileExtension = fileName.split('.').pop().toLowerCase();

    if (!['pdf', 'doc', 'docx'].includes(fileExtension)) {
      alert('Разрешены файлы с расширениями .pdf, .doc, .docx');
      this.value = '';
      return;
    }
  }
});

document.getElementById('file').addEventListener('change', function() {
    var fileNameDisplay = document.getElementById('file-name');
    var fileCount = this.files.length;

    if (fileCount === 1) {
        fileNameDisplay.textContent = 'Выбран 1 файл.';
    } else if (fileCount > 1) {
        fileNameDisplay.textContent = 'Выбрано ' + fileCount + ' файла.';
    } else {
        fileNameDisplay.textContent = 'Файл не выбран.';
    }
});