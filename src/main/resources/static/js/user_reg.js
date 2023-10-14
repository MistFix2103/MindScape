document.getElementById('back').addEventListener('click', function() {
    window.history.back();
});

function validateForm() {
    var password = document.getElementById("userPassword").value;
    var confirmPassword = document.getElementById("userPassword1").value;

    if (password !== confirmPassword) {
        alert("Пароли не совпадают!");
        return false;
    }

    return true;
}