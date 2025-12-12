
package com.example.caredent.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.ClaimLine;
import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.DentistNetwork;
import com.example.caredent.bean.Dependent;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.PlanCoverageRule;
import com.example.caredent.bean.User;
import com.example.caredent.dto.ClaimQueueItemDTO;
import com.example.caredent.repository.ClaimRepository;
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.DentistNetworkRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.PlanCoverageRuleRepository;
import com.example.caredent.repository.UserRepository;
import com.example.caredent.service.AdminClaimQueueService;
//import com.example.caredent.service.EnrollmentService;


@Controller
@RequestMapping("/api/auth/admin")
public class AdminController {

    @Autowired
    private DentalPlanRepository dentalPlanRepository;

    @Autowired
    private PlanCoverageRuleRepository planCoverageRuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    DentistNetworkRepository dentistNetworkRepository;

    // Dashboard view
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        // Fetch relevant data for the admin dashboard
            // Fetch relevant data for the admin dashboard
    long totalPlans = dentalPlanRepository.count();
    model.addAttribute("totalPlans", totalPlans);
    
    // Add enrollment statistics
   // EnrollmentService.EnrollmentStats stats = enrollmentService.getEnrollmentStatistics();
    //model.addAttribute("enrollmentStats", stats);
    
    // Add other dashboard statistics
    //model.addAttribute("totalEnrollments", stats.getTotalEnrollments());
       // long totalPlans = dentalPlanRepository.count();
        model.addAttribute("totalPlans", totalPlans);
        return "admin_dashboard"; // Thymeleaf template for Admin Dashboard
    }

    // List all Plans
    @GetMapping("/plans")
    public String listPlans(Model model) {
        // Retrieve all dental plans from the database
        model.addAttribute("plans", dentalPlanRepository.findAll());
        return "admin_plans"; // Thymeleaf template for managing plans
    }

    // Create a new Plan
    @GetMapping("/plans/create")
    public String createPlanForm(Model model) {
        model.addAttribute("dentalPlan", new DentalPlan()); // Create an empty DentalPlan object
        return "admin_create_plan"; // Thymeleaf template for the "Create Plan" form
    }

    @PostMapping("/plans/create")
    public String createPlan(@ModelAttribute DentalPlan dentalPlan) {
        // Save the new dental plan to the database
        dentalPlanRepository.save(dentalPlan);
        return "redirect:/api/auth/admin/plans"; // Redirect to the list of plans after saving
    }

    // Edit an existing Plan
    @GetMapping("/plans/edit/{id}")
    public String editPlanForm(@PathVariable Long id, Model model) {
        DentalPlan dentalPlan = dentalPlanRepository.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
        model.addAttribute("dentalPlan", dentalPlan); // Add the existing plan to the model
        return "admin_edit_plan"; // Thymeleaf template for the "Edit Plan" form
    }

    @PostMapping("/plans/edit/{id}")
    public String editPlan(@PathVariable Long id, @ModelAttribute DentalPlan dentalPlan) {
        // Update the existing dental plan
        dentalPlan.setId(id);
        dentalPlanRepository.save(dentalPlan);
        return "redirect:/api/auth/admin/plans"; // Redirect to the list of plans after editing
    }

        // Delete a Plan
    @GetMapping("/plans/delete/{id}")
    public String deletePlan(@PathVariable Long id) {
        // First, check if there are any coverage rules associated with this plan
        DentalPlan dentalPlan = dentalPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found"));
        
        // Delete associated coverage rules first
        List<PlanCoverageRule> coverageRules = planCoverageRuleRepository.findAllByDentalPlan(dentalPlan);
        planCoverageRuleRepository.deleteAll(coverageRules);
        
        // Then delete the plan
        dentalPlanRepository.deleteById(id);
        
        return "redirect:/api/auth/admin/plans"; // Redirect to the list of plans after deletion
    }

    // Delete Plan with POST method (more secure)
    @PostMapping("/plans/delete/{id}")
    public String deletePlanPost(@PathVariable Long id) {
        // First, check if there are any coverage rules associated with this plan
        DentalPlan dentalPlan = dentalPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found"));
        
        // Delete associated coverage rules first
        List<PlanCoverageRule> coverageRules = planCoverageRuleRepository.findAllByDentalPlan(dentalPlan);
        planCoverageRuleRepository.deleteAll(coverageRules);
        
        // Then delete the plan
        dentalPlanRepository.deleteById(id);
        
        return "redirect:/api/auth/admin/plans"; // Redirect to the list of plans after deletion
    }

    // Manage Coverage (Define coverage percentages for different categories)
    @GetMapping("/plans/manage-coverage/{planId}")
    public String manageCoverageForm(@PathVariable Long planId, Model model) {
        DentalPlan dentalPlan = dentalPlanRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found"));
        model.addAttribute("dentalPlan", dentalPlan);
        model.addAttribute("coverageRules", planCoverageRuleRepository.findAllByDentalPlan(dentalPlan)); // Fetch coverage rules
        return "admin_manage_coverage"; // Thymeleaf template for managing coverage
    }

    @PostMapping("/plans/manage-coverage/{planId}")
    public String manageCoverage(@PathVariable Long planId, @RequestParam String procedureCategory, @RequestParam Double coveragePercentage) {
        // Create and save a new coverage rule for the plan
        DentalPlan dentalPlan = dentalPlanRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found"));
        PlanCoverageRule rule = new PlanCoverageRule();
        rule.setDentalPlan(dentalPlan);
        rule.setProcedureCategory(procedureCategory);
        rule.setCoveragePercentage(coveragePercentage);
        planCoverageRuleRepository.save(rule);
        return "redirect:/api/auth/admin/plans/manage-coverage/" + planId; // Redirect to the manage coverage page
    }



