package com.smart.contactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.contactManager.dao.UserRepository;

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
        // model.addAttribute("title", "Smart Contact Manager");
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Registration - Smart Contact Manager");
        return "signup";
    }
}
