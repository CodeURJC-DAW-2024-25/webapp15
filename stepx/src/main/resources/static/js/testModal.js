
async function openModal(productId, action) {
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

async function openCartModal1() {
    try {
        const response = await fetch("/cart"); // Ruta para obtener los productos del carrito

        if (!response.ok) {
            throw new Error("Error al cargar el carrito: " + response.status);
        }

        const cartContent = await response.text();
        document.getElementById("cart-modal-body").innerHTML = cartContent;

        // Obtener el modal y verificar si ya existe una instancia
        let modalElement = document.getElementById("modallong");
        let myModal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);

        // Asegurar que no haya restos de aria-hidden que bloqueen el cierre
        modalElement.removeAttribute("aria-hidden");

        myModal.show();

        // Evento para restablecer el foco al cerrar y evitar que el fondo quede bloqueado
        modalElement.addEventListener("hidden.bs.modal", () => {
            document.activeElement.blur(); // Quitar el foco del botón de cierre
        });

    } catch (error) {
        console.error("Error en la solicitud:", error);
    }
}


window.openCartModal = openCartModal;

window.openModal = openModal;


async function AddtoCart(id) {
    try {
        const response = await fetch(`/single-product/${id}/add`); // Ruta para añadir los prodcutos

        if (!response.ok) {
            throw new Error("Error al cargar el carrito: " + response.status);
        }

    } catch (error) {
        console.error("Error en la solicitud:", error);
    }
}

