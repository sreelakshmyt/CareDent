



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
        return "Email is already taken";
    }

    // Create a new user and set details
    User user = new User();
    user.setUsername(userDto.getUsername());
    user.setPassword(userDto.getPassword());  // Ideally hash this password
    user.setEmail(userDto.getEmail());

    // Assign default role (patient for now)
    Role role = roleRepository.findByName("Patient")
                              .orElseThrow(() -> new RuntimeException("Role not found"));
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



    // Serve the login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // This will look for login.html in the templates folder
    }
//     @PostMapping("/login")
// public String login(@ModelAttribute LoginDto loginDto) {
//     // Find the user by email
//     User user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);

//     if (user == null) {
//         return "User not found";
//     }

//     // Check if the password matches
//     if (!user.getPassword().equals(loginDto.getPassword())) {
//         return "Incorrect password";
//     }
//     Role userRole = user.getRole();
//       switch (userRole.getName()) {
//         case "Admin":
//             return "redirect:/api/auth/admin/dashboard";  // Redirect to Admin Dashboard
//         case "Doctor":
//             return "redirect:/api/auth/doctor/dashboard";  // Redirect to Doctor Dashboard
//         case "Patient":
//             return "redirect:/patient/dashboard"; // Redirect to Patient Dashboard
//         default:
//             return "redirect:/api/auth/login";  // Redirect to login if the role is not found
//     }

//}
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
        case "Doctor":
            return "redirect:/api/auth/doctor/dashboard";
        case "Patient":
            return "redirect:/patient/dashboard";
        default:
            return "redirect:/api/auth/login";
    }
}


}
