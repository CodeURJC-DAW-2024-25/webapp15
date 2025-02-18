document.addEventListener("DOMContentLoaded", function () {
    const stars = document.querySelectorAll(".stars i");
    
    // Loop through the "stars" NodeList
    stars.forEach((star, index1) => {
        // Add an event listener that runs a function when the "click" event is triggered
        star.addEventListener("click", () => {
            // Loop through the "stars" NodeList Again
            stars.forEach((star, index2) => {
                // Add the "active" class to the clicked star and any stars with a lower index
                // and remove the "active" class from any stars with a higher index
                index1 >= index2 ? star.classList.add("active") : star.classList.remove("active");
            });
        });
    });

    const items = document.querySelectorAll(".select-size-item");

    items.forEach(item => {
        item.addEventListener("click", function (event) {
            event.preventDefault();

            // Eliminar la clase 'selected' de todas las opciones
            items.forEach(el => el.classList.remove("selected"));

            // Agregar la clase 'selected' a la opción clickeada
            this.classList.add("selected");
        });
    });
});
