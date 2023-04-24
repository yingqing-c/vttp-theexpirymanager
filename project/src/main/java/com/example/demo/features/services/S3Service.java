package com.example.demo.features.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger logger = LogManager.getLogger(S3Service.class);
    @Autowired
    private AmazonS3 s3Client;

    private final String endpoint = "https://theexpirymanager.sgp1.digitaloceanspaces.com/";
    private final String BUCKET = "theexpirymanager";

    public String upload(MultipartFile file, String username, String title) throws IOException {

        // User data
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("uploadTime", (new Date()).toString());
        userData.put("originalFilename", file.getOriginalFilename());

        // Metadata of the file
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setUserMetadata(userData);

        String key = UUID.randomUUID().toString().substring(0, 8);

        // Create a put request
        PutObjectRequest putReq = new PutObjectRequest(
                BUCKET, // bucket name
                key, //key
                file.getInputStream(), //inputstream
                metadata);

        // Allow public access
        putReq.withCannedAcl(CannedAccessControlList.PublicRead);

        // upload object to s3 storage
        s3Client.putObject(putReq);

        return endpoint + key;
    }

    public byte[] getImage(String username, String fileName) {
        try {
            GetObjectRequest getReq = new GetObjectRequest(BUCKET, fileName);
            S3Object result = s3Client.getObject(getReq);
            ObjectMetadata metadata = result.getObjectMetadata();
            Map<String, String> userData = metadata.getUserMetadata();
            if (!userData.get("username").equals(username)) {
                // this user is not allowed to access this image
                return null;
            }
            try (S3ObjectInputStream is = result.getObjectContent()) {
                byte[] buffer = is.readAllBytes();
                return buffer;
            } catch (IOException e) {
                logger.error("Could not read object content", e);
                e.printStackTrace();
            }
            return null;
        } catch (AmazonS3Exception ex) {
            // key not found
            logger.error("Could not find " + fileName, ex);
        } catch (Exception ex) {
            logger.error("getImage failed with error.", ex);
        }
        return null;
    }

    public void deleteImg(String imageUrl) {
        String key = imageUrl.replace(endpoint, "");
        DeleteObjectRequest delReq = new DeleteObjectRequest(BUCKET, key);
        s3Client.deleteObject(delReq);
    }
}
