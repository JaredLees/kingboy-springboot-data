package com.kingboy.controller.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kingboy.common.utils.page.PageParam;
import com.kingboy.common.utils.page.PageResult;
import com.kingboy.common.utils.page.PageResultFactory;
import com.kingboy.common.utils.result.ApiResult;
import com.kingboy.domain.user.Sex;
import com.kingboy.domain.user.Status;
import com.kingboy.domain.user.User;
import com.kingboy.dto.user.UserQueryDTO;
import com.kingboy.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    PageResultFactory pageResultFactory;

    /**
     * 保存单个用户
     * @param user  {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     * @return
     */
    @PostMapping
    public ApiResult save(@RequestBody User user) {
        userRepository.saveUser(user);
        return ApiResult.success(user);
    }

    /**
     * 批量保存
     * @param users
     * [
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"},
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"},
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     * ]
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResult save(@RequestBody List<User> users) {
        userRepository.saveUserList(users);
        return ApiResult.success(users);
    }

    /**
     * 更新单个用户
     * @return
     * {"id":"1","nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     */
    @PutMapping
    public ApiResult update(@RequestBody User user) {
        userRepository.updateUser(user);
        return ApiResult.success("success");
    }

    /**
     * 删除用户
     * @return
     */
    @DeleteMapping("/{id}")
    public ApiResult remove(@PathVariable Long id) {
        //软删除
        userRepository.remove(id, Status.DELETE);
        //硬删除
        //userRepository.delete(id);
        return ApiResult.success("success");
    }

    /**
     * 通过ID获取用户
     * @return
     */
    @GetMapping("/{id}")
    public ApiResult get(@PathVariable Long id) {
        User user = userRepository.get(id);
        return ApiResult.success(user);
    }

    /**
     * 分页查询用户集合
     * 需要注意的是PageHelper是从1开始分页，而Hibernate/Jpa是从0开始分页的
     * @return
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ApiResult get(@ModelAttribute PageParam pageParam) {
        //分页并查询
        Page<User> pageInfo = PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<User> users = userRepository.listUser();

        //实际使用时，最后一个参数传入要转换的类型DTO
        PageResult<User> result = pageResultFactory.createAndConvert(pageParam.getMybatisPage(), pageInfo.getTotal(), users, User.class);
        return ApiResult.success(result);
    }

    /**
     * 通过id集合查询用户,这里就不做分页了。
     * @return
     */
    @PostMapping("/ids")
    public ApiResult getUserByIds(@RequestBody List<Long> ids) {
        List<User> users = userRepository.listUserByIds(ids);
        return ApiResult.success(users);
    }

    /**
     * 通过查询条件赖查询用户，这里也不做分页了
     * @param userQueryDTO
     * @return
     */
    @PostMapping("/query")
    public ApiResult queryUser(@RequestBody UserQueryDTO userQueryDTO) {
        List<User> users = userRepository.queryByCondition(userQueryDTO);
        return ApiResult.success(users);
    }

    /**
     * 根据条件的顺序来查询用户
     * 演示choose标签的用法:如果传入age就按年龄查询用户，age没有传入就按照sex赖查询用户。如果两者都没有传入，那就查询所有status没有冻结的用户
     * 类似如下：
     * switch(value) {
     *     case age:
     *        //查询age
     *        break;
      *     case sex:
     *        //查询sex
     *        break;
     *     default:
     *        //查询status
     *        break;
     * }
     */
    @GetMapping("/query")
    public ApiResult findUserByOrderCondition(@RequestParam(required = false) Integer age, @RequestParam(required = false) Sex sex) {
        List<User> users = userRepository.getByOrderCondition(age, sex);
        return ApiResult.success(users);
    }

}
