package com.kingboy.domain.student;

import com.kingboy.domain.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:18
 * @desc 学生实体.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private Long id;

    private String name;

    private Sex sex;

    private Address address;

    private Integer age;

    private LocalDateTime birthday;

    private List<Teacher> teachers;
}
