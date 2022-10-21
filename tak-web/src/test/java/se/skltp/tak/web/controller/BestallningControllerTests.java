package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;
import se.skltp.tak.web.service.ConfigurationService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BestallningController.class)
public class BestallningControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean private AnvandareService anvandareService;
    @MockBean private BestallningService bestallningService;
    @MockBean private BestallningsStodetConnectionService bestallningsStodetConnectionService;
    @MockBean(name = "configurationService") private ConfigurationService configurationService;

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
    public void bestallningStartPageTest () throws Exception {
        mockMvc.perform(get("/bestallning")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Skapa Beställning")));
    }

    @Test
    public void bestallningGetTest () throws Exception {
        Mockito.when(bestallningsStodetConnectionService.getBestallning(42L)).thenReturn("{}");
        Mockito.when(bestallningService.parseAndFormatJson("{}")).thenReturn("{ \"formatted\" : \"true\" }");
        mockMvc.perform(post("/bestallning")
                .param("bestallningsNummer", "42")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningsNummer", 42L))
                .andExpect(model().attribute("bestallningJson", "{ \"formatted\" : \"true\" }"))
                .andExpect(content().string(containsString("Skapa Beställning")));
        verify(bestallningsStodetConnectionService, times(1)).getBestallning(42);
    }
}
