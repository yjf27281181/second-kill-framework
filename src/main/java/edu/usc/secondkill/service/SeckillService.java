package edu.usc.secondkill.service;

import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.Seckill;

import java.util.List;

public interface SeckillService {
    /**
     * get all items
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * get Seckill item by Id
     * @param seckillId item's id
     * @return item object
     */
    Seckill getById(Long seckillId);

    /**
     * get inventory of a item
     * @param seckillId
     * @return
     */
    Long getSeckillCount(Long seckillId);

    void deleteSeckill(Long seckillId);

    //different ways to make data consistence
    Result startSeckill(Long seckillId, Long userId);
    Result startSeckillLock(Long seckillId, Long userId);
    Result startSeckillAopLock(Long seckillId, Long userId);
    Result startSeckillDBPCC_ONE(Long seckillId, Long userId);
    Result startSeckillDBPCC_TWO(Long seckillId, Long userId);
    Result startSeckillDBOCC(Long seckillId, Long userId);

}
