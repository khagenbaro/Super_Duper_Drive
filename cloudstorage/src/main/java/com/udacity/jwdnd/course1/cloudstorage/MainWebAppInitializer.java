package com.udacity.jwdnd.course1.cloudstorage;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MainWebAppInitializer implements WebApplicationInitializer {
    /**Declaring maximum size of a file to be uploaded*/
    /**Took help from friend*/
    private String TMP_FOLDER = "/tmp"; 
    private int MAX_UPLOAD_SIZE = 5 * 1024 * 1024; 
    
    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        
        ServletRegistration.Dynamic appServlet = sc.addServlet("mvc", new DispatcherServlet(
          new GenericWebApplicationContext()));

        appServlet.setLoadOnStartup(1);
        
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(TMP_FOLDER,
          MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2, MAX_UPLOAD_SIZE / 2);
        
        appServlet.setMultipartConfig(multipartConfigElement);
    }
}