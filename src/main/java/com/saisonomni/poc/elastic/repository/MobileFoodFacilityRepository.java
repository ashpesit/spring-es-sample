package com.saisonomni.poc.elastic.repository;

import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileFoodFacilityRepository extends ElasticsearchRepository<MobileFoodFacility,String> {
}
