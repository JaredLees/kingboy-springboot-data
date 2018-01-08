package com.kingboy.domain.convert;

import com.kingboy.domain.UserType;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午4:13
 * @desc 用户类型枚举转换.
 */
@Convert
public class UserTypeConvert implements AttributeConverter<UserType, String> {
    @Override
    public String convertToDatabaseColumn(UserType attribute) {
        return attribute.getType();
    }

    @Override
    public UserType convertToEntityAttribute(String dbData) {
        return UserType.valueOf(dbData);
    }
}
