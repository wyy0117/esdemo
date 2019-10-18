package com.wyy.esdemo

import com.wyy.esdemo.service.BulkService
import com.wyy.esdemo.service.DocumentService
import com.wyy.esdemo.service.IndexService
import com.wyy.esdemo.service.SearchService
import org.elasticsearch.action.search.MultiSearchResponse
import org.elasticsearch.action.search.SearchResponse
import org.junit.After
import org.junit.Before
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
class SearchTest {

    @Autowired
    private SearchService searchService;
    @Autowired
    private IndexService indexService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private BulkService bulkService;

    private final String indexName = "index1";

    @Before
    public void before() throws IOException {
        indexService.createIndex(indexName);
    }

    @After
    public void after() throws IOException {
        indexService.deleteIndex(indexName);
    }

    @Test
    void test() {
        Map map = [
                "1": [name: "a", age: 10],
                "2": [name: "b", age: 10],
                "3": [name: "c", age: 10],
                "4": [name: "d", age: 10],
                "5": [name: "d", age: 20],
        ]

        bulkService.bulkAddDocument(indexName, map)

        SearchResponse searchResponse = searchService.search(indexName, 0, 2)
        assert searchResponse.hits.hits.length == 2

        searchResponse = searchService.andSearch(indexName, 0, 2, [name: "a"])
        assert searchResponse.hits.hits.length == 1

        searchResponse = searchService.andSearch(indexName, 0, 2, [name: "a", age: 20])
        assert searchResponse.hits.hits.length == 0

        searchResponse = searchService.orSearch(indexName, 0, 3, [name: "a", age: 20])
        assert searchResponse.hits.hits.length == 2
        assert (searchResponse.hits.hits*.id - ["1", "5"]).size() == 0

        MultiSearchResponse multiSearchResponse = searchService.multiSearch(indexName, [[name: "a"], [name: "d"]])
        assert multiSearchResponse.responses[0].response.hits.hits.length == 1
        assert multiSearchResponse.responses[0].response.hits.hits[0].getSourceAsMap().name == "a"
        assert multiSearchResponse.responses[1].response.hits.hits.length == 2
        assert multiSearchResponse.responses[1].response.hits.hits[0].getSourceAsMap().age == 10
        assert multiSearchResponse.responses[1].response.hits.hits[1].getSourceAsMap().age == 20
    }


}
