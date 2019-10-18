package com.wyy.esdemo.service;

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Date: 19-10-18
 * @Author: wyy
 */
@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public SearchResponse search(String indexName, int from, int size) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        request.source(searchSourceBuilder);
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public SearchResponse andSearch(String indexName, int from, int size, Map<String, Object> andSearch) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, Object> entry : andSearch.entrySet()) {
            boolQueryBuilder.must(new TermQueryBuilder(entry.getKey(), entry.getValue()));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        request.source(searchSourceBuilder);
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public SearchResponse orSearch(String indexName, int from, int size, Map<String, Object> orSearch) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, Object> entry : orSearch.entrySet()) {
            boolQueryBuilder.should(new TermQueryBuilder(entry.getKey(), entry.getValue()));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        request.source(searchSourceBuilder);
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public MultiSearchResponse multiSearch(String indexName, List<Map<String, Object>> orSearch) throws IOException {
        MultiSearchRequest request = new MultiSearchRequest();
        for (Map<String, Object> search : orSearch) {
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            for (Map.Entry<String, Object> entry : search.entrySet()) {
                searchSourceBuilder.query(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
            }
            searchRequest.source(searchSourceBuilder);
            request.add(searchRequest);
        }

        return restHighLevelClient.msearch(request, RequestOptions.DEFAULT);

    }
}
