package com.kingboy.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kingboy.domain.user.Sex;
import com.kingboy.domain.user.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:18
 * @desc 用户查询条件.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {

    private String nickName;

    private String phoneNumber;

    private Sex sex;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime fromBirthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime toBirthday;

    private Status status;

}
