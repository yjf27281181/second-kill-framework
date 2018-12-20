package edu.usc.secondkill.service;

import edu.usc.secondkill.common.entities.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SeckillServiceImplTest {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeckillService seckillService;

    @Test
    public void startSeckill() {
        Result result = seckillService.startSeckill(1000L,1L);
        LOGGER.info(result.toString());
    }
}