package com.wyy.esdemo

import com.wyy.esdemo.service.DocumentService
import com.wyy.esdemo.service.IndexService
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.update.UpdateResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * @Date: 19-10-17
 * @Author: wyy
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentTest {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private IndexService indexService;

    private final String indexName = "index1";
    private final String id = "10000";

    @Before
    public void before() throws IOException {
        indexService.createIndex(indexName);
    }

    @After
    public void after() throws IOException {
        indexService.deleteIndex(indexName);
    }

    @Test
    public void testAdd() throws IOException {
        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;
    }

    @Test
    public void testGet() throws IOException {
        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        GetResponse getResponse = documentService.getDocument(indexName, id);
        assert getResponse.isExists();
        assert getResponse.getSource().equals(document);
    }

    @Test
    public void testUpdate() throws IOException {
        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        document.sex = true
        indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.UPDATED;

        GetResponse getResponse = documentService.getDocument(indexName, id);
        assert getResponse.isExists();
        assert getResponse.getSource().equals(document);


        document.remove("name");
        indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.UPDATED;

        getResponse = documentService.getDocument(indexName, id);
        assert getResponse.isExists();
        assert getResponse.getSource().equals(document);

        getResponse = documentService.getDocument(indexName, id, false);
        assert getResponse.isExists();
        assert getResponse.getSource() == null;

        getResponse = documentService.getDocument(indexName, id, ["age"] as String[], new String[0]);
        assert getResponse.source.keySet().equals(["age"] as Set)

        getResponse = documentService.getDocument(indexName, id, new String[0], ["age"] as String[]);
        assert !getResponse.getSource().keySet().contains("age");
        assert getResponse.getSource().keySet().contains("sex");

        document.age = 20
        UpdateResponse updateResponse = documentService.updateDocument(indexName, id, document);
        assert updateResponse.getResult() == DocWriteResponse.Result.UPDATED;

        getResponse = documentService.getDocument(indexName, id);
        assert getResponse.getSource().age == 20;

    }

    @Test
    public void testExist() throws IOException {
        boolean existed = documentService.documentExisted(indexName, id);
        assert !existed;

        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        existed = documentService.documentExisted(indexName, id);
        assert existed;
    }

    @Test
    public void testDelete() throws IOException {

        DeleteResponse deleteResponse = documentService.deleteDocument(indexName, id);
        assert deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND;

        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        boolean existed = documentService.documentExisted(indexName, id);
        assert existed;

        deleteResponse = documentService.deleteDocument(indexName, id);
        assert deleteResponse.getResult() == DocWriteResponse.Result.DELETED;

        existed = documentService.documentExisted(indexName, id);
        assert !existed;
    }

    @Test
    void queryDocument() {
        Map document = [
                name: "abcd",
                age : 10
        ]
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, "1", document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        indexResponse = documentService.addOrUpdateDocument(indexName, "2", document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;

        MultiGetResponse response = documentService.queryDocuments(indexName, ["1", "2"])
        assert response.getResponses()[0].response.source == document
        assert response.getResponses()[1].response.source == document

    }
}
