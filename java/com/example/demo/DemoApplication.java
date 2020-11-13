package com.example.demoS3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.AmazonClientException;
import com.example.demoS3.controller.ClientS3;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
	
		ClientS3 client = new ClientS3();
		client.s3Client = client.s3client();
		
	    String keyName = "a.pptx";
	    String filePath = "C:\\Users\\N129592\\Downloads\\a.pptx";
	    String filepathDowload = "C:\\Users\\N129592\\Downloads\\b.pptx";
	    
        try {
        	
			client.uploadDownloadMultiPartFile(keyName, filePath, filepathDowload);
			
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	//	SpringApplication.run(DemoApplication.class, args);
		
		
	}

}
