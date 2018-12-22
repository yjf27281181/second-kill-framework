package edu.usc.secondkill.service;

import edu.usc.secondkill.common.entities.Result;

public interface SeckillDistributedService {
    Result startSeckilRedisLock(long seckillId, long userId);
}
