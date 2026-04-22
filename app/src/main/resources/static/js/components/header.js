// header.js

export function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;
  
    // ✅ If homepage → clear session & show simple header
    if (window.location.pathname.endsWith("/")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
  
      headerDiv.innerHTML = `
        <header class="header">
          <div class="logo-section">
            <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
            <span class="logo-title">Hospital CMS</span>
          </div>
        </header>
      `;
      return;
    }
  
    // ✅ Get role & token
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
  
    // ✅ Handle invalid session
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
    }
  
    // ✅ Base header
    let headerContent = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
        <nav>
    `;
  
    // =====================
    // Role-based UI
    // =====================
  
    if (role === "admin") {
      headerContent += `
        <button id="addDocBtn" class="adminBtn">Add Doctor</button>
        <a href="#" id="logoutBtn">Logout</a>
      `;
    }
  
    else if (role === "doctor") {
      headerContent += `
        <button id="doctorHome" class="adminBtn">Home</button>
        <a href="#" id="logoutBtn">Logout</a>
      `;
    }
  
    else if (role === "patient") {
      headerContent += `
        <button id="patientLogin" class="adminBtn">Login</button>
        <button id="patientSignup" class="adminBtn">Sign Up</button>
      `;
    }
  
    else if (role === "loggedPatient") {
      headerContent += `
        <button id="homeBtn" class="adminBtn">Home</button>
        <button id="appointmentsBtn" class="adminBtn">Appointments</button>
        <a href="#" id="logoutPatientBtn">Logout</a>
      `;
    }
  
    headerContent += `
        </nav>
      </header>
    `;
  
    // ✅ Inject into DOM
    headerDiv.innerHTML = headerContent;
  
    // ✅ Attach listeners AFTER render
    attachHeaderButtonListeners();
  }
  
  // =====================
  // Event Listeners
  // =====================
  function attachHeaderButtonListeners() {
  
    // Admin → Add Doctor Modal
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
      addDocBtn.addEventListener("click", () => {
        if (typeof openModal === "function") {
          openModal("addDoctor");
        }
      });
    }
  
    // Doctor → Home
    const doctorHome = document.getElementById("doctorHome");
    if (doctorHome) {
      doctorHome.addEventListener("click", () => {
        window.location.href = "/doctorDashboard.html";
      });
    }
  
    // Patient → Login
    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
      patientLogin.addEventListener("click", () => {
        if (typeof openModal === "function") {
          openModal("patientLogin");
        }
      });
    }
  
    // Patient → Signup
    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
      patientSignup.addEventListener("click", () => {
        if (typeof openModal === "function") {
          openModal("patientSignup");
        }
      });
    }
  
    // Logged Patient → Home
    const homeBtn = document.getElementById("homeBtn");
    if (homeBtn) {
      homeBtn.addEventListener("click", () => {
        window.location.href = "/pages/patientDashboard.html";
      });
    }
  
    // Logged Patient → Appointments
    const appointmentsBtn = document.getElementById("appointmentsBtn");
    if (appointmentsBtn) {
      appointmentsBtn.addEventListener("click", () => {
        window.location.href = "/pages/patientAppointments.html";
      });
    }
  
    // Logout (Admin/Doctor)
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", logout);
    }
  
    // Logout Patient
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
      logoutPatientBtn.addEventListener("click", logoutPatient);
    }
  }
  
  // =====================
  // Logout Functions
  // =====================
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
  }
  
  function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient"); // fallback to guest patient
    window.location.href = "/pages/patientDashboard.html";
  }