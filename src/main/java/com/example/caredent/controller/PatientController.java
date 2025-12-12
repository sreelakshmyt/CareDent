package com.example.caredent.controller;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import com.example.caredent.bean.CancellationRequest; 
import com.example.caredent.dto.PatientForm;
import com.example.caredent.dto.CancellationRequestForm; 
import com.example.caredent.dto.ProfileForm; // New DTO
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.UserRepository;
import com.example.caredent.repository.CancellationRequestRepository;
import com.example.caredent.service.Patientprofilemanage;
 // <-- CORRECTED SERVICE IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    @Autowired // Inject the new service
    private Patientprofilemanage profileService; // <-- CORRECTED TYPE

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
            
            // patientForm.setCity(patient.getCity()); 
            // patientForm.setState(patient.getState());
            // patientForm.setZipCode(patient.getZipCode());
            // patientForm.setGender(patient.getGender());
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

        return "cancelPlanForm"; 
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
    //               NEW: PROFILE MANAGEMENT ROUTES (Delegated to Service)
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
}