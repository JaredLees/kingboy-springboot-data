package com.kingboy.domain.base;

import com.kingboy.domain.generate.LoggedUserGenerator;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午6:31
 * @desc .
 */
@Data
public class BaseEntity {

    @Id
    private Integer id;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.INSERT)
    private String ceateBy;

    @GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.ALWAYS)
    private String updateBy;

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;


}
