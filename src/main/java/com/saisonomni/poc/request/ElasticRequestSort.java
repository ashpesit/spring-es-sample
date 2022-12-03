package com.saisonomni.poc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saisonomni.poc.enums.ElasticOperationsEnum;
import com.saisonomni.poc.enums.ElasticSortEnum;
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
public class ElasticRequestSort {

    @NotNull
    @NotEmpty
    private String key;

    @NotNull
    private ElasticSortEnum sortType;

    private List<Double> value;

}
