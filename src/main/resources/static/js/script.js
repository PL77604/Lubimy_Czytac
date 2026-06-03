document.addEventListener("DOMContentLoaded", function() {
    const elementy = document.querySelectorAll(".animacja");
    elementy.forEach(function(element, index) {
        setTimeout(function() {
            element.classList.add("widoczny");
        }, index * 80);
    });

    const burgerMenu = document.getElementById("burgerMenu");
    const mainMenu = document.getElementById("mainMenu");
    const menuOverlay = document.getElementById("menuOverlay");

    if (burgerMenu && mainMenu) {
        const toggleMenu = function() {
            burgerMenu.classList.toggle("active");
            mainMenu.classList.toggle("active");
            if (menuOverlay) {
                menuOverlay.classList.toggle("active");
            }
            document.body.style.overflow = mainMenu.classList.contains("active") ? "hidden" : "";
            document.body.classList.toggle("menu-open", mainMenu.classList.contains("active"));
        };

        burgerMenu.addEventListener("click", function(e) {
            e.stopPropagation();
            toggleMenu();
        });

        if (menuOverlay) {
            menuOverlay.addEventListener("click", function() {
                if (mainMenu.classList.contains("active")) {
                    toggleMenu();
                }
            });
        }

        const menuLinks = mainMenu.querySelectorAll("a");
        menuLinks.forEach(function(link) {
            link.addEventListener("click", function() {
                if (mainMenu.classList.contains("active")) {
                    toggleMenu();
                }
            });
        });
    }

    const profilInfo = document.querySelector(".profilinfo");
    const dropdownMenu = document.querySelector(".dropdown-menu");

    if (profilInfo && dropdownMenu) {
        profilInfo.addEventListener("click", function(e) {
            e.stopPropagation();
            if (mainMenu && mainMenu.classList.contains("active")) {
                burgerMenu.classList.remove("active");
                mainMenu.classList.remove("active");
                if (menuOverlay) menuOverlay.classList.remove("active");
                document.body.style.overflow = "";
            }
            profilInfo.classList.toggle("active");
            dropdownMenu.classList.toggle("show");
        });

        document.addEventListener("click", function(event) {
            if (!profilInfo.contains(event.target) && !dropdownMenu.contains(event.target)) {
                profilInfo.classList.remove("active");
                dropdownMenu.classList.remove("show");
            }
        });
    }

    document.addEventListener("click", function(event) {
        const link = event.target.closest("a");
        if (!link) return;

        const adres = link.getAttribute("href");
        if (!adres || adres.startsWith("#") || adres.startsWith("http") || adres === "/Wyloguj") {
            return;
        }

        if (mainMenu && mainMenu.classList.contains("active")) {
            burgerMenu.classList.remove("active");
            mainMenu.classList.remove("active");
            if (menuOverlay) menuOverlay.classList.remove("active");
            document.body.style.overflow = "";
        }

        event.preventDefault();
        const main = document.querySelector("main");
        if (main) main.classList.add("zanikanie");

        setTimeout(function() {
            window.location.href = adres;
        }, 180);
    });
});