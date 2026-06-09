let bookId = window.location.pathname.split('/').pop();

function toggleLike() {
    fetch(`/api/books/${bookId}/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    });
}

function addComment() {
    const textarea = document.getElementById('commentContent');
    const content = textarea.value;

    if (!content.trim()) {
        alert('Napisz komentarz!');
        return;
    }

    fetch(`/api/books/${bookId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ content: content })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            textarea.value = '';
            location.reload();
        } else {
            alert(data.message);
        }
    });
}

function likeComment(button) {
    const commentId = button.getAttribute('data-id');
    fetch(`/api/comments/${commentId}/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        }
    });
}

function deleteComment(button, commentId) {
    if (!confirm('Czy na pewno chcesz usunąć ten komentarz?')) return;
    fetch(`/api/comments/${commentId}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    });
}

function deleteBook() {
    if (!confirm('Czy na pewno chcesz usunąć tę książkę?')) return;
    fetch(`/api/books/delete/${bookId}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            window.location.href = '/Katalog';
        } else {
            alert(data.message);
        }
    });
}

function editBook() {
    window.location.href = `/EdycjaKsiazki/${bookId}`;
}

function goToBookLink(event, url) {
    event.preventDefault();

    fetch(`/api/books/${bookId}/reading-history`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(() => {
        window.open(url, '_blank');
    })
    .catch(() => {
        window.open(url, '_blank');
    });
}

function saveToMyBooks() {
    fetch(`/api/books/${bookId}/add-to-list?listType=MY_BOOKS`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Książka została zapisana do twoich książek!');
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Błąd:', error);
        alert('Wystąpił błąd podczas zapisywania.');
    });
}

function removeFromMyBooks() {
    if (!confirm('Czy na pewno chcesz usunąć tę książkę z twoich książek?')) return;

    fetch(`/api/books/${bookId}/remove-from-list?listType=MY_BOOKS`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Książka została usunięta z twoich książek!');
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Błąd:', error);
        alert('Wystąpił błąd podczas usuwania.');
    });
}