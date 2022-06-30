package com.smart.contactManager.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contactManager.dao.UserRepository;
import com.smart.contactManager.entities.User;
import com.smart.contactManager.helper.Message;

@Controller
public class HomeController {   
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model, Principal principal){
        model.addAttribute("title", "Smart Contact Manager");
        
        return "home";
    }
    @GetMapping("/about")
    public String about(Model model){
        return "about";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Registration - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // For registering the user
    @PostMapping(value = "/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,  BindingResult resultBindingResult, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
        try {
            if(!agreement){
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("Please accept the Terms and Conditions.");
            }
            if(resultBindingResult.hasErrors()){
                
                model.addAttribute("user", user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Succesfully Registered", "alert-success"));
            return "signup";    
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went Wrong"+"  "+e.getMessage(), "alert-danger"));
            return "signup";
        }
        
    }


    // Login Handler
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("user", new User());
        return "login";
    }
}
