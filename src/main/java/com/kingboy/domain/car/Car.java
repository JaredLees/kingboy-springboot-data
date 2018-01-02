package com.kingboy.domain.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/3 上午1:41
 * @desc 车，一个用户可以拥有多个车.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    private Long id;

    private String color;

    private String name;

    private Long userId;

}
