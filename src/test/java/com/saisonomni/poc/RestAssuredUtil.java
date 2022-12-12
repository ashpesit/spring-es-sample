package com.saisonomni.poc;

import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.Map;

import static com.saisonomni.poc.utils.Constants.*;
import static io.restassured.RestAssured.given;

public class RestAssuredUtil {
    public static  <T> T  execute(Object body, String endPoint, Map<String,String> params, Map<String,String> header, Class<T> clazz, String method) {
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
