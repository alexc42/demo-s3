package com.example.demoS3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.AmazonClientException;
import com.example.demoS3.Controller.ClientS3;

@SpringBootApplication
public class demoS3 {

	public static void main(String[] args) {
		//SpringApplication.run(ApiSharepointApplication.class, args);
		
		ClientS3 client = new ClientS3();
		client.s3Client = client.s3client();
		
	    String keyName = "AD-SER-001.docx";
	    String filePath = "C:\\Users\\N129592\\Downloads\\AD-SER-001.docx";
	    String filepathDowload = "C:\\Users\\N129592\\Downloads\\AD-SER-001.docx";
	    
        try {
        	
			client.uploadDownloadMultiPartFile(keyName, filePath, filepathDowload);
			
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

