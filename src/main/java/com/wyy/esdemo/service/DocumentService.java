package com.wyy.esdemo.service;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @Date: 19-10-17
 * @Author: wyy
 */
@Service
public class DocumentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public IndexResponse addOrUpdateDocument(String indexName, String id, Map<String, Object> document) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.id(id);
        request.source(document);
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    public GetResponse getDocument(String indexName, String id) throws IOException {
        return getDocument(indexName, id, true);
    }

    public GetResponse getDocument(String indexName, String id, boolean fetchSource) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        if (fetchSource) {
            request.fetchSourceContext(FetchSourceContext.FETCH_SOURCE);
        } else {
            request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        }
        return restHighLevelClient.get(request, RequestOptions.DEFAULT);
    }

    public GetResponse getDocument(String indexName, String id, String[] include, String[] exclude) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, include, exclude);
        request.fetchSourceContext(fetchSourceContext);
        return restHighLevelClient.get(request, RequestOptions.DEFAULT);
    }

    public boolean documentExisted(String indexName, String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
    }

    public DeleteResponse deleteDocument(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName, id);
        return restHighLevelClient.delete(request, RequestOptions.DEFAULT);
    }


    public GetResponse getDocuments(String indexName) throws IOException {
        GetRequest request = new GetRequest(indexName);
        return restHighLevelClient.get(request, RequestOptions.DEFAULT);
    }

}
