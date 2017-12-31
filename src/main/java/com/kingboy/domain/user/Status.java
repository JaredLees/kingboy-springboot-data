package com.kingboy.domain.user;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:39
 * @desc 性别枚举.
 */
public enum Status {
    LOCKED("冻结"), UNLOCK("正常"), DELETE("已删除");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getStatus() {
        return this.value;
    }
}
