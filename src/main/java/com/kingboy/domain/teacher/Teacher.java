package com.kingboy.domain.teacher;

import com.kingboy.domain.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:42
 * @desc 老师实体.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    //id
    private Long id;

    //老师姓名
    private String name;

    //科目
    private Subject Subject;

    //学生
    private List<Student> students;

}
