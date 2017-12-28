package com.kingboy.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/11/19 下午10:35
 * @desc 默认.
 */
@RestController
public class IndexController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<html>"
                + "<head><title>kingboy-springboot-es</title></head>"
                + "<body>"
                + "<h1 style='text-align: center;margin: 100px;padding: 50px;background-color: lightskyblue;'>"
                + "欢迎访问!"
                + "</h1>"
                + "</body>"
                + "</html>";
    }

}
