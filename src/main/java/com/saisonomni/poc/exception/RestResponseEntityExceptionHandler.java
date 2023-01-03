package com.saisonomni.poc.exception;


import com.saisonomni.poc.enums.ElasticOperationsEnum;
import com.saisonomni.poc.response.BaseResponse;
import com.saisonomni.poc.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        log.error("inside controller advice of IllegalArgumentException & IllegalStateException",ex);
        String message="Invalid Input";
        BaseResponse bodyOfResponse=new BaseResponse(400,message);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        return handleExceptionInternal(ex, bodyOfResponse, responseHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("inside controller advice of HttpMessageNotReadableException",ex);
        String message="Invalid Input";
        if(ex.getLocalizedMessage().contains(ElasticOperationsEnum.class.getSimpleName()))
            message="Invalid Operator Key. Acceptable values are NOT_IN, GEO_DISTANCE, IN, GROUP_BY, GTE, NOT_EQUALS, EQ, LTE, NOT_EMPTY, LIKE";
        BaseResponse bodyOfResponse=new BaseResponse(400,message);
        headers.set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        return this.handleExceptionInternal(ex, bodyOfResponse, headers, status, request);
    }
}