package com.saisonomni.poc.manager;

import com.saisonomni.poc.elastic.repository.MobileFoodFacilityRepository;
import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.exception.InvalidRequestException;
import com.saisonomni.poc.exception.RecordNotFoundException;
import com.saisonomni.poc.response.BaseResponse;
import com.saisonomni.poc.response.CustomCrudResponse;
import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.Constants;
import com.saisonomni.poc.utils.NullAwareBeanUtilsBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;

/**
 * This c
 */
@Log4j2
@Service
public class CrudManager {
    @Autowired
    public MobileFoodFacilityRepository mobileFoodFacilityRepository;

    @Autowired
    public NullAwareBeanUtilsBean nullAwareBeanUtilsBean;


    public BaseResponse createDocument(MobileFoodFacility mobileFoodFacility){
        MobileFoodFacility v = mobileFoodFacilityRepository.save(mobileFoodFacility);
        return new CustomCrudResponse(Constants.SUCCESS_CODE,Constants.SUCCESS_MESSAGE,v.getId());
    }
    public BaseResponse readDocument(String id){
        Optional<MobileFoodFacility> op = mobileFoodFacilityRepository.findById(id);
        if(!op.isPresent())
            throw new RecordNotFoundException("document with id "+id+" does not exist");
        else{
            CustomSearchResponse<MobileFoodFacility> response=new CustomSearchResponse<>();
            response.setDocuments(Collections.singletonList(op.get()));
            return response;
        }
    }
    public BaseResponse updateDocument(MobileFoodFacility mobileFoodFacility){
        Optional<MobileFoodFacility> op = mobileFoodFacilityRepository.findById(mobileFoodFacility.getId());
        if(!op.isPresent())
            throw new RecordNotFoundException("document with id "+mobileFoodFacility.getId()+" does not exist");
        else{
            MobileFoodFacility m=op.get();
            try {
                nullAwareBeanUtilsBean.copyProperties(m,mobileFoodFacility);
                mobileFoodFacilityRepository.save(m);
                return new CustomCrudResponse(Constants.SUCCESS_CODE,Constants.SUCCESS_MESSAGE,mobileFoodFacility.getId());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidRequestException("could not process the request");
            }
        }

    }

    public BaseResponse deleteDocument(String id){
        mobileFoodFacilityRepository.deleteById(id);
        return new CustomCrudResponse(Constants.SUCCESS_CODE,Constants.SUCCESS_MESSAGE,id);
    }

}
