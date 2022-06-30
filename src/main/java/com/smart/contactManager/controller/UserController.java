package com.smart.contactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smart.contactManager.dao.ContactRepository;
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

    @Autowired
    private ContactRepository contactRepository;

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
        User user=userRepository.getUserByUserName(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("size", user.getContact().size());
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
                contact.setImage("default_img.png");
            } else{
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
                String fileName = user.getId()+"_"+timeStamp+file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
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

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal){
        model.addAttribute("title","Your Contacts");
        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contacts=contactRepository.findContactsByUser(user.getId(),pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("contacts", contacts);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";
    }
    
    // Specific contact detail
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){
    
        Optional<Contact> contactOptional=contactRepository.findById(cId);
        Contact contact=contactOptional.get();

        String userName = principal.getName();
        User user=userRepository.getUserByUserName(userName);
        if(user.getId()!=contact.getUser().getId()){
            return "/error";
        }
        model.addAttribute("contact", contact);
        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable ("cId") Integer cId, Model model, Principal principal
                , HttpSession session){
        Optional<Contact> contactOptional=contactRepository.findById(cId);
        Contact contact=contactOptional.get();
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        user.getContact().remove(contact);
        if(user.getId()!=contact.getUser().getId()){
            return "/error";
        }
        File file = new File(contact.getImage());
        if(file!=null&&!file.getName().equals("default_img.png"))
            file.delete();
        contact.setUser(null);
        contactRepository.delete(contact);
        session.setAttribute("message", new Message("Deleted", "success"));
        userRepository.save(user);
        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title", "Edit Contact");
        Contact contact = contactRepository.findById(cId).get();
        model.addAttribute("contact",contact);
        
        return "normal/updateForm";
    }

    // Update Contact Handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
                        Model model, HttpSession session, Principal principal){
        try {
            System.out.println(contact);
            Contact old=contactRepository.findById(contact.getCId()).get();
            User user=userRepository.getUserByUserName(principal.getName());
            if(!file.isEmpty()){
                // Delete
                File deleteFile = new ClassPathResource("/static/img").getFile();
                File file1 = new File(deleteFile, old.getImage());
                file1.delete();
                // New
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
                String fileName = user.getId()+"_"+timeStamp+file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
                contact.setImage(fileName);
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(fileName);
            } else{
                contact.setImage(old.getImage());
            }
            contact.setUser(user);
            contactRepository.save(contact);
            session.setAttribute("message", new Message("Contact is updated....", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/"+contact.getCId()+"/contact";
    }
}
