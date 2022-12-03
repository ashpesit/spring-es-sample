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


    @Bean
    public String indexName(){
        return indexName;
    }
}
