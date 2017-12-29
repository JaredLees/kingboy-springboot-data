package com.kingboy;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KingboySpringbootDataApplicationTests {

    @Resource
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 添加单个用户测试
     * @throws Exception
     */
    @Test
    public void saveUserWhenSuccess() throws Exception {
        mockMvc.perform(post("/user")
                .content("{\"id\":1,\"username\":\"king\",\"realname\":\"小金\",\"password\":\"king123\",\"age\":24,\"birth\":\"2016-12-12 11:12\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserWhenSuccess() throws Exception {
        mockMvc.perform(delete("/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void saveUsersWhenSuccess() throws Exception {
        mockMvc.perform(post("/user/list")
                .content(getContent())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findByNameWhenSuccess() throws Exception {
        String contentAsString = mockMvc.perform(get("/user/username/king")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
    }

    @Test
    public void findByAgeAndUsernameStartAndIdWhenSuccess() throws Exception {
        String contentAsString = mockMvc.perform(get("/user/ageTo/18/name_start/ki/id/12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
    }

    private String getContent() {
        return "[" +
                "{\"id\":10,\"username\":\"king\",\"realname\":\"小金\",\"password\":\"king123\",\"age\":24,\"birth\":\"2017-12-12 11:12\"}," +
                "{\"id\":11,\"username\":\"boy\",\"realname\":\"小明\",\"password\":\"king123\",\"age\":56,\"birth\":\"2016-09-12 12:12\"}," +
                "{\"id\":12,\"username\":\"kingboy\",\"realname\":\"小南\",\"password\":\"king123\",\"age\":12,\"birth\":\"1993-08-12 07:12\"}," +
                "{\"id\":13,\"username\":\"boyking\",\"realname\":\"小孩\",\"password\":\"king123\",\"age\":11,\"birth\":\"1992-05-12 23:12\"}," +
                "{\"id\":14,\"username\":\"kiboy\",\"realname\":\"金子\",\"password\":\"king123\",\"age\":2,\"birth\":\"2006-12-13 20:12\"}," +
                "{\"id\":15,\"username\":\"baby\",\"realname\":\"哈喽小金\",\"password\":\"king123\",\"age\":66,\"birth\":\"2012-09-12 05:12\"}," +
                "{\"id\":16,\"username\":\"xiaohong\",\"realname\":\"Baby\",\"password\":\"king123\",\"age\":12,\"birth\":\"2000-02-13 06:12\"}" +
                "]";
    }
}
