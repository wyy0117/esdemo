package com.wyy.esdemo.service;

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public SearchResponse customSearch(String indexName, QueryBuilder queryBuilder) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public MultiSearchResponse multiSearch(Map<String, QueryBuilder> index_query) throws IOException {
        MultiSearchRequest request = new MultiSearchRequest();
        for (Map.Entry<String, QueryBuilder> entry : index_query.entrySet()) {
            SearchRequest searchRequest = new SearchRequest(entry.getKey());
            searchRequest.source(new SearchSourceBuilder().query(entry.getValue()));
            request.add(searchRequest);
        }
        return restHighLevelClient.msearch(request, RequestOptions.DEFAULT);
    }
}
