package com.example.demoS3.Controller;


import java.io.File;
import java.time.LocalTime;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.AwsHostNameUtils;



@Component
public class ClientS3 {

	private  String accessKey;
	private String secretKey;
	private String region;
	private String endpointUrl;
	public AmazonS3 s3Client;
	private String bucketName;
	// A nivel de bucket se activa el cifrado en reposo, y en este caso particular el versionado
	private String DEFAULT;

	
	
public ClientS3(){
	
	 accessKey="SYZ1JFJUOGOSV5BGGYA3";
	 secretKey="fxicSN84OkEqr9gHwU6HWVsaKwwzjGrhhs7bjgmo";
	 region="boaw";
	 endpointUrl="http://s3.boaw.cloudstorage.corp";
	 bucketName="hola";
	 DEFAULT="";

}



/**
* Amazon client
* @return respuesta s3
*/

public AmazonS3 s3client() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        // A nivel de cliente se activa el grado de paralelismo tanto en subidas como bajadas
        ClientConfiguration conf = new ClientConfiguration()
                                        .withMaxConnections(10)
                                        .withSocketTimeout(3600_000);
        
        String region =   AwsHostNameUtils.parseRegion(endpointUrl, AmazonS3Client.S3_SERVICE_NAME);
        		 
        
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                                        .withEndpointConfiguration(new EndpointConfiguration(endpointUrl, region))
                                        .withClientConfiguration(conf)
                                        .build();
        
            
        return s3Client;
}

//

public void uploadDownloadMultiPartFile(String keyName,String filePath,String filepathDowload) throws AmazonClientException, InterruptedException
{
try {

        // A nivel de servidor se activa el tamaño de los bloques a subir/bajar
        // en este caso 10MB, con un grado de paralelismo de 10 threads
        // Se ha ajustado a un tamaño mas grande de 200 MB en el caso de remesas
        // Se observa que tanto para subida y bajadas, en fichero de 1 GB se ajusta
        // a todo el ancho de banda del que dispongo, por lo que el software funciona
        // muy bien, el cuello de botella está en el ancho de banda de OHE
        
        
TransferManager tm = TransferManagerBuilder.standard()
        .withMinimumUploadPartSize(Long.valueOf(10*1024*1024))
        .withMultipartUploadThreshold(Long.valueOf(10*1024*1024))
        .withExecutorFactory(() -> Executors.newFixedThreadPool(10))
.withS3Client(s3Client)
.build();


// TransferManager processes all transfers asynchronously,
// so this call returns immediately.

System.out.println("Object upload started: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());


PutObjectRequest por = new PutObjectRequest(bucketName, keyName, new File(filePath));
por.withCannedAcl(CannedAccessControlList.PublicRead);
Upload upload=tm.upload(por);




// Optionally, wait for the upload to finish before continuing.
upload.waitForCompletion();


//s3Client.setObjectAcl(bucketName, keyName,CannedAccessControlList.PublicRead);
System.out.println("Object upload complete: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());



File f = new File(filepathDowload);
System.out.println("Object download started: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());
Download download = tm.download(bucketName, keyName, f);

download.waitForCompletion();
System.out.println("Object download complete: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());

// Para chequeart el versionado de documento comentar esta línea de código
// ya que el borrado del documento implica el borrado de todas las versiones

//if (deleteFileFromS3(keyName)) System.out.println("Object delete complete: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());
//else System.out.println("Object delete error: "+"Bucket: "+ bucketName +" File: "+ keyName +" Time: "+LocalTime.now());


tm.shutdownNow();

//
} catch (AmazonServiceException e) {
// The call was transmitted successfully, but Amazon S3 couldn't process 
// it, so it returned an error response.
e.printStackTrace();
} catch (SdkClientException e) {
// Amazon S3 couldn't be contacted for a response, or the client
// couldn't parse the response from Amazon S3.
e.printStackTrace();
}

}

public Boolean deleteFileFromS3(String fileName) {
        if (DEFAULT.equals(fileName)) {
                        System.out.println("El documento no dispone de ningún fichero");
                        return Boolean.TRUE;
        } else {
                        //System.out.println("Fichero a borrar: "+ fileName);
                        if (this.s3Client.doesBucketExist(bucketName)
                                                        && this.s3Client.doesObjectExist(bucketName, fileName)) {
                                        this.s3Client.deleteObject(bucketName, fileName);
                                        return Boolean.TRUE;
                        } else {
                                        System.out.println("The file path does not exist!");
                                        return Boolean.FALSE;
                        }
        }


}

	
}
