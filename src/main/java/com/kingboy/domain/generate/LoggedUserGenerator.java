package com.kingboy.domain.generate;

import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2018/1/4 下午5:46
 * @desc .
 */
public class LoggedUserGenerator implements ValueGenerator<String> {
    @Override
    public String generateValue(Session session, Object owner) {
        return "当前登录人";
    }
}