@PostMapping("/networks/add-dentist-to-plan")
public String addDentistToPlan(@RequestParam Long planId, @RequestParam Long dentistId) {
    DentalPlan dentalPlan = dentalPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan not found"));
    User dentist = userRepository.findById(dentistId)
            .orElseThrow(() -> new RuntimeException("Dentist not found"));

    DentistNetwork dentistNetwork = new DentistNetwork();
    dentistNetwork.setDentalPlan(dentalPlan);
    dentistNetwork.setDentist(dentist);

    dentistNetworkRepository.save(dentistNetwork);

    return "redirect:/api/auth/admin/networks";
}
@GetMapping("/networks")
public String manageNetworks(Model model) {
    List<DentalPlan> dentalPlans = dentalPlanRepository.findAll();
    List<User> dentists = userRepository.findByRoleName("Dentist");

    // Attach dentist networks to each plan
    for (DentalPlan plan : dentalPlans) {
        List<DentistNetwork> networks = dentistNetworkRepository.findByDentalPlan(plan);
        plan.setDentistNetworks(networks); // Add a transient field in DentalPlan
    }

    model.addAttribute("dentalPlans", dentalPlans);
    model.addAttribute("dentists", dentists);

    return "admin_manage_networks";
}


// @GetMapping("/manageenrollment")
// public String manageEnrollment(Model model) {
//     List<DentalPlan> plans = dentalPlanRepository.findAll();

//     // Attach active enrollments to each plan
//     for (DentalPlan plan : plans) {
//         List<Enrollment> activeEnrollments = enrollmentRepository.findByDentalPlanAndStatus(plan, "ACTIVE");
//         plan.setDentistNetworks(null); // optional, avoid confusion
//         model.addAttribute("activeEnrollments_" + plan.getId(), activeEnrollments);
//     }

//     model.addAttribute("plans", plans);
//     return "admin_manage_enrollment"; // Thymeleaf template
// }


@GetMapping("/manageenrollment")
public String manageEnrollment(Model model) {
    List<DentalPlan> plans = dentalPlanRepository.findAll();

    Map<Long, List<Enrollment>> enrollmentsByPlan = new HashMap<>();
    for (DentalPlan plan : plans) {
        List<Enrollment> activeEnrollments = enrollmentRepository.findByDentalPlanAndStatus(plan, "ACTIVE");
        enrollmentsByPlan.put(plan.getId(), activeEnrollments);
    }

    model.addAttribute("plans", plans);
    model.addAttribute("enrollmentsByPlan", enrollmentsByPlan);

    return "admin_manage_enrollment";
}



// // Add these autowired repositories in the controller
@Autowired
private EnrollmentRepository enrollmentRepository;

@Autowired
private PatientRepository patientRepository;

// @Autowired
// private EnrollmentService enrollmentService;






// // 1. View all pending enrollments
// @GetMapping("/enrollments/pending")
// public String viewPendingEnrollments(Model model) {
//     List<Enrollment> pendingEnrollments = enrollmentRepository.findByStatus("PENDING");
//     model.addAttribute("pendingEnrollments", pendingEnrollments);
//     return "admin_pending_enrollments";
// }

// // 2. View enrollment details
// @GetMapping("/enrollments/view/{id}")
// public String viewEnrollmentDetails(@PathVariable Long id, Model model) {
//     Enrollment enrollment = enrollmentRepository.findById(id)
//         .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    
//     // Get patient details
//     Patient patient = enrollment.getPatient();
    
//     model.addAttribute("enrollment", enrollment);
//     model.addAttribute("patient", patient);
//     model.addAttribute("dentalPlan", enrollment.getDentalPlan());
    
//     // Get dependents if any
//     // You'll need to create a DependentRepository for this
    
//     return "admin_enrollment_details";
// }

