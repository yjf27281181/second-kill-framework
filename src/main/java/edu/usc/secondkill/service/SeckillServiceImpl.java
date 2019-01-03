package edu.usc.secondkill.service;

import edu.usc.secondkill.common.aop.ServiceLimit;
import edu.usc.secondkill.common.aop.ServiceLock;
import edu.usc.secondkill.common.dynamicquery.DynamicQuery;
import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.Seckill;
import edu.usc.secondkill.common.entities.SuccessKilled;
import edu.usc.secondkill.common.enums.SeckillStatEnum;
import edu.usc.secondkill.common.exceptions.SeckillRepeatedException;
import edu.usc.secondkill.repository.SeckillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
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
    public Result startSeckillLock(Long seckillId, Long userId) {
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
        } catch (SeckillRepeatedException e) {
            return Result.error(SeckillStatEnum.MUCH);
        } catch (Exception e) {
            //e.printStackTrace();
            return Result.error(SeckillStatEnum.MUCH);
        } finally {
            lock.unlock();
        }
        return Result.ok(SeckillStatEnum.SUCCESS);
    }

    @Override
    @ServiceLock
    @Transactional
    public Result startSeckillAopLock(Long seckillId, Long userId) {
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
    public Result startSeckillDBPCC_ONE(Long seckillId, Long userId) {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=? FOR UPDATE";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number =  ((Number) object).longValue();
        try {
            if(number > 0) {
                nativeSql = "UPDATE seckill SET number = number-1 WHERE seckill_id=?";
                dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
                saveKilled(seckillId, userId);
                return Result.ok(SeckillStatEnum.SUCCESS);
            } else
                return Result.error(SeckillStatEnum.END);
        } catch (Exception e) {
            return Result.error(SeckillStatEnum.MUCH);
        }
    }

    @Override
    @Transactional
    public Result startSeckillDBPCC_TWO(Long seckillId, Long userId) {
        String nativeSql = "UPDATE seckill SET number = number-1 WHERE seckill_id=? AND number>0";
        int count = dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
        if(count>0) {
            saveKilled(seckillId, userId);
            return Result.ok(SeckillStatEnum.SUCCESS);
        } else
            return Result.error(SeckillStatEnum.END);
    }

    @Override
    @Transactional
    public Result startSeckillDBOCC(Long seckillId, Long userId) {
        Optional<Seckill> optionalSeckill = seckillRepository.findById(seckillId);
        Seckill kill = optionalSeckill.orElse(null);
        if(kill.getNumber() > 0) {
            String nativeSql = "UPDATE seckill SET number=number-1 WHERE seckill_id=? AND version=?";
            int count = dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{userId, kill.getVersion()});
            if(count > 0) {
                saveKilled(seckillId, userId);
                return Result.ok(SeckillStatEnum.SUCCESS);
            }
        }
        return Result.error(SeckillStatEnum.END);
    }


    private void saveKilled(Long seckillId, Long userId) {

        SuccessKilled killed = new SuccessKilled();
        killed.setSeckillId(seckillId);
        killed.setUserId(userId);
        killed.setState((short) 0);
        killed.setCreateTime(new Timestamp(new Date().getTime()));
        try {
            dynamicQuery.save(killed);
        } catch (SQLException e) {
            throw new SeckillRepeatedException("repeated kill");
        }
    }
}
