## SpringBoot使用Mybatis注解开发教程-分页-动态sql

代码示例可以参考个人GitHub项目[kingboy-springboot-data](https://github.com/KingBoyWorld/kingboy-springboot-data/tree/feature_mybatis_annotation)

### 一、环境配置

1.引入mybatis依赖
```properties
    compile(
            //SpringMVC
            'org.springframework.boot:spring-boot-starter-web',
            "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.3",
            //Mybatis依赖及分页插件
            "org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1",
            "com.github.pagehelper:pagehelper:4.1.0",
            'mysql:mysql-connector-java'
    )
```

2.数据源配置

```yaml
spring:
  datasource:
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql:///mybatis?characterEncoding=utf8&useSSL=false
    //根据resources目录下的schema.sql自动建表，可省略
    schema: classpath:schema.sql
    //根据resources目录下的data.sql自动插入假数据,可省略
    data: classpath:data.sql
```

3.分页插件配置

```java
@Configuration
public class CommonConfiguration {

    /**
     * 注册MyBatis分页插件PageHelper
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }

}
```

4.配置SpringBoot扫描Mybatis仓储，有两种配置方式

- 在启动类上加入@MapperScan，填写Mapper接口所在的包名，SpringBoot就会自动将该包中的Mapper接口进行加载

```java
@MapperScan(basePackages = "com.kingboy.repository")
@SpringBootApplication
public class KingboySpringbootDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(KingboySpringbootDataApplication.class, args);
    }
}
```

- 在Mapper接口上加上@Mapper注解，SpringBoot就会自动夹在该Mapper接口

```java
@Mapper
public interface UserMapper {

        @Insert("INSERT INTO `user` VALUES (#{id}, #{nickName}, #{phoneNumber}, #{sex}, #{age}, #{birthday}, #{status})")
        @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
        void saveUser(User user);
        
}
```
这两种方式都可以，根据项目结构进行选择即可，也可以结合私用。

5.配置驼峰属性自动映射
```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

例如实体中属性为phoneNumber,数据库属性为phone_number，Mybatis默认是不能自动转换的，通常我们要自定义以下结果集映射
```java
@Result(column = "phone_number", property = "phoneNumber")
```
然而这样很不方便,当我们加上以上配置之后，Mybatis在映射结果时，会自动转换，去掉下划线，变n为大写N。

6.设置mybatis日志打印，在配置文件中加入如下配置

```yaml
logging:
  level:
    #Mapper所在的包
    com.kingboy.repository: debug
```

开启日志有三种方式，可以参考我的另一篇博客[SpringBoot开启Mybatis的SQL日志](http://blog.csdn.net/kingboyworld/article/details/78644416)



### 二、Mybatis注解

Mybatis提供了四个注解，先简单了解一下，后面我们会编写相应的实例

@Insert("sql")  增
@Delete("sql")  删
@Update("sql")  改
@Select("sql")  查


### 三、方法参数读取

1.普通参数读取

在方法参数前使用@Param注解对参数进行标记，在sql中使用#{属性名}来读取属性。

示例
```java
    //删除用户，根据用户ID和名称删除用户
    @Delete("DELETE FROM `user` WHERE id = #{id} AND nick_name = #{nickName}")
    void delete(@Param(value = "id") Long id, @Param(value = "nickName") String nickName);
```

2.对象参数读取

直接在sql中使用#{属性名}来读取属性

示例
```java
    @Insert("INSERT INTO `user` VALUES (#{id}, #{nickName}, #{phoneNumber}, #{sex}, #{age}, #{birthday}, #{status})")
    void saveUser(User user);
```


### 四、分页插件的使用

分页插件单独拿出来说，是因为项目实战中发现很多同学都使用错了，所以着重强调一下。

分页插件的核心类是PageHelper,这个类提供了很多的静态方法，其中我们最常用的是PageHelper.startPage(int page, int size)这个方法，返回com.github.pagehelper.Page对象，
该对象包含了包含了查询结果result, 当前页pageNum，每页大小pageSize, 总条数total等信息，可以获取这些信息返回给前端。需要注意的是，PageHelper的页数是从1开始的。

我们来看一段实际的代码操作,这里就不写service层了。
```
//controller
RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    UserRepository userRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public Page get(@RequestParam Integer page, @RequestParam Integer size) {
        //分页并查询
        Page<User> pageInfo = PageHelper.startPage(page, size);
        List<User> users = userRepository.listUser();

        //获取分页信息演示, 实际项目中一般会封装为自己的返回体。
        int pageNum = pageInfo.getPageNum();
        int pageSize = pageInfo.getPageSize();
        long total = pageInfo.getTotal();
        List<User> result = pageInfo.getResult();//和上面的users结果相同

        return pageInfo;
    }

}



