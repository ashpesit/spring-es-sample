package com.saisonomni.poc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticRequestQueries {

    @Valid
    @ApiModelProperty("list of boolean condition to be applied in es query")
    private List<ElasticRequestQuery> queries;
    @ApiModelProperty("no of documents to be returned")
    private Integer limit;
    @ApiModelProperty("page no of the request")
    private Integer pageNo;
    @ApiModelProperty("list of fields to be returned in the response")
    private List<String> fields;


    @Valid
    @ApiModelProperty
    private List<ElasticRequestSort> sorts;

}
