// Import required modules
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// ----------------------
// GLOBAL VARIABLES
// ----------------------
const tableBody = document.getElementById("patientTableBody");

// Today's date (YYYY-MM-DD)
let selectedDate = new Date().toISOString().split("T")[0];

// Token for auth
const token = localStorage.getItem("token");

// Search filter
let patientName = null;

// ----------------------
// SEARCH BAR
// ----------------------
const searchBar = document.getElementById("searchBar");

if (searchBar) {
  searchBar.addEventListener("input", () => {
    const value = searchBar.value.trim();
    patientName = value !== "" ? value : "null";
    loadAppointments();
  });
}

// ----------------------
// TODAY BUTTON
// ----------------------
const todayBtn = document.getElementById("todayButton");

if (todayBtn) {
  todayBtn.addEventListener("click", () => {
    selectedDate = new Date().toISOString().split("T")[0];

    const datePicker = document.getElementById("datePicker");
    if (datePicker) datePicker.value = selectedDate;

    loadAppointments();
  });
}

// ----------------------
// DATE PICKER
// ----------------------
const datePicker = document.getElementById("datePicker");

if (datePicker) {
  datePicker.value = selectedDate;

  datePicker.addEventListener("change", () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

// ----------------------
// LOAD APPOINTMENTS
// ----------------------
async function loadAppointments() {
  try {
    if (!tableBody) return;

    // Fetch data
    const appointments = await getAllAppointments(
      selectedDate,
      patientName,
      token
    );

    // Clear table
    tableBody.innerHTML = "";

    // No data case
    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" style="text-align:center;">
            No Appointments found for today.
          </td>
        </tr>
      `;
      return;
    }

    // Render rows
    appointments.forEach((appt) => {
      const patient = {
        id: appt.patient?.id,
        name: appt.patient?.name,
        phone: appt.patient?.phone,
        email: appt.patient?.email
      };

      const row = createPatientRow(patient, appt);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("Error loading appointments:", error);

    tableBody.innerHTML = `
      <tr>
        <td colspan="5" style="text-align:center;">
          Error loading appointments. Try again later.
        </td>
      </tr>
    `;
  }
}

// ----------------------
// INITIAL LOAD
// ----------------------
window.addEventListener("DOMContentLoaded", () => {
  // If you have layout rendering
  if (window.renderContent) {
    window.renderContent();
  }

  loadAppointments();
});