package com.example.caredent.controller;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import com.example.caredent.bean.CancellationRequest; 
import com.example.caredent.dto.PatientForm;
import com.example.caredent.dto.CancellationRequestForm; 
import com.example.caredent.dto.ProfileForm; 
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.UserRepository;
import com.example.caredent.repository.CancellationRequestRepository;

// --- FIND A DENTIST IMPORTS ---
import com.example.caredent.bean.Doctor; 
import com.example.caredent.repository.DentistNetworkRepository; 
import com.example.caredent.service.NetworkService; 
// --- END FIND A DENTIST IMPORTS ---

// --- CLAIMS IMPORTS ---
import com.example.caredent.bean.Claim; 
import com.example.caredent.repository.ClaimRepository; 
// --- END NEW CLAIMS IMPORTS ---

import com.example.caredent.service.Patientprofilemanage;
import com.example.caredent.service.PdfService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse; 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.thymeleaf.context.Context; 

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DentalPlanRepository planRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CancellationRequestRepository cancellationRepository;
    
    @Autowired
    private Patientprofilemanage profileService; 
    
    // --- FIND A DENTIST REPOSITORIES ---
    @Autowired
    private DentistNetworkRepository dentistNetworkRepository;

    @Autowired
    private NetworkService networkService;
    // --- END FIND A DENTIST REPOSITORIES ---
    
    // --- CLAIMS REPOSITORY ---
    @Autowired
    private ClaimRepository claimRepository; 
    // --- END CLAIMS REPOSITORY ---

    // --- PDF SERVICE ---
    @Autowired
    private PdfService pdfService; 
    // --- END PDF SERVICE ---

    // Constants for session attributes
    private static final String LOGGED_IN_USER = "loggedInUser";
    private static final String PENDING_ENROLLMENT_FORM = "pendingEnrollmentForm";


    /** Dashboard: show patient info + available plans + ALL enrollments */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login"; 
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        List<DentalPlan> plans = planRepository.findAll();
        
        List<Enrollment> enrollments = (patient != null)
                ? enrollmentRepository.findAllByPatient(patient) 
                : List.of(); 

        List<Enrollment> activeEnrollments = enrollments.stream()
            .filter(e -> "ACTIVE".equals(e.getStatus()) || "PENDING_CANCELLATION".equals(e.getStatus()))
            .collect(Collectors.toList());
            
        // Sorting Active Enrollments (ACTIVE first, PENDING second)
        activeEnrollments.sort((e1, e2) -> {
            int statusPriority1 = "ACTIVE".equals(e1.getStatus()) ? 0 : 1;
            int statusPriority2 = "ACTIVE".equals(e2.getStatus()) ? 0 : 1;
            return Integer.compare(statusPriority1, statusPriority2);
        });
            
        List<Enrollment> historyEnrollments = enrollments.stream()
            .filter(e -> "CANCELLED".equals(e.getStatus()) || "EXPIRED".equals(e.getStatus()))
            .collect(Collectors.toList());
            
        // Prepare list of IDs for quick check in Thymeleaf template (enrollment restriction)
        List<Long> activePlanIds = activeEnrollments.stream()
                                                     .map(e -> e.getDentalPlan().getId())
                                                     .collect(Collectors.toList());

        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        model.addAttribute("plans", plans);
        model.addAttribute("activeEnrollments", activeEnrollments);
        model.addAttribute("historyEnrollments", historyEnrollments);
        model.addAttribute("activePlanIds", activePlanIds); // Used for disabling plan cards
        
        return "patientDashboard";
    }

    /** STEP 0: Show enrollment form for selected plan with Enrollment Restriction */
    @GetMapping("/enrollForm/{planId}")
    public String showEnrollForm(@PathVariable Long planId, Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        DentalPlan plan = planRepository.findById(planId).orElseThrow();
        
        // --- ENROLLMENT RESTRICTION ---
        if (patient != null) {
            boolean alreadyActive = enrollmentRepository.findAllByPatient(patient).stream()
                .filter(e -> e.getDentalPlan().getId().equals(planId))
                .anyMatch(e -> "ACTIVE".equals(e.getStatus()));

            if (alreadyActive) {
                ra.addFlashAttribute("error", "This plan is already enrolled.");
                return "redirect:/patient/dashboard";
            }
        }
        // --- END RESTRICTION LOGIC ---

        PatientForm patientForm = new PatientForm();
        patientForm.setPlanId(plan.getId());

        // Pre-fill form fields if patient record already exists
        if (patient != null) {
            patientForm.setFirstName(patient.getFirstName());
            patientForm.setLastName(patient.getLastName());
            patientForm.setDob(patient.getDob());
            patientForm.setSsn(patient.getSsn());
            patientForm.setAddress(patient.getAddress());
            patientForm.setPhone(patient.getPhone());
        }

        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        model.addAttribute("plan", plan);
        model.addAttribute("patientForm", patientForm);

        return "enrollForm";
    }

    /** STEP 1: Process enrollment form, save patient (with new fields), store form in session, and redirect to payment */
    @PostMapping("/enroll")
    public String processEnrollment(@ModelAttribute PatientForm patientForm, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // 1. Create or update patient record
        Patient patient = patientRepository.findByUser(user).orElse(new Patient());
        patient.setUser(user);
        
        // Mapping all fields from the comprehensive form to the Patient entity
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setDob(patientForm.getDob());
        patient.setSsn(patientForm.getSsn());
        patient.setPhone(patientForm.getPhone());
        
        patient.setGender(patientForm.getGender()); 
        patient.setAddress(patientForm.getAddress());
        patient.setCity(patientForm.getCity()); 
        patient.setState(patientForm.getState());
        patient.setZipCode(patientForm.getZipCode()); 
        
        patientRepository.save(patient); 

        // 2. Store the full form data in the session for the next step (payment)
        session.setAttribute(PENDING_ENROLLMENT_FORM, patientForm);

        // 3. Redirect to the payment gateway
        return "redirect:/patient/payment";
    }
    
    /** STEP 2a: Show Payment Form */
    @GetMapping("/payment")
    public String showPaymentForm(Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        PatientForm patientForm = (PatientForm) session.getAttribute(PENDING_ENROLLMENT_FORM);
        
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        
        if (patientForm == null) {
            return "redirect:/patient/dashboard?error=sessionExpired";
        }

        DentalPlan plan = planRepository.findById(patientForm.getPlanId()).orElseThrow();
        
        model.addAttribute("plan", plan);
        model.addAttribute("premiumAmount", plan.getPremium()); 

        return "payment";
    }

    /** STEP 2b: Handle Payment Submission and finalize Enrollment */
    @PostMapping("/completePayment")
    public String completePayment(HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        PatientForm patientForm = (PatientForm) session.getAttribute(PENDING_ENROLLMENT_FORM);

        if (user == null || patientForm == null) {
            return "redirect:/patient/dashboard?error=sessionExpired";
        }

        // ** SIMULATE PAYMENT SUCCESS **
        boolean paymentSuccessful = true; // Hardcoded to true for simulation

        if (!paymentSuccessful) {
            session.removeAttribute(PENDING_ENROLLMENT_FORM);
            return "redirect:/patient/dashboard?error=paymentFailed";
        }

        // ** FINALIZATION LOGIC **
        Patient patient = patientRepository.findByUser(user).orElseThrow();
        DentalPlan plan = planRepository.findById(patientForm.getPlanId()).orElseThrow();

        // 1. Create and save Enrollment record
        Enrollment enrollment = new Enrollment();
        enrollment.setPatient(patient);
        enrollment.setDentalPlan(plan);
        enrollment.setBenefitYearStart(LocalDate.now());
        enrollment.setBenefitYearEnd(LocalDate.now().plusYears(1));
        enrollment.setDeductibleUsed(0.0);
        enrollment.setAnnualMaxUsed(0.0);
        enrollment.setStatus("ACTIVE"); 

        enrollmentRepository.save(enrollment);

        // 2. Clear the temporary session data
        session.removeAttribute(PENDING_ENROLLMENT_FORM);

        // 3. Redirect back to the dashboard to show the new card and success message
        return "redirect:/patient/dashboard?success=paid";
    }
    
    // =========================================================
    //               PLAN CANCELLATION FLOW
    // =========================================================

    /** Show the cancellation request form, pre-filling data */
    @GetMapping("/cancelForm/{enrollmentId}")
    public String showCancellationForm(
        @PathVariable Long enrollmentId, Model model, HttpSession session, RedirectAttributes ra) 
    {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found."));

        if ("PENDING_CANCELLATION".equals(enrollment.getStatus())) {
             ra.addFlashAttribute("error", "This plan is already pending cancellation review by the admin.");
             return "redirect:/patient/dashboard";
        }
        
        CancellationRequestForm form = new CancellationRequestForm();
        form.setEnrollmentId(enrollmentId);

        model.addAttribute("enrollment", enrollment);
        String planName = enrollment.getDentalPlan() != null ? enrollment.getDentalPlan().getName() : "Unknown Plan";
        model.addAttribute("planName", planName);
        model.addAttribute("cancellationForm", form);

        // FIX APPLIED HERE: The view name matches the HTML file name (cancelForm.html)
        return "cancelForm"; 
    }

    /** Process the submission of the cancellation request */
    @PostMapping("/requestCancellation")
    public String processCancellationRequest(@ModelAttribute CancellationRequestForm form, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        Patient patient = patientRepository.findByUser(user).orElse(null);
        
        if (user == null || patient == null) {
            return "redirect:/api/auth/login";
        }

        Enrollment enrollment = enrollmentRepository.findById(form.getEnrollmentId())
            .orElseThrow(() -> new RuntimeException("Enrollment not found."));
            
        // 1. Create the Cancellation Request record (for Admin's queue)
        CancellationRequest request = new CancellationRequest();
        request.setEnrollment(enrollment);
        request.setPatient(patient); 
        request.setReason(form.getReason());
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING"); 
        
        cancellationRepository.save(request); 

        // 2. Update Enrollment status to PENDING_CANCELLATION
        enrollment.setStatus("PENDING_CANCELLATION");
        enrollmentRepository.save(enrollment);

        ra.addFlashAttribute("success", "Cancellation request submitted for " + enrollment.getDentalPlan().getName() + ". Awaiting admin review.");
        return "redirect:/patient/dashboard";
    }

    // =========================================================
    //               PROFILE MANAGEMENT ROUTES 
    // =========================================================

    /** Show the minimalist profile management form */
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        Patient patient = patientRepository.findByUser(user).orElseThrow();
        
        // DELEGATE: Fetch data via the service
        ProfileForm form = profileService.getProfileData(user);
        
        model.addAttribute("profileForm", form);
        // Pass view-only data separately for display (DOB, SSN)
        model.addAttribute("patient", patient); 
        
        return "manageProfile"; 
    }

    /** Process the update of basic profile details and handle password change requests */
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ProfileForm form, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        try {
            // DELEGATE: Perform all business logic in the service
            boolean success = profileService.updateProfile(user, form);
            
            if (success) {
                // Determine if password fields were used
                if (form.getNewPassword() != null && !form.getNewPassword().isEmpty()) {
                    ra.addFlashAttribute("success", "Profile and password updated successfully!");
                } else {
                    ra.addFlashAttribute("success", "Profile details updated successfully!");
                }
            } else {
                // If service returns false, it implies a password failure (e.g., incorrect current password)
                ra.addFlashAttribute("error", "Incorrect current password. Changes failed.");
            }
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors (like mismatched new passwords)
            ra.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "An unexpected error occurred during update.");
        }
        
        return "redirect:/patient/profile";
    }
    
    // =========================================================
    //               FIND A DENTIST ROUTE
    // =========================================================
    
    /** Show the Find a Dentist form and display results if a plan is selected. */
    @GetMapping("/network")
    public String showFindDentist(
        @RequestParam(name = "planId", required = false) Long planId,
        Model model, HttpSession session) 
    {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        
        // Fetch all available dental plans to populate the dropdown
        List<DentalPlan> plans = planRepository.findAll();
        model.addAttribute("plans", plans);
        
        List<Doctor> doctors = List.of();
        DentalPlan selectedPlan = null;
        
        if (planId != null) {
            // Find doctors based on the selected plan ID, delegated to networkService
            doctors = networkService.findInNetworkDoctors(planId);
            selectedPlan = planRepository.findById(planId).orElse(null);
        }
        
        model.addAttribute("selectedPlan", selectedPlan);
        model.addAttribute("doctors", doctors);
        
        return "findDentist";
    }
    
    // =========================================================
    //               NEW: VIEW CLAIMS ROUTE (MODIFIED FOR NO plan_id COLUMN)
    // =========================================================

    /** Shows all claims submitted by the dentist for the currently logged-in patient. */
    @GetMapping("/claims")
    public String viewClaims(Model model, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // 1. Identify the patient record associated with the logged-in user
        Patient patient = patientRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Patient record not found for logged-in user."));

        // 2. Retrieve all claims associated with this patient
        List<Claim> patientClaims = claimRepository.findAllByPatient(patient); 
        
        // 3. DETERMINE THE PLAN NAME (WORKAROUND for missing claim.plan_id column)
        // We find the patient's current active plan and pass its name to the view.
        
        // First, get all enrollments for the patient
        List<Enrollment> enrollments = enrollmentRepository.findAllByPatient(patient);
        
        // Find the currently active enrollment
        Optional<Enrollment> activeEnrollment = enrollments.stream()
            .filter(e -> "ACTIVE".equals(e.getStatus()))
            .findFirst();

        String activePlanName = "N/A - Check Enrollment";
        if (activeEnrollment.isPresent()) {
            DentalPlan plan = activeEnrollment.get().getDentalPlan();
            if (plan != null) {
                 activePlanName = plan.getName();
            }
        }
        
        // 4. Add data to model
        model.addAttribute("patientClaims", patientClaims);
        model.addAttribute("activePlanName", activePlanName); // Pass the plan name for display
        model.addAttribute("user", user); 

        return "patientClaimsView";
    }
    
    // =========================================================
    //               NEW: DOWNLOAD PDF ROUTE
    // =========================================================

    /** Downloads the insurance card as a PDF by rendering the PDF template. */
    @GetMapping("/card/download/{enrollmentId}")
    public void downloadInsuranceCard(
        @PathVariable Long enrollmentId,
        HttpServletResponse response,
        HttpSession session
    ) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found."));

        // 1. Prepare data context for the PDF template
        Context context = new Context();
        context.setVariable("enrollment", enrollment);
        context.setVariable("patientName", enrollment.getPatient().getFirstName() + " " + enrollment.getPatient().getLastName());

        // 2. Generate PDF bytes
        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtml("insuranceCardPdf", context);

            // 3. Set HTTP Headers for PDF Download
            String filename = "CareDent_Card_" + enrollment.getDentalPlan().getName().replaceAll("\\s", "") + "_" + enrollmentId + ".pdf";
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLength(pdfBytes.length);

            // 4. Stream PDF to response output stream
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            // If PDF fails, set error status and stream a small error message (optional)
            // Note: This often only shows a blank page, but prevents a crash.
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Error: Could not generate PDF. Check server logs.");
            } catch (java.io.IOException ioE) {
                // Ignore secondary IO exception
            }
        }
    }
}