package com.example.demo.features.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Bean
    public AmazonS3 createS3Client() {
        BasicAWSCredentials cred = new BasicAWSCredentials(accessKey, secretKey);
        AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(
                "sgp1.digitaloceanspaces.com", "sgp1");
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(ep)
                .withCredentials(new AWSStaticCredentialsProvider(cred))
                .build();
    }
}
