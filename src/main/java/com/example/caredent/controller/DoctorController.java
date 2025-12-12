// package com.example.caredent.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import com.example.caredent.bean.DentalProcedure;
// import com.example.caredent.bean.User;
// import com.example.caredent.repository.DentalPlanRepository;
// import com.example.caredent.repository.DentalProcedureRepository;

// @Controller
// @RequestMapping("api/auth/doctor")
// public class DoctorController {

//     // ------------------ OVERVIEW ------------------
//     // @GetMapping("/dashboard")
//     // public String overview(Model model) {
//     //     // Dummy values
//     //     model.addAttribute("procedureCount", 10);
//     //     model.addAttribute("claimCount", 5);
//     //     model.addAttribute("approvedClaims", 3);
//     //     return "overview"; // loads overview.html
//     // }
//     @GetMapping("/dashboard")
// public String overview(Model model) {
//     model.addAttribute("procedureCount", 15);
//     model.addAttribute("claimCount", 12);
//     model.addAttribute("approvedClaims", 9);

//     // Add user for topbar
//     User dummyUser = new User();
//     dummyUser.setUsername("Dummy Doctor");
//     model.addAttribute("user", dummyUser);

//     return "overview";
// }


//     // ------------------ PROFILE ------------------
//     @GetMapping("/profile")
//     public String profile(Model model) {
//         // Dummy user
//         model.addAttribute("user", "Dr. Dummy User");
//         return "profile"; // loads profile.html
//     }

//     @Autowired
//     DentalPlanRepository dentalPlanRepository;

//     @Autowired
//     DentalProcedureRepository procedureRepository;

//     // ------------------ PLAN ------------------
//     @GetMapping("/plan")
//     public String plan(Model model) {
//         // Dummy plan
//         model.addAttribute("assignedPlanName", "PPO Premier");
//         model.addAttribute("deductible", 200);
//         model.addAttribute("annualMax", 1500);
//         return "plan"; // loads plan.html
//     }

//       @GetMapping("/manageProcedures")
//     public String manageProcedures(Model model) {
//         model.addAttribute("procedures", procedureRepository.findAll());
//         return "manageProcedures"; // loads manageProcedures.html
//     }

//     @PostMapping("/addProcedure")
//     public String addProcedure(@RequestParam String procedureCode,
//                                @RequestParam String description,
//                                @RequestParam String category) {
//         DentalProcedure procedure = new DentalProcedure();
//         procedure.setProcedureCode(procedureCode);
//         procedure.setDescription(description);
//         procedure.setCategory(category);
//         procedureRepository.save(procedure);
//         return "redirect:/doctor/manageProcedures";
//     }

//     @PostMapping("/deleteProcedure/{id}")
//     public String deleteProcedure(@PathVariable Long id) {
//         procedureRepository.deleteById(id);
//         return "redirect:/doctor/manageProcedures";
//     }

//     // // ------------------ MANAGE PROCEDURES ------------------
//     // @GetMapping("/manageProcedures")
//     // public String manageProcedures(Model model) {
//     //     // Dummy list
//     //     model.addAttribute("procedures", java.util.Arrays.asList(
//     //             new Object[]{"D1110", "Prophylaxis", "Preventive", 100},
//     //             new Object[]{"D1206", "Fluoride Varnish", "Preventive", 80}
//     //     ));
//     //     return "manageProcedures"; // loads manageProcedures.html
//     // }

//     // @PostMapping("/addProcedure")
//     // public String addProcedure() {
//     //     // Dummy action
//     //     return "redirect:/doctor/manageProcedures";
//     // }

//     // @PostMapping("/deleteProcedure/{id}")
//     // public String deleteProcedure(@PathVariable Long id) {
//     //     // Dummy action
//     //     return "redirect:/doctor/manageProcedures";
//     // }

//     // ------------------ SUBMIT CLAIM ------------------
//     @GetMapping("/claimForm")
//     public String claimForm(Model model) {
//         // Dummy patients & procedures
//         model.addAttribute("patients", java.util.Arrays.asList("John", "Mary"));
//         model.addAttribute("procedures", java.util.Arrays.asList("D1110", "D1206"));
//         return "claimForm"; // loads claimForm.html
//     }

//     @PostMapping("/submitClaim")
//     public String submitClaim(@RequestParam Long patientId,
//                               @RequestParam Long procedureId) {
//         // Dummy action
//         return "redirect:/doctor/trackClaims";
//     }

//     // ------------------ TRACK CLAIMS ------------------
//     @GetMapping("/trackClaims")
//     public String trackClaims(Model model) {
//         // Dummy claims
//         model.addAttribute("claims", java.util.Arrays.asList(
//                 new Object[]{"101", "John", "2025-12-01", "Pending", 300, 50},
//                 new Object[]{"102", "Mary", "2025-12-02", "Approved", 400, 20}
//         ));
//         return "trackClaims"; // loads trackClaims.html
//     }
// }

package com.example.caredent.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.DentistNetwork;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.PlanCoverageRule;
import com.example.caredent.bean.User;
import com.example.caredent.repository.ClaimRepository;
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.DentalProcedureRepository;
import com.example.caredent.repository.DentistNetworkRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.PlanCoverageRuleRepository;
import com.example.caredent.service.ClaimService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/auth/doctor")
public class DoctorController {

    @Autowired
    private DentalPlanRepository dentalPlanRepository;

    @Autowired
    private DentalProcedureRepository procedureRepository;

    /** Dashboard: show doctor overview */
    @GetMapping("/dashboard")
    public String overview(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login"; // force login if no session
        }

