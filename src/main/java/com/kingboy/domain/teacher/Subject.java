package com.kingboy.domain.teacher;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/12/30 下午11:44
 * @desc 科目.
 */
public enum Subject {

    MATH("数学"), CHINESE("语文"), ENGLISH("英语");

    private String value;

    Subject(String value) {
        this.value = value;
    }

    public String getSubject() {
        return this.value;
    }
}
