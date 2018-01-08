package com.kingboy.domain;

import javax.persistence.Embeddable;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午3:30
 * @desc 地址.
 */
@Embeddable
public class Address {

    //省份
    private String province;

    //城市
    private String city;
}
