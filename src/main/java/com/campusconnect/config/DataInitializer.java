package com.campusconnect.config;

import com.campusconnect.model.*;
import com.campusconnect.repository.ComplaintRepository;
import com.campusconnect.repository.NotificationRepository;
import com.campusconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ComplaintRepository complaintRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // Seed Users
        User alex = new User(
                "usr-student-01",
                "Alex Rivera",
                "student@campus.edu",
                passwordEncoder.encode("password123"),
                Role.ROLE_STUDENT,
                "Computer Science & Engineering",
                "CS2023-042",
                "+1 (555) 234-5678"
        );
        alex.setCreatedAt(LocalDateTime.now().minusDays(30));
        userRepository.save(alex);

        User priya = new User(
                "usr-student-02",
                "Priya Patel",
                "priya.patel@campus.edu",
                passwordEncoder.encode("password123"),
                Role.ROLE_STUDENT,
                "Electronics & Communication",
                "EC2022-108",
                "+1 (555) 345-6789"
        );
        priya.setCreatedAt(LocalDateTime.now().minusDays(25));
        userRepository.save(priya);

        User evelyn = new User(
                "usr-mgt-01",
                "Dr. Evelyn Reed",
                "admin@campus.edu",
                passwordEncoder.encode("password123"),
                Role.ROLE_MANAGEMENT,
                "Campus Administration",
                "DIR-201",
                "+1 (555) 890-1234"
        );
        evelyn.setCreatedAt(LocalDateTime.now().minusDays(90));
        userRepository.save(evelyn);

        User marcus = new User(
                "usr-mgt-02",
                "Marcus Sterling",
                "facilities@campus.edu",
                passwordEncoder.encode("password123"),
                Role.ROLE_MANAGEMENT,
                "Facilities & Estate Office",
                "ENG-104",
                "+1 (555) 901-2345"
        );
        marcus.setCreatedAt(LocalDateTime.now().minusDays(80));
        userRepository.save(marcus);

        // Seed Complaints
        // 1. Water leakage in Room 302
        Complaint c1 = new Complaint();
        c1.setId("c-1001");
        c1.setComplaintId("CC-2026-1001");
        c1.setStudentId(alex.getId());
        c1.setStudentName(alex.getFullName());
        c1.setStudentEmail(alex.getEmail());
        c1.setStudentDepartment(alex.getDepartment());
        c1.setStudentRollNo(alex.getIdentifier());
        c1.setTitle("Ceiling water leakage damaging benches in Room 302");
        c1.setDescription("During rainfall yesterday, persistent water was dripping directly over benches in rows 3 and 4 of Room 302 (Block B). This makes the classroom unusable during lectures and poses an electrical slip hazard near the perimeter switchboards.");
        c1.setCategory(Category.INFRASTRUCTURE);
        c1.setLocation("Academic Block B, 3rd Floor, Room 302");
        c1.setPriority(Priority.HIGH);
        c1.setStatus(ComplaintStatus.ACTION_REQUIRED);
        c1.setSubmittedAt(LocalDateTime.now().minusDays(3).minusHours(4));
        c1.setUpdatedAt(LocalDateTime.now().minusHours(5));

        c1.addActivity(new ComplaintActivity(alex.getFullName(), "Complaint Submitted", "Lodge grievance under category 'Infrastructure' with priority High."));
        c1.addActivity(new ComplaintActivity(evelyn.getFullName(), "Status Updated to Under Review", "Administration has acknowledged the report and forwarded inspection request to Estate Office."));
        c1.addActivity(new ComplaintActivity(marcus.getFullName(), "Internal Note Added", "Estate inspector inspected roof membrane at 11 AM; replacement of waterproofing sealant needed."));
        c1.addActivity(new ComplaintActivity(evelyn.getFullName(), "Status Updated to Action Required", "Contractor assigned for roof patching on Saturday."));
        c1.addActivity(new ComplaintActivity(marcus.getFullName(), "Response Sent", "We have dispatched the roofing contractor to reseal the top slab by Friday evening. The classroom has been temporarily relocated to Room 204."));

        c1.addInternalNote(new InternalNote(marcus.getFullName(), marcus.getEmail(), "Maintenance team requisition #8831 approved for ₹18,000 waterproofing tar felt roll. Work order executed."));
        c1.addResponse(new ManagementResponse(marcus.getFullName(), marcus.getEmail(), marcus.getDepartment(), "We have dispatched the roofing contractor to reseal the top slab by Friday evening. The classroom has been temporarily relocated to Room 204."));
        complaintRepository.save(c1);

        // 2. Overheating projector
        Complaint c2 = new Complaint();
        c2.setId("c-1002");
        c2.setComplaintId("CC-2026-1002");
        c2.setStudentId(alex.getId());
        c2.setStudentName(alex.getFullName());
        c2.setStudentEmail(alex.getEmail());
        c2.setStudentDepartment(alex.getDepartment());
        c2.setStudentRollNo(alex.getIdentifier());
        c2.setTitle("Overheating digital projector shutting down during lectures");
        c2.setDescription("The EPSON ceiling mounted projector in Seminar Hall 1 shuts down automatically after roughly 15 minutes of continuous use with a flashing red warning lamp indicating thermal shutdown.");
        c2.setCategory(Category.FACILITIES);
        c2.setLocation("Main Campus, Seminar Hall 1");
        c2.setPriority(Priority.MEDIUM);
        c2.setStatus(ComplaintStatus.UNDER_REVIEW);
        c2.setSubmittedAt(LocalDateTime.now().minusDays(2).minusHours(2));
        c2.setUpdatedAt(LocalDateTime.now().minusDays(1));

        c2.addActivity(new ComplaintActivity(alex.getFullName(), "Complaint Submitted", "Grievance lodged under Facilities."));
        c2.addActivity(new ComplaintActivity(marcus.getFullName(), "Status Updated to Under Review", "AV technician dispatched to test filter cleanliness and cooling fan."));
        c2.addInternalNote(new InternalNote(marcus.getFullName(), marcus.getEmail(), "AV vendor contacted for lamp replacement quote."));
        complaintRepository.save(c2);

        // 3. Canteen Drinking Water
        Complaint c3 = new Complaint();
        c3.setId("c-1003");
        c3.setComplaintId("CC-2026-1003");
        c3.setStudentId(priya.getId());
        c3.setStudentName(priya.getFullName());
        c3.setStudentEmail(priya.getEmail());
        c3.setStudentDepartment(priya.getDepartment());
        c3.setStudentRollNo(priya.getIdentifier());
        c3.setTitle("Unsealed drinking water dispenser filters in South Canteen");
        c3.setDescription("The primary water cooler near the dining area had not undergone the scheduled monthly cartridge maintenance since last month and exhibited mineral turbidity.");
        c3.setCategory(Category.FOOD_CANTEEN);
        c3.setLocation("South Canteen Ground Floor");
        c3.setPriority(Priority.HIGH);
        c3.setStatus(ComplaintStatus.RESOLVED);
        c3.setSubmittedAt(LocalDateTime.now().minusDays(6));
        c3.setUpdatedAt(LocalDateTime.now().minusDays(1));

        c3.addActivity(new ComplaintActivity(priya.getFullName(), "Complaint Submitted", "Complaint lodged under Food & Canteen."));
        c3.addActivity(new ComplaintActivity(evelyn.getFullName(), "Status Updated to Action Required", "Health & Sanitation officer instructed to inspect drinking water unit."));
        c3.addActivity(new ComplaintActivity(marcus.getFullName(), "Status Updated to Resolved", "All RO cartridges replaced and water quality TDS certified."));
        c3.addResponse(new ManagementResponse(evelyn.getFullName(), evelyn.getEmail(), "Campus Administration", "The water filter cartridges and UV chamber were completely replaced yesterday. Water samples passed laboratory microbial safety checks (TDS: 110 ppm)."));
        complaintRepository.save(c3);

        // 4. Hostel geyser failure
        Complaint c4 = new Complaint();
        c4.setId("c-1004");
        c4.setComplaintId("CC-2026-1004");
        c4.setStudentId(alex.getId());
        c4.setStudentName(alex.getFullName());
        c4.setStudentEmail(alex.getEmail());
        c4.setStudentDepartment(alex.getDepartment());
        c4.setStudentRollNo(alex.getIdentifier());
        c4.setTitle("Water heater failure in Block 4 second-floor wing");
        c4.setDescription("The central geyser unit on the eastern wing of Hostel Block 4 tripped the main MCB breaker early this morning. There is currently no hot water for over 60 residents.");
        c4.setCategory(Category.HOSTEL);
        c4.setLocation("Boys Hostel Block 4, Wing B, 2nd Floor");
        c4.setPriority(Priority.CRITICAL);
        c4.setStatus(ComplaintStatus.SUBMITTED);
        c4.setSubmittedAt(LocalDateTime.now().minusHours(2));
        c4.setUpdatedAt(LocalDateTime.now().minusHours(2));

        c4.addActivity(new ComplaintActivity(alex.getFullName(), "Complaint Submitted", "Complaint logged with priority Critical."));
        complaintRepository.save(c4);

        // 5. Library AC issue
        Complaint c5 = new Complaint();
        c5.setId("c-1005");
        c5.setComplaintId("CC-2026-1005");
        c5.setStudentId(priya.getId());
        c5.setStudentName(priya.getFullName());
        c5.setStudentEmail(priya.getEmail());
        c5.setStudentDepartment(priya.getDepartment());
        c5.setStudentRollNo(priya.getIdentifier());
        c5.setTitle("Central AC unit rattling noise in 3rd Floor Digital Library");
        c5.setDescription("Duct motor vibrating loudly in reading hall 3B causing severe disturbance for students studying for end-semester exams.");
        c5.setCategory(Category.LIBRARY);
        c5.setLocation("Central Library, 3rd Floor Reading Hall 3B");
        c5.setPriority(Priority.MEDIUM);
        c5.setStatus(ComplaintStatus.RESOLVED);
        c5.setSubmittedAt(LocalDateTime.now().minusDays(5));
        c5.setUpdatedAt(LocalDateTime.now().minusDays(2));

        c5.addActivity(new ComplaintActivity(priya.getFullName(), "Complaint Submitted", "Lodge under Library."));
        c5.addActivity(new ComplaintActivity(marcus.getFullName(), "Status Updated to Resolved", "HVAC technician balanced fan rotor and replaced worn belt."));
        c5.addResponse(new ManagementResponse(marcus.getFullName(), marcus.getEmail(), "Estate Maintenance", "The HVAC unit blower motor belt was replaced and acoustic dampeners were installed. Noise level is back to library quiet standards."));
        complaintRepository.save(c5);

        // 6. Security Pathway lighting
        Complaint c6 = new Complaint();
        c6.setId("c-1006");
        c6.setComplaintId("CC-2026-1006");
        c6.setStudentId(alex.getId());
        c6.setStudentName(alex.getFullName());
        c6.setStudentEmail(alex.getEmail());
        c6.setStudentDepartment(alex.getDepartment());
        c6.setStudentRollNo(alex.getIdentifier());
        c6.setTitle("Broken streetlight near Gate 3 parking pathway");
        c6.setDescription("The walkway between Gate 3 and the student two-wheeler parking lot is pitch dark after 6:30 PM due to two broken LED fixtures, creating a safety issue for evening commuters.");
        c6.setCategory(Category.CAMPUS_SAFETY);
        c6.setLocation("Campus South Gate 3 Pathway to Parking B");
        c6.setPriority(Priority.HIGH);
        c6.setStatus(ComplaintStatus.ACTION_REQUIRED);
        c6.setSubmittedAt(LocalDateTime.now().minusDays(1).minusHours(10));
        c6.setUpdatedAt(LocalDateTime.now().minusHours(8));

        c6.addActivity(new ComplaintActivity(alex.getFullName(), "Complaint Submitted", "Lodge under Campus Safety."));
        c6.addActivity(new ComplaintActivity(evelyn.getFullName(), "Status Updated to Action Required", "Campus security officer notified and electrical division ordered to replace lighting pole fixtures."));
        c6.addResponse(new ManagementResponse(evelyn.getFullName(), evelyn.getEmail(), "Security & Estate", "High-lumen LED armatures have been requisitioned and will be mounted by today 4:00 PM."));
        complaintRepository.save(c6);

        // 7. Duplicate / Rejected complaint
        Complaint c7 = new Complaint();
        c7.setId("c-1007");
        c7.setComplaintId("CC-2026-1007");
        c7.setStudentId(alex.getId());
        c7.setStudentName(alex.getFullName());
        c7.setStudentEmail(alex.getEmail());
        c7.setStudentDepartment(alex.getDepartment());
        c7.setStudentRollNo(alex.getIdentifier());
        c7.setTitle("Requesting personal bean bags in CS departmental student lounge");
        c7.setDescription("Requesting management to replace standard chairs with luxury recliners or personal beanbags in the department corridor.");
        c7.setCategory(Category.OTHER);
        c7.setLocation("Computer Science Dept, 2nd Floor Corridor");
        c7.setPriority(Priority.LOW);
        c7.setStatus(ComplaintStatus.REJECTED);
        c7.setRejectionReason("Corridor safety guidelines prohibit soft blocking furniture along fire evacuation paths. Ergonomic chairs are already installed.");
        c7.setSubmittedAt(LocalDateTime.now().minusDays(4));
        c7.setUpdatedAt(LocalDateTime.now().minusDays(3));

        c7.addActivity(new ComplaintActivity(alex.getFullName(), "Complaint Submitted", "Lodge under Other Issues."));
        c7.addActivity(new ComplaintActivity(evelyn.getFullName(), "Complaint Rejected", "Reason: Corridor safety guidelines prohibit soft blocking furniture along fire evacuation paths. Ergonomic chairs are already installed."));
        complaintRepository.save(c7);

        // Seed some sample notifications
        notificationRepository.save(new Notification(
                alex.getId(),
                "Update on Complaint #CC-2026-1001",
                "Roofing contractor dispatched for Room 302 roof repair.",
                "/student/complaints/c-1001"
        ));
        notificationRepository.save(new Notification(
                alex.getId(),
                "Response Received #CC-2026-1006",
                "Management responded regarding Gate 3 pathway lights.",
                "/student/complaints/c-1006"
        ));
        notificationRepository.save(new Notification(
                priya.getId(),
                "Complaint Resolved #CC-2026-1003",
                "Canteen drinking water filter maintenance complete.",
                "/student/complaints/c-1003"
        ));
    }
}

