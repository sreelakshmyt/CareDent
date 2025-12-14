package com.example.caredent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    // This method is mapped directly to the root path /logout
    // // as it does NOT have a class-level @RequestMapping.
    // @GetMapping("/logout")
    // public String logout(HttpSession session) {
    //     // Invalidate the entire session, clearing all stored attributes (like "loggedInUser")
    //     session.invalidate(); 
        
    //     // Redirect back to the login page defined in AuthController.
    //     return "redirect:/api/auth/login";
    // }
}
