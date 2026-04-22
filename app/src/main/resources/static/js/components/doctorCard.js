// doctorCard.js

// 🔗 Imports (adjust paths if needed)
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "./modals.js";

// =====================
// Create Doctor Card
// =====================
export function createDoctorCard(doctor) {
  // Main card
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get role
  const role = localStorage.getItem("userRole");

  // =====================
  // Doctor Info Section
  // =====================
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${
    Array.isArray(doctor.availability)
      ? doctor.availability.join(", ")
      : doctor.availability
  }`;

  // Append info
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // =====================
  // Actions Section
  // =====================
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // ===== ADMIN =====
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";

    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm("Are you sure you want to delete this doctor?");
      if (!confirmDelete) return;

      try {
        const token = localStorage.getItem("token");

        if (!token) {
          alert("Session expired. Please login again.");
          return;
        }

        // API call
        const res = await deleteDoctor(doctor.id, token);

        if (res?.success) {
          alert("Doctor deleted successfully.");
          card.remove(); // remove from UI
        } else {
          alert("Failed to delete doctor.");
        }
      } catch (err) {
        console.error(err);
        alert("Error deleting doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // ===== PATIENT (NOT LOGGED IN) =====
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", () => {
      alert("Please login to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // ===== LOGGED PATIENT =====
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", async (e) => {
      try {
        const token = localStorage.getItem("token");

        if (!token) {
          alert("Session expired. Please login again.");
          window.location.href = "/";
          return;
        }

        // Fetch patient info
        const patientData = await getPatientData(token);

        // Open booking UI
        showBookingOverlay(e, doctor, patientData);

      } catch (err) {
        console.error(err);
        alert("Error fetching patient data.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // =====================
  // Final Assembly
  // =====================
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}