package com.kingboy.domain.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/3 上午1:10
 * @desc 地址类，演示一对一.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private Long id;

    private String province;

    private String city;
}
