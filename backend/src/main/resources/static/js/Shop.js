document.addEventListener("DOMContentLoaded", function () {
    var deleteButtons = document.querySelectorAll('.delete-product-btn');
    deleteButtons.forEach(function (button) {
        button.addEventListener('click', function (event) {
            var productName = this.getAttribute('data-product-name');
            var productImage = this.getAttribute('data-product-image');

            document.getElementById('productNameModal').textContent = productName;
            document.getElementById('productImageModal').src = productImage;

            // Show the modal
            var modal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'));
            modal.show();
        });
    });

    document.getElementById('confirmCheck').addEventListener('change', function () {
        document.getElementById('confirmDeleteBtn').disabled = !this.checked;
    });

    document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
        alert('El producto ha sido eliminado correctamente.');
        // logic to delete the product
        var modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmationModal'));
        modal.hide();
    });
});