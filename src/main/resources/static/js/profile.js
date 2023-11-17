var currentModalId = null;

document.addEventListener('DOMContentLoaded', function() {
    var containerId = document.getElementById('highlightContainer')?.value;
    if (containerId) {
        setTimeout(function() {
            highlightContainer(containerId);
        }, 700);
    }

    function processError(elementId, modalId) {
        var errorElement = document.getElementById(elementId);
        if (errorElement && errorElement.textContent.trim() !== '') {
            errorElement.classList.add('active');
            openModal(modalId);
        }
    }

    processError('emailError', 'emailModal');
    processError('passError', 'passModal');
    processError('nameError', 'nameModal');
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' && currentModalId) {
        closeModal(currentModalId);
    }
});

function openModal(modalId) {
    var modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'block';
        currentModalId = modalId;
    } else {
        console.error('Modal not found: ' + modalId);
    }
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';

        var errorMessage = modal.querySelector('.error-message');
        if (errorMessage) {
            errorMessage.textContent = '';
            errorMessage.style.display = 'none';
        }

        currentModalId = null;
    } else {
        console.error('Modal not found: ' + modalId);
    }
}

function highlightContainer(containerId) {
    var container = document.getElementById(containerId);
    if (container) {
        container.classList.add('highlighted');
        setTimeout(function() {
            container.classList.remove('highlighted');
        }, 800);
    } else {
        console.error('Container not found: ' + containerId);
    }
}

function getQueryParam(param) {
    var searchParams = new URLSearchParams(window.location.search);
    return searchParams.get(param);
}

function loadFile(event) {
    var file = event.target.files[0];
    if (file) {
        if (file.type === "image/png") {
            if (file.size <= 16777216) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    var output = document.getElementById('profileImage');
                    output.src = e.target.result;
                    document.getElementById('photo').submit();
                };
                reader.readAsDataURL(file);
            } else {
                alert('Файл слишком большой. Размер файла должен быть не более 16 МБ.');
            }
        } else {
            alert('Пожалуйста, выберите изображение в формате PNG.');
        }
    }
}

