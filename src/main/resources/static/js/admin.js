function refreshHeaderData() {
    fetch('/api/user/refresh', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Aktualizuj dane w headerze jeśli istnieją elementy
            const usernameEl = document.querySelector('.nazwaprofilu span');
            if (usernameEl && data.username) {
                usernameEl.textContent = data.username;
            }
        }
    })
    .catch(error => console.error('Błąd odświeżania:', error));
}

function makeAdmin(button) {
    const userId = button.getAttribute('data-id');
    const email = button.getAttribute('data-email');
    
    if (confirm(`Czy na pewno chcesz nadac uprawnienia administratora uzytkownikowi ${email}?`)) {
        fetch('/api/admin/make-admin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert('Blad: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Blad:', error);
            alert('Wystapil blad podczas nadawania uprawnien.');
        });
    }
}

function removeAdmin(button) {
    const userId = button.getAttribute('data-id');
    const email = button.getAttribute('data-email');
    
    if (confirm(`Czy na pewno chcesz odebrac uprawnienia administratora uzytkownikowi ${email}?`)) {
        fetch('/api/admin/remove-admin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                // Odśwież stronę aby zobaczyć zmiany
                location.reload();
            } else {
                alert('Blad: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Blad:', error);
            alert('Wystapil blad podczas odbierania uprawnien.');
        });
    }
}

function deleteUser(button) {
    const userId = button.getAttribute('data-id');
    const username = button.getAttribute('data-username');
    
    if (confirm(`Czy na pewno chcesz usunąć użytkownika ${username}? Tej operacji nie można cofnąć!`)) {
        fetch(`/api/admin/delete-user/${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                button.closest('tr').remove();
                updateStats();
            } else {
                alert('Blad: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Blad:', error);
            alert('Wystapil blad podczas usuwania użytkownika.');
        });
    }
}

function filterUsers() {
    const searchTerm = document.getElementById('searchUser').value.toLowerCase();
    const rows = document.querySelectorAll('#usersTableBody tr');
    let visibleCount = 0;
    
    rows.forEach(row => {
        const username = row.getAttribute('data-username')?.toLowerCase() || '';
        const email = row.getAttribute('data-email')?.toLowerCase() || '';
        const show = searchTerm === '' || username.includes(searchTerm) || email.includes(searchTerm);
        row.style.display = show ? '' : 'none';
        if (show) visibleCount++;
    });
    
    const noResults = document.getElementById('noResults');
    if (visibleCount === 0 && rows.length > 0) {
        noResults.style.display = 'block';
    } else {
        noResults.style.display = 'none';
    }
}

function clearSearch() {
    document.getElementById('searchUser').value = '';
    filterUsers();
}

function updateStats() {
    const rows = document.querySelectorAll('#usersTableBody tr');
    let adminCount = 0;
    let regularCount = 0;
    
    rows.forEach(row => {
        if (row.style.display !== 'none') {
            const roleBadge = row.querySelector('.role-badge');
            if (roleBadge && roleBadge.classList.contains('admin')) {
                adminCount++;
            } else {
                regularCount++;
            }
        }
    });
    
    document.getElementById('totalUsers').textContent = rows.length;
    document.getElementById('adminCount').textContent = adminCount;
    document.getElementById('regularCount').textContent = regularCount;
}

document.addEventListener('DOMContentLoaded', function() {
    updateStats();
});