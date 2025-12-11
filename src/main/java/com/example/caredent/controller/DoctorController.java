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

import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.User;
import com.example.caredent.repository.DentalPlanRepository;
import com.example.caredent.repository.DentalProcedureRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    /** Assigned plan */
    @GetMapping("/plan")
    public String plan(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // Dummy plan values for now
        model.addAttribute("assignedPlanName", "PPO Premier");
        model.addAttribute("deductible", 200);
        model.addAttribute("annualMax", 1500);
        model.addAttribute("user", user);

        return "plan";
    }

    /** Manage procedures */
    @GetMapping("/manageProcedures")
    public String manageProcedures(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        model.addAttribute("procedures", procedureRepository.findAll());
        model.addAttribute("user", user);
        return "manageProcedures";
    }

    @PostMapping("/addProcedure")
    public String addProcedure(@RequestParam String procedureCode,
                               @RequestParam String description,
                               @RequestParam String category,
                               HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        DentalProcedure procedure = new DentalProcedure();
        procedure.setProcedureCode(procedureCode);
        procedure.setDescription(description);
        procedure.setCategory(category);
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

    /** Claim submission form */
    @GetMapping("/claimForm")
    public String claimForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        model.addAttribute("patients", java.util.Arrays.asList("John", "Mary"));
        model.addAttribute("procedures", java.util.Arrays.asList("D1110", "D1206"));
        model.addAttribute("user", user);

        return "claimForm";
    }

    @PostMapping("/submitClaim")
    public String submitClaim(@RequestParam Long patientId,
                              @RequestParam Long procedureId,
                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        // TODO: implement claim persistence
        return "redirect:/api/auth/doctor/trackClaims";
    }

    /** Track claims */
    @GetMapping("/trackClaims")
    public String trackClaims(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/api/auth/login";
        }

        model.addAttribute("claims", java.util.Arrays.asList(
                new Object[]{"101", "John", "2025-12-01", "Pending", 300, 50},
                new Object[]{"102", "Mary", "2025-12-02", "Approved", 400, 20}
        ));
        model.addAttribute("user", user);

        return "trackClaims";
    }
}
