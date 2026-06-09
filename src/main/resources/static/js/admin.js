function refreshUserSession() {
    return fetch('/api/user/refresh', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            updateHeaderUI(data);
            return data;
        }
        return null;
    })
    .catch(error => console.error('Błąd odświeżania sesji:', error));
}

function updateHeaderUI(userData) {
    const usernameSpan = document.querySelector('.nazwaprofilu span');
    if (usernameSpan && userData.username) {
        usernameSpan.textContent = userData.username;
    }

    const avatarDiv = document.querySelector('.avatarprofilu');
    if (avatarDiv) {
        if (userData.avatar && userData.avatar.includes('http')) {
            avatarDiv.innerHTML = `<img src="${userData.avatar}" alt="Avatar">`;
        } else {
            avatarDiv.innerHTML = `<span><i class="fa-solid fa-user"></i></span>`;
        }
    }

    const dodaneStat = document.querySelector('.statystyki-dropdown .stats-item:first-child .stats-value');
    if (dodaneStat && userData.dodaneKsiazki !== undefined) {
        dodaneStat.textContent = userData.dodaneKsiazki;
    }

    const pobraneStat = document.querySelector('.statystyki-dropdown .stats-item:last-child .stats-value');
    if (pobraneStat && userData.pobraneKsiazki !== undefined) {
        pobraneStat.textContent = userData.pobraneKsiazki;
    }

    const adminLink = document.querySelector('.menu a[href="/admin/panel"]');
    const adminLi = document.querySelector('.menu li');

    if (userData.isAdmin) {
        if (!adminLink) {
            const menu = document.querySelector('.menu');
            const adminLiNew = document.createElement('li');
            const adminANew = document.createElement('a');
            adminANew.href = '/admin/panel';
            adminANew.textContent = 'Panel Admina';
            adminLiNew.appendChild(adminANew);
            if (menu) menu.appendChild(adminLiNew);
        }
    } else {
        if (adminLink) {
            adminLink.closest('li').remove();
        }
    }

    if (typeof window.roleChanged === 'undefined') {
        window.roleChanged = false;
    }

    if (userData.isAdminChanged && !window.roleChanged) {
        window.roleChanged = true;
        setTimeout(() => {
            location.reload();
        }, 500);
    }
}

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
                // Odśwież stronę TYLKO po zmianie roli
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
                // Odśwież stronę TYLKO po zmianie roli
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
                // Tylko usuń wiersz z tabeli, bez odświeżania całej strony
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