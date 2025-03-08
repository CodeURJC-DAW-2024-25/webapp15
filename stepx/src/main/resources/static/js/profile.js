
// Función para abrir el selector de archivos
function openFileSelector() {
  document.getElementById('photoInput').click();
}

// Función principal para manejar el cambio de foto
async function handleChangePhoto() {
  try {
      // Primero abrimos el selector de archivos
      openFileSelector();

      // Configuramos el evento change del input file
      const photoInput = document.getElementById('photoInput');
      photoInput.onchange = async (e) => {
          const file = e.target.files[0];
          if (!file) return;

          try {
              // Crear el FormData
              const formData = new FormData();
              formData.append('imageUser', file);

              // Mostrar preview de la imagen
              const profileImage = document.querySelector('.image-wrapper img');
              const reader = new FileReader();
              reader.onload = (e) => {
                  profileImage.src = e.target.result;
              };
              reader.readAsDataURL(file);

              // Obtener el token CSRF
              const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
              const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

              // Enviar la imagen al servidor
              const response = await fetch('/user/upload-profile-image', {
                  method: 'POST',
                  headers: {
                    [csrfHeader]: csrfToken // 🔹 Incluir el token CSRF en la solicitud
                },
                  body: formData
              });

              if (!response.ok) {
                  throw new Error('Error uploading image');
              }

              const data = await response.text();
              
              // Actualizar la imagen con la URL devuelta por el servidor
              profileImage.innerHTML=data;

              alert('Profile picture updated successfully!');

          } catch (error) {
              console.error('Error:', error);
              alert('Error updating profile picture. Please try again.');
          }
      };

  } catch (error) {
      console.error('Error:', error);
      alert('Error opening file selector');
  }
}

// Opcional: También puedes agregar validaciones para el tipo de archivo
function validateImage(file) {
  // Verificar el tipo de archivo
  if (!file.type.startsWith('image/')) {
      alert('Please select an image file');
      return false;
  }

  // Verificar el tamaño del archivo (por ejemplo, máximo 5MB)
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
      alert('File size must be less than 5MB');
      return false;
  }

  return true;
}


async function updateUserInformation() {
  try{
    const form = document.getElementById("user-profile-form");
    
    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
    
    let formData = new FormData(form);
    
    const response = await fetch('/user/updateInformation',{
      method:"POST",
      body:formData,
      headers:{
        [csrfHeader]:csrfToken,
      }
    })

    if (!response.ok) {
      throw new Error("Error al actualizar la información del usuario");
    }
    let info= await response.text();
    form.innerHTML=info;
    alert("your information has been updated")
  }catch(error){
    console.error('error: ', error)
    alert("no se pudo actualizar la informacion del usuario")
  }
}

let PageOrders=0;
async function loadMoreOrders() {
  try{
    PageOrders++;
    const response = await fetch(`/profile/orders?page=${PageOrders}`);
    const data= await response.text();
    let ordersDiv=document.getElementById("ordersDiv");
    ordersDiv.innerHTML+=data;
    if (String(data).includes("No more orders available.")) {
      let loadMoreButton = document.getElementById("loadMoreBtn");
      if (loadMoreButton) {
        loadMoreButton.style.display = "none";
      }
    }
  }catch(error){
  console.log("error triying to load orders: ",error)
}  
}
window.loadMoreOrders = loadMoreOrders;
console.log("profile.js ha sido cargado correctamente.");