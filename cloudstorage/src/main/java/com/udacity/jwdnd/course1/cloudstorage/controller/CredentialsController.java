package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialImpl;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteImpl;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CredentialsController {

    private FileService fileService;
    private UserMapper userMapper;
    private NoteService noteService;
    private CredentialsService credentialsService;


    public CredentialsController(FileService fileService, UserMapper userMapper, NoteService noteService,
                                 CredentialsService credentialsService) {
        this.fileService = fileService;
        this.userMapper = userMapper;
        this.noteService = noteService;
        this.credentialsService = credentialsService;
    }

    @PostMapping("/credentials")
    public String addCredential(@ModelAttribute("credential") CredentialImpl credentialImpl, @ModelAttribute("note") NoteImpl note, Model model, Authentication authentication) {
        String username = authentication.getName();
        int userId = userMapper.getUser(username).getUserId();
        Credential objOfCredentials = new Credential();
        objOfCredentials.setUserid(userId);
        objOfCredentials.setUsername(credentialImpl.getUsername());
        objOfCredentials.setUrl(credentialImpl.getUrl());
        objOfCredentials.setUnencodedPassword(credentialImpl.getPassword());

        if (credentialImpl.getId() != null && !credentialImpl.getId().isBlank()) {
            objOfCredentials.setCredentialid(Integer.parseInt(credentialImpl.getId()));
            credentialsService.updateCredential(objOfCredentials);
        } else {
            credentialsService.createCredential(objOfCredentials);
        }

        model.addAttribute("success", true);
        model.addAttribute("tab", "credentials/tab");
        /**add all model attribute to credential model also*/

        model.addAttribute("credentials", credentialsService.getUserCredentials(userId));
        model.addAttribute("notes", noteService.getUserNotes(userId));
        model.addAttribute("files", fileService.getUserFiles(userId));
        return "home";
    }


    @RequestMapping(value = "credentials/delete/{id}")
    private String deleteCredential(@PathVariable(name = "id") String id, RedirectAttributes redirectAttributes){
        System.out.println("deleteCredential: " + id);
        credentialsService.deleteCredential(Integer.parseInt(id));
        redirectAttributes.addFlashAttribute("tab", "credentials/tab");
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/home";
    }


}
