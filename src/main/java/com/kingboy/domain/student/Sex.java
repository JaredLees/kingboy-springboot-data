package com.kingboy.domain.student;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:39
 * @desc 性别枚举.
 */
public enum Sex {
    BOY("男"),GIRL("女");

    private String value;

    Sex(String value) {
        this.value = value;
    }

    public String getSex() {
        return this.value;
    }
}
