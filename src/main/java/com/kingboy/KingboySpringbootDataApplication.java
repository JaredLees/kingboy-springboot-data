package com.kingboy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.kingboy.repository")
@SpringBootApplication
public class KingboySpringbootDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(KingboySpringbootDataApplication.class, args);
    }
}