//repository
@Mapper
public interface UserRepository {
    //分页查询用户
    @Select("SELECT * FROM `user`")
    List<User> listUser();
}

```


### 五、动态标签

熟悉Mybatis的同学一定对Mybatis的动态SQL很了解，解决了我们编写动态SQL的很多痛点。接下来介绍以下Mybatis常用的动态sql的注解写法，其实和xml是几乎完全一致的。

首先在注解中使用动态sql时，我们需要为sql加上`<script></script>`标签，这时候mybatis才会解析里面的标签。

1.`<if test="条件判断">sql</if>`

用来对参数进行判断，若为真，则包裹的sql语句生效。条件中可以使用and作连接
```java
    //分页查询用户,如果sex有值，则查询相应sex的所有用户，sex没值则查询所有
    @Select("<script>SELECT * FROM `user` <if test='sex != null'>where sex = #{sex}</if><script>")
    List<User> listUser(Sex sex);//sex是一个枚举类型，值为BOY,GIRL
```

2.`<foreach item = 'item' index = 'index' collection='list' separator=',', open='(', close=')'>sql</foreach>`

循环参数中的集合参数进行操作，和java中的for循环比较类似，不过多了open/close/separator三个参数。
- open是在操作之前加上一个"("
- close是在操作之后加上一个")"
- separator是每个元素间的分割符

需要注意的是，collection的值只能是list或者collection

示例
```java
    @Select("<script>"
            + "SELECT * FROM `user` WHERE id in "
            + "<foreach item='item' collection='list' open='(' close=')' separator=','>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    List<User> listUserByIds(List<Long> ids);
```
例如传入的ids为[2,4,6]生成的sql如下：
```
SELECT * FROM `user` WHERE id in ( 2 , 4, 6 ) 
```

3.`<set></set>`

更新实体属性要用到的标签，一般我们使用动态sql更新用户信息`update user set username = ?, age = ?,`时要处理最后一个逗号的问题，那么使用set标签就可以自动帮我们处理这个逗号。

示例
```java
    //使用set标签进行动态set，要注意条件判断：没被删除的用户才可以更新数据
    @Update("<script>"
            + "UPDATE `user` "
            + "<set>"
            + "<if test='nickName != null'>nick_name = #{nickName}, </if>"
            + "<if test='age != null'>age = #{age}, </if>"
            + "<if test='phoneNumber != null'>phone_number = #{phoneNumber}, </if>"
            + "<if test='birthday != null'>birthday = #{birthday}, </if>"
            + "<if test='status != null'>status = #{status}, </if>"
            + "<if test='sex != null'>sex = #{sex}, </if>"
            + "</set>"
            + "WHERE id = #{id} AND status != 'DELETE';"
            + "</script>")
    void updateUser(User user);
```

4.`<where></where>`

和更新数据时遇到的问题一样，我们在动态查询时也要动态拼接查询条件，最前边一个查询条件是没有AND或者OR的，以前通常使用`1 = 1`避开这个问题，那么现在可以使用<where>标签自动帮我们处理。

示例
```java
    /**
     * 根据条件查询用户
     * 注意其中nickName模糊查询的处理方法
     * 注意其中关于生日的区间判断, &lt; &gt;
     * @param userQueryDTO
     * @return
     */
    @Select("<script>"
            + "SELECT * FROM `user`"
            + "<where>"
            + "<bind name='nickName' value=\"'%' + nickName + '%'\" />"
            + "<if test='nickName != null'>AND nick_name like #{nickName}</if>"
            + "<if test='phoneNumber !=null'>AND phone_number = #{phoneNumber}</if>"
            + "<if test='sex !=null'>AND sex = #{sex}</if>"
            + "<if test='age !=null'>AND age = #{age}</if>"
            + "<if test='fromBirthday !=null'>AND birthday &gt; #{fromBirthday}</if>"
            + "<if test='toBirthday !=null'>AND birthday &lt; #{toBirthday}</if>"
            + "<if test='status !=null'>AND status = #{status}</if>"
            + "</where>"
            + "</script>")
    List<User> queryByCondition(UserQueryDTO userQueryDTO);
```

5.`<bind name='新属性' value="'%' + 原属性 + '%'" />`

在模糊查询时，我们通常使用`where name like %金%`这样的方式来进行查询(当然这样效率很低，不推荐)，但是在mybatis里面我们直接写`where name like %#{name}%`是不行的，
会生成`where name like %'金'%`这样的sql, 很明显是不行的。有两种方式可以处理。

1)、传入的属性上加上%号，例如传入的name就为`%金%`,这样可以解决，但是不方便。

