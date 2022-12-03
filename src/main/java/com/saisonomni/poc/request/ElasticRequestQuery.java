package com.saisonomni.poc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saisonomni.poc.enums.ElasticOperationsEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class ElasticRequestQuery {

    @NotNull
    @NotEmpty
    @ApiModelProperty("key on which the operation has to be performed")
    private String key;

    @NotNull
    @ApiModelProperty("GTE, LTE, EQ, LIKE etc")
    private ElasticOperationsEnum operator;

    @ApiModelProperty("list of value to be applied in the condition")
    private List<String> value;

}
