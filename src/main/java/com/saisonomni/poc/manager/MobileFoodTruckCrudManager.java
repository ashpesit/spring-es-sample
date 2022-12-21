package com.saisonomni.poc.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisonomni.poc.elastic.repository.MobileFoodFacilityRepository;
import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.exception.InvalidRequestException;
import com.saisonomni.poc.exception.RecordNotFoundException;
import com.saisonomni.poc.response.BaseResponse;
import com.saisonomni.poc.response.CustomCrudResponse;
import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.ConfigUtils;
import com.saisonomni.poc.utils.Constants;
import com.saisonomni.poc.utils.NullAwareBeanUtilsBean;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;

/**
 * This class manages crud request for food trucks
 */
@Log4j2
@Service
public class MobileFoodTruckCrudManager {
    @Autowired
    public MobileFoodFacilityRepository mobileFoodFacilityRepository;

    @Autowired
    public NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    @Autowired
    public ConfigUtils configUtils;

    @Autowired
    public RestHighLevelClient restHighLevelClient;

    @Autowired
    public ObjectMapper mapper;


    public BaseResponse createDocument(MobileFoodFacility mobileFoodFacilityRequest) {
        try {
            IndexRequest indexRequest = new IndexRequest(configUtils.getIndexName()).type("_doc");
            String json=mapper.writeValueAsString(mobileFoodFacilityRequest);
            log.info("request object = {}",json);
            indexRequest.source(json, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest,
                    RequestOptions.DEFAULT);
            return new CustomCrudResponse(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE, indexResponse.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseResponse readDocument(String id) {
        Optional<MobileFoodFacility> op = mobileFoodFacilityRepository.findById(id);
        if (!op.isPresent())
            throw new RecordNotFoundException("document with id " + id + " does not exist");
        else {
            CustomSearchResponse<MobileFoodFacility> response = new CustomSearchResponse<>();
            log.info("doc found {}",op.get());
            response.setDocuments(Collections.singletonList(op.get()));
            return response;
        }
    }

    public BaseResponse updateDocument(MobileFoodFacility mobileFoodFacility) {
        Optional<MobileFoodFacility> op = mobileFoodFacilityRepository.findById(mobileFoodFacility.getId());
        if (!op.isPresent())
            throw new RecordNotFoundException("document with id " + mobileFoodFacility.getId() + " does not exist");
        else {
            MobileFoodFacility m = op.get();
            try {
                nullAwareBeanUtilsBean.copyProperties(m,mobileFoodFacility);
                UpdateRequest request = new UpdateRequest();
                request.index(configUtils.getIndexName());
                request.type("_doc");
                request.id(mobileFoodFacility.getId());
                IndexRequest indexRequest = new IndexRequest(configUtils.getIndexName()).type("_doc");
                String json=mapper.writeValueAsString(m);
                log.info("request object = {}",json);
                indexRequest.source(json, XContentType.JSON);
                request.doc(indexRequest);
                UpdateResponse updateResponse = restHighLevelClient.update(request,
                        RequestOptions.DEFAULT);
                return new CustomCrudResponse(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE, updateResponse.getId());
            } catch (IllegalAccessException | InvocationTargetException |IOException e) {
                throw new InvalidRequestException("could not process the request");
            }
        }

    }

    public BaseResponse deleteDocument(String id) {
        mobileFoodFacilityRepository.deleteById(id);
        return new CustomCrudResponse(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE, id);
    }

}
