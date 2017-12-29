package com.kingboy.main;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/29 下午2:01
 * @desc 不加锁的售卖示例.
 */
public class CommonLock {

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
