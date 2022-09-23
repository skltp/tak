package se.skltp.tak.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.service.AnvandareService;

@WebMvcTest(HomeController.class)
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnvandareService anvandareService;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void homePageSmokeTest () throws Exception {
        mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Välkommen till Ineras Tjänstekatalog")));
    }
}
