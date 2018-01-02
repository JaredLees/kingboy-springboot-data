package com.kingboy.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kingboy.domain.address.Address;
import com.kingboy.domain.car.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:18
 * @desc 用户实体.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    //地址信息，和用户是一对一的关系
    private Address address;

    private Long addressId;

    //用户拥有的车，和用户是一对多的关系
    private List<Car> cars;

}
