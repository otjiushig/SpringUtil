package com.jiushig.sample.springUtil;

import com.jiushig.springutil.CommonUtil;
import com.jiushig.springutil.ServerInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by zk on 2018/8/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommonUtilTest {
    private final Logger logger = LoggerFactory.getLogger(CommonUtilTest.class);

    @Test
    public void getSystemPath() {
        logger.info(CommonUtil.getSystemPath());
    }

    @Test
    public void isEmpty() {
        assertTrue(CommonUtil.isEmpty(""));
    }

    @Test
    public void getPort() {
        logger.info(String.valueOf(ServerInfo.getPort()));
    }

    @Test
    public void formatText() {
        assertEquals(CommonUtil.formatText("\n\n\n\n"), "\n");
        assertEquals(CommonUtil.formatText("\n"), "\n");
        assertEquals(CommonUtil.formatText("123\n13"), "123\n13");
        assertEquals(CommonUtil.formatText("\n13"), "\n13");

    }

    @Test
    public void formatMarkdownText() {
        assertEquals(CommonUtil.formatMarkdownText("##1*1``12"), "1112");
        assertEquals(CommonUtil.formatMarkdownText("[test](http://google.com)"), "[链接]");
        assertEquals(CommonUtil.formatMarkdownText("![test](http://google.com)"), "[图片]");
        assertEquals(CommonUtil.formatMarkdownText("\n\n##1*1``12[test](http://google.com)\n\n![test](http://google.com)"),
                "\n1112[链接]\n[图片]");
    }
}