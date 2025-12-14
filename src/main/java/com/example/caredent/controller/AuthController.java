package com.example.caredent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.caredent.bean.Role;
import com.example.caredent.bean.User;
import com.example.caredent.dto.LoginDto;
import com.example.caredent.dto.UserDto;
import com.example.caredent.repository.RoleRepository;
import com.example.caredent.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Serve the Login/Register page
    @GetMapping("/login_register")
    public String loginRegisterPage() {
        return "login_register1";  // This will look for login_register.html in the templates folder
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute UserDto userDto) {
        // Check if the email is already taken
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            // Note: You should handle this via Model/RedirectAttributes to show an error message in the UI.
            return "redirect:/api/auth/register?error=EmailTaken"; 
        }

        // Create a new user and set details
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());  // WARNING: Use password hashing (e.g., BCrypt) in production!
        user.setEmail(userDto.getEmail());

        // Assign default role (patient for now)
        Role role = roleRepository.findByName("Patient")
                                  .orElseThrow(() -> new RuntimeException("Role 'Patient' not found in database."));
        user.setRole(role);

        // Save the user to the database
        userRepository.save(user);

        // Redirect to login page after successful registration
        return "redirect:/api/auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto());  // Add a new UserDto to the model for form binding
        return "register";  // This will look for register.html in the templates folder
    }

    @GetMapping("/login")  // Make sure this matches your login endpoint
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());  // Add this line
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, HttpSession session) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);

        if (user == null || !user.getPassword().equals(loginDto.getPassword())) {
            return "redirect:/api/auth/login?error";
        }

        // Save logged-in user in session
        session.setAttribute("loggedInUser", user);

        switch (user.getRole().getName()) {
            case "Admin":
                return "redirect:/api/auth/admin/dashboard";
            case "Dentist": // Assuming 'Doctor' role is named 'Dentist' in the database based on your switch case
                return "redirect:/api/auth/doctor/dashboard";
            case "Patient":
                return "redirect:/patient/dashboard";
            default:
                return "redirect:/api/auth/login";
        }
    }

    // ===================================================
    // NEW: LOGOUT METHOD
    // Handles the request from th:href="@{/logout}"
    // ===================================================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the entire session, clearing the "loggedInUser" attribute
        session.invalidate(); 
        
        // Redirect back to the login page as requested
        return "redirect:/api/auth/login";
    }
}