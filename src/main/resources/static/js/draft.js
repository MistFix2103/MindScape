var currentModalId = null;
var text = null;

document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeModal(currentModalId);
        }
    });
});

function openModal(element, modalId) {
    if (modalId === 'storyModal') {
        document.getElementById('storyModal').classList.add('show');
        var header = document.getElementById('text');
        header.textContent = 'Черновик';
        var textarea = document.getElementById('storyText');
        textarea.textContent = element.dataset.text;
        text = element.dataset.text;
        document.getElementById('editStoryId').value = element.getAttribute('data-id');
        document.getElementById('editStoryText').value = element.getAttribute('data-text');
        var form = document.getElementById('story-form');
        form.action = '/home/publishDraft';
    } else {
        document.getElementById('deleteModal').style.display = 'block';
        document.getElementById('deleteStoryId').value = element.getAttribute('data-id');
        var caution = document.getElementById('caution');
        caution.textContent = 'Вы действительно хотите удалить черновик? Это действие нельзя будет отменить';
    }
    currentModalId = modalId;
}

function closeModal(modalId) {
    if (modalId === 'storyModal') {
            var textarea = document.getElementById('storyText');
            if (textarea.value != text) {
                if (confirm('Вы хотите сохранить черновик?')) {
                    saveDraft(textarea.value, document.getElementById('editStoryId').value);
                } else {
                    document.getElementById('storyModal').classList.remove('show');
                    textarea.value = text;
                    text = null;
                }
            } else {
                document.getElementById('storyModal').classList.remove('show');
            }
    } else {
        document.getElementById('deleteModal').style.display = 'none';
    }
    currentModalId = null;
}

document.getElementById('story-form').addEventListener('submit', function(e) {
    var editStoryText = document.getElementById('storyText').value;
    if (editStoryText.length < 100) {
        alert('История должна содержать не менее 100 символов.');
        e.preventDefault();
    }
});

function saveDraft(newText, id) {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/home/saveEditedDraft", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
    xhr.onload = function() {
            if (xhr.status === 200) {
                document.getElementById('storyModal').classList.remove('show');
                document.getElementById('storyText').value = newText;
                window.location.reload();
            }
            else {
                alert("Ошибка при сохранении черновика: " + xhr.statusText);
            }
        };
        xhr.send("story=" + encodeURIComponent(newText) + "&storyId=" + encodeURIComponent(id));
}