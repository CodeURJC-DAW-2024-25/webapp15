let currentPage=2;
let selectedBrand=null;
let selectedCategory=null;


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




//first 9 of brand
async function searchByBrand(event,brand){
    event.preventDefault();
    selectedBrand=brand;
    selectedCategory=null;
    currentPage=2;

    try{
        const response=await fetch(`/shop/getByBrand?brand=${brand}`);
        if(!response.ok){
            throw new Error ("error en la solicitud"+ response.status);
        }

        const shoebybrand=await response.text();

        document.getElementById("shoes").innerHTML=shoebybrand;
        document.getElementById("loadMoreButtom").style.display="block";

    }catch(error){
        console.log("error al buscar por marcas");
    }

}

//first 9 of category
async function searchByCategory(event,category){
    event.preventDefault();
    selectedCategory=category;
    selectedBrand=null;
    currentPage=2;

    try{
        const response=await fetch(`/shop/getByCategory?category=${category}`);
        if(!response.ok){
            throw new Error ("error en la solicitud"+ response.status);
        }
        const shoebycatagory= await response.text();
        document.getElementById("shoes").innerHTML=shoebycatagory;
        document.getElementById("loadMoreButtom").style.display = 'block';

    }catch(error){
        console.log("error trying to load from categories")
    }
}

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

async function loadMore() {
    try{
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

window.openCartModal = openCartModal;

window.openModal = openModal;

    
window.openModal = openModal;