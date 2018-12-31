package edu.usc.secondkill.service;

import edu.usc.secondkill.common.distributedlock.redisson.RedissonLockUtil;
import edu.usc.secondkill.common.distributedlock.zookeeper.ZkLockUtil;
import edu.usc.secondkill.common.dynamicquery.DynamicQuery;
import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.SuccessKilled;
import edu.usc.secondkill.common.enums.SeckillStatEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillDistributedServiceImpl implements SeckillDistributedService {

    @Autowired
    private DynamicQuery dynamicQuery;

    @Override
    @Transactional
    public Result startSeckillRedisLock(long seckillId, long userId) {
        boolean res = false;
        try {
            res = RedissonLockUtil.tryLock(seckillId+"", 3, 20);
            if(res) {
                String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
                Object object =  dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
                Long number = ((Number) object).longValue();
                if(number > 0) {
                    saveKilled(seckillId, userId);
                    nativeSql = "UPDATE seckill  SET number=number-1 WHERE seckill_id=? AND number>0";
                    dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
                } else
                    return Result.error(SeckillStatEnum.END);
            } else
                return Result.error(SeckillStatEnum.MUCH);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(seckillId+"");
        }

        return Result.ok(SeckillStatEnum.SUCCESS);
    }

    @Override
    @Transactional
    public Result startSeckillZooKeeperLock(long seckillId, long userId) {
        boolean res = false;
        try {
            res = ZkLockUtil.aquire(3, TimeUnit.SECONDS);
            if(res) {
                String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
                Object object =  dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
                Long number = ((Number) object).longValue();
                if(number > 0) {
                    saveKilled(seckillId, userId);
                    nativeSql = "UPDATE seckill  SET number=number-1 WHERE seckill_id=? AND number>0";
                    dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
                } else
                    return Result.error(SeckillStatEnum.END);
            } else
                return Result.ok(SeckillStatEnum.MUCH);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ZkLockUtil.release();
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
