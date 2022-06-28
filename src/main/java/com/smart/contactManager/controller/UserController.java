package com.smart.contactManager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.contactManager.dao.UserRepository;
import com.smart.contactManager.entities.Contact;
import com.smart.contactManager.entities.User;

import javassist.CtNewConstructor;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String userName = principal.getName();
        System.out.println(userName);
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);
    }
    @GetMapping("/index")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User DashBoard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String addContact(Model model, Principal principal){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, Principal principal){
        String name=principal.getName();
        
        User user=userRepository.getUserByUserName(name );
        contact.setUser(user);
        
        user.getContact().add(contact);
        this.userRepository.save(user);
        return "normal/add_contact_form";
    }
    
}
