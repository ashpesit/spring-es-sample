package com.saisonomni.poc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saisonomni.poc.enums.ElasticSortEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticRequestQueries {

    @Valid
    @ApiModelProperty("list of boolean condition to be applied in es query")
    private List<ElasticRequestQuery> p;
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
