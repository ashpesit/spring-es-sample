package com.saisonomni.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Component
@ConfigurationProperties(prefix = "elastic", ignoreInvalidFields = true)
@Data
public class ElasticDataTypeMapping {

    private List<String> integerFields;
    private List<String> longFields;
    private List<String> stringFields;
    private List<String> floatFields;
    private List<String> dateFields;

}