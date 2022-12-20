package com.saisonomni.poc.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisonomni.poc.config.ElasticDataTypeMapping;
import com.saisonomni.poc.elastic.entity.AbstractEntity;
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
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


/**
 * SearchFacade is a facade class that serves as a front-facing contact point for all search requirement and at the same time masking the complex underlying structural code</br>
 * This is to improve the readability of and usability of other search classes; which mainly deals with the fulfilment of actual requirement.</br>
 * This also promotes the single responsibility principle in this project.</br>
 */
@Log4j2
@Service
public class SearchFacadeImpl implements SearchFacade{


    private Set<String> integerFields;

    private Set<String> longFields;

    private Set<String> dateFields;

    private static Set<String> stringFields;

    private static Set<String> floatFields;

    @Autowired
    public ElasticDataTypeMapping elasticDataTypeMapping;

    @Autowired
    public ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    private ConfigUtils configUtils;


    @PostConstruct
    void initialize() {
        integerFields = new HashSet<>(elasticDataTypeMapping.getIntegerFields());
        longFields = new HashSet<>(elasticDataTypeMapping.getLongFields());
        stringFields = new HashSet<>(elasticDataTypeMapping.getStringFields());
        floatFields = new HashSet<>(elasticDataTypeMapping.getFloatFields());
        dateFields = new HashSet<>(elasticDataTypeMapping.getDateFields());
    }


    /**
     * A generic search function that takes in a search request object which has all the information required for search document sorting them etc; in elasticsearch
     *
     * @param requestQueries generic search request object
     * @param clazz          Data type of search result object
     * @param indexName      name of the index where search will be performed
     * @param <T>            list of search result
     * @return
     */
    @Override
    public <T extends AbstractEntity> CustomSearchResponse<T> search(ElasticRequestQueries requestQueries, Class<T> clazz, String indexName) {
        validate(requestQueries);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder(); // It builds into final search query containing all queries, filtering, sorting and sourceFiltering.
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery(); // Default query in-case of empty query in request
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // It contains all queries requested and then it goes in nativeSearchQueryBuilder
        SearchQuery searchQuery; // nativeSearchQueryBuilder builds into search query which we pass to ES

        // All queries requested are iterated and build here according to their data type.
        if (!CollectionUtils.isEmpty(requestQueries.getQueries())) {

            Map<String, Set<ElasticRequestQuery>> keyBasedMap = new HashMap<>();
            int queryListSize=CollectionUtils.size(requestQueries.getQueries());
            for (List<ElasticRequestQuery> pList : requestQueries.getQueries()) {
                BoolQueryBuilder andBoolQueryBuilder = QueryBuilders.boolQuery();
                for (ElasticRequestQuery p : pList) {
                    Set<ElasticRequestQuery> set = keyBasedMap.getOrDefault(p.getKey(), new HashSet<>());
                    set.add(p);
                    keyBasedMap.put(p.getKey(), set);
                }

                for (Map.Entry<String, Set<ElasticRequestQuery>> e : keyBasedMap.entrySet()) {
                    String k = e.getKey();
                    Set<ElasticRequestQuery> o = e.getValue();
                    if (integerFields.contains(k)) {
                        List<Integer> integerValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Integer::parseInt).collect(Collectors.toList());
                        addQuery(andBoolQueryBuilder, k, o, integerValues);
                    } else if (longFields.contains(k)) {
                        List<Long> longValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Long::parseLong).collect(Collectors.toList());
                        addQuery(andBoolQueryBuilder, k, o, longValues);
                    } else if (stringFields.contains(k)) {
                        addQuery(andBoolQueryBuilder, k, o, o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).collect(Collectors.toList()));
                    } else if (floatFields.contains(k)) {
                        List<Float> floatValues = o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).map(Float::parseFloat).collect(Collectors.toList());
                        addQuery(andBoolQueryBuilder, k, o, floatValues);
                    } else if (dateFields.contains(k)) {
                        addQuery(andBoolQueryBuilder, k, o, o.stream().map(ElasticRequestQuery::getValue).flatMap(Collection::stream).collect(Collectors.toList()));
                    }
                }
                if(queryListSize<=1)
                    boolQueryBuilder=andBoolQueryBuilder;
                else {
                    boolQueryBuilder=boolQueryBuilder.should(andBoolQueryBuilder);
                }
            }
            if(queryListSize>1)
                boolQueryBuilder=boolQueryBuilder.minimumShouldMatch(1);
        }
        if (!CollectionUtils.isEmpty(requestQueries.getQueries()))
            queryBuilder = boolQueryBuilder;
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        if (CollectionUtils.isNotEmpty(requestQueries.getSorts())) {
            for (ElasticRequestSort sort : requestQueries.getSorts()) {
                if (sort.getSortType().equals(ElasticSortEnum.ASC))
                    nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sort.getKey()).order(SortOrder.ASC));
                else if (sort.getSortType().equals(ElasticSortEnum.DESC))
                    nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sort.getKey()).order(SortOrder.DESC));
                else if (sort.getSortType().equals(ElasticSortEnum.GEO_DISTANCE_ASC))
                    nativeSearchQueryBuilder.withSort(new GeoDistanceSortBuilder(sort.getKey(), sort.getValue().get(0), sort.getValue().get(1)).unit(DistanceUnit.METERS).order(SortOrder.ASC));
                else if (sort.getSortType().equals(ElasticSortEnum.GEO_DISTANCE_DESC))
                    nativeSearchQueryBuilder.withSort(new GeoDistanceSortBuilder(sort.getKey(), sort.getValue().get(0), sort.getValue().get(1)).unit(DistanceUnit.METERS).order(SortOrder.DESC));
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
            searchResponse.getHits();
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
            return new CustomSearchResponse<>(searchResult, null, requestQueries.getPageNo(), searchResponse.getHits().getTotalHits());
        });
    }

    /**
     * Adds new query to the final bool query builder that will be executed
     *
     * @param boolQueryBuilder boolean query builder where query is added
     * @param key              field name in the search condition
     * @param querySet         query set which contains operator type and values
     * @param values
     */
    private void addQuery(BoolQueryBuilder boolQueryBuilder, String key, Set<ElasticRequestQuery> querySet, List<?> values) {

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

    private void validate(ElasticRequestQueries requestQueries) {
        if (null == requestQueries || CollectionUtils.isEmpty(requestQueries.getQueries()))
            return;
        Map<String, Set<ElasticOperationsEnum>> operationMap = new HashMap<>();

        for (List<ElasticRequestQuery> elasticRequestQueryList : requestQueries.getQueries()) {
            for (ElasticRequestQuery elasticRequestQuery : elasticRequestQueryList) {
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
    }

    /**
     * page request builder for limit the search result
     *
     * @param page      page no; 1 indexed
     * @param limit     limit value
     * @param hardLimit configured hard limit
     * @return
     */
    private PageRequest pageRequestUtil(Integer page, Integer limit, Integer hardLimit) {
        page = (page != null && page > 0) ? page - 1 : 0;
        limit = (limit != null && limit > 0 && limit <= hardLimit) ? limit : hardLimit;
        return PageRequest.of(page, limit);
    }

}
