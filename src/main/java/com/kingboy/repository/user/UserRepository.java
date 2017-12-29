package com.kingboy.repository.user;


import com.kingboy.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/11/28 下午4:28
 * @desc ${DESCRIPTION}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 查询用户名为username的用户
     * @param username
     * @return
     */
    List<User> findByUsername(String username);


    /**
     * 查询用户名为username的用户,带分页
     * @param username
     * @return
     */
    List<User> findByUsername(String username, Pageable pageable);

    /**
     * 使用@Query和hql进行查询
     * @param name
     * @return
     */
    @Query("from User where username = :name")
    List<User> findByNameHQL(@Param(value = "name") String name, Pageable pageable);

    /**
     * 使用原生sql进行查询
     * @param name
     * @return
     */
    @Query(value = "select * from user where username = :name", nativeQuery = true)
    List<User> findByNameNative(@Param(value = "name") String name);


    /**
     * 查询用户名为username并且真实姓名为realname的用户
     * @param username
     * @param realname
     */
    List<User> findByUsernameAndRealname(String username, String realname);

    /**
     * 查询用户名为username或者姓名为realname的用户
     */
    List<User> findByUsernameOrRealname(String username, String realname);

    /**
     * 查询用户名不是username的所有用户
     * @param username
     * @return
     */
    List<User> findByUsernameNot(String username);


    /**
     * 查询年龄段为ageFrom到ageTo的用户
     * @param ageFrom
     * @param ageTo
     * @return
     */
    List<User> findByAgeBetween(Integer ageFrom, Integer ageTo);

    /**
     * 查询生日小于birthTo的用户
     */
    List<User> findByBirthLessThan(LocalDateTime birthTo);


    /**
     * 查询生日段大于birthFrom的用户
     * @param birthFrom
     * @return
     */
    List<User> findByBirthGreaterThan(LocalDateTime birthFrom);

    /**
     * 查询年龄小于或等于ageTo的用户
     */
    List<User> findByAgeBefore(Integer ageTo);

    /**
     * 查询年龄大于或等于ageFrom的用户
     * @param ageFrom
     * @return
     */
    List<User> findByAgeAfter(Integer ageFrom);

    /**
     * 用户名模糊查询
     * @param username
     * @return
     */
    List<User> findByUsernameLike(String username);


    /**
     * 查询以start开头的用户
     * @param start
     * @return
     */
    List<User> findByUsernameStartingWith(String start);

    /**
     * 查询以end结尾的用户
     * @return
     */
    List<User> findByUsernameEndingWith(String end);

    /**
     * 查询用户名包含word的用户
     * @param word
     * @return
     */
    List<User> findByUsernameContaining(String word);

    /**
     * 查询名字属于usernames中的用户
     * @param usernames
     * @return
     */
    List<User> findByUsernameIn(Collection<String> usernames);

    /**
     * 查询名字不属于usernames中的用户
     * @param usernames
     * @return
     */
    List<User> findByUsernameNotIn(Collection<String> usernames);

    /**
     *最后来个复杂点的：查询年龄小于ageTo,姓名以start开头，id大于idTo的用户，并且按照年龄倒序
     * @return
     */
    List<User> findByAgeBeforeAndUsernameStartingWithAndIdGreaterThanOrderByAgeDesc(Integer ageTo, String start, Long idTo);

}
