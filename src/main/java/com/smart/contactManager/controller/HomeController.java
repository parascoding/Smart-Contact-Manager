package com.smart.contactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.contactManager.dao.UserRepository;
import com.smart.contactManager.entities.User;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/home")
    public String home(Model model){
        model.addAttribute("title", "Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        // model.addAttribute("title", "Smart Contact Manager");
        return "about";
    }
    
}
