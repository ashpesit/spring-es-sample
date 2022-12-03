package com.saisonomni.poc.elastic.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

@Data
public class AbstractEntity {
    @Id
    @ReadOnlyProperty
    private String id;
}
