package com.saisonomni.poc.elastic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "#{@indexName}", type = "_doc")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileFoodFacility extends AbstractEntity{

    @JsonProperty("locationid")
    @Field(type = FieldType.Long, name = "locationid")
    private Long locationId;

    @JsonProperty("Applicant")
    @Field(type = FieldType.Text, name = "Applicant")
    private String applicant;

    @JsonProperty("FacilityType")
    @Field(type = FieldType.Text, name = "FacilityType")
    private String facilityType;

    @JsonProperty("cnn")
    @Field(type = FieldType.Long, name = "cnn")
    private Long cnn;

    @JsonProperty("LocationDescription")
    @Field(type = FieldType.Text, name = "LocationDescription")
    private String locationDescription;

    @JsonProperty("Address")
    @Field(type = FieldType.Text, name = "Address")
    private String address;

    @JsonProperty("blocklot")
    @Field(type = FieldType.Text, name = "blocklot")
    private String blockLot;

    @JsonProperty("block")
    @Field(type = FieldType.Text, name = "block")
    private String block;

    @Field(type = FieldType.Text, name = "lot")
    private String lot;

    @Field(type = FieldType.Text, name = "permit")
    private String permit;

    @JsonProperty("Status")
    @Field(type = FieldType.Text, name = "Status")
    private String status;

    @JsonProperty("FoodItems")
    @Field(type = FieldType.Text, name = "FoodItems")
    private String foodItems;

    @JsonProperty("X")
    @Field(type = FieldType.Double, name = "X")
    private Double x;

    @JsonProperty("Y")
    @Field(type = FieldType.Double, name = "Y")
    private Double y;

    @JsonProperty("Latitude")
    @Field(type = FieldType.Text, name = "Latitude")
    private Double latitude;

    @JsonProperty("Longitude")
    @Field(type = FieldType.Text, name = "Longitude")
    private Double longitude;

    @JsonProperty("Schedule")
    @Field(type = FieldType.Text, name = "Schedule")
    private String schedule;

    @Field(type = FieldType.Text, name = "dayshours")
    private String dayshours;

    @JsonProperty("NOISent")
    @Field(type = FieldType.Text, name = "NOISent")
    private String nOISent;

    @JsonProperty("Approved")
    @Field(type = FieldType.Text, name = "Approved")
    private String approved;

    @JsonProperty("Received")
    @Field(type = FieldType.Text, name = "Received")
    private Integer received;

    @JsonProperty("PriorPermit")
    @Field(type = FieldType.Text, name = "PriorPermit")
    private Integer priorPermit;

    @JsonProperty("ExpirationDate")
    @JsonFormat(pattern="MM/dd/yyyy H:mm:ss")
    @Field(type = FieldType.Text, name = "ExpirationDate")
    private Date expirationDate;

    @JsonProperty("Location")
    @Field(type = FieldType.Text, name = "Location")
    private String location;

    @JsonProperty("FirePreventionDistricts")
    @Field(type = FieldType.Text, name = "FirePreventionDistricts")
    private Integer firePreventionDistricts;

    @JsonProperty("PoliceDistricts")
    @Field(type = FieldType.Text, name = "PoliceDistricts")
    private Integer policeDistricts;

    @JsonProperty("SupervisorDistricts")
    @Field(type = FieldType.Text, name = "SupervisorDistricts")
    private Integer supervisorDistricts;

    @JsonProperty("ZipCodes")
    @Field(type = FieldType.Text, name = "ZipCodes")
    private Integer zipCodes;

    @JsonProperty("Neighborhoods")
    @Field(type = FieldType.Text, name = "Neighborhoods")
    private Integer neighborhoods;

    @JsonProperty("Distance")
    @Transient
    private Double distance;
}
