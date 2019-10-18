package com.wyy.esdemo.service;

import com.google.gson.Gson;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @Date: 19-10-18
 * @Author: wyy
 */
@Service
public class BulkService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private final Gson gson = new Gson();

    public BulkResponse bulkAddDocument(String indexName, Map<String, Map<String, Object>> id_document) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        //The Bulk API supports only documents encoded in JSON or SMILE. Providing documents in any other format will result in an error.
        for (Map.Entry<String, Map<String, Object>> entry : id_document.entrySet()) {
            bulkRequest.add(new IndexRequest(indexName).id(entry.getKey()).source(gson.toJson(entry.getValue()), XContentType.JSON));
            bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        }
        return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

    }
}
