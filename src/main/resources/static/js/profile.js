var currentModalId = null;

document.addEventListener('DOMContentLoaded', function() {
    var containerId = document.getElementById('highlightContainer')?.value;
    if (containerId) {
        setTimeout(function() {
            highlightContainer(containerId);
        }, 700);
    }

    var emailError = document.getElementById('emailError');
    if (emailError && emailError.textContent.trim() !== '') {
        emailError.classList.add('active');
        openModal('emailModal');
    }

    var passError = document.getElementById('passError');
    if (passError && passError.textContent.trim() !== '') {
        passError.classList.add('active');
        openModal('passModal');
    }
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
        }, 500);
    } else {
        console.error('Container not found: ' + containerId);
    }
}

function getQueryParam(param) {
    var searchParams = new URLSearchParams(window.location.search);
    return searchParams.get(param);
}
