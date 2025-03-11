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

            // Remove 'selected' class from all options
            items.forEach(el => el.classList.remove("selected"));

            // Add 'selected' class to the clicked option
            this.classList.add("selected");
        });
    });
});

async function deleteReview(productId,idItem) {
    if (!idItem) {
        console.error("Error: idItem o idUser es undefined");
        return;
    }else{
        console.error("This is the review with the ID : " + idItem);
    }

    try {
        const response = await fetch(`/shop/${productId}/deleteReview/${idItem}`, { method: 'GET' });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.text();
        console.log("✅ Response from server:", result);

        document.getElementById("ReviewsList").innerHTML = result;


    } catch (error) {
        console.error("Error ocurred deleting an item:", error);
    }
}
