var inputElement = document.querySelector('input[type="file"]');

inputElement.addEventListener('change', function() {
  // Проверка на количество файлов
  if (this.files.length > 3) {
    alert('Вы можете загрузить только 3 файла!');
    this.value = ''; // сброс выбранных файлов
    return;
  }

  // Проверка на расширение файла
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