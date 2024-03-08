package se.skltp.tak.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TakWebApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	BuildProperties buildProperties;

	@Test
	void enviromentInfoIntegrationTest() throws Exception {
		mockMvc.perform(get("/auth/login")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Logga in till Ineras Tjänstekatalog (SKLTP-TEST)")))
				.andExpect(content().string(containsString(buildProperties.getVersion())));
	}

	@Test
	void headersIntegrationTest() throws Exception {
		mockMvc.perform(get("/auth/login")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(header().string("X-Content-Type-Options", "nosniff"))
				.andExpect(header().string("Content-Security-Policy", "frame-ancestors 'none'"))
				.andExpect(header().string("X-Frame-Options", "DENY"));
	}

	@Test
	void signInIntegrationTest() throws Exception {
		mockMvc.perform(post("/auth/signIn")
						.param("username", "skltp")
						.param("password", "skltp")
				).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

}
