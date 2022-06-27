package com.smart.contactManager.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contactManager.dao.UserRepository;
import com.smart.contactManager.entities.User;
import com.smart.contactManager.helper.Message;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model){
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
    public String registerUser(@ModelAttribute("user") User user, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
        try {
            if(!agreement){
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("Please accept the Terms and Conditions.");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            System.out.println("Agreement "+agreement);
            System.out.println("USER "+user);
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
    @RequestMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new User());
        return "login";
    }
}
