var currentModalId = null;

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
        var modalButton = document.getElementById('send-story');
        modalButton.textContent = 'Редактировать';
        var header = document.getElementById('text');
        header.textContent = 'Редактировать историю';
        var textarea = document.getElementById('storyText');
        textarea.textContent = element.dataset.text;
        document.getElementById('editStoryId').value = element.getAttribute('data-id');
        document.getElementById('editStoryText').value = element.getAttribute('data-text');
        var form = document.getElementById('story-form');
        form.action = '/home/editStory';
    } else {
        document.getElementById('deleteModal').style.display = 'block';
        document.getElementById('deleteStoryId').value = element.getAttribute('data-id');
    }
    currentModalId = modalId;
}

function closeModal(modalId) {
    if (modalId === 'storyModal') {
        document.getElementById('storyModal').classList.remove('show');
    } else {
        document.getElementById('deleteModal').style.display = 'none';
    }
    currentModalId = null;
}