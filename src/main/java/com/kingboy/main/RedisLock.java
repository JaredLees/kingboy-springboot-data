package com.kingboy.main;

import com.kingboy.common.utils.RedisTool;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/29 下午2:02
 * @desc 使用Redis加锁的售卖示例.
 */
public class RedisLock {

    //库存个数
    static int goodsCount = 900;

    //卖出个数
    static int saleCount = 0;

    @SneakyThrows
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.0.130", 6379, 1000);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
                Jedis jedis = jedisPool.getResource();
                boolean lock = false;
                while (!lock) {
                    lock = RedisTool.tryGetDistributedLock(jedis, "goodsCount", Thread.currentThread().getName(), 10);
                }
                if (lock) {
                    if (goodsCount > 0) {
                        goodsCount--;
                        System.out.println("剩余库存：" + goodsCount + " 卖出个数" + ++saleCount);
                    }
                }
                RedisTool.releaseDistributedLock(jedis, "goodsCount", Thread.currentThread().getName());
                jedis.close();
            }).start();
        }
        Thread.sleep(3000);
        jedisPool.close();
    }

}
