document.addEventListener('DOMContentLoaded', function() {
    // Sample data - Replace this with actual user data from your backend
    const monthlyData = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
      datasets: [{
        label: 'Money Spent ($)',
        data: [150, 220, 180, 290, 200, 340],
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 2,
        tension: 0.4
      }]
    };
  
    const ctx = document.getElementById('spendingChart').getContext('2d');
    new Chart(ctx, {
      type: 'line',
      data: monthlyData,
      options: {
        responsive: true,
        maintainAspectRatio: false, // Allow the chart to fill the container
        plugins: {
          title: {
            display: true,
            text: 'Your Shopping History'
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Amount ($)'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Month'
            }
          }
        }
      }
    });
  });

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
