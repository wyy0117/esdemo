package com.wyy.esdemo.service;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Date: 19-10-16
 * @Author: wyy
 */
@Service
public class EsService {

    @Value("${spring.elasticsearch.shards}")
    private int shards;
    @Value("${spring.elasticsearch.replicas}")
    private int replicas;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void createIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build());

        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    public void deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
    }

    public boolean indexExisted(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

}
