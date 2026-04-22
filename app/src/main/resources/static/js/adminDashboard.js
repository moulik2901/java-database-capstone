// Import required modules
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// ----------------------
// EVENT: ADD DOCTOR BUTTON
// ----------------------
const addDocBtn = document.getElementById("addDocBtn");

if (addDocBtn) {
  addDocBtn.addEventListener("click", () => {
    openModal("addDoctor");
  });
}

// ----------------------
// LOAD ON PAGE READY
// ----------------------
window.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Attach filter listeners
  document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
});

// ----------------------
// LOAD ALL DOCTORS
// ----------------------
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}

// ----------------------
// RENDER DOCTORS
// ----------------------
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors available</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ----------------------
// FILTER HANDLER
// ----------------------
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    const doctors = await filterDoctors(name, time, specialty);

    if (doctors.length === 0) {
      document.getElementById("content").innerHTML =
        "<p>No doctors found with the given filters.</p>";
    } else {
      renderDoctorCards(doctors);
    }

  } catch (error) {
    console.error("Filter error:", error);
    alert("Error filtering doctors");
  }
}

// ----------------------
// ADD DOCTOR HANDLER
// ----------------------
window.adminAddDoctor = async function () {
  try {
    // Collect form values
    const name = document.getElementById("docName").value;
    const email = document.getElementById("docEmail").value;
    const password = document.getElementById("docPassword").value;
    const phone = document.getElementById("docPhone").value;
    const specialty = document.getElementById("docSpecialty").value;

    // Collect availability (checkboxes)
    const timeCheckboxes = document.querySelectorAll("input[name='availability']:checked");
    const availability = Array.from(timeCheckboxes).map(cb => cb.value);

    // Get token
    const token = localStorage.getItem("token");

    if (!token) {
      alert("Unauthorized! Please login again.");
      return;
    }

    const doctor = {
      name,
      email,
      password,
      phone,
      specialty,
      availability
    };

    const result = await saveDoctor(doctor, token);

    if (result.success) {
      alert(result.message || "Doctor added successfully");

      // Close modal (simple way)
      document.querySelector(".modal")?.classList.remove("show");

      // Reload doctors list
      loadDoctorCards();

    } else {
      alert(result.message || "Failed to add doctor");
    }

  } catch (error) {
    console.error("Add doctor error:", error);
    alert("Something went wrong");
  }
};