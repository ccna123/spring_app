package com.example.spring_app.config;

import java.util.Map;
import org.springframework.context.annotation.Bean;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

public class SSMConfig {

    @Bean
    AWSSimpleSystemsManagement ssmClient(){
        return AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getTenantDbConfig(String tenantId) throws IOException, JsonMappingException, JsonProcessingException{
        GetParameterRequest request = new GetParameterRequest().withName("/" + tenantId + "dbconfig").withWithDecryption(true);
        GetParameterResult result = ssmClient().getParameter(request);

        String parameterValue = result.getParameter().getValue();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(parameterValue, Map.class);
    }
}