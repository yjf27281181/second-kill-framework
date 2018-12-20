package edu.usc.secondkill.web;

import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "second kill")
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    @Autowired
    private SeckillService seckillService;

    @ApiOperation(value="method 1 (basic way)", nickname = "YanJF")
    @PostMapping("/start")
    public Result start(Long seckillId) {
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        //initial database
        seckillService.deleteSeckill(seckillId);

        final long killId =  seckillId;
        LOGGER.info("start second kill, could be oversold");
        for(int i=0; i<skillNum; i++) {
            final long userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Result result = seckillService.startSeckill(killId, userId);
                    LOGGER.info("user {} : {}", userId, result.get("msg"));
                    latch.countDown();
                }
            };
            executor.execute(task);;
        }
        try {
            latch.await();// wait for other people
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("A total of {} items sold",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

}
