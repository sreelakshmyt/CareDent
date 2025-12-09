// package com.example.caredent.controller;




// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.caredent.bean.Role;
// import com.example.caredent.bean.User;
// import com.example.caredent.dto.LoginDto;
// import com.example.caredent.dto.UserDto;
// import com.example.caredent.repository.RoleRepository;
// import com.example.caredent.repository.UserRepository;

// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private RoleRepository roleRepository;

//     // Removed BCryptPasswordEncoder since we're using plain-text passwords

//     @PostMapping("/register")
//     public String register(@RequestBody UserDto userDto) {
//         // Check if the email already exists
//         if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
//             return "Email is already taken";
//         }

//         // Create a new user and assign the provided details
//         User user = new User();
//         user.setUsername(userDto.getUsername());
//         user.setPassword(userDto.getPassword());  // Directly using the plain-text password
//         user.setEmail(userDto.getEmail());

//         // Assign a default role (patient for now)
//         Role role = roleRepository.findByName("Patient")
//                                   .orElseThrow(() -> new RuntimeException("Role not found"));
//         user.setRole(role);

//         // Save the user
//         userRepository.save(user);
//         return "Registration successful";
//     }

//     @PostMapping("/login")
//     public String login(@RequestBody LoginDto loginDto) {
//         // Find the user by email
//         User user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);

//         if (user == null) {
//             return "User not found";
//         }

//         // Compare the plain-text password (without BCrypt)
//         if (!user.getPassword().equals(loginDto.getPassword())) {
//             return "Incorrect password";
//         }

//         // Role-based redirection logic
//         String redirectUrl = "";
//         switch (user.getRole().getName()) {
//             case "Admin":
//                 redirectUrl = "/admin/dashboard";  // Redirect to Admin dashboard
//                 break;
//             case "Patient":
//                 redirectUrl = "/patient/dashboard";  // Redirect to Patient dashboard
//                 break;
//             case "Dentist":
//                 redirectUrl = "/dentist/dashboard";  // Redirect to Dentist dashboard
//                 break;
//             default:
//                 redirectUrl = "/";  // Default to home if role is undefined
//                 break;
//         }

//         return "Login success! Redirecting to: " + redirectUrl;
//     }
// }




package com.example.caredent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.caredent.bean.Role;
import com.example.caredent.bean.User;
import com.example.caredent.dto.LoginDto;
import com.example.caredent.dto.UserDto;
import com.example.caredent.repository.RoleRepository;
import com.example.caredent.repository.UserRepository;

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


// @PostMapping("/register")
// public String register(@ModelAttribute UserDto userDto) {
//     // Check if the email is already taken
//     if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
//         return "Email is already taken";
//     }

//     // Create a new user and set details
//     User user = new User();
//     user.setUsername(userDto.getUsername());
//     user.setPassword(userDto.getPassword());  // Ideally hash this
//     user.setEmail(userDto.getEmail());

//     // Assign default role (patient for now)
//     Role role = roleRepository.findByName("Patient")
//                               .orElseThrow(() -> new RuntimeException("Role not found"));
//     user.setRole(role);

//     // Save the user to the database
//     userRepository.save(user);
//     return "Registration successful";
// }

    // Serve the login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // This will look for login.html in the templates folder
    }

    // Handle the login form submission
    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        // Find the user by email
        User user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);

        if (user == null) {
            return "User not found";
        }

        // Check if the password matches
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return "Incorrect password";
        }

        return "Login success!";
    }
}
