package com.kingboy;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KingboySpringbootJpaApplicationTests {

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
		saveUserWhenSuccess();
		mockMvc.perform(delete("/user/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void findByNameWhenSuccess() throws Exception {
		String contentAsString = mockMvc.perform(get("/user/username/boy")
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

}