2)、使用bind标签，我们可以在sql中加入一行赋值操作`<bind name='newName' value="'%' + name + '%'" />`

示例：参照上一个示例。

6.`<choose><when test='条件1'></when><when test='条件2'></when><otherwise></otherwise></choose>`

这个java中的switch比较像，如果条件1成立，就执行条件1中的语句，剩下的不执行。条件1不成立就判断条件2，如果条件2还不成立就执行otherwise中的sql。

示例
```java
    /**
     * 如果age有值，通过age查询
     * 如果age没有值，则通过sex查询
     * 如果age和sex都没值，则查询所有status为UNLOCK的用户
     * @param age
     * @param sex
     * @return
     */
    @Select("<script>"
            + "SELECT * FROM `user`"
            + "<where>"
            + "<choose>"
            + "<when test='age != null'>AND age = #{age}</when>"
            + "<when test='sex != null'>AND sex = #{sex}</when>"
            + "<otherwise>AND status = 'UNLOCK'</otherwise>"
            + "</choose>"
            + "</where>"
            + "</script>")
    List<User> getByOrderCondition(@Param("age") Integer age, @Param("sex") Sex sex);
```

### 六、完整示例

其中的用户实体等没有贴出来，太乱了，完整示例代码可以参考文章开头的github项目。

UserController
```java
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
     * http://localhost:8080/user
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     * @param user
     * @return
     */
    @PostMapping
    public ApiResult save(@RequestBody User user) {
        userRepository.saveUser(user);
        return ApiResult.success(user);
    }

    /**
     * 批量保存
     * http://localhost:8080/user/list
     * [
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"},
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"},
     * {"nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     * ]
     * @param users
     * @return
     */
    @PostMapping(value = "/list")
    public ApiResult save(@RequestBody List<User> users) {
        userRepository.saveUserList(users);
        return ApiResult.success(users);
    }

    /**
     * 更新单个用户
     * http://localhost:8080/user
     * {"id":"1","nickName":"kingboy","age":"26","sex":"BOY","phoneNumber":"13132296607","birthday":"2011-12-12 12:12","status":"UNLOCK"}
     * @return
     */
    @PutMapping
    public ApiResult update(@RequestBody User user) {
        userRepository.updateUser(user);
        return ApiResult.success("success");
    }

    /**
     * 删除用户
     * http://localhost:8080/user/1
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
     * http://localhost:8080/user/1
     * @return
     */
    @GetMapping("/{id}")
    public ApiResult get(@PathVariable Long id) {
        User user = userRepository.get(id);
        return ApiResult.success(user);
    }

    /**
     * 分页查询用户集合
     * http://localhost:8080/user
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
     * http://localhost:8080/user/ids
     * [1,3]
     * @return
     */
    @PostMapping("/ids")
    public ApiResult getUserByIds(@RequestBody List<Long> ids) {
        List<User> users = userRepository.listUserByIds(ids);
        return ApiResult.success(users);
    }

    /**
     * 通过查询条件赖查询用户，这里也不做分页了
     * http://localhost:8080/user/query
     * {
     *   "nickName":"i",
     *   "fromBirthday":"1999-12-31 12:12",
     * }
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
     * http://localhost:8080/user/query?age=24
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

```

