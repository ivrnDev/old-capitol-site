// BARANGAY CERTIFICATE

const barangayCertificateModal = document.getElementById(
    "barangayCertificateModal"
);
const closeBarangayCertificateModal = document.getElementById(
    "closeBarangayCertificateModal"
);
const barangayCertificate = document.getElementById(
    "barangayCertificate"
);

// open
barangayCertificate.addEventListener("click", function () {
    barangayCertificateModal.style.display = "flex";
});

//close
closeBarangayCertificateModal.addEventListener("click", function () {
    barangayCertificateModal.style.display = "none";
});

//outside click
window.addEventListener("click", function (event) {
    if (event.target === barangayCertificateModal) {
        barangayCertificateModal.style.display = "none";
    }

    //close modal function
    function closeBarangayCertificateModalFunc() {
        barangayCertificateModal.style.display = "none";
    }
});


//close modal function
function closeBarangayCertificateModalFunc() {
    barangayCertificateModal.style.display = "none";
}


// HEALTH MODAL

const healthServicesModal = document.getElementById(
    "healthServicesModal"
);
const closeModal = document.getElementById("closeModal");
const healthAppointment = document.getElementById("healthAppointment");

// open
healthAppointment.addEventListener("click", function () {
    healthServicesModal.style.display = "flex";
});

//close
closeModal.addEventListener("click", function () {
    healthServicesModal.style.display = "none";
});

//outside click
window.addEventListener("click", function (event) {
    if (event.target === healthServicesModal) {
        healthServicesModal.style.display = "none";
    }

    //close modal function
    function closeModalFunc() {
        healthServicesModal.style.display = "none";
    }
});

//close modal function
function closeModalFunc() {
    healthServicesModal.style.display = "none";
}

//ADD RESIDENT MODAL

const addResidentModal = document.getElementById("addResidentModal");
const closeResidentModal = document.getElementById("closeResidentModal");
const addResidentButton = document.getElementById("addResident");

// open
addResidentButton.addEventListener("click", function () {
    addResidentModal.style.display = "flex";
});

// close
closeResidentModal.addEventListener("click", function () {
    addResidentModal.style.display = "none";
});

// outside click
window.addEventListener("click", function (event) {
    if (event.target === addResidentModal) {
        addResidentModal.style.display = "none";
    }
});

// close modal func
function closeResidentModalFunc() {
    addResidentModal.style.display = "none";
}

// VALIDATION (BUTTON)
const form = addResidentModal.querySelector("form");
const confirmButton = addResidentModal.querySelector(".confirm");
const cancelButton = addResidentModal.querySelector(".cancel");

//Form Submission with Validation
confirmButton.addEventListener("click", function (event) {
    event.preventDefault();

    let isValid = true;
    const requiredFields = form.querySelectorAll("[required]");

    // Loop through all required fields and check if they're filled out
    requiredFields.forEach((field) => {
        if (!field.value.trim()) {
            field.classList.add("error");
            isValid = false;
        } else {
            field.classList.remove("error");
        }
    });

    if (isValid) {
        alert("Form submitted successfully!");
        closeResidentModalFunc();
    } else {
        alert("Please fill out all required fields.");
    }
});

//Close modal when clicking the cancel button
cancelButton.addEventListener("click", function () {
    closeResidentModalFunc();
});