package com.wyy.esdemo;

import com.wyy.esdemo.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexTest {

    @Autowired
    private IndexService indexService;

    @Test
    public void testIndex() throws IOException {
        boolean existed = indexService.indexExisted("test");
        assert !existed;
        indexService.createIndex("test");
        existed = indexService.indexExisted("test");
        assert existed;
        indexService.deleteIndex("test");
        existed = indexService.indexExisted("test");
        assert !existed;

    }

}
