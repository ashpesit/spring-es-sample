package com.saisonomni.poc;

import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.response.BaseResponse;
import com.saisonomni.poc.response.CustomSearchResponse;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ScenarioScope
public class ScenarioContext {
    private BaseResponse response;
    private MobileFoodFacility foodFacility;
    private CustomSearchResponse<?> searchResponse;
    private String id;
    private Map<String,String> genericMapObject;
}