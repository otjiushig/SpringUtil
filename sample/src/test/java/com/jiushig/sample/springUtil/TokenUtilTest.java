package com.jiushig.sample.springUtil;

import com.jiushig.springutil.TokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

/**
 * Created by zk on 2018/8/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenUtilTest {

    private final Logger logger = LoggerFactory.getLogger(TokenUtilTest.class);

    @Before
    public void init() {
        TokenUtil.init("test", 10, "test",new String[]{"token","token121"});
    }

    @Test
    public void test() {
        String s = TokenUtil.create(1);
        logger.info(s);
        Assert.assertEquals(1, (int) Objects.requireNonNull(TokenUtil.getByKey(TokenUtil.getToken(s), TokenUtil.KEY_USER_ID, Integer.class)));
    }
}