// // 3. Approve enrollment
// @PostMapping("/enrollments/approve/{id}")
// public String approveEnrollment(@PathVariable Long id) {
//     Enrollment enrollment = enrollmentRepository.findById(id)
//         .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    
//     // Update status to ACTIVE
//     enrollment.setStatus("ACTIVE");
//     enrollmentRepository.save(enrollment);
    
//     // Optional: Send notification to patient
//     // enrollmentService.sendEnrollmentApprovalEmail(enrollment);
    
//     return "redirect:/api/auth/admin/enrollments/pending";
// }

// // 4. Bulk approve enrollments (approve multiple at once)
// @PostMapping("/enrollments/bulk-approve")
// public String bulkApproveEnrollments(@RequestParam List<Long> enrollmentIds) {
//     for (Long id : enrollmentIds) {
//         Enrollment enrollment = enrollmentRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
//         enrollment.setStatus("ACTIVE");
//         enrollmentRepository.save(enrollment);
//     }
    
//     return "redirect:/api/auth/admin/enrollments/pending";
// }

// // 5. Reject enrollment (optional but recommended)
// @PostMapping("/enrollments/reject/{id}")
// public String rejectEnrollment(@PathVariable Long id, @RequestParam String reason) {
//     Enrollment enrollment = enrollmentRepository.findById(id)
//         .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    
//     enrollment.setStatus("REJECTED");
//     // You might want to add a rejectionReason field to Enrollment bean
//     enrollmentRepository.save(enrollment);
    
//     return "redirect:/api/auth/admin/enrollments/pending";
// }




@Autowired
private com.example.caredent.repository.DentalProcedureRepository dentalProcedureRepository;

@PostMapping("/claims/{id}/approve")
public String approveClaim(@PathVariable Long id) {
    Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
    Patient patient = claim.getPatient();

    // Find enrollment for patient
    Enrollment enrollment = enrollmentRepository.findByPatientId(patient.getId())
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));

    double totalInsurancePaid = 0.0;
    double totalPatientResponsibility = 0.0;
    double totalDeductibleApplied = 0.0;

    // Loop through claim lines
    for (ClaimLine line : claim.getClaimLines()) {
        // Find procedure
        DentalProcedure procedure = dentalProcedureRepository.findByProcedureCode(line.getProcedureCode())
                .orElseThrow(() -> new RuntimeException("Procedure not found"));

        // Find coverage rule
        PlanCoverageRule rule = planCoverageRuleRepository.findByDentalPlanIdAndProcedureCategory(
                enrollment.getDentalPlan().getId(), procedure.getCategory())
                .orElseThrow(() -> new RuntimeException("Coverage rule not found"));

        double coveragePercentage = rule.getCoveragePercentage();
        double insurancePaid = line.getAmount() * coveragePercentage / 100;
        double patientResponsibility = line.getAmount() - insurancePaid;

        // Deductible logic (simplified: apply deductible until exhausted)
        double deductibleApplied = 0.0;
        if (enrollment.getDeductibleUsed() < enrollment.getDentalPlan().getDeductible()) {
            double remainingDeductible = enrollment.getDentalPlan().getDeductible() - enrollment.getDeductibleUsed();
            deductibleApplied = Math.min(patientResponsibility, remainingDeductible);
            enrollment.setDeductibleUsed(enrollment.getDeductibleUsed() + deductibleApplied);
            patientResponsibility += deductibleApplied; // patient pays deductible
        }

        // Update claim line
        line.setInsurancePaid(insurancePaid);
        line.setPatientResponsibility(patientResponsibility);
        line.setDeductibleApplied(deductibleApplied);

        totalInsurancePaid += insurancePaid;
        totalPatientResponsibility += patientResponsibility;
        totalDeductibleApplied += deductibleApplied;
    }

    // Update claim
    claim.setClaimStatus("APPROVED");
    claim.setApprovalDate(new java.util.Date());
    claim.setInsurancePaid(totalInsurancePaid);
    claim.setPatientResponsibility(totalPatientResponsibility);

    // Update enrollment annual max
    enrollment.setAnnualMaxUsed(enrollment.getAnnualMaxUsed() + totalInsurancePaid);

    // Save everything
    enrollmentRepository.save(enrollment);
    claimRepository.save(claim);

    return "redirect:/api/auth/admin/claims"; // Redirect back to claims list
}




   
    @Autowired
    AdminClaimQueueService claimQueueService;
    @Autowired
    ClaimRepository claimRepository;


    @GetMapping("/claims")
    public String claimsQueue(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<ClaimQueueItemDTO> pending = claimQueueService.getPendingClaims(page, size);
        model.addAttribute("pendingClaims", pending.getContent());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", pending.getTotalPages());
        model.addAttribute("pendingCount", claimRepository.countByClaimStatus("PENDING"));
        return "claims-queue"; // Thymeleaf template
    }

   
}

    

