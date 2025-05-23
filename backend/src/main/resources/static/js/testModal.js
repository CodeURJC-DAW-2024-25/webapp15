let currentPage = 2;
let selectedBrand = null;
let selectedCategory = null;

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("modaltoggle").addEventListener("hidden.bs.modal", function () {
        document.activeElement.blur();
    });
});

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

async function showOrderDetail(order_id) {
    const detailsDiv = document.getElementById(order_id);
    const button = document.querySelector(`button[onclick="showOrderDetail('${order_id}')"]`);
    if (detailsDiv.innerHTML.trim() === "") {
        try {
            const response = await fetch(`/user/orderItems?id_order=${order_id}`);
            if (!response.ok) {
                throw new Error("Can't load items from order");
            }
            const items = await response.text();
            detailsDiv.innerHTML = items;
            button.textContent = "Close details";
        } catch (error) {
            console.error("Error at the request:", error);
        }
    } else {
        detailsDiv.innerHTML = "";
        button.textContent = "Show details";
    }
}


async function AddtoCart(id_Shoe, size, quantity) {

    if (!size) {
        alert("⚠️ Please select a valid size");
        return;
    }

    if (!quantity || isNaN(quantity) || quantity <= 0) {
        alert("⚠️ Input a valid numeric value");
        return;
    }
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        const formData = new URLSearchParams();
        formData.append("id_Shoe", id_Shoe);
        formData.append("size", size);
        formData.append("cuantity", quantity);

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

        if (!response.ok) {
            alert("⚠️ There was an issue adding the product, we are going to try to update your cart");
        }

        let modal = document.getElementById("modaltoggle");
        if (modal) {
            let bootstrapModal = bootstrap.Modal.getInstance(modal);
            if (bootstrapModal) {
                openCartModal();
            }
        }
        openCartModal();
    } catch (error) {
        console.error("Error in request:", error);
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
            throw new Error("error in request" + response.status);
        }

        const shoebybrand = await response.text();

        document.getElementById("shoes").innerHTML = shoebybrand;
        document.getElementById("loadMoreButtom").style.display = "block";

    } catch (error) {
        console.error("error trying to load from brands")
    }
}

//first 9 of category(check)
async function searchByCategory(event, category) {
    event.preventDefault();
    selectedCategory = category;
    selectedBrand = null;
    currentPage = 2;

    try {
        const response = await fetch(`/shop/getByCategory?category=${category}`);
        if (!response.ok) {
            throw new Error("error in request" + response.status);
        }
        const shoebycatagory = await response.text();
        document.getElementById("shoes").innerHTML = shoebycatagory;
        document.getElementById("loadMoreButtom").style.display = 'block';

    } catch (error) {
        console.error("error trying to load from categories")
    }
}

async function openCartModal() {
    try {
        const response = await fetch(`/user/cart`); // Route to obtain the products from the cart

        if (!response.ok) {
            throw new Error("Error ocurred adding to cart: " + response.status);
        }

        const cartContent = await response.text();
        document.getElementById("modal-body-content").innerHTML = cartContent;

        // Get the modal and check if an instance already exists
        let modalElement = document.getElementById("modaltoggle");
        let myModal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);

        // Ensure that there are no aria-hidden remains blocking the closure
        modalElement.removeAttribute("aria-hidden");

        myModal.show();

        // Event to reset focus on close and prevent background from being blocked
        modalElement.addEventListener("hidden.bs.modal", () => {
            document.activeElement.blur(); // Remove focus from the close button
        });

    } catch (error) {
        console.error("Error in request:", error);
    }
}

function DownloadTicket(orderId) {
    fetch(`/checkout/downloadTicket?orderId=${orderId}`)// Adjusting the URL
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
        currentPage++ // We increase the page

        let url = `/shop/loadMoreShoes/?currentPage=${currentPage}`; // URL by default

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
        shoesDiv.innerHTML += resp; // Adding more products

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
        alert("Error trying to reset filters");
    }
}

// Function to obtain the selected size
function getSelectedSize() {
    const selectedSizeElement = document.querySelector(".select-size-item.selected");
    return selectedSizeElement ? selectedSizeElement.getAttribute("data-value") : null;
}

// Size selection function by clicking
document.querySelectorAll(".select-size-item").forEach(item => {
    item.addEventListener("click", function (event) {
        event.preventDefault();
        document.querySelectorAll(".select-size-item").forEach(i => i.classList.remove("selected"));
        this.classList.add("selected");
    });
});

// Function to get the selected quantity
function getQuantity() {
    return document.querySelector(".quantity").value;
}



window.openCartModal = openCartModal;

window.openModal = openModal;


window.openModal = openModal;
