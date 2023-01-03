package com.saisonomni.poc.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Data
public class ConfigUtils {

    @Value("${elastic.indexName}")
    private String indexName;

    @Value("${request.pagination.hard-limit:5}")
    private Integer paginationLimit;

    @Value("${elastic.document.field-name.location-description:LocationDescription}")
    private String locationDescriptionFieldName;

    @Value("${elastic.document.field-name.address:Address}")
    private String addressFieldName;

    @Value("${elastic.document.field-name.location:Location}")
    private String locationFieldName;

    @Value("${elastic.document.field-name.expiration-date:ExpirationDate}")
    private String expirationDateFieldName;

    @Value("${elastic.document.field-name.applicant:Applicant}")
    private String applicantFieldName;

    @Value("${ES_URL}")
    private String esDomain;

    @Value("${ES_USER}")
    private String esUser;

    @Value("${ES_PASS}")
    private String esPass;

    @Bean
    public String indexName(){
        return indexName;
    }
}
