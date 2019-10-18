package com.wyy.esdemo.service;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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

    /**
     * only update,if document not exist will throw exception
     *
     * @param indexName
     * @param id
     * @param document
     * @return
     * @throws IOException
     */
    public UpdateResponse updateDocument(String indexName, String id, Map<String, Object> document) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id);
        request.doc(document);
        return restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    public MultiGetResponse queryDocuments(String indexName, List<String> idList) throws IOException {
        MultiGetRequest request = new MultiGetRequest();
        for (String id : idList) {
            request.add(new MultiGetRequest.Item(indexName, id));
        }
        return restHighLevelClient.mget(request, RequestOptions.DEFAULT);
    }
}
