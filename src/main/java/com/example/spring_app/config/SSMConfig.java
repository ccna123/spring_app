package com.example.spring_app.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

@Configuration
public class SSMConfig {

    final Logger logger = LoggerFactory.getLogger(SSMConfig.class);

    @Bean
    SsmClient ssmClient() {
        return SsmClient.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String getParameterValue(String parameterName) {
        try {
            SsmClient ssmClient = ssmClient();
            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(true)
                    .build();
            
            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            return parameterResponse.parameter().value();
        } catch (SsmException e) {
            return "Error: " + e.getMessage();
        }
    }

    public Map<String, String> getParameterByPath(String path){
        Map<String, String> parameters = new HashMap<>();

        try {
            String nextToken = null;

            do{
                GetParametersByPathRequest request = GetParametersByPathRequest
                .builder()
                .path(path)
                .recursive(true)
                .withDecryption(true)
                .nextToken(nextToken)
                .build();

                GetParametersByPathResponse response = ssmClient().getParametersByPath(request);
                List<Parameter> parameterList = response.parameters(); 

                for (Parameter parameter : parameterList) {
                    parameters.put(parameter.name(), parameter.value());
                }

                nextToken = response.nextToken();

            }while(nextToken != null);
        } catch (SsmException e) {
            logger.error("Error fetching parameters by path: {}", e.getMessage());
        }
        return parameters;
    }
}