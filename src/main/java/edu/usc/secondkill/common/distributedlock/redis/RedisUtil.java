package edu.usc.secondkill.common.distributedlock.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    public static final String KEY_PREFIX_VALUE = "yanjf:seckill:value:";

    @Resource
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    public boolean cacheValue(String k, Serializable v, long time, TimeUnit unit) {
        String key = KEY_PREFIX_VALUE + k;
        try {

            ValueOperations<Serializable, Serializable> valueOps = redisTemplate.opsForValue();
            valueOps.set(key,v);
            if(time > 0) redisTemplate.expire(key, time, unit);
            return true;
        } catch (Throwable e) {
            logger.error("cache[{}] failure, value[{}]",key,v,e);
        }
        return false;
    }

    public boolean cacheValue(String k, Serializable v, long time) {
        return cacheValue(k,v,time, TimeUnit.SECONDS);
    }

    public  boolean cacheValue(String k, Serializable v) {
        return cacheValue(k, v, -1);
    }

    public boolean containsKey(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable e) {
            logger.error("cache[{}] failure, value[{}]",key,e);
        }
        return false;
    }

    public  Serializable getValue(String k) {
        try {
            ValueOperations<Serializable, Serializable> valueOps =  redisTemplate.opsForValue();
            return valueOps.get(KEY_PREFIX_VALUE + k);
        } catch (Throwable t) {
            logger.error("cache failure key[" + KEY_PREFIX_VALUE + k + ", error[" + t + "]");
        }
        return null;
    }
    public  boolean removeValue(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            logger.error("cache failure key[" + key + ", error[" + t + "]");
        }
        return false;
    }
}