UserRepository
```
package com.kingboy.repository.user;

import com.kingboy.domain.user.Sex;
import com.kingboy.domain.user.Status;
import com.kingboy.domain.user.User;
import com.kingboy.dto.user.UserQueryDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/31 上午12:01
 * @desc 用户仓储.
 */
public interface UserRepository {

    /**
     * 添加用户
     * @Options返回在数据库中主键，自动赋值到user的id字段中
     * keyProperty = "id"的默认值为id,可以省略
     */
    @Insert("INSERT INTO `user` VALUES (#{id}, #{nickName}, #{phoneNumber}, #{sex}, #{age}, #{birthday}, #{status})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void saveUser(User user);

    /**
     * 批量保存用户
     * @param users
     */
    @Insert("<script>" +
            "INSERT INTO `user` VALUES " +
            "<foreach item = 'item' index = 'index' collection='list' separator=','>" +
            "(#{item.id}, #{item.nickName}, #{item.phoneNumber}, #{item.sex}, #{item.age}, #{item.birthday}, #{item.status})" +
            "</foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true)
    void saveUserList(List<User> users);

    //使用set标签进行动态set，要注意条件判断：没被删除的用户才可以更新数据
    @Update("<script>"
            + "UPDATE `user` "
            + "<set>"
            + "<if test='nickName != null'>nick_name = #{nickName}, </if>"
            + "<if test='age != null'>age = #{age}, </if>"
            + "<if test='phoneNumber != null'>phone_number = #{phoneNumber}, </if>"
            + "<if test='birthday != null'>birthday = #{birthday}, </if>"
            + "<if test='status != null'>status = #{status}, </if>"
            + "<if test='sex != null'>sex = #{sex}, </if>"
            + "</set>"
            + "WHERE id = #{id} AND status != 'DELETE';"
            + "</script>")
    void updateUser(User user);


    //删除用户，软删除
    @Update("UPDATE `user` SET status = #{status} WHERE id = #{id}")
    void remove(@Param(value = "id") Long id, @Param(value = "status") Status status);

    //删除用户，硬删除
    @Delete("DELETE FROM `user` WHERE id = #{id}")
    void delete(@Param(value = "id") Long id);

    /**
     * 查询用户
     * 单个参数时，@Param注解可以省略
     * 在配置中指定了驼峰映射，所以@Results的结果映射可以省略，不是驼峰类型的仍然需要写结果映射。
     */
    @Select("SELECT * FROM `user` WHERE id = #{id}")
    @Results({
            @Result(column = "nick_name", property = "nickName"),
            @Result(column = "phone_number", property = "phoneNumber")
    })
    User get(Long id);

    //分页查询用户
    @Select("SELECT * FROM `user`")
    List<User> listUser();

    /**
     * 通过id集合查询用户
     * @param ids
     * @return
     */
    @Select("<script>"
            + "SELECT * FROM `user` WHERE id in "
            + "<foreach item='item' collection='list' open='(' close=')' separator=','>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    List<User> listUserByIds(List<Long> ids);

    /**
     * 根据条件查询用户
     * 注意其中nickName模糊查询的处理方法
     * 注意其中关于生日的区间判断
     * @param userQueryDTO
     * @return
     */
    @Select("<script>"
            + "SELECT * FROM `user`"
            + "<where>"
            + "<bind name='nickName' value=\"'%' + nickName + '%'\" />"
            + "<if test='nickName != null'>AND nick_name like #{nickName}</if>"
            + "<if test='phoneNumber !=null'>AND phone_number = #{phoneNumber}</if>"
            + "<if test='sex !=null'>AND sex = #{sex}</if>"
            + "<if test='age !=null'>AND age = #{age}</if>"
            + "<if test='fromBirthday !=null'>AND birthday &gt; #{fromBirthday}</if>"
            + "<if test='toBirthday !=null'>AND birthday &lt; #{toBirthday}</if>"
            + "<if test='status !=null'>AND status = #{status}</if>"
            + "</where>"
            + "</script>")
    List<User> queryByCondition(UserQueryDTO userQueryDTO);


    /**
     * 如果age有值，通过age查询
     * 如果age没有值，则通过sex查询
     * 如果age和sex都没值，则查询所有status为UNLOCK的用户
     * @param age
     * @param sex
     * @return
     */
    @Select("<script>"
            + "SELECT * FROM `user`"
            + "<where>"
            + "<choose>"
            + "<when test='age != null'>AND age = #{age}</when>"
            + "<when test='sex != null'>AND sex = #{sex}</when>"
            + "<otherwise>AND status = 'UNLOCK'</otherwise>"
            + "</choose>"
            + "</where>"
            + "</script>")
    List<User> getByOrderCondition(@Param("age") Integer age, @Param("sex") Sex sex);
}

```




