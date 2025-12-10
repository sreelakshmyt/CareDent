package com.example.caredent.controller;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import com.example.caredent.dto.PatientForm;
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

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

    /** Dashboard: show patient info + available plans */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login"; // force login if no session
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        List<DentalPlan> plans = planRepository.findAll();
        Enrollment enrollment = (patient != null)
                ? enrollmentRepository.findByPatient(patient).orElse(null)
                : null;

        // Add both patient and user to the model for null-safe rendering
        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        model.addAttribute("plans", plans);
        model.addAttribute("enrollment", enrollment);

        return "patientDashboard"; // Thymeleaf template
    }

    /** Show enrollment form for selected plan */
    @GetMapping("/enrollForm/{planId}")
    public String showEnrollForm(@PathVariable Long planId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);
        DentalPlan plan = planRepository.findById(planId).orElseThrow();

        PatientForm patientForm = new PatientForm();
        patientForm.setPlanId(plan.getId());

        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        model.addAttribute("plan", plan);
        model.addAttribute("patientForm", patientForm);

        return "enrollForm"; // Thymeleaf template
    }

    /** Handle enrollment submission */
    @PostMapping("/enroll")
    public String enrollPatient(@ModelAttribute PatientForm patientForm, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // Create or update patient record
        Patient patient = patientRepository.findByUser(user).orElse(new Patient());
        patient.setUser(user);
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setDob(patientForm.getDob());
        patient.setSsn(patientForm.getSsn());
        patient.setAddress(patientForm.getAddress());
        patient.setPhone(patientForm.getPhone());
        patientRepository.save(patient);

        // Link patient to chosen plan
        DentalPlan plan = planRepository.findById(patientForm.getPlanId()).orElseThrow();

        // Prevent duplicate enrollment
        if (enrollmentRepository.findByPatient(patient).isPresent()) {
            return "redirect:/patient/dashboard?error=alreadyEnrolled";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setPatient(patient);
        enrollment.setDentalPlan(plan);
        enrollment.setBenefitYearStart(LocalDate.now());
        enrollment.setBenefitYearEnd(LocalDate.now().plusYears(1));
        enrollment.setDeductibleUsed(0.0);
        enrollment.setAnnualMaxUsed(0.0);

        enrollmentRepository.save(enrollment);

        return "redirect:/patient/dashboard?success=enrolled";
    }
}
