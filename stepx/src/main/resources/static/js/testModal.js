let currentPage = 2;
let selectedBrand = null;
let selectedCategory = null;


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


async function AddtoCart(id_Shoe, size, quantity) {

    console.log(size)
    console.log(quantity)
    if (!size) {
        alert("⚠️ Por favor selecciona un tamaño.");
        return;
    }

    if (!quantity || isNaN(quantity) || quantity <= 0) {
        alert("⚠️ Ingresa una cantidad válida.");
        return;
    }
    try {

        console.log("Añadiendo producto al carrito:");
        console.log("ID:", id_Shoe);
        console.log("Size:", size);
        console.log("Quantity:", quantity);

        // Obtener el token CSRF del head de la página
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        console.log("CSRF Token:", csrfToken);
        console.log("CSRF Header:", csrfHeader);

        const formData = new URLSearchParams();
        formData.append("id_Shoe", id_Shoe);
        formData.append("size", size);
        formData.append("cuantity", quantity); // 🔥 Aquí revisa que sea 'quantity' y no 'cuantity'

        console.log("Datos enviados:", formData.toString());

        const response = await fetch("/OrderItem/addItem", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrfHeader]: csrfToken
            },
            body: formData.toString(),
            credentials: "include"
        });

        const responseText = await response.text();
        console.log("Respuesta del servidor:", responseText);

        if (!response.ok) {
            alert("⚠️ Hubo un problema al agregar el producto, pero se intentará actualizar el carrito.");
        }
        console.log("Producto añadido con éxito");

        let modal = document.getElementById("modaltoggle");
        if (modal) {
            let bootstrapModal = bootstrap.Modal.getInstance(modal);
            if (bootstrapModal) {
                openCartModal();
            }
        }
        openCartModal();
    } catch (error) {
        console.error("Error en la solicitud:", error);
    }
}




//first 9 of brand
async function searchByBrand(event, brand) {
    event.preventDefault();
    selectedBrand = brand;
    selectedCategory = null;
    currentPage = 2;

    try {
        const response = await fetch(`/shop/getByBrand?brand=${brand}`);
        if (!response.ok) {
            throw new Error("error en la solicitud" + response.status);
        }

        const shoebybrand = await response.text();

        document.getElementById("shoes").innerHTML = shoebybrand;
        document.getElementById("loadMoreButtom").style.display = "block";

    } catch (error) {
        console.log("error al buscar por marcas");
    }

}

//first 9 of category
async function searchByCategory(event, category) {
    event.preventDefault();
    selectedCategory = category;
    selectedBrand = null;
    currentPage = 2;

    try {
        const response = await fetch(`/shop/getByCategory?category=${category}`);
        if (!response.ok) {
            throw new Error("error en la solicitud" + response.status);
        }
        const shoebycatagory = await response.text();
        document.getElementById("shoes").innerHTML = shoebycatagory;
        document.getElementById("loadMoreButtom").style.display = 'block';

    } catch (error) {
        console.log("error trying to load from categories")
    }
}

async function openCartModal() {
    try {
        const response = await fetch(`/user/cart`); // Ruta para obtener los productos del carrito

        if (!response.ok) {
            throw new Error("Error al cargar el carrito: " + response.status);
        }

        const cartContent = await response.text();
        document.getElementById("modal-body-content").innerHTML = cartContent;

        // Obtener el modal y verificar si ya existe una instancia
        let modalElement = document.getElementById("modaltoggle");
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

function DownloadTicket(orderId) {
    fetch(`/checkout/downloadTicket?orderId=${orderId}`)// Ajustamos la URL
        .then(response => {
            if (!response.ok) {
                throw new Error("Error generating PDF.");
            }
            return response.blob();
        })
        .then(blob => {
            const link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = "ticket.pdf";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error generating PDF.");
        });
}


async function loadMore() {
    try {
        currentPage++ // Aumentamos la página

        let url = `/shop/loadMoreShoes/?currentPage=${currentPage}`; // URL por defecto

        if (selectedBrand !== null) {
            url = `/shop/loadMoreShoesByBrand?currentPage=${currentPage}&brand=${selectedBrand}`;
        } else if (selectedCategory !== null) {
            url = `/shop/loadMoreShoesByCategory?currentPage=${currentPage}&category=${selectedCategory}`;
        }

        const response = await fetch(url);
        const resp = await response.text();

        if (resp.includes("<!--hasMoreShoes-->")) {
            document.getElementById("loadMoreButtom").style.display = 'none';
        }

        let shoesDiv = document.getElementById("shoes");
        shoesDiv.innerHTML += resp; // Agregar más productos

    } catch (error) {
        console.error("Error at trying to load 3 more shoes: ", error)
    }
}

async function resetFilters(event) {
    event.preventDefault();
    selectedBrand = null;
    selectedCategory = null;
    currentPage = 2;

    try {
        const response = await fetch(`/shop/resetFilters`);
        const allShoes = await response.text();

        document.getElementById("shoes").innerHTML = allShoes;
        document.getElementById("loadMoreButtom").style.display = 'block';

    } catch (error) {
        console.log("Error al restablecer los filtros", error);
    }
}

// Función para obtener la talla seleccionada
function getSelectedSize() {
    const selectedSizeElement = document.querySelector(".select-size-item.selected");
    return selectedSizeElement ? selectedSizeElement.getAttribute("data-value") : null;
}

// Función para seleccionar talla al hacer clic
document.querySelectorAll(".select-size-item").forEach(item => {
    item.addEventListener("click", function (event) {
        event.preventDefault();
        document.querySelectorAll(".select-size-item").forEach(i => i.classList.remove("selected"));
        this.classList.add("selected");
    });
});

// Función para obtener la cantidad seleccionada
function getQuantity() {
    return document.querySelector(".quantity").value;
}



window.openCartModal = openCartModal;

window.openModal = openModal;


window.openModal = openModal;
