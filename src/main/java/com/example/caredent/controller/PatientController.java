package com.example.caredent.controller;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import com.example.caredent.bean.CancellationRequest; 
import com.example.caredent.dto.PatientForm;
import com.example.caredent.dto.CancellationRequestForm; 
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.UserRepository;
import com.example.caredent.repository.CancellationRequestRepository; 

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
    
    // REQUIRED: Dependency for handling cancellation requests
    @Autowired
    private CancellationRequestRepository cancellationRepository;

    // Constants for session attributes
    private static final String LOGGED_IN_USER = "loggedInUser";
    private static final String PENDING_ENROLLMENT_FORM = "pendingEnrollmentForm";


    /** Dashboard: show patient info + available plans + ALL enrollments (Active/Cancelled) */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            // Assuming /api/auth/login is your login endpoint
            return "redirect:/api/auth/login"; 
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        List<DentalPlan> plans = planRepository.findAll();
        
        // --- MODIFIED LOGIC: Fetch ALL enrollments for the patient ---
        // NOTE: This assumes EnrollmentRepository has a findAllByPatient(Patient) method
        List<Enrollment> enrollments = (patient != null)
                ? enrollmentRepository.findAllByPatient(patient) 
                : List.of(); 

        // Filter lists for dashboard display based on a simulated status field in Enrollment
        List<Enrollment> activeEnrollments = enrollments.stream()
            // Plans the patient currently views as active or pending cancellation
            .filter(e -> "ACTIVE".equals(e.getStatus()) || "PENDING_CANCELLATION".equals(e.getStatus()))
            .collect(Collectors.toList());
            
        List<Enrollment> historyEnrollments = enrollments.stream()
            // Plans that are fully cancelled or expired
            .filter(e -> "CANCELLED".equals(e.getStatus()) || "EXPIRED".equals(e.getStatus()))
            .collect(Collectors.toList());

        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        model.addAttribute("plans", plans);
        model.addAttribute("activeEnrollments", activeEnrollments); // List for cards (available plans/active plans)
        model.addAttribute("historyEnrollments", historyEnrollments); // List for history table (cancelled/taken plans)
        
        return "patientDashboard";
    }

    /** STEP 0: Show enrollment form for selected plan */
    @GetMapping("/enrollForm/{planId}")
    public String showEnrollForm(@PathVariable Long planId, Model model, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        DentalPlan plan = planRepository.findById(planId).orElseThrow();

        // Removed check for single active enrollment to allow multiple plans.

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

    /** STEP 1: Process enrollment form, save patient, store form in session, and redirect to payment */
    @PostMapping("/enroll")
    public String processEnrollment(@ModelAttribute PatientForm patientForm, HttpSession session) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // 1. Create or update patient record (Patient info is final at this point)
        Patient patient = patientRepository.findByUser(user).orElse(new Patient());
        patient.setUser(user);
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setDob(patientForm.getDob());
        patient.setSsn(patientForm.getSsn());
        
        patient.setPhone(patientForm.getPhone());
        patient.setGender(patientForm.getGender()); // <--- New mapping
patient.setAddress(patientForm.getAddress());
patient.setCity(patientForm.getCity());     // <--- New mapping
patient.setState(patientForm.getState());   // <--- New mapping
patient.setZipCode(patientForm.getZipCode()); // <--- New mapping
        patientRepository.save(patient); // Patient is saved/updated

        // 2. Store the full form data in the session for the next step (payment)
        session.setAttribute(PENDING_ENROLLMENT_FORM, patientForm);

        // 3. Redirect to the payment gateway
        return "redirect:/patient/payment";
    }
    
    /** STEP 2a: Show Payment Form (using the new payment.html) */
    @GetMapping("/payment")
    public String showPaymentForm(Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        PatientForm patientForm = (PatientForm) session.getAttribute(PENDING_ENROLLMENT_FORM);
        
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        
        if (patientForm == null) {
            // Data loss: force re-enrollment
            return "redirect:/patient/dashboard?error=sessionExpired";
        }

        // Retrieve the dental plan details needed for payment info
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

        // ** FINALIZATION LOGIC (Only runs if paymentSuccessful) **

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
        enrollment.setStatus("ACTIVE"); // Set to ACTIVE after successful payment

        enrollmentRepository.save(enrollment);

        // 2. Clear the temporary session data
        session.removeAttribute(PENDING_ENROLLMENT_FORM);

        // 3. Redirect back to the dashboard to show the new card and success message
        return "redirect:/patient/dashboard?success=paid";
    }
    
    // =========================================================
    //               NEW: PLAN CANCELLATION FLOW
    // =========================================================

    /** Show the cancellation request form, pre-filling data */
    @GetMapping("/cancelForm/{enrollmentId}")
    public String showCancellationForm(@PathVariable Long enrollmentId, Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute(LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        
        // Fetch the enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found."));

        // Check if the plan is already pending cancellation
        if ("PENDING_CANCELLATION".equals(enrollment.getStatus())) {
             ra.addFlashAttribute("error", "This plan is already pending cancellation review by the admin.");
             return "redirect:/patient/dashboard";
        }
        
        // Pre-fill the form: fetches the enrollment ID automatically
        CancellationRequestForm form = new CancellationRequestForm();
        form.setEnrollmentId(enrollmentId);

        model.addAttribute("enrollment", enrollment);
        String planName = enrollment.getDentalPlan() != null ? enrollment.getDentalPlan().getName() : "Unknown Plan";
        model.addAttribute("planName", planName);
        model.addAttribute("cancellationForm", form);

        return "cancelForm"; // Uses the new Thymeleaf template
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

        // 2. Update Enrollment status to pending (so it disappears from "ACTIVE" and appears as "PENDING_CANCELLATION" on the dashboard)
        enrollment.setStatus("PENDING_CANCELLATION");
        enrollmentRepository.save(enrollment);

        ra.addFlashAttribute("success", "Cancellation request submitted for " + enrollment.getDentalPlan().getName() + ". Awaiting admin review.");
        return "redirect:/patient/dashboard";
    }
}