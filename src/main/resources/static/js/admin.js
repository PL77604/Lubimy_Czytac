function makeAdmin(button) {
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
                // Sprawdź czy to my straciliśmy lub zyskaliśmy admina
                fetch('/api/user/refresh')
                    .then(res => res.json())
                    .then(userData => {
                        if (userData.success) {
                            if (!userData.isAdmin && window.location.pathname === '/admin/panel') {
                                // Jeśli jesteśmy na admin panelu i straciliśmy admina - wróć na stronę główną
                                window.location.href = '/';
                            } else {
                                // W przeciwnym razie odśwież stronę
                                location.reload();
                            }
                        } else {
                            location.reload();
                        }
                    });
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
                // Sprawdź czy to my straciliśmy admina
                fetch('/api/user/refresh')
                    .then(res => res.json())
                    .then(userData => {
                        if (userData.success) {
                            if (!userData.isAdmin && window.location.pathname === '/admin/panel') {
                                // Jeśli jesteśmy na admin panelu i straciliśmy admina - wróć na stronę główną
                                window.location.href = '/';
                            } else {
                                // W przeciwnym razie odśwież stronę
                                location.reload();
                            }
                        } else {
                            location.reload();
                        }
                    });
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
    if (noResults) {
        noResults.style.display = visibleCount === 0 && rows.length > 0 ? 'block' : 'none';
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

    const totalEl = document.getElementById('totalUsers');
    const adminEl = document.getElementById('adminCount');
    const regularEl = document.getElementById('regularCount');

    if (totalEl) totalEl.textContent = rows.length;
    if (adminEl) adminEl.textContent = adminCount;
    if (regularEl) regularEl.textContent = regularCount;
}

document.addEventListener('DOMContentLoaded', function() {
    updateStats();
});