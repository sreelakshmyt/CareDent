

// package com.example.caredent.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.example.caredent.service.EnrollmentService;

// @Controller
// @RequestMapping("/api/auth/admin/manageenrollment")
// public class EnrollmentController {

//     private final EnrollmentService service;

//     public EnrollmentController(EnrollmentService service) {
//         this.service = service;
//     }

//     @GetMapping
//     public String viewEnrollments(Model model) {
//         model.addAttribute("pendingEnrollments", service.getPendingEnrollments());
//         model.addAttribute("acceptedEnrollments", service.getAcceptedEnrollments());
//         model.addAttribute("rejectedEnrollments", service.getRejectedEnrollments());
//         return "manage-enrollment"; // Thymeleaf template
//     }

//     @PostMapping("/accept/{id}")
//     public String acceptEnrollment(@PathVariable Long id) {
//         service.updateStatus(id, "ACCEPTED");
//         return "redirect:/api/auth/admin/manageenrollment";
//     }

//     @PostMapping("/reject/{id}")
//     public String rejectEnrollment(@PathVariable Long id) {
//         service.updateStatus(id, "REJECTED");
//         return "redirect:/api/auth/admin/manageenrollment";
//     }

// }