package com.saisonomni.poc.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisonomni.poc.config.ElasticDataTypeMapping;
import com.saisonomni.poc.elastic.entity.AbstractEntity;
import com.saisonomni.poc.elastic.entity.MobileFoodFacility;
import com.saisonomni.poc.enums.ElasticOperationsEnum;
import com.saisonomni.poc.enums.ElasticSortEnum;
import com.saisonomni.poc.exception.InvalidRequestException;
import com.saisonomni.poc.request.ElasticRequestQueries;
import com.saisonomni.poc.request.ElasticRequestQuery;
import com.saisonomni.poc.request.ElasticRequestSort;
import com.saisonomni.poc.response.CustomSearchResponse;
import com.saisonomni.poc.utils.ConfigUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.util.SloppyMath;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Log4j2
@Service
public class SearchManager {

    @Autowired
    public ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ElasticDataTypeMapping elasticDataTypeMapping;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    private ConfigUtils configUtils;

    private Set<String> integerFields;

    private Set<String> longFields;

    private Set<String> dateFields;

    private static Set<String> stringFields;

    private static Set<String> floatFields;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");



    @PostConstruct
    void initialize() {
        integerFields = new HashSet<>(elasticDataTypeMapping.getIntegerFields());
        longFields = new HashSet<>(elasticDataTypeMapping.getLongFields());
        stringFields = new HashSet<>(elasticDataTypeMapping.getStringFields());
        floatFields = new HashSet<>(elasticDataTypeMapping.getFloatFields());
        dateFields = new HashSet<>(elasticDataTypeMapping.getDateFields());
    }

    public <T extends AbstractEntity> CustomSearchResponse<T> search(@Nonnull ElasticRequestQueries requestQueries, Class<T> clazz, String indexName) {
        validate(requestQueries);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder(); // It builds into final search query containing all queries, filtering, sorting and sourceFiltering.
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery(); // Default query in-case of empty query in request
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // It contains all queries requested and then it goes in nativeSearchQueryBuilder
        SearchQuery searchQuery; // nativeSearchQueryBuilder builds into search query which we pass to ES

        // All queries requested are iterated and build here according to their data type.
        if (!CollectionUtils.isEmpty(requestQueries.getQueries())) {

            GeoDistanceQueryBuilder geoDistanceQueryBuilder = null;

            Set<String> skipOperations = new HashSet<>();

            Map<String, Set<ElasticRequestQuery>> keyBasedMap = new HashMap<>();
            for (ElasticRequestQuery p : requestQueries.getQueries()) {
                Set<ElasticRequestQuery> set = keyBasedMap.getOrDefault(p.getKey(), new HashSet<>());
                set.add(p);
                keyBasedMap.put(p.getKey(), set);
            }
            for (Map.Entry<String, Set<ElasticRequestQuery>> e : keyBasedMap.entrySet()) {
                String k = e.getKey();
                Set<ElasticRequestQuery> o = e.getValue();
                if (integerFields.contains(k)) {
                    List<Integer> integerValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Integer::parseInt).collect(Collectors.toList());
                    addQuery(boolQueryBuilder, k, o, integerValues);
                } else if (longFields.contains(k)) {
                    List<Long> longValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Long::parseLong).collect(Collectors.toList());
                    addQuery(boolQueryBuilder, k, o, longValues);
                } else if (stringFields.contains(k)) {
                    addQuery(boolQueryBuilder, k, o, o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).collect(Collectors.toList()));
                } else if (floatFields.contains(k)) {
                    List<Float> floatValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Float::parseFloat).collect(Collectors.toList());
                    addQuery(boolQueryBuilder, k, o, floatValues);
                }else if (dateFields.contains(k)) {
                    addQuery(boolQueryBuilder, k, o, o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).collect(Collectors.toList()));
                }
            }
        }
        if (!CollectionUtils.isEmpty(requestQueries.getQueries()))
            queryBuilder = boolQueryBuilder;
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        if (CollectionUtils.isNotEmpty(requestQueries.getSorts())) {
            for (ElasticRequestSort sort : requestQueries.getSorts()) {
                if (sort.getSortType().equals(ElasticSortEnum.ASC))
                    nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sort.getKey()).order(SortOrder.ASC));
                else if(sort.getSortType().equals(ElasticSortEnum.DESC))
                    nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sort.getKey()).order(SortOrder.DESC));
                else if(sort.getSortType().equals(ElasticSortEnum.GEO_DISTANCE_ASC))
                    nativeSearchQueryBuilder.withSort(new GeoDistanceSortBuilder(sort.getKey(),sort.getValue().get(0),sort.getValue().get(1)).unit(DistanceUnit.METERS).order(SortOrder.ASC));
                else if(sort.getSortType().equals(ElasticSortEnum.GEO_DISTANCE_DESC))
                    nativeSearchQueryBuilder.withSort(new GeoDistanceSortBuilder(sort.getKey(),sort.getValue().get(0),sort.getValue().get(1)).unit(DistanceUnit.METERS).order(SortOrder.DESC));
            }
        }

        if (!CollectionUtils.isEmpty(requestQueries.getFields())) {
            FetchSourceFilter sourceFilter = new FetchSourceFilter(requestQueries.getFields().toArray(new String[0]), new String[]{"q", "location"});
            nativeSearchQueryBuilder.withSourceFilter(sourceFilter);

        }
        nativeSearchQueryBuilder.withIndices(indexName);
        nativeSearchQueryBuilder.withPageable(pageRequestUtil(requestQueries.getPageNo(), requestQueries.getLimit(), configUtils.getPaginationLimit()));
        searchQuery = nativeSearchQueryBuilder.build();
        log.info("search query {}", searchQuery.getQuery());
        return elasticsearchOperations.query(searchQuery, searchResponse -> {
            List<T> searchResult = new ArrayList<>();
            if (searchResponse.getHits().getHits().length != 0) {
                for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                    T document;
                    try {
                        document = objectMapper.readValue(searchHit.getSourceAsString(), clazz);
                        document.setId(searchHit.getId());
                        searchResult.add(document);
                    } catch (JsonProcessingException e) {
                        log.error("Error in extract", e);
                    }
                }
            }
            return new CustomSearchResponse<>(searchResult, null, requestQueries.getPageNo(), searchResponse.getHits().getTotalHits());
        });
    }

    public void addQuery(BoolQueryBuilder boolQueryBuilder, String key, Set<ElasticRequestQuery> querySet, List<?> values) {

        if (stringFields.contains(key))
            key = key.concat(".keyword");

        // Query is build for all operators
        QueryBuilder queryBuilder = null;
        Set<ElasticOperationsEnum> operations = querySet.stream().map(ElasticRequestQuery::getOperator).collect(Collectors.toSet());

        if (operations.contains(ElasticOperationsEnum.IN) || operations.contains(ElasticOperationsEnum.NOT_IN))
            queryBuilder = QueryBuilders.termsQuery(key, values);
        else if (operations.contains(ElasticOperationsEnum.LTE) && operations.contains(ElasticOperationsEnum.GTE))
            queryBuilder = QueryBuilders.rangeQuery(key).lte(values.get(0)).gte(values.get(1));
        else if (operations.contains(ElasticOperationsEnum.LTE))
            queryBuilder = QueryBuilders.rangeQuery(key).lte(values.get(0));
        else if (operations.contains(ElasticOperationsEnum.GTE))
            queryBuilder = QueryBuilders.rangeQuery(key).gte(values.get(0));
        else if (operations.contains(ElasticOperationsEnum.EQ) || operations.contains(ElasticOperationsEnum.NOT_EQUALS))
            queryBuilder = QueryBuilders.termQuery(key, values.get(0));
        else if (operations.contains(ElasticOperationsEnum.NOT_EMPTY))
            queryBuilder = QueryBuilders.existsQuery(key);
        else if (operations.contains(ElasticOperationsEnum.LIKE)) {
            if (values.get(0).toString().contains(" "))
                queryBuilder = QueryBuilders.matchPhraseQuery(key.replace(".keyword", ""), values.get(0).toString().toLowerCase());
            else
                queryBuilder = QueryBuilders.wildcardQuery(key.replace(".keyword", ""), "*" + values.get(0).toString().toLowerCase() + "*");
        } else if (operations.contains(ElasticOperationsEnum.GEO_DISTANCE)) {
            queryBuilder = QueryBuilders.geoDistanceQuery(key)
                    .point((Double) values.get(0), (Double) values.get(1))
                    .distance((Double) values.get(0), DistanceUnit.KILOMETERS);
        }

        // For EQ, IN, NOT_EMPTY, LTE, GTE, LIKE we use must operation in our query
        // Also, for inner_hits we add source filer in setFetchSourceContext
        if (operations.contains(ElasticOperationsEnum.EQ) || operations.contains(ElasticOperationsEnum.IN) || operations.contains(ElasticOperationsEnum.NOT_EMPTY) || operations.contains(ElasticOperationsEnum.LTE) || operations.contains(ElasticOperationsEnum.GTE) || operations.contains(ElasticOperationsEnum.LIKE)) {
            boolQueryBuilder.must(queryBuilder);
        }// For NOT_IN and NOT_EQUALS we use mustNot operation as they belong to negative query
        else if (operations.contains(ElasticOperationsEnum.NOT_IN) || operations.contains(ElasticOperationsEnum.NOT_EQUALS)) {
            boolQueryBuilder.mustNot(queryBuilder);
        } else if (operations.contains(ElasticOperationsEnum.GEO_DISTANCE))
            boolQueryBuilder.filter(queryBuilder);
    }

    public void validate(ElasticRequestQueries requestQueries) {
        if (null == requestQueries || CollectionUtils.isEmpty(requestQueries.getQueries()))
            return;
        Map<String, Set<ElasticOperationsEnum>> operationMap = new HashMap<>();

        for (ElasticRequestQuery elasticRequestQuery : requestQueries.getQueries()) {
            String key = elasticRequestQuery.getKey();
            ElasticOperationsEnum operator = elasticRequestQuery.getOperator();
            if (!operator.equals(ElasticOperationsEnum.GROUP_BY)) {
                if (!operationMap.containsKey(key))
                    operationMap.put(key, new HashSet<>());
                else if (operationMap.get(key).contains(operator) || !((operationMap.get(key).contains(ElasticOperationsEnum.GTE) && operator.equals(ElasticOperationsEnum.LTE)) || (operationMap.get(key).contains(ElasticOperationsEnum.LTE) && operator.equals(ElasticOperationsEnum.GTE))))
                    throw new InvalidRequestException("Cannot execute multiple operation, key - " + elasticRequestQuery.getKey());

                operationMap.get(key).add(operator);
                if (!operator.equals(ElasticOperationsEnum.NOT_EMPTY)) {
                    if (CollectionUtils.isEmpty(elasticRequestQuery.getValue())) {
                        throw new InvalidRequestException("Empty value for key-" + key + " and operation-" + operator);
                    }
                }
                if (stringFields.contains(key) && (operationMap.get(key).contains(ElasticOperationsEnum.GTE) || operationMap.get(key).contains(ElasticOperationsEnum.LTE))) {
                    throw new InvalidRequestException("Invalid operation for key " + key);
                }
            }

        }
    }

    private PageRequest pageRequestUtil(Integer page, Integer limit, Integer hardLimit) {
        page = (page != null && page > 0) ? page - 1 : 0;
        limit = (limit != null && limit > 0 && limit <= hardLimit) ? limit : hardLimit;
        return PageRequest.of(page, limit);
    }

    public CustomSearchResponse<MobileFoodFacility> searchMobileFoodFacility(ElasticRequestQueries requestQueries) {
        return search(requestQueries, MobileFoodFacility.class, configUtils.getIndexName());
    }

    public CustomSearchResponse<MobileFoodFacility> searchByApplicantName(String applicantName) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQuery.setKey("Applicant");
        requestQuery.setOperator(ElasticOperationsEnum.EQ);
        requestQuery.setValue(Collections.singletonList(applicantName));
        requestQueries.setQueries(Collections.singletonList(requestQuery));
        return searchMobileFoodFacility(requestQueries);
    }

    public CustomSearchResponse<MobileFoodFacility> searchExpiredFacility(Boolean expiredFlag) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQuery.setKey("ExpirationDate");
        if (expiredFlag)
            requestQuery.setOperator(ElasticOperationsEnum.LTE);
        else
            requestQuery.setOperator(ElasticOperationsEnum.GTE);
        requestQuery.setValue(Collections.singletonList(dateFormat.format(new Date())));
        requestQueries.setQueries(Collections.singletonList(requestQuery));
        return searchMobileFoodFacility(requestQueries);
    }

    public CustomSearchResponse<MobileFoodFacility> searchByStreetNameLike(String name) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestQuery requestQuery = new ElasticRequestQuery();
        requestQuery.setKey("LocationDescription");
        requestQuery.setOperator(ElasticOperationsEnum.LIKE);
        List<ElasticRequestQuery> list=new ArrayList<>();
        requestQuery.setValue(Collections.singletonList(name));
        list.add(requestQuery);
        requestQuery = new ElasticRequestQuery();
        requestQuery.setKey("Address");
        requestQuery.setOperator(ElasticOperationsEnum.LIKE);
        requestQuery.setValue(Collections.singletonList(name));
        list.add(requestQuery);
        requestQueries.setQueries(list);
        return searchMobileFoodFacility(requestQueries);
    }

    public CustomSearchResponse<MobileFoodFacility> searchByGeoDistance(double lat,double log) {
        ElasticRequestQueries requestQueries = new ElasticRequestQueries();
        ElasticRequestSort requestSort=new ElasticRequestSort();
        List<Double> values=new ArrayList<>();
        values.add(lat);
        values.add(log);
        requestSort.setValue(values);
        requestSort.setSortType(ElasticSortEnum.GEO_DISTANCE_ASC);
        requestSort.setKey("Location");
        requestQueries.setSorts(Collections.singletonList(requestSort));
        requestQueries.setLimit(4);
        CustomSearchResponse<MobileFoodFacility> wrapper= searchMobileFoodFacility(requestQueries);
        for(MobileFoodFacility m : wrapper.getDocuments()){
            m.setDistance(SloppyMath.haversinMeters(lat, log, m.latitude, m.longitude));
        }
        return wrapper;
    }
}

