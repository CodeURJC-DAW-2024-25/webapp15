// 🔹 Verifica el stock y desactiva el botón si hay productos sin stock
function checkStockAvailability() {
    const checkoutButton = document.getElementById("checkoutButton");
    const stockWarning = document.getElementById("stockWarning");

    if (!checkoutButton || !stockWarning) return; // Evitar errores si los elementos no existen

    // Buscar si hay algún elemento <p> con el texto "No stock available"
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

    let formData = new FormData();

    document.querySelectorAll(".quantity-input").forEach(input => {
        let id = input.getAttribute("data-id");
        let quantity = parseInt(input.value, 10);

        if (quantity < 1) {
            quantity = 1;
            input.value = 1; 
        }

        formData.append("ids", id);
        formData.append("quantities", quantity);
    });

    try {
        let response = await fetch(`/checkout/recalculate`, {
            method: "POST",
            body: formData
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

        // 🔹 Verificar stock después de recalcular
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
        console.log("✅ Respuesta del servidor:", result);

        document.getElementById("CartItemsList").innerHTML = result;

        if (typeof window.initProductQty === "function") {
            window.initProductQty();
        } else {
            console.error("initProductQty no está disponible. Verifica si script.js fue cargado.");
        }

        // 🔹 Verificar stock después de eliminar un producto
        checkStockAvailability();

    } catch (error) {
        console.error("Error al eliminar el item:", error);
    }
}
