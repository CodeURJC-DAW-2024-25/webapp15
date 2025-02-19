async function openModal(productId) {
    console.log("productId", productId); // Verificar en consola

    try {
        const response = await fetch(`/shop/${productId}`);

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