package com.example.demoS3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonClientException;

@RestController
public class ClientS3Controller {
	
	@Autowired
    ClientS3 clientS3;

	 @GetMapping("/add")
	 public String addFileS3()
	    {
		 
		 
		    String keyName = "tema2fisica.pdf";
		    String filePath = "C:\\proyectos\\demo\\";
		    String filepathDowload = "C:\\proyectos\\demo\\down";
		    
	        try {
	        	
				clientS3.uploadDownloadMultiPartFile(keyName, filePath, filepathDowload);
				
			} catch (AmazonClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        return " Fichero " + keyName + "  subido a " + filePath;
	    }
}
