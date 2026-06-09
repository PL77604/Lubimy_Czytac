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