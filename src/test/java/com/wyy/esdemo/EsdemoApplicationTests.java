package com.wyy.esdemo;

import com.wyy.esdemo.service.EsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsdemoApplicationTests {

    @Autowired
    private EsService esService;

    @Test
    public void testIndex() throws IOException {
        boolean existed = esService.indexExisted("test");
        assert !existed;
        esService.createIndex("test");
        existed = esService.indexExisted("test");
        assert existed;
        esService.deleteIndex("test");
        existed = esService.indexExisted("test");
        assert !existed;

    }

}
