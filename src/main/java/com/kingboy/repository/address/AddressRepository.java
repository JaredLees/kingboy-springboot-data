package com.kingboy.repository.address;

import org.apache.ibatis.annotations.Select;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/3 上午2:22
 * @desc 地址仓储.
 */
public interface AddressRepository {

    /**
     * 根据地址id查询地址
     * @param id
     * @return
     */
    @Select("SELECT * FROM `address` WHERE id = #{id}")
    com.kingboy.domain.address.Address findAddressById(Long id);

}
