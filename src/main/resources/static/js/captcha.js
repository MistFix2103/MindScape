document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeModal();
        }
    });
});

function openModal() {
    document.getElementById('captchaModal').classList.add('show');
}

function closeModal() {
    var modal = document.getElementById('captchaModal');
    if (modal) {
        modal.classList.remove('show');
        var errorMessage = modal.querySelector('.modalError-message');
        if (errorMessage) {
            errorMessage.textContent = '';
            errorMessage.style.display = 'none';
        }
    }
}

document.addEventListener('submit', function(event) {
    if (event.target.matches('.registration-form, .pass-form')) {
        event.preventDefault();
        handleForm(event.target);
    } else if (event.target.matches('.captcha-form')) {
        event.preventDefault();
        handleCaptchaForm(event.target);
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
            var errorElement = document.getElementById('error');
            errorElement.textContent = data.error;
            errorElement.style.display = 'block';
        } else if (data.num1 && data.num2) {
            document.getElementById('num1').textContent = data.num1;
            document.getElementById('num2').textContent = data.num2;
            openModal();
        }
    })
    .catch(error => {
        console.error('Ошибка регистрации или смены пароля:', error);
    });
}

function handleCaptchaForm(form) {
    var formData = new FormData(form);
    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.captchaError) {
            var errorElement = document.getElementById('captchaError');
            errorElement.textContent = data.captchaError;
            errorElement.style.display = 'block';
            openModal();
        } else if (data.role) {
            window.location.href = '/registration/verification?role=' + data.role;
        } else if (data.action) {
            window.location.href = '/forgot_password/verification';
        }
    })
    .catch(error => {
        console.error('Ошибка при подтверждении капчи:', error);
    });
}
