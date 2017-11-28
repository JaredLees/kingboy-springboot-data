package com.kingboy.repository;

import com.kingboy.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/11/28 下午4:28
 * @desc ${DESCRIPTION}.
 */
public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findByUsername(String username);

    List<User> findByAgeBeforeAndUsernameStartingWithAndIdGreaterThanOrderByAgeDesc(Integer ageTo, String nameStart, Long id);
}
