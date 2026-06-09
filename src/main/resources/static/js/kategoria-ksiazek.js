function removeFromList(event, button, listType) {
    event.stopPropagation();
    const bookCard = button.closest('.book-card');
    const bookId = bookCard.getAttribute('data-id');

    if (!bookId) {
        alert('Nie znaleziono ID książki!');
        return;
    }

    if (!confirm('Czy na pewno chcesz usunąć tę książkę z listy?')) return;

    fetch(`/api/books/${bookId}/remove-from-list?listType=${listType}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            bookCard.remove();
            alert(data.message);

            refreshAllStats();

            const section = button.closest('.books-section');
            const grid = section.querySelector('.books-grid');
            if (grid && grid.children.length === 0) {
                section.remove();
            }
            const sections = document.querySelectorAll('.books-section');
            if (sections.length === 0) {
                location.reload();
            }
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Blad:', error);
        alert('Wystąpił błąd podczas usuwania.');
    });
}

function refreshAllStats() {
    if (typeof refreshHeaderStats === 'function') {
        refreshHeaderStats();
    }
    if (typeof refreshProfileStats === 'function') {
        refreshProfileStats();
    }
    fetch('/api/user/refresh', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('Statystyki odświeżone');
        }
    })
    .catch(error => console.error('Błąd:', error));
}