let bookId = window.location.pathname.split('/').pop();

function toggleGenres() {
    const panel = document.getElementById('genresPanel');
    if (panel.style.display === 'none' || panel.style.display === '') {
        panel.style.display = 'block';
    } else {
        panel.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('editBookForm');

    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const formData = new FormData(form);
            const messageDiv = document.getElementById('formMessage');

            const selectedGenres = [];
            document.querySelectorAll('input[name="gatunki"]:checked').forEach(checkbox => {
                selectedGenres.push(checkbox.value);
            });

            formData.delete('gatunki');
            selectedGenres.forEach(genre => {
                formData.append('gatunki', genre);
            });

            messageDiv.style.display = 'block';
            messageDiv.style.backgroundColor = '#cfe2ff';
            messageDiv.style.color = '#084298';
            messageDiv.style.border = '1px solid #b6d4fe';
            messageDiv.innerHTML = '⏳ Zapisywanie zmian...';

            try {
                const response = await fetch(`/api/books/edit/${bookId}`, {
                    method: 'POST',
                    body: formData
                });

                const result = await response.json();

                if (result.success) {
                    messageDiv.style.backgroundColor = '#d4edda';
                    messageDiv.style.color = '#155724';
                    messageDiv.style.border = '1px solid #c3e6cb';
                    messageDiv.innerHTML = '✓ ' + result.message + ' Za chwilę nastąpi przekierowanie...';

                    setTimeout(() => {
                        window.location.href = `/Katalog/${result.bookId}`;
                    }, 2000);
                } else {
                    messageDiv.style.backgroundColor = '#f8d7da';
                    messageDiv.style.color = '#721c24';
                    messageDiv.style.border = '1px solid #f5c6cb';
                    messageDiv.innerHTML = '✗ ' + result.message;

                    setTimeout(() => {
                        messageDiv.style.display = 'none';
                    }, 5000);
                }
            } catch (error) {
                console.error('Błąd:', error);
                messageDiv.style.backgroundColor = '#f8d7da';
                messageDiv.style.color = '#721c24';
                messageDiv.style.border = '1px solid #f5c6cb';
                messageDiv.innerHTML = '✗ Wystąpił błąd podczas zapisywania zmian.';
            }
        });
    }
});