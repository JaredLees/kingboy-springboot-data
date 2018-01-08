package com.kingboy.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午6:07
 * @desc .
 */
@Entity
@Where( clause = "active = true" )
public class Account {

    @Id
    private Long id;

    @ManyToOne
    private User user;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    private Double amount;

    private Double rate;

    private boolean active;

}
