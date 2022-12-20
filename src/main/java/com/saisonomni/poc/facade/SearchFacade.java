package com.saisonomni.poc.facade;

import com.saisonomni.poc.elastic.entity.AbstractEntity;
import com.saisonomni.poc.request.ElasticRequestQueries;
import com.saisonomni.poc.response.CustomSearchResponse;

public interface SearchFacade {
    public <T extends AbstractEntity> CustomSearchResponse<T> search(ElasticRequestQueries requestQueries, Class<T> clazz, String indexName);
}
