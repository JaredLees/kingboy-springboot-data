## SpringBoot使用Mybatis注解进行一对多和多对多查询

GitHub的完整示例项目地址[kingboy-springboot-data](https://github.com/KingBoyWorld/kingboy-springboot-data/tree/feature_mybatis_annotation_one_many)

### 一、模拟的业务查询

系统中的用户user都有唯一对应的地址信息address，每个用户可以有多量车car，类似如下结构
```
|-- user
    |-- address
    |-- carList
        |-- car1
        |-- car2
```

### 二、对应的实体类如下
```java
//省略setter/getter

public class Address {
    private Long id;
    private String province;
    private String city;
}

public class Car {
    private Long id;
    private String color;
    private String name;
    //用户id
    private Long userId;
}

public class User {
    private Long id;
    //地址信息，和用户是一对一的关系
    private Address address;
    //地址id
    private Long addressId;
    //用户拥有的车，和用户是一对多的关系
    private List<Car> cars;
}
```

### 三、对应的建表语句和模拟数据如下

```sql
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(50) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `car` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `color` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO
    `user`
VALUES
    ('1', 'baby', '1'),
    ('2', 'kingboy', '2'),
    ('3', 'boy', '3'),
    ('4', 'kingbaby', '4');

INSERT INTO
    `address`
VALUES
    ('1', '北京', '北京'),
    ('2', '天津', '天津'),
    ('3', '安徽', '宿州'),
    ('4', '广东', '广州');

INSERT INTO
    `car`
VALUES
    ('1', 'green', '路虎', '1'),
    ('2', 'white', '奔驰', '2'),
    ('3', 'blue', '玛莎拉蒂', '4'),
    ('4', 'yellow', '兰博基尼', '4');
```

### 四、@One一对一映射

以获取用户的唯一地址为例，首先我们定义一个根据地址id查询地址的查询方法
```java
public interface AddressRepository {
    /**
     * 根据地址id查询地址
     */
    @Select("SELECT * FROM `address` WHERE id = #{id}")
    Address findAddressById(Long id);
}
```

然后我们定义一个根据用户id查询用户的方法
```java
public interface UserRepository {
    @Select("SELECT * FROM `user` where id = #{id}")
    User findUserWithAddress(Long id);
}
```
这个时候我们查询出来的user对象中的address属性是空的，和address并没有任何关联。
那么我们要把user中的addressId传递给AddressRepository的查询地址的方法，
然后把查询出的地址对象address赋值给user的address属性,那么我们怎么做呢？
```java
public interface UserRepository {
    @Select("SELECT * FROM `user` where id = #{id}")
    @Results({
            @Result(property = "address", column = "address_id",
                    one = @One(select = "com.kingboy.repository.address.AddressRepository.findAddressById"))
    })
    User findUserWithAddress(Long id);
}
```
我们要使用@Resutl注解对返回的结果进行配置，
- property = "address", 表示要将返回的查询结果赋值给user的address属性
- column = "address_id" 是指将user表中的address_id作为com.kingboy.repository.address.AddressRepository.findAddressById的查询参数
- one 表示这是一个一对一的查询
- @One(select = "方法全路径) 表示我们调用的方法


### 五、@Many一对多查询

以获取用户拥有的所有车car为例，首先我们定义一个根据用户id查询车的查询方法

```java
public interface CarRepository {
    /**
     * 根据用户id查询所有的车
     */
    @Select("SELECT * FROM `car` WHERE user_id = #{userId}")
    List<Car> findCarByUserId(Long userId);
}
```

然后我们定义一个根据用户id查询用户的方法
```java
public interface UserRepository {
    @Select("SELECT * FROM `user` where id = #{id}")
    User findUserWithAddress(Long id);
}
```

这个时候我们查询出来的user对象中的List<Car>属性是空的，和car的查询方法并没有任何关联。
那么我们要把user中的用户id传递给CarRepository的查询车的方法，
然后把查询出的集合对象List<Car>赋值给user的cars属性,那么我们怎么做呢？
```java
public interface UserRepository {
    /**
     * 查询带有车信息的用户===============演示一对多(关于多对多其实就是两个一对多组成)
     */
    @Select("SELECT * FROM `user` WHERE id = #{id}")
    @Results({
            @Result(property = "cars", column = "id",
                    many = @Many(select = "com.kingboy.repository.car.CarRepository.findCarByUserId"))
    })
    User getUserWithCar(Long id);
}
```

我们要使用@Resutl注解对返回的结果进行配置，
- property = "cars", 表示要将返回的查询结果赋值给user的cars属性
- column = "id" 是指将user表中的用户主键id作为com.kingboy.repository.address.CarRepository.findCarByUserId的查询参数
- many 表示这是一个一对多的查询
- @Many(select = "方法全路径) 表示我们调用的方法, 方法参数userId就是上面column指定的列值


### 六、@One @Many的总结

首先我们统一下概念：查询Address或Car的方法，接下来统称为User的附属查询。

共同点：
- 无论是一对一还是一对多，都是通过附属查询来实现的，我们需要定义这个附属查询方法。
- 在主查询方法中通过@One、@Many指定附属查询方法的全路径。
- 都通过column来传递参数给附属方法。

不同点：
- 一对一，那么附属方法返回的是一个单独的对象
- 一对多，那么附属方法返回的是一个对象集合
