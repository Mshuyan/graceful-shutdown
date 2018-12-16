package com.shuyan.gracefulshutdown;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GracefulShutdownApplicationTests {
    Logger logger = LoggerFactory.getLogger(GracefulShutdownApplicationTests.class);
    @Test
    public void contextLoads() {
        logger.info("111");
        test();
        TestClass testClass = new TestClass();
        testClass.test();
    }

    private void test(){
        logger.info("222");
    }

    private class TestClass{
        Logger logger = LoggerFactory.getLogger(TestClass.class);
        public void test(){
            logger.info("333");
        }
    }
}

