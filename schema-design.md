## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- age: INT
- phone: VARCHAR(15), Unique
- email: VARCHAR(100), Unique
- created_at: DATETIME, Default CURRENT_TIMESTAMP

---

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- specialization: VARCHAR(100)
- phone: VARCHAR(15), Unique
- email: VARCHAR(100), Unique

---

### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id)
- doctor_id: INT, Foreign Key → doctors(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

---

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- password: VARCHAR(255), Not Null

---

### Table: payments (optional)
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id)
- amount: DECIMAL(10,2), Not Null
- payment_date: DATETIME


## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 1,
  "doctorId": 2,
  "appointmentId": 10,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours"
    }
  ],
  "doctorNotes": "Patient should rest and stay hydrated.",
  "refillCount": 2,
  "createdAt": "2026-04-18T10:00:00Z"
}

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 1,
  "doctorId": 2,
  "appointmentId": 10,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours"
    }
  ],
  "doctorNotes": "Patient should rest and stay hydrated.",
  "refillCount": 2,
  "createdAt": "2026-04-18T10:00:00Z"
}

{
  "_id": "ObjectId('64abc999999')",
  "patientId": 1,
  "doctorId": 2,
  "rating": 4,
  "comment": "Doctor was very helpful and professional.",
  "tags": ["friendly", "quick service"],
  "createdAt": "2026-04-18T12:00:00Z"
}

