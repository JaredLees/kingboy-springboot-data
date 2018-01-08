package com.kingboy.domain;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午4:12
 * @desc 用户类型枚举.
 */
public enum UserType {

    COMMON("普通用户"), ADMIN("管理员");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
