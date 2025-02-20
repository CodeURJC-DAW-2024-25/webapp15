
async function openModal(productId, action) {
    console.log("productId:", productId, "Action:", action); // Verificar en consola

    try {
        const response = await fetch(`/shop/${productId}?action=${action}`);

        if (!response.ok) {
            throw new Error("Error en la solicitud: " + response.status);
        }

        const modalContent = await response.text();
        document.getElementById("modal-body-content").innerHTML = modalContent;

    } catch (error) {
        console.error("Error at the request:", error);
    }
}
    
window.openModal = openModal;

async function openCartModal() {
    try {
        const response = await fetch("/cart"); // Ruta para obtener los productos del carrito

        if (!response.ok) {
            throw new Error("Error al cargar el carrito: " + response.status);
        }

        const cartContent = await response.text();
        document.getElementById("cart-modal-body").innerHTML = cartContent;

        // Forzar la apertura del modal en caso de problemas con Bootstrap
        var myModal = new bootstrap.Modal(document.getElementById('modallong'));
        myModal.show();

    } catch (error) {
        console.error("Error en la solicitud:", error);
    }
}

window.openCartModal = openCartModal;

window.openModal = openModal;
