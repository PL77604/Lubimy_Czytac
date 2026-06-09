function goToBook(bookCard) {
    const bookId = bookCard.getAttribute('data-id');
    if (bookId) {
        window.location.href = '/Katalog/' + bookId;
    }
}

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

            // Odśwież statystyki w headerze
            if (typeof refreshHeaderStats === 'function') {
                refreshHeaderStats();
            }

            const section = bookCard.closest('.books-section');
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

function deleteAllFromList(button) {
    const listType = button.getAttribute('data-list');
    if (!confirm('Czy na pewno chcesz usunąć WSZYSTKIE książki z tej listy?')) return;

    const section = button.closest('.books-section');
    const cards = section.querySelectorAll('.book-card');

    if (cards.length === 0) return;

    let deleted = 0;
    cards.forEach(card => {
        const bookId = card.getAttribute('data-id');
        fetch(`/api/books/${bookId}/remove-from-list?listType=${listType}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                card.remove();
                deleted++;
                if (deleted === cards.length) {
                    section.remove();
                    alert('Usunięto wszystkie książki z listy!');
                    if (typeof refreshHeaderStats === 'function') {
                        refreshHeaderStats();
                    }
                    const remainingSections = document.querySelectorAll('.books-section');
                    if (remainingSections.length === 0) {
                        location.reload();
                    }
                }
            }
        });
    });
}