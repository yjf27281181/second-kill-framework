package edu.usc.secondkill.service;

import edu.usc.secondkill.common.aop.ServiceLimit;
import edu.usc.secondkill.common.dynamicquery.DynamicQuery;
import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.Seckill;
import edu.usc.secondkill.common.entities.SuccessKilled;
import edu.usc.secondkill.common.enums.SeckillStatEnum;
import edu.usc.secondkill.repository.SeckillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillServiceImpl implements  SeckillService {

    private Lock lock = new ReentrantLock(true); //make the lock fair

    @Autowired
    private DynamicQuery dynamicQuery;

    @Autowired
    private SeckillRepository seckillRepository;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillRepository.findAll();
    }

    @Override
    public Seckill getById(Long seckillId) {
        Optional<Seckill> optionalSeckill = seckillRepository.findById(seckillId);
        return optionalSeckill.orElse(null);
    }

    @Override
    public Long getSeckillCount(Long seckillId) {
        String nativeSql = "SELECT count(*) FROM success_killed WHERE seckill_id = ?";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        return ((Number) object).longValue();
    }

    @Override
    @Transactional
    public void deleteSeckill(Long seckillId) {
        String nativeSql = "DELETE FROM success_killed WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
        nativeSql = "UPDATE seckill SET number = 100 WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
    }

    /**
     * using transaction to avoid inconsistency
     * @param seckillId
     * @param userId
     * @return
     */
    @Override
    @ServiceLimit
    @Transactional
    public Result startSeckill(Long seckillId, Long userId) {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?1";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number =  ((Number) object).longValue();
        if(number > 0) {
            nativeSql = "UPDATE seckill SET number = number-1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
            saveKilled(seckillId, userId);
            return Result.ok(SeckillStatEnum.SUCCESS);
        } else
            return Result.error(SeckillStatEnum.END);
    }

    @Override
    @Transactional
    public Result startSeckillLoct(Long seckillId, Long userId) {
        Result result = null;
        try {
            lock.lock();
            String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
            Object object =  dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
            Long number =  ((Number) object).longValue();
            if(number>0) {
                nativeSql = "UPDATE seckill  SET number=number-1 WHERE seckill_id=?";
                dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
                saveKilled(seckillId, userId);
            } else
                return Result.error(SeckillStatEnum.END);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return Result.ok(SeckillStatEnum.SUCCESS);
    }

    private void saveKilled(Long seckillId, Long userId) {

            SuccessKilled killed = new SuccessKilled();
            killed.setSeckillId(seckillId);
            killed.setUserId(userId);
            killed.setState((short) 0);
            killed.setCreateTime(new Timestamp(new Date().getTime()));
            dynamicQuery.save(killed);
    }
}