        // Example KPIs
        model.addAttribute("procedureCount", procedureRepository.count());
        model.addAttribute("claimCount", 12);
        model.addAttribute("approvedClaims", 9);

        model.addAttribute("user", user); // for topbar
        return "overview"; // Thymeleaf template
    }

    /** Profile page */
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }




    @Autowired 
    private DentistNetworkRepository dentistNetworkRepo;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @GetMapping("/plan")
    public String myPlan(Model model, HttpSession session) {
        User dentist = (User) session.getAttribute("loggedInUser");
        if (dentist == null) {
            return "redirect:/api/auth/login";
        }

        // Find the plan assigned to this dentist
        Optional<DentistNetwork> dnOpt = dentistNetworkRepo.findByDentist(dentist);
        DentalPlan plan = dnOpt.map(DentistNetwork::getDentalPlan).orElse(null);

        List<Enrollment> activeEnrollments = new ArrayList<>();
        if (plan != null) {
            activeEnrollments = enrollmentRepo.findByDentalPlanAndStatus(plan, "ACTIVE");
        }

        model.addAttribute("plan", plan);
        model.addAttribute("enrollments", activeEnrollments);
        model.addAttribute("user", dentist);

        return "myPlan";
    }




@Autowired 
private PlanCoverageRuleRepository planCoverageRuleRepo;

@GetMapping("/manageProcedures")
public String manageProcedures(Model model, HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        return "redirect:/api/auth/login";
    }

    // Find dentist's plan
    Optional<DentistNetwork> dnOpt = dentistNetworkRepo.findByDentist(user);
    DentalPlan plan = dnOpt.map(DentistNetwork::getDentalPlan).orElse(null);

    // Get categories from PlanCoverageRule for this plan
    List<PlanCoverageRule> coverageRules = new ArrayList<>();
    if (plan != null) {
        coverageRules = planCoverageRuleRepo.findByDentalPlan(plan);
    }

    model.addAttribute("procedures", procedureRepository.findAll());
    model.addAttribute("categories", coverageRules); // pass categories to view
    model.addAttribute("user", user);

    return "manageProcedures";
}

@PostMapping("/addProcedure")
public String addProcedure(@RequestParam String procedureCode,
                           @RequestParam String description,
                           @RequestParam String category,
                           @RequestParam Double standardFee,
                           HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        return "redirect:/api/auth/login";
    }

    DentalProcedure procedure = new DentalProcedure();
    procedure.setProcedureCode(procedureCode);
    procedure.setDescription(description);
    procedure.setCategory(category); // selected from dropdown
    procedure.setStandardFee(standardFee);
    procedure.setDentist(user); // set the dentist who added this procedure
    procedureRepository.save(procedure);
    

    return "redirect:/api/auth/doctor/manageProcedures";
}


    @PostMapping("/deleteProcedure/{id}")
    public String deleteProcedure(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        procedureRepository.deleteById(id);
        return "redirect:/api/auth/doctor/manageProcedures";
    }


    @Autowired 
    private ClaimService claimService;
    @Autowired 
    private PatientRepository patientRepo;
    @Autowired 
    private DentalProcedureRepository procedureRepo;
    @Autowired 
    private ClaimRepository claimRepo;

    // /** Show Claim Form */
    // @GetMapping("/claimForm")
    // public String showClaimForm(Model model, HttpSession session) {
    //     User user = (User) session.getAttribute("loggedInUser");
    //     if (user == null) {
    //         return "redirect:/api/auth/login";
    //     }

    //     model.addAttribute("patients", patientRepo.findAll());
    //     model.addAttribute("procedures", procedureRepo.findAll());
    //     model.addAttribute("user", user);
    //     return "claimForm";
    // }


@GetMapping("/claimForm")
public String showClaimForm(Model model, HttpSession session) {
    User dentist = (User) session.getAttribute("loggedInUser");
    if (dentist == null) {
        return "redirect:/api/auth/login";
    }

    // Find dentist's plan
    Optional<DentistNetwork> dnOpt = dentistNetworkRepo.findByDentist(dentist);
    DentalPlan plan = dnOpt.map(DentistNetwork::getDentalPlan).orElse(null);

    List<Patient> activePatients = new ArrayList<>();
    if (plan != null) {
        List<Enrollment> enrollments = enrollmentRepo.findByDentalPlanAndStatus(plan, "ACTIVE");
        for (Enrollment e : enrollments) {
            activePatients.add(e.getPatient());
        }
    }

    model.addAttribute("patients", activePatients);
    model.addAttribute("procedures", procedureRepo.findByDentist(dentist)); // dentist’s own CDT codes
    model.addAttribute("user", dentist);

    return "claimForm";
}



    /** Submit Claim */
    @PostMapping("/submitClaim")
    public String submitClaim(@RequestParam Long patientId,
                              @RequestParam List<Long> procedureIds,
                              HttpSession session) {
        User dentist = (User) session.getAttribute("loggedInUser");
        if (dentist == null) {
            return "redirect:/api/auth/login";
        }

        Patient patient = patientRepo.findById(patientId).orElseThrow();
        List<DentalProcedure> procedures = procedureRepo.findAllById(procedureIds);

        claimService.submitClaim(patient, dentist, procedures);
        return "redirect:/doctor/trackClaims";
    }



    /** Track Claims */
    @GetMapping("/trackClaims")
    public String trackClaims(Model model, HttpSession session) {
        User dentist = (User) session.getAttribute("loggedInUser");
        if (dentist == null) {
            return "redirect:/api/auth/login";
        }

        model.addAttribute("claims", claimRepo.findByDentist(dentist));
        model.addAttribute("user", dentist);
        return "doctor/trackClaims";
    }
}






