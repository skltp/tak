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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.ConfigurationService;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BestallningRestController.class)
public class BestallningRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AnvandareService anvandareService;
    @MockBean private BestallningService bestallningService;
    @MockBean(name = "configurationService") private ConfigurationService configurationService;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        Mockito.when(mockSubject.getPrincipal()).thenReturn("TEST_USER");
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void restBestallningTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        Mockito.when(mockData.hasErrors()).thenReturn(false);
        Mockito.when(bestallningService.parseAndFormatJson("{ hej }")).thenReturn("{ \"formatted\" : \"true\" }");
        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);
        mockMvc.perform(post("/rest/create").content("{ hej }").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Beställning klar")));
    }
}