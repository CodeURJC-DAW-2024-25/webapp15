
// main function to open the file selector
function openFileSelector() {
  document.getElementById('photoInput').click();
}

//function to handle the photo change
async function handleChangePhoto() {
  try {
    openFileSelector();
    const photoInput = document.getElementById('photoInput');
    photoInput.onchange = async (e) => {
      const file = e.target.files[0];
      if (!file) return;

      try {
        const formData = new FormData();
        formData.append('imageUser', file);

        const profileImage = document.querySelector('.image-wrapper img');
        const reader = new FileReader();
        reader.onload = (e) => {
          profileImage.src = e.target.result;
        };
        reader.readAsDataURL(file);

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        const response = await fetch('/user/upload-profile-image', {
          method: 'POST',
          headers: {
            [csrfHeader]: csrfToken
          },
          body: formData
        });

        if (!response.ok) {
          throw new Error('Error uploading image');
        }

        const data = await response.text();

        //update the image with the URL returned by the server
        profileImage.innerHTML = data;

        alert('Profile picture updated successfully!');

      } catch (error) {
        alert('Error updating profile picture. Please try again.');
      }
    };

  } catch (error) {
    alert('Error opening file selector');
  }
}

function validateImage(file) {

  if (!file.type.startsWith('image/')) {
    alert('Please select an image file');
    return false;
  }

  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    alert('File size must be less than 5MB');
    return false;
  }

  return true;
}


async function updateUserInformation() {
  try {
    const form = document.getElementById("user-profile-form");

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    let formData = new FormData(form);

    const response = await fetch('/user/updateInformation', {
      method: "POST",
      body: formData,
      headers: {
        [csrfHeader]: csrfToken,
      }
    })

    if (!response.ok) {
      throw new Error("error updating information");
    }
    let info = await response.text();
    form.innerHTML = info;
    alert("your information has been updated")
  } catch (error) {
    alert("information could not be updated")
  }
}

let PageOrders = 0;
async function loadMoreOrders() {
  try {
    PageOrders++;
    const response = await fetch(`/profile/orders?page=${PageOrders}`);
    const data = await response.text();
    let ordersDiv = document.getElementById("ordersDiv");
    ordersDiv.innerHTML += data;
    if (String(data).includes("No more orders available.")) {
      let loadMoreButton = document.getElementById("loadMoreBtn");
      if (loadMoreButton) {
        loadMoreButton.style.display = "none";
      }
    }
  } catch (error) {
    alert("Error updating orders");
  }
}
window.loadMoreOrders = loadMoreOrders;
