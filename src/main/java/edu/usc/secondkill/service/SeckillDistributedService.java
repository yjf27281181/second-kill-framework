package edu.usc.secondkill.service;

import edu.usc.secondkill.common.entities.Result;

public interface SeckillDistributedService {
    Result startSeckillRedisLock(long seckillId, long userId);
    Result startSeckillZooKeeperLock(long seckillId, long userId);
}
