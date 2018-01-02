package com.kingboy.repository.car;

import com.kingboy.domain.car.Car;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/3 上午2:21
 * @desc 汽车仓储.
 */
public interface CarRepository {

    /**
     * 根据用户id查询所有的车
     * @param id
     * @return
     */
    @Select("SELECT * FROM `car` WHERE user_id = #{id}")
    List<Car> findCarByUserId(Long id);

}
