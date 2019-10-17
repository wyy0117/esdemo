package com.wyy.esdemo;

import com.wyy.esdemo.service.DocumentService;
import com.wyy.esdemo.service.IndexService;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    public void test() throws IOException {


        Map<String, Object> document = new HashMap<>();
        document.put("name", "abcd");
        document.put("age", 10);
        IndexResponse indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.CREATED;


        GetResponse getResponse = documentService.getDocument(indexName, id);
        assert getResponse.isExists();
        assert getResponse.getSource().equals(document);

        document.put("sex", true);
        indexResponse = documentService.addOrUpdateDocument(indexName, id, document);
        assert indexResponse.getResult() == DocWriteResponse.Result.UPDATED;

        getResponse = documentService.getDocument(indexName, id);
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

        String[] include = new String[]{"age"};
        getResponse = documentService.getDocument(indexName, id, include, new String[0]);
        assert Arrays.equals(getResponse.getSource().keySet().toArray(new String[0]), include);

        String[] exclude = new String[]{"age"};
        getResponse = documentService.getDocument(indexName, id, new String[0], exclude);
        assert !getResponse.getSource().keySet().contains("age");
        assert getResponse.getSource().keySet().contains("sex");

        boolean existed = documentService.documentExisted(indexName, id);
        assert existed;

        DeleteResponse deleteResponse = documentService.deleteDocument(indexName, id);
        assert deleteResponse.getResult() == DocWriteResponse.Result.DELETED;

        existed = documentService.documentExisted(indexName, id);
        assert !existed;

        deleteResponse = documentService.deleteDocument(indexName, id);
        assert deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND;
    }
}
