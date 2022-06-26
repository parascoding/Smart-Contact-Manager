package com.smart.contactManager.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Smart Contact Manager");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Registration - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // For registering the user
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
        try {
            if(!agreement){
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception();
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            System.out.println("Agreement "+agreement);
            System.out.println("USER "+user);
            userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Succesfully Registered", "Alert-Success"));
            return "signup";    
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went Wrong"+e.getMessage(), "Alert-Error"));
            return "signup";
        }
        
    }
}
