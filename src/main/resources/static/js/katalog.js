function toggleGenresMenu() {
    const menu = document.getElementById('genresMenu');
    if (menu.style.display === 'none' || menu.style.display === '') {
        menu.style.display = 'block';
    } else {
        menu.style.display = 'none';
    }
}

function goToBook(bookCard) {
    const bookId = bookCard.getAttribute('data-id');
    if (bookId) {
        window.location.href = '/Katalog/' + bookId;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchInput = document.getElementById('szukaj');
            const searchTerm = searchInput ? searchInput.value : '';

            let url = '/Katalog?';
            if (searchTerm) {
                url += 'szukaj=' + encodeURIComponent(searchTerm);
            }

            window.location.href = url;
        });
    }

    document.addEventListener('click', function(event) {
        const menu = document.getElementById('genresMenu');
        const button = document.querySelector('.przycisk-gatunki');
        if (menu && button) {
            if (!menu.contains(event.target) && !button.contains(event.target)) {
                menu.style.display = 'none';
            }
        }
    });
});