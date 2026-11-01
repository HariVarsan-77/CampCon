# Campus Connect – College Grievance & Complaint Management System

**Campus Connect** is a modern, responsive web application for university grievance management. It empowers students to privately report issues concerning campus facilities, academics, hostels, dining, and infrastructure, while providing college management with a centralized dashboard to review complaints, dispatch official replies, reject irrelevant reports with stated reasons, maintain confidential internal staff notes, and advance corrective resolution workflows.

---

## 1. Technology Stack

- **Backend:** Java 21+ with **Spring Boot 3.4.1**
- **Security:** Spring Security 6 with Role-Based Access Control (`ROLE_STUDENT`, `ROLE_MANAGEMENT`)
- **Build Tool:** Apache Maven
- **Template Engine:** Thymeleaf with Spring Security 6 dialect
- **UI & Styling:** Custom Responsive CSS (Dark Navy Blue `#071426` & Gold `#D4AF37` palette)
- **Icons:** Lucide Icons (clean SVG icons)
- **Image Handling:** Pluggable `FileStorageService` abstraction (supports local filesystem upload with JPG, JPEG, PNG, WEBP validation and ready for Firebase Storage or Supabase Storage)
- **Data Persistence:** In-memory thread-safe repositories (`ConcurrentHashMap`) with clean interfaces (`UserRepository`, `ComplaintRepository`, `NotificationRepository`) prepared for seamless JPA / Firebase / Supabase transition.

---

## 2. Default Test Accounts (Pre-seeded)

For immediate testing, sample accounts are automatically initialized:

| Role | Name | Email | Password | Department / Roll No |
|---|---|---|---|---|
| **Student** | Alex Rivera | `student@campus.edu` | `password123` | Computer Science (`CS2023-042`) |
| **Student** | Priya Patel | `priya.patel@campus.edu` | `password123` | Electronics & Comm (`EC2022-108`) |
| **Management** | Dr. Evelyn Reed | `admin@campus.edu` | `password123` | Campus Administration (`DIR-201`) |
| **Management** | Marcus Sterling | `facilities@campus.edu` | `password123` | Facilities & Estate (`ENG-104`) |

*Note: You can also sign up new Student or Management accounts directly via `/signup`.*

---

## 3. How to Run Locally

### Prerequisites
- Java JDK 21 or higher installed (`java -version`)
- Maven 3.8+ installed (`mvn -version`)

### Quick Start
```bash
# Clone or navigate to the project directory
cd "c:\PROJECT FILES\CampCon"

# Compile and run with Spring Boot
mvn spring-boot:run
```

Or run via packaged JAR:
```bash
mvn package -DskipTests
java -jar target/campus-connect-1.0.0-SNAPSHOT.jar
```

Once started, open your web browser and navigate to:
**`http://localhost:8080`**

---

## 4. Key Workflows & Features

### A. Authentication & Role Enforcement
- Dedicated **Student** and **Management** login tabs with password visibility toggles and quick-fill test credentials.
- Full registration page allowing role selection (`Student` or `Management`), department, and identification numbers.
- Strict Spring Security authorization: Students cannot access `/management/**` and Management cannot access `/student/**` by URL tampering (verified with an Access Denied view).

### B. Common Home Page (`/home`)
- University hero banner: *"Your Voice. Our Campus. A Better Tomorrow."*
- Explanations of the three-step pipeline: **Report &rarr; Review &rarr; Resolve**.
- Role comparison breakdown cards for Students and Administrators.

### C. Student Portal (`/student/**`)
- **Dashboard:** Welcome greeting, grievance metric cards (Total, Pending, Resolved, Rejected), and recent complaint activity table.
- **10 Campus Categories:**
  1. *Infrastructure* (Classroom leaks, building cracks, structural hazards)
  2. *Facilities* (Projectors, fans, AC, electricity, water coolers)
  3. *Faculty & Academics* (Lecture scheduling, syllabus, faculty concerns)
  4. *Food & Canteen* (Hygiene, nutrition, pricing, dispenser filters)
  5. *Transportation* (Bus routes, schedules, vehicle maintenance)
  6. *Hostel* (Geysers, room maintenance, water pressure)
  7. *Library* (Book availability, digital lab equipment, noise levels)
  8. *Sanitation & Hygiene* (Restroom cleanliness, waste disposal)
  9. *Campus Safety & Security* (Pathway lighting, locks, gate control)
  10. *Other Issues* (General campus concerns)
- **Filing Form:** Category pre-selection, Priority selection (*Low, Medium, High, Critical*), Location, Title, Description, and photo evidence upload with instant preview and remove capability.
- **My Complaints:** Table/cards with live search, status filter, category filter, and sorting.
- **Detail View:** Visual 4-stage progress tracker (*Submitted &rarr; Under Review &rarr; Action Required &rarr; Resolved*), photo evidence popup modal, official management replies, and complete audit timeline.

### D. Management Portal (`/management/**`)
- **Dashboard Overview:** 6 statistical counters (*Total, New/Submitted, Under Review, Action Required, Resolved, Rejected*), urgent/critical issue alert banner, and recent submissions table.
- **Shared Repository:** Centralized view of all campus grievances across departments.
- **Management Actions:**
  1. **Draft a Reply:** Dispatches an official response to the student's detail view.
  2. **Update Status:** Advances status between *Under Review*, *Action Required*, and *Resolved* with optional transition notes.
  3. **Reject Complaint:** Flags complaint as *Rejected* with a mandatory explanation reason and modal confirmation dialog.
  4. **Internal Staff Notes:** Private notes visible strictly to administrative and estate personnel (hidden from students).
  5. **Audit Activity Log:** Automated timestamped history tracking every status change, note, and reply.

---

## 5. Architectural Structure

```
src/main/java/com/campusconnect/
├── CampusConnectApplication.java   # Spring Boot entry point
├── config/
│   ├── SecurityConfig.java         # Spring Security 6 role-based filters
│   ├── WebMvcConfig.java           # Static & upload resource mapping
│   └── DataInitializer.java        # CommandLineRunner seeding demo data
├── controller/
│   ├── AuthController.java         # Login, signup, password reset
│   ├── HomeController.java         # Public landing page
│   ├── StudentController.java      # Student dashboard, submission, tracking
│   ├── ManagementController.java   # Admin dashboard, shared issues, actions
│   ├── ProfileController.java      # User profile & password updates
│   └── NotificationController.java # In-app notification management
├── dto/                            # Strongly typed form requests
├── exception/                      # Global exception handlers
├── model/                          # Domain entities (User, Complaint, Role, Category...)
├── repository/
│   ├── UserRepository.java         # User repository interface
│   ├── InMemoryUserRepository.java # Thread-safe in-memory implementation
│   ├── ComplaintRepository.java    # Complaint repository interface
│   ├── InMemoryComplaintRepository.java # Thread-safe in-memory implementation
│   └── NotificationRepository.java # Notification repository interface
├── security/                       # CustomUserDetails & authentication handlers
└── service/
    ├── UserService.java            # Account creation & credentials
    ├── ComplaintService.java       # Grievance processing & metrics
    ├── NotificationService.java    # Alert dispatching
    └── storage/
        ├── FileStorageService.java      # Storage abstraction interface
        └── LocalFileStorageService.java # Local filesystem storage provider
```

---

## 6. Future Cloud Integration (Firebase / Supabase)

The codebase is prepared for cloud migration:
- **Database:** Swap `InMemoryComplaintRepository` and `InMemoryUserRepository` with Spring Data JPA entities or Firebase Realtime / Firestore / Supabase PostgreSQL client without modifying controllers or services.
- **Storage:** Implement `FileStorageService` using Google Cloud Storage / Firebase Storage SDK or Supabase Storage API (`storeFile(MultipartFile)` returns public storage URI; complaint entity holds reference URL, not Base64).

