package com.wyy.esdemo

import com.wyy.esdemo.service.BulkService
import com.wyy.esdemo.service.DocumentService
import com.wyy.esdemo.service.IndexService
import com.wyy.esdemo.service.SearchService
import com.wyy.esdemo.util.ESQueryBuilder
import org.elasticsearch.action.search.MultiSearchResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.index.query.QueryBuilder
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
    void search() {
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
    }

    @Test
    void customSearch() {
        Map map = [
                "1": [name: "aa", age: 1],
                "2": [name: "bb", age: 2],
                "3": [name: "ca", age: 3],
                "4": [name: "da", age: 4],
                "5": [name: "ee", age: 5],
        ]
        bulkService.bulkAddDocument(indexName, map)

        QueryBuilder queryBuilder = ESQueryBuilder.and(
                ESQueryBuilder.eq("name", "aa"),
                ESQueryBuilder.ne("name", "11"),
                ESQueryBuilder.gt("age", 0),
                ESQueryBuilder.lt("age", 2),
        )

        SearchResponse searchResponse = searchService.customSearch(indexName, queryBuilder)
        assert searchResponse.hits.hits.length == 1

        queryBuilder = ESQueryBuilder.or(
                ESQueryBuilder.gte("age", 5),
                ESQueryBuilder.lte("age", 1),
                ESQueryBuilder.in("name", ["aa", "ee"]),
                ESQueryBuilder.between("age", 0, 1),
                ESQueryBuilder.like("name", "*e")
        )

        searchResponse = searchService.customSearch(indexName, queryBuilder)
        assert searchResponse.hits.hits.length == 2
    }

    @Test
    void multiSearch() {
        Map map = [
                "1": [name: "aa", age: 1],
                "2": [name: "bb", age: 2],
                "3": [name: "ca", age: 3],
                "4": [name: "da", age: 4],
                "5": [name: "ee", age: 5],
        ]
        bulkService.bulkAddDocument(indexName, map)

        bulkService.bulkAddDocument("index2", map)

        Map query = [
                (indexName): ESQueryBuilder.eq("name", "aa"),
                index2     : ESQueryBuilder.lte("age", 2)
        ]

        MultiSearchResponse multiSearchResponse = searchService.multiSearch(query)
        assert multiSearchResponse.responses[0].response.hits.hits.length == 1
        assert multiSearchResponse.responses[1].response.hits.hits.length == 2
    }
}
