
// package com.example.caredent.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.example.caredent.repository.ClaimRepository;
// import com.example.caredent.repository.DentalPlanRepository;
// import com.example.caredent.repository.EnrollmentRepository;

// @Controller
// @RequestMapping("/api/auth/admin")
// public class AdminController {

//     @Autowired
//     private ClaimRepository claimRepository;

//     @Autowired
//     private DentalPlanRepository dentalPlanRepository;

//     @Autowired
//     private EnrollmentRepository enrollmentRepository;

//     // Dashboard view
//     @GetMapping("/dashboard")
//     public String getDashboard(Model model) {
//         // Fetch relevant data for the admin dashboard
//         long totalClaims = claimRepository.count();
//         long totalEnrollments = enrollmentRepository.count();
//         //long totalPayouts = claimRepository.getTotalPayouts(); // Assume a method for summing payouts
        
//         model.addAttribute("totalClaims", totalClaims);
//         model.addAttribute("totalEnrollments", totalEnrollments);
//        // model.addAttribute("totalPayouts", totalPayouts);
        
//         return "admin_dashboard"; // Thymeleaf template for Admin Dashboard
//     }

//     // Manage Plans
//     @GetMapping("/plans")
//     public String managePlans(Model model) {
//         model.addAttribute("plans", dentalPlanRepository.findAll());
//         return "admin_plans"; // Thymeleaf template for managing plans
//     }
 
//     // Manage Claims
//     @GetMapping("/claims")
//     public String manageClaims(Model model) {
//         model.addAttribute("claims", claimRepository.findAll());
//         return "admin_claims"; // Thymeleaf template for managing claims
//     }

//     // Other methods for handling plan creation, claim approval/rejection, etc.
// }
package com.example.caredent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.DentistNetwork;
import com.example.caredent.bean.Network;
import com.example.caredent.bean.PlanCoverageRule;
import com.example.caredent.bean.Role;
import com.example.caredent.bean.User;
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.DentistNetworkRepository;
import com.example.caredent.repository.NetworkRepository;
import com.example.caredent.repository.PlanCoverageRuleRepository;
import com.example.caredent.repository.RoleRepository;
import com.example.caredent.repository.UserRepository;

@Controller
@RequestMapping("/api/auth/admin")
public class AdminController {

    @Autowired
    private DentalPlanRepository dentalPlanRepository;

    @Autowired
    private PlanCoverageRuleRepository planCoverageRuleRepository;

    @Autowired
    private UserRepository userRepository;
  

    // Dashboard view
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        // Fetch relevant data for the admin dashboard
        long totalPlans = dentalPlanRepository.count();
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


    
}

    

