package com.kingboy.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/29 下午11:43
 * @desc 用户.
 */
@Data
@Entity
public class User {

    @Id
    private Long id;

    private String username;

    private String realname;

    private String password;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime birth;

}
