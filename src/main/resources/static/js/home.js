document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeModal();
        }
    });
});

function openModal() {
    document.getElementById('storyModal').classList.add('show');
}

function closeModal() {
    var textarea = document.getElementById('storyText');
    if (textarea.value.trim().length > 0) {
        if (confirm('Вы хотите сохранить черновик истории?')) {
            saveDraft(textarea.value);
        } else {
            document.getElementById('storyModal').classList.remove('show');
            textarea.value = '';
        }
    } else {
        document.getElementById('storyModal').classList.remove('show');
    }
}

function saveDraft(text) {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/home/saveDraft", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
    xhr.onload = function() {
            if (xhr.status === 200) {
                document.getElementById('storyModal').classList.remove('show');
                document.getElementById('storyText').value = '';
            }
            else {
                alert("Ошибка при сохранении черновика: " + xhr.statusText);
            }
        };
        xhr.send("story=" + encodeURIComponent(text));
}

document.getElementById('story-form').addEventListener('submit', function(e) {
    var text = document.getElementById('storyText').value;
    if (text.length < 100) {
        alert('История должна содержать не менее 100 символов.');
        e.preventDefault();
    }
});
