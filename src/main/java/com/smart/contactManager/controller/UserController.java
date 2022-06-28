package com.smart.contactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.security.Timestamp;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.coyote.ContainerThreadMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smart.contactManager.dao.UserRepository;
import com.smart.contactManager.entities.Contact;
import com.smart.contactManager.entities.User;
import com.smart.contactManager.helper.Message;

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
    public String processContact(@Valid @ModelAttribute Contact contact, BindingResult resuBindingResult, @RequestParam("profileImage") MultipartFile file, Principal principal, Model model,HttpSession session){
        try {
            String name=principal.getName();
            
            // Processing and uploading file
            User user=userRepository.getUserByUserName(name);
            if(resuBindingResult.hasErrors()){
                model.addAttribute("contact",contact);
                return "normal/add_contact_form";
            }   
            if(file.isEmpty()){
                System.out.println("File Empty");
            } else{
                
                String fileName = user.getId()+"_"+contact.getPhone()+file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
                contact.setImage(fileName);
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image Uploaded");
            }
            session.setAttribute("message", new Message("Your Contact is Added", "success"));
            contact.setUser(user);
            user.getContact().add(contact);
            this.userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EROOR: "+e.getMessage());
            session.setAttribute("message", new Message("Something went worng ! Try Again", "danger"));

        }
        return "normal/add_contact_form";    
        
    }
    
}
