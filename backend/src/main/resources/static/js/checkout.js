//Check the stock and deactivate the button if there are products out of stock
function checkStockAvailability(responseText) {
    const checkoutButton = document.getElementById("continueButtom");
    const stockWarning = document.getElementById("recalculateButtom");
    console.log(responseText);
    // Asegurarse de que hay elementos antes de buscar stock
    let hasOutOfStockItems = false;

    // 🔹 Si tenemos el responseText, verificamos directamente el contenido
    if (responseText.includes("<p>No stock available</p>")) {
        hasOutOfStockItems = true;
    } else {
        hasOutOfStockItems = false;
    }


    if (hasOutOfStockItems) {
        checkoutButton.disabled = true;
        stockWarning.style.display = "none";
    } else {
        checkoutButton.disabled = false;
        stockWarning.style.display = "block";
    }
}



async function recalculate() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
    let formData = new FormData();
    let resultado;

    document.querySelectorAll(".quantity-input").forEach(input => {
        let id = input.getAttribute("data-id");// id_orderItem
        let quantity = parseInt(input.value, 10);

        //If the user left the field empty, we use the previous value or 1
        if (isNaN(quantity) || input.value.trim() === "") {
            quantity = input.dataset.previousValue ? parseInt(input.dataset.previousValue, 10) : 1;
            input.value = quantity; // We fill in the input so that the user sees it corrected
        }

        formData.append("ids", id);
        formData.append("quantities", quantity);
    });

    try {
        let response = await fetch(`/checkout/recalculate`, {
            method: "POST",
            body: formData,
            headers: {
                [csrfHeader]: csrfToken //Include the CSRF token in the request
            }
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        let result = await response.text();
        resultado = result;
        document.getElementById("CartItemsList").innerHTML = result;

        if (typeof window.initProductQty === "function") {
            window.initProductQty();
        } else {
            console.error("initProductQty no está disponible.");
        }
    } catch (error) {
        alert("Something bad happened while trying to recalculate.");
    }

}


async function deleteItemfromCart(idItem) {
    if (!idItem) {
        console.error("Error: idItem o idUser es undefined");
        return;
    }

    try {
        const response = await fetch(`/checkout/deleteItem/${idItem}`, { method: 'GET' });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.text();
        document.getElementById("CartItemsList").innerHTML = result;

        if (typeof window.initProductQty === "function") {
            window.initProductQty();
        } else {
            console.error("initProductQty is not available. Please check if script.js was loaded.");
        }

    } catch (error) {
        console.error("Error deleting item:", error);
    }

}

document.addEventListener("DOMContentLoaded", function () {
    const couponInput = document.querySelector('input[name="coupon"]');
    const applyCouponButton = document.createElement('button');
    applyCouponButton.textContent = 'Apply Coupon';
    applyCouponButton.classList.add('btn', 'btn-red', 'hvr-sweep-to-right', 'dark-sweep');
    applyCouponButton.type = 'button';
    applyCouponButton.onclick = applyCoupon;

    const couponSection = document.querySelector('.coupon-section');
    couponSection.appendChild(applyCouponButton);
});

async function applyCoupon() {
    const couponCode = document.querySelector('input[name="coupon"]').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
    const form = new FormData();
    form.append("coupon", couponCode);
    try {
        const response = await fetch('/checkout/applyCoupon', {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            body: form
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.text();
        console.log(result);
        if (result.includes('<p style="color: red; font-style:italic;">coupon does not exist</p>')) {
            const error = document.getElementById("cuponError");
            error.innerHTML = result;
        } else {
            document.querySelectorAll(".btn-link.text-danger").forEach(button => { button.style.display = "none"; });
            document.getElementById("recalculateButtom").style.display = "none";
            const summarydiv = document.getElementById("finalSummary");
            summarydiv.innerHTML = result;
        }

    } catch (error) {
        console.error('Error applying coupon:', error);
        alert('Error applying coupon.');
    }
}