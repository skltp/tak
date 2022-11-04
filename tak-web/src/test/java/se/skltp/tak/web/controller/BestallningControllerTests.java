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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;
import se.skltp.tak.web.service.ConfigurationService;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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
        Mockito.when(mockSubject.getPrincipal()).thenReturn("TEST_USER");
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

    @Test
    public void bestallningGetErrorTest () throws Exception {
        Mockito.when(bestallningsStodetConnectionService.getBestallning(99L)).thenThrow(new FileNotFoundException());
        mockMvc.perform(post("/bestallning")
                        .param("bestallningsNummer", "99")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningsNummer", 99L))
                .andExpect(content().string(containsString("Kunde inte hämta beställning")));
    }

    @Test
    public void bestallningParseErrorTest () throws Exception {
        Mockito.when(bestallningsStodetConnectionService.getBestallning(98L)).thenReturn("");
        Mockito.when(bestallningService.parseAndFormatJson("")).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/bestallning")
                        .param("bestallningsNummer", "98")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningsNummer", 98L))
                .andExpect(content().string(containsString("Beställning 98 kunde inte tolkas")));
    }

    @Test
    public void bestallningConfirmTest () throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        BestallningsRapport mockRapport = mock(BestallningsRapport.class);
        Mockito.when(mockRapport.getMetadata()).thenReturn(new LinkedHashMap<>());
        Mockito.when(mockRapport.getInkludera()).thenReturn(new LinkedHashMap<>());
        Mockito.when(mockRapport.getExkludera()).thenReturn(new LinkedHashMap<>());
        Mockito.when(mockData.getBestallningsRapport()).thenReturn(mockRapport);
        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(post("/bestallning/confirm")
                        .param("bestallningJson", "{}")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningHash", mockData.hashCode()))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("bestallning", mockData))
                .andExpect(content().string(containsString("Bekräfta Beställning")));
    }

    @Test
    public void bestallningConfirmValidationErrorsTest () throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        Mockito.when(mockData.hasErrors()).thenReturn(true);
        Set<String> errors = new HashSet<>();
        errors.add("ERROR 1");
        errors.add("ERROR 2");
        Mockito.when(mockData.getBestallningErrors()).thenReturn(errors);

        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(post("/bestallning/confirm")
                        .param("bestallningJson", "{}")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
    }

    @Test
    public void bestallningConfirmExceptionTest () throws Exception {
        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/bestallning/confirm")
                        .param("bestallningJson", "{}")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
    }

    @Test
    public void bestallningSaveWrongHashTest () throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", "13")
                        .session(mockSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ett fel inträffade")));
    }

    @Test
    public void bestallningSaveTest () throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", Integer.toString(mockData.hashCode()))
                        .session(mockSession)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Denna beställning är sparad")));
    }
}
