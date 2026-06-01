document.addEventListener("DOMContentLoaded", function(){
    const elementy = document.querySelectorAll(".animacja");

    elementy.forEach(function(element, index){
        setTimeout(function(){
            element.classList.add("widoczny");
        }, index * 80);
    });
});

document.addEventListener("click", function(event){
    const link = event.target.closest("a");

    if(!link){
        return;
    }

    const adres = link.getAttribute("href");

    if(!adres || adres.startsWith("#") || adres.startsWith("http")){
        return;
    }

    event.preventDefault();

    const main = document.querySelector("main");

    if(main){
        main.classList.add("zanikanie");
    }

    setTimeout(function(){
        window.location.href = adres;
    }, 180);
});