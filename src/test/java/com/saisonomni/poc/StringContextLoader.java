package com.saisonomni.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.saisonomni.poc.utils.Constants.*;
import static io.restassured.RestAssured.given;

@Log4j2
@CucumberContextConfiguration
@SpringBootTest(classes = InterviewApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class StringContextLoader {
    @Autowired
    private ObjectMapper objectMapper;
    private static final String PROFILE_FILE_LOCATION = "./src/test/resources/automation/profile/";
    @PostConstruct
    public void restAssuredSetup() {
        String env = System.getProperty("environment");
        if (StringUtils.isBlank(env))
            env = "default";
        String baseConfigPath = PROFILE_FILE_LOCATION + env + "/server-info.json";
        try (BufferedReader br = new BufferedReader(new FileReader(baseConfigPath))) {
            HashMap<String,Object> serverInfo = objectMapper.readValue(br, HashMap.class);
            if (serverInfo.containsKey("uri") && serverInfo.get("uri") != null)
                RestAssured.baseURI = serverInfo.get("uri").toString();
            if (serverInfo.containsKey("port") && serverInfo.get("port") != null)
                RestAssured.port = Integer.parseInt(serverInfo.get("port").toString());
        } catch (IOException e) {
            log.error("unable to load server configuration: going with default configuration");
        }
    }

    public <T> T  execute(Object body,String endPoint,Map<String,String> params,Map<String,String> header,Class<T> clazz,String method) throws IOException {
        RequestSpecification builder = given().log().all();
        for(Map.Entry<String,String> entry : header.entrySet())
            builder=builder.header(entry.getKey(),entry.getValue());
        for(Map.Entry<String,String> entry : params.entrySet())
            builder=builder.params(entry.getKey(),entry.getValue());
        switch (method){
            case POST: return builder.body(body).when().post(endPoint).as(clazz);
            case GET: return builder.when().get(endPoint).as(clazz);
            case PATCH: return builder.body(body).when().patch(endPoint).as(clazz);
            case PUT: return builder.body(body).when().put(endPoint).as(clazz);
            case DELETE: return builder.when().delete(endPoint).as(clazz);
        }
        return null;
    }
}