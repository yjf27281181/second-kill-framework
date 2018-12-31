package edu.usc.secondkill.web;


import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.service.SeckillDistributedService;
import edu.usc.secondkill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "Distributed Lock")
@RestController
@RequestMapping("/seckillDistributed")
public class SeckillDistributedController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillDistributedController.class);

    @Autowired
    private SeckillDistributedService distributedService;

    @Autowired
    private SeckillService seckillService;

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000));

    @ApiOperation(value = "method 1 rediss distributed lock")
    @PostMapping("/startRedisLock")
    public Result startRedisLock(Long seckillId) {
        seckillService.deleteSeckill(seckillId);
        for(int i=0; i<1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = distributedService.startSeckillRedisLock(seckillId, userId);
                    LOGGER.info("User{}:{}",userId,result.get("msg"));
                }
            };
            executor.execute(task);
        }

        try {
            Thread.sleep(15000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("sold {} items",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value = "method 2 ZooKeeper distributed lock")
    @PostMapping("/startZookeeperLock")
    public Result startZkLock(Long seckillId) {
        seckillService.deleteSeckill(seckillId);
        for(int i=0; i<1000; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = distributedService.startSeckillZooKeeperLock(seckillId, userId);
                    LOGGER.info("User{}:{}",userId,result.get("msg"));
                }
            };
            executor.execute(task);
        }

        try {
            Thread.sleep(20000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("sold {} items",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }
}
