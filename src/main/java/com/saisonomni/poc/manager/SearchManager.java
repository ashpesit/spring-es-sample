package com.saisonomni.poc.manager;

import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.enums.ElasticOperationsEnum;
import com.saisonomni.poc.enums.ElasticSortEnum;
import com.saisonomni.poc.facade.SearchFacade;
import com.saisonomni.poc.request.ElasticRequestQueries;
import com.saisonomni.poc.request.ElasticRequestQuery;
import com.saisonomni.poc.request.ElasticRequestSort;
import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.ConfigUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.util.SloppyMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Search manager is for fulfilling all the search requirement of this project.</br>
 * It makes use of search facade to execute the search.
 */
@Log4j2
@Service
public class SearchManager {

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private SearchFacade searchFacade;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");

    /**
     * Generic Search api for searching mobile food facility index.</br>
     * Calls facade class to execute the actual search</br>
     *
     * @param requestQueries generic search request object.
     * @return list of search result
     */
    public CustomSearchResponse<MobileFoodFacility> searchMobileFoodFacility(ElasticRequestQueries requestQueries) {
        return searchFacade.search(requestQueries, MobileFoodFacility.class, configUtils.getIndexName());
    }

    /**
     * Search by name of applicant in mobile food facility index.
     *
     * @param applicantName applicant name
     * @param page          page no
     * @param fetchLimit    fetch limit
     * @return list of search result
     */
    public CustomSearchResponse<MobileFoodFacility> searchByApplicantName(String applicantName, Integer page, Integer fetchLimit) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        requestQueries.setLimit(fetchLimit);
        requestQueries.setPageNo(page);
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQuery.setKey(configUtils.getApplicantFieldName());
        requestQuery.setOperator(ElasticOperationsEnum.EQ);
        requestQuery.setValue(Collections.singletonList(applicantName));
        requestQueries.setQueries(Collections.singletonList(requestQuery));
        return searchMobileFoodFacility(requestQueries);
    }

    /**
     * Search by expiration date, to find whose permits have expired/not expired
     *
     * @param expiredFlag ture for expired facility; false for not expired facility
     * @param page        page no
     * @param fetchLimit  fetch limit
     * @return list of search result
     */
    public CustomSearchResponse<MobileFoodFacility> searchExpiredFacility(Boolean expiredFlag, Integer page, Integer fetchLimit) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQueries.setLimit(fetchLimit);
        requestQueries.setPageNo(page);
        requestQuery.setKey(configUtils.getExpirationDateFieldName());
        if (expiredFlag)
            requestQuery.setOperator(ElasticOperationsEnum.LTE);
        else
            requestQuery.setOperator(ElasticOperationsEnum.GTE);
        requestQuery.setValue(Collections.singletonList(dateFormat.format(new Date())));
        requestQueries.setQueries(Collections.singletonList(requestQuery));
        return searchMobileFoodFacility(requestQueries);
    }

    /**
     * Search by street name. This uses wild-card search, hence if partial street name should give some result
     *
     * @param name       name of the street
     * @param page       page no
     * @param fetchLimit fetch limit
     * @return list of search result
     */
    public CustomSearchResponse<MobileFoodFacility> searchByStreetNameLike(String name, Integer page, Integer fetchLimit) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQueries.setLimit(fetchLimit);
        requestQueries.setPageNo(page);
        requestQuery.setKey(configUtils.getLocationDescriptionFieldName());
        requestQuery.setOperator(ElasticOperationsEnum.LIKE);
        List<ElasticRequestQuery> list = new ArrayList<>();
        requestQuery.setValue(Collections.singletonList(name));
        list.add(requestQuery);
        requestQuery = new ElasticRequestQuery();
        requestQuery.setKey(configUtils.getAddressFieldName());
        requestQuery.setOperator(ElasticOperationsEnum.LIKE);
        requestQuery.setValue(Collections.singletonList(name));
        list.add(requestQuery);
        requestQueries.setQueries(list);
        return searchMobileFoodFacility(requestQueries);
    }

    /**
     * Given a delivery location, find out the closest truck possible.
     *
     * @param lat        latitude
     * @param lon        longitude
     * @return
     */
    public CustomSearchResponse<MobileFoodFacility> searchByGeoDistance(Double lat, Double lon) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestSort requestSort = new ElasticRequestSort();
        List<Double> values = new ArrayList<>();
        values.add(lat);
        values.add(lon);
        requestSort.setValue(values);
        requestSort.setSortType(ElasticSortEnum.GEO_DISTANCE_ASC);
        requestSort.setKey(configUtils.getLocationFieldName());
        requestQueries.setSorts(Collections.singletonList(requestSort));
        requestQueries.setLimit(4);
        CustomSearchResponse<MobileFoodFacility> wrapper = searchMobileFoodFacility(requestQueries);
        for (MobileFoodFacility m : wrapper.getDocuments()) {
            m.setDistance(SloppyMath.haversinMeters(lat, lon, m.getLatitude(), m.getLongitude()));
        }
        return wrapper;
    }
}

