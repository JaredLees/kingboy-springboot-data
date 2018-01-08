package com.kingboy.domain;

import com.kingboy.domain.convert.UserTypeConvert;
import com.kingboy.domain.generate.LoggedUserGenerator;
import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午3:30
 * @desc 用户信息.
 */
@Entity(name = "user899")
@Data
@Table(name = "user11")//对Entity的补充
public class User {

    @Id
    private Long id;

    @Basic(fetch = FetchType.EAGER, optional = true)
    @Column(name = "user_name")
    @Type(type = "text")
    private String username;

    @Embedded
    private Address address;

    //@MapKeyEnumerated(value = EnumType.ORDINAL)
    @Enumerated(value = EnumType.STRING)
    private Sex sex;

    @Convert(converter = UserTypeConvert.class)
    private UserType userType;

    @Lob
    private byte[] image;

    @Temporal(value = TemporalType.DATE)
    private Date birthday1;

    //不可以使用@Temporal，会报异常
    //类型已经默认规定了
    private LocalDateTime birthday2;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.INSERT)
    private String ceateBy;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.ALWAYS)
    private String updateBy;

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

    @ColumnTransformer(
            //forColumn = "money",如果是对象可以指定某一列
            read = "money / 100",
            write = "? * 100"
    )
    private Double money;

    private Double rate;

    @Formula(value = "money * rate")
    private Double result;

    @Where( clause = "account_type = 'DEBIT'")
    @OneToMany(mappedBy = "user")
    private List<Account> debitAccounts = new ArrayList<>( );

    @Where( clause = "account_type = 'CREDIT'")
    @OneToMany(mappedBy = "user")
    private List<Account> creditAccounts = new ArrayList<>( );

    //乐观锁
    @Access( AccessType.FIELD )
    @Version
    private int version;

    @ElementCollection
    @CollectionTable(
            name = "stringList",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private List<String> sList;

    @OneToOne
    @OneToMany
    @ManyToMany
//    @ManyToAny(metaColumn = Column.)
    @ManyToOne
    private String temp;
}
