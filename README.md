## SpringBoot使用Redis作为全局锁的示例

> 微服务的项目中，一个服务我们启动多份，在不同的进程中。这些服务是无状态的，而由数据存储容器(mysql/redis/es)进行状态数据的持久化。这就会导致资源竞争，出现多线程的问题。

### 一、下面代码模拟了没有锁情况下的资源竞争。
```
public class CommonConsumerService {

    //库存个数
    static int goodsCount = 900;

    //卖出个数
    static int saleCount = 0;

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {Thread.sleep(2);} catch (InterruptedException e) {}
                if (goodsCount > 0) {
                    goodsCount--;
                    System.out.println("剩余库存：" + goodsCount + " 卖出个数" + ++saleCount);
                }
            }).start();
        }
        Thread.sleep(3000);
    }

}
```

运行一次，最后几行的输出结果如下，很明显出错了，剩余0个商品却只卖出了899个商品，很明显有商品被某个线程私吞了。
```
...
剩余库存：5 卖出个数893
剩余库存：5 卖出个数894
剩余库存：4 卖出个数895
剩余库存：2 卖出个数896
剩余库存：2 卖出个数897
剩余库存：1 卖出个数898
剩余库存：0 卖出个数899
```


### 二、使用redis加锁

> redis是单线程的，串行执行，那么接下来使用redis为资源进行加锁。

1.首先引入依赖
```
compile "org.springframework.boot:spring-boot-starter-data-redis"
```

2.引入redis加锁工具类
```
package com.kingboy.common.utils;

import redis.clients.jedis.Jedis;
import java.util.Collections;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/29 下午1:57
 * @desc Redis工具.
 */
public class RedisTool {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}
```


3.将上面没有锁的示例代码改编如下：
```
public class RedisLockConsumerService {

    //库存个数
    static int goodsCount = 900;

    //卖出个数
    static int saleCount = 0;

    @SneakyThrows
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.0.130", 6379, 1000);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {Thread.sleep(2);} catch (InterruptedException e) {}
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
```

执行几次程序输出结果如下，可以看到结果是有序，并且正确的。

```
...
剩余库存：6 卖出个数894
剩余库存：5 卖出个数895
剩余库存：4 卖出个数896
剩余库存：3 卖出个数897
剩余库存：2 卖出个数898
剩余库存：1 卖出个数899
剩余库存：0 卖出个数900
```


具体实例可以参考[github项目](https://github.com/KingBoyWorld/kingboy-springboot-data)，切换到feature_redis_lock分支。