package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialImpl;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteImpl;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileAlreadyExitsException;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FileController {

    private FileService fileService;
    private UserMapper userMapper;
    private NoteService noteService;
    private CredentialsService credentialsService;

    public FileController(FileService noteService, UserMapper userMapper, NoteService noteService1, CredentialsService credentialsService) {
        this.fileService = noteService;
        this.userMapper = userMapper;
        this.noteService = noteService1;
        this.credentialsService = credentialsService;
    }

    @PostMapping("/files")
    public String uploadFile(Model model, @RequestParam("fileUpload") MultipartFile file, Authentication authentication,
                             @ModelAttribute("note") NoteImpl note, @ModelAttribute("credential") CredentialImpl credentialImpl) {
        String username = authentication.getName();
        int userId = userMapper.getUser(username).getUserId();
        if (!file.isEmpty()) {
            File fileObj = new File();
            fileObj.setContenttype(file.getContentType());
            fileObj.setFilename(file.getOriginalFilename());
            fileObj.setUserid(userId);
            fileObj.setFilesize(file.getSize() + "");

            /**Try file saving */
            try {
                fileObj.setFiledata(file.getBytes());
                fileService.createFile(fileObj);
                model.addAttribute("success", "File saved Successfully");

                /** Check if file is already in the db*/

            } catch (FileAlreadyExitsException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "File already exists!");

                /** Check if any error occurs*/

            } catch (IOException e) {
                model.addAttribute("errorMessage", "Unknown error!");
            }
        } else {  //if file is empty
            model.addAttribute("errorMessage", "File is empty");

        }

        model.addAttribute("tab", "files/tab");

        /**add all model attribute to file model also*/

        model.addAttribute("files", fileService.getUserFiles(userId));
        model.addAttribute("notes", noteService.getUserNotes(userId));
        model.addAttribute("credentials", credentialsService.getUserCredentials(userId));

        return "home";
    }
    /**Help from friend who already passed this project*/
    @RequestMapping(value = {"/files/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<byte[]> viewFile(@PathVariable(name = "id") String id,
                                           HttpServletResponse response, HttpServletRequest request) {
        File objOfFile = fileService.getFileById(Integer.parseInt(id));
        byte[] fileContents = objOfFile.getFiledata();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(objOfFile.getContenttype()));
        String fileName = objOfFile.getFilename();
        httpHeaders.setContentDispositionFormData(fileName, fileName);
        httpHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> serverResponse = new ResponseEntity<byte[]>(fileContents, httpHeaders, HttpStatus.OK);
        return serverResponse;
    }

    @RequestMapping(value = "files/delete/{id}")
    private String deleteFile(@PathVariable(name = "id") String id, RedirectAttributes redirectAttributes) {
        System.out.println("deleteFile" + id);
        fileService.deleteFile(Integer.parseInt(id));
        redirectAttributes.addFlashAttribute("tab", "files/tab");
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/home";
    }
}
