// 🔹 Check the stock and deactivate the button if there are products out of stock
function checkStockAvailability() {
    const checkoutButton = document.getElementById("checkoutButton");
    const stockWarning = document.getElementById("stockWarning");

    if (!checkoutButton || !stockWarning) return; 

    // Find if there is any <p> element with the text "No stock available"
    const hasOutOfStockItems = Array.from(document.querySelectorAll(".order-item p"))
        .some(p => p.textContent.trim() === "No stock available");

    if (hasOutOfStockItems) {
        checkoutButton.disabled = true;
        stockWarning.style.display = "block";
    } else {
        checkoutButton.disabled = false;
        stockWarning.style.display = "none";
    }
}

document.addEventListener("DOMContentLoaded", checkStockAvailability);

async function recalculate() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
    let formData = new FormData();

    document.querySelectorAll(".quantity-input").forEach(input => {
        let id = input.getAttribute("data-id"); // id_orderItem
        let quantity = parseInt(input.value, 10);

        // 🔹 If the user left the field empty, we use the previous value or 1
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
                [csrfHeader]: csrfToken // 🔹 Include the CSRF token in the request
            }
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        let result = await response.text();
        document.getElementById("CartItemsList").innerHTML = result;

        if (typeof window.initProductQty === "function") {
            window.initProductQty();
        } else {
            console.error("initProductQty no está disponible.");
        }

        // 🔹 Check stock after recalculation
        checkStockAvailability();

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
        console.log("✅ Response from server:", result);

        document.getElementById("CartItemsList").innerHTML = result;

        if (typeof window.initProductQty === "function") {
            window.initProductQty();
        } else {
            console.error("initProductQty is not available. Please check if script.js was loaded.");
        }

        // 🔹 Check stock after deleting a product
        checkStockAvailability();

    } catch (error) {
        console.error("Error deleting item:", error);
    }

}
document.addEventListener("DOMContentLoaded", function() {
    const couponInput = document.querySelector('input[name="coupon"]');
    const applyCouponButton = document.createElement('button');
    applyCouponButton.textContent = 'Apply Coupon';
    applyCouponButton.classList.add('btn','btn-red', 'hvr-sweep-to-right', 'dark-sweep');
    applyCouponButton.type = 'button';
    applyCouponButton.onclick = applyCoupon;

    const couponSection = document.querySelector('.coupon-section');
    couponSection.appendChild(applyCouponButton);
});

async function applyCoupon() {
    const couponCode = document.querySelector('input[name="coupon"]').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    try {
        const response = await fetch('/checkout/applyCoupon', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ coupon: couponCode })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.json();
        if (result.success) {
            alert('Coupon applied successfully!');
            document.querySelector('.total-price').textContent = result.newTotal;
        } else {
            alert('Invalid coupon code.');
        }
    } catch (error) {
        console.error('Error applying coupon:', error);
        alert('Error applying coupon.');
    }
}