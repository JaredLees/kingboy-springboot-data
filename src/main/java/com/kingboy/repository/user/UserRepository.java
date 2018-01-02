package com.kingboy.repository.user;

import com.kingboy.domain.user.*;
import org.apache.ibatis.annotations.*;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/31 上午12:01
 * @desc 用户仓储.
 */
public interface UserRepository {

    /**
     * 查询带有地址信息的用户=============演示一对一
     * @param id
     * @return
     */
    @Select("SELECT * FROM `user` where id = #{id}")
    @Results({
            @Result(property = "address", column = "address_id",
                    one = @One(select = "com.kingboy.repository.address.AddressRepository.findAddressById"))
    })
    User findUserWithAddress(Long id);


    /**
     * 查询带有车信息的用户===============演示一对多(关于多对多其实就是两个一对多组成)
     * @param id
     * @return
     */
    @Select("SELECT * FROM `user` WHERE id = #{id}")
    @Results({
            @Result(property = "cars", column = "id",
                    many = @Many(select = "com.kingboy.repository.car.CarRepository.findCarByUserId"))
    })
    User getUserWithCar(Long id);
}
