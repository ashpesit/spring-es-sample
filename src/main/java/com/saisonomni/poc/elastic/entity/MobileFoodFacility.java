package com.saisonomni.poc.elastic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

@Document(indexName = "foodtruck",type = "_doc")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class MobileFoodFacility extends AbstractEntity{

    @JsonProperty("locationid")
    @Field(type = FieldType.Long, name = "locationid")
    public Long locationId;

    @JsonProperty("Applicant")
    @Field(type = FieldType.Text, name = "locationid")
    public String applicant;

    @JsonProperty("FacilityType")
    @Field(type = FieldType.Text, name = "FacilityType")
    public String facilityType;

    @JsonProperty("cnn")
    @Field(type = FieldType.Long, name = "cnn")
    public Long cnn;

    @JsonProperty("LocationDescription")
    @Field(type = FieldType.Text, name = "LocationDescription")
    public String locationDescription;

    @JsonProperty("Address")
    @Field(type = FieldType.Text, name = "Address")
    public String address;

    @JsonProperty("blocklot")
    @Field(type = FieldType.Text, name = "blocklot")
    public String blockLot;

    @JsonProperty("block")
    @Field(type = FieldType.Text, name = "block")
    public String block;

    @Field(type = FieldType.Text, name = "lot")
    public String lot;

    @Field(type = FieldType.Text, name = "permit")
    public String permit;

    @JsonProperty("Status")
    @Field(type = FieldType.Text, name = "Status")
    public String status;

    @JsonProperty("FoodItems")
    @Field(type = FieldType.Text, name = "FoodItems")
    public String foodItems;

    @JsonProperty("X")
    @Field(type = FieldType.Double, name = "X")
    public Double x;

    @JsonProperty("Y")
    @Field(type = FieldType.Double, name = "Y")
    public Double y;

    @JsonProperty("Latitude")
    @Field(type = FieldType.Text, name = "Latitude")
    public Double latitude;

    @JsonProperty("Longitude")
    @Field(type = FieldType.Text, name = "Longitude")
    public Double longitude;

    @JsonProperty("Schedule")
    @Field(type = FieldType.Text, name = "Schedule")
    public String schedule;

    @Field(type = FieldType.Text, name = "dayshours")
    public String dayshours;

    @JsonProperty("NOISent")
    @Field(type = FieldType.Text, name = "NOISent")
    public String nOISent;

    @JsonProperty("Approved")
    @Field(type = FieldType.Text, name = "Approved")
    public String approved;

    @JsonProperty("Received")
    @Field(type = FieldType.Text, name = "Received")
    public Integer received;

    @JsonProperty("PriorPermit")
    @Field(type = FieldType.Text, name = "PriorPermit")
    public Integer priorPermit;

    @JsonProperty("ExpirationDate")
    @JsonFormat(pattern="MM/dd/yyyy H:mm:ss")
    @Field(type = FieldType.Text, name = "ExpirationDate")
    public Date expirationDate;

    @JsonProperty("Location")
    @Field(type = FieldType.Text, name = "Location")
    public String location;

    @JsonProperty("FirePreventionDistricts")
    @Field(type = FieldType.Text, name = "FirePreventionDistricts")
    public Integer firePreventionDistricts;

    @JsonProperty("PoliceDistricts")
    @Field(type = FieldType.Text, name = "PoliceDistricts")
    public Integer policeDistricts;

    @JsonProperty("SupervisorDistricts")
    @Field(type = FieldType.Text, name = "SupervisorDistricts")
    public Integer supervisorDistricts;

    @JsonProperty("ZipCodes")
    @Field(type = FieldType.Text, name = "ZipCodes")
    public Integer zipCodes;

    @JsonProperty("Neighborhoods")
    @Field(type = FieldType.Text, name = "Neighborhoods")
    public Integer neighborhoods;

    @JsonProperty("Distance")
    @Transient
    public Double distance;
}
