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

async function deleteReview(productId,idItem) {
    if (!idItem) {
        console.error("Error: idItem o idUser es undefined");
        return;
    }else{
        console.error("Esta es la review con id: " + idItem);
    }

    try {
        const response = await fetch(`/shop/${productId}/deleteReview/${idItem}`, { method: 'GET' });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.text();
        console.log("✅ Respuesta del servidor:", result);

        document.getElementById("ReviewsList").innerHTML = result;


    } catch (error) {
        console.error("Error al eliminar el item:", error);
    }
}


let pageReviews = 0;

async function loadMoreReviews(shoeId) {
    try {
        pageReviews++;
        const response = await fetch(`/shop/single-product/loadMoreReviews?page=${pageReviews}&shoeId=${shoeId}`);
        
        const data = await response.text();
        console.log(data);
        let reviewsDiv = document.getElementById("ReviewsList");

        // Agregar las nuevas reseñas al contenedor
        reviewsDiv.innerHTML += data;

        // Ocultar botón si ya no hay más reseñas disponibles
        if (String(data).includes("No more reviews available.")) {
            let loadMoreButton = document.getElementById("loadMoreReviewsButtom");
            if (loadMoreButton) {
                loadMoreButton.style.display = "none";
            }
        }
    } catch (error) {
        console.log("Error trying to load reviews: ", error);
    }
}

window.loadMoreReviews = loadMoreReviews;
