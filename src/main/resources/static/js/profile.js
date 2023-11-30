var currentModalId = null;

document.addEventListener('DOMContentLoaded', (event) => {
    var highlightContainerName = sessionStorage.getItem('highlightContainerName');
    var containerId = document.getElementById('highlightContainer')?.value;
    var highlightElement = highlightContainerName || containerId;
    if (highlightElement) {
        setTimeout(function() {
            highlightContainer(highlightElement);
        }, 700);
    }
    if (highlightContainerName) {
        sessionStorage.removeItem('highlightContainerName');
    }
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' && currentModalId) {
        closeModal(currentModalId);
    }
});

function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
    currentModalId = modalId;
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    document.getElementById(modalId).style.display = 'none';
    var errorMessage = modal.querySelector('.error-message');
    if (errorMessage) {
        errorMessage.textContent = '';
        errorMessage.style.display = 'none';
    }
    currentModalId = null;
}

function highlightContainer(containerId) {
    var container = document.getElementById(containerId);
    container.classList.add('highlighted');
    setTimeout(function() {
        container.classList.remove('highlighted');
    }, 800);
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

document.addEventListener('submit', function(event) {
    if (event.target.matches('.form-container')) {
        event.preventDefault();
        handleForm(event.target);
    }
});

function handleForm(form) {
    var formData = new FormData(form);
    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            var errorElement = document.getElementById('profileError');
            errorElement.textContent = data.error;
            errorElement.style.display = 'block';
        }
        if (data.highlightContainerName) {
            closeModal(currentModalId);
            sessionStorage.setItem('highlightContainerName', data.highlightContainerName);
            window.location.reload();
        }
        if (data.operationType) {
            closeModal(currentModalId);
            window.location.href = '/home/profile/verification';
        }
    })
    .catch(error => {
        console.error('Ошибка изменения данных:', error);
    });
}