package com.wyy.esdemo

import com.google.gson.Gson
import com.wyy.esdemo.service.BulkService
import com.wyy.esdemo.service.DocumentService
import com.wyy.esdemo.service.IndexService
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * @Date: 19-10-18
 * @Author: wyy
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BulkTest {

    @Autowired
    private IndexService indexService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private BulkService bulkService;

    private final String indexName = "index1";
    private final Gson gson = new Gson();

    @After
    public void after() throws IOException {
        indexService.deleteIndex(indexName);
    }

    @Test
    public void bulkAddDocuments() {
        Map map = [
                "1": [name: "a", age: 10],
                "2": [name: "b", age: 10],
                "3": [name: "c", age: 10],
                "4": [name: "d", age: 10],
                "5": [name: "e", age: 10],
        ]

        BulkResponse bulkResponse = bulkService.bulkAddDocument(indexName, map)
        bulkResponse.items.each { item ->
            assert item.response.result == DocWriteResponse.Result.CREATED
        }

        1..5.each {
            assert documentService.documentExisted(indexName, it.toString())
        }
    }

    @Test
    void bulkAddAndUpdateDocument() {
        BulkRequest request = new BulkRequest()
        request.add(new IndexRequest(indexName).id("1").source(gson.toJson([name: "abcd", age: 10]), XContentType.JSON))
        request.add(new UpdateRequest(indexName, "1").doc([age: 20]))
        BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT)
        assert bulkResponse.items[0].response.result == DocWriteResponse.Result.CREATED
        assert bulkResponse.items[1].response.result == DocWriteResponse.Result.UPDATED

        GetResponse getResponse = documentService.getDocument(indexName, "1")
        assert getResponse.getSource().age == 20

    }

}
