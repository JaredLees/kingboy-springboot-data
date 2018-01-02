package com.kingboy.controller.user;

import com.kingboy.common.utils.result.ApiResult;
import com.kingboy.domain.user.User;
import com.kingboy.repository.user.UserRepository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:59
 * @desc 用户接口.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    UserRepository userRepository;

    /**
     * 查询一个包含地址信息的用户
     * @param id
     * @return
     */
    @GetMapping(value = "/id/{id}/include/address")
    public ApiResult findUserWithAddress(@PathVariable Long id) {
        User userWithAddress = userRepository.findUserWithAddress(id);
        return ApiResult.success(userWithAddress);
    }

    /**
     * 查询某个地址的所有用户
     * @param id
     * @return
     */
    @GetMapping(value = "/id/{id}/include/car")
    public ApiResult findUserWithCar(@PathVariable Long id) {
        User user = userRepository.getUserWithCar(id);
        return ApiResult.success(user);
    }
}
