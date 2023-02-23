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
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.util.PublishDataWrapper;


import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PubVersionController.class)
public class PubVersionControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean PubVersionService pubVersionMock;
    @MockBean LockService lockServiceMock;
    @MockBean AlerterService alerterServiceMock;
    @MockBean AnvandareService anvandareServiceMock;
    @MockBean(name = "configurationService") ConfigurationService configurationServiceMock;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        when(mockSubject.getPrincipal()).thenReturn("TEST_USER");
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void testPubVersionPreviewNoChanges() throws Exception {
        PublishDataWrapper mockData = getEmptyPublishData();
        mockData.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_ALL_USERS;
        when(pubVersionMock.ScanForPrePublishedEntries()).thenReturn(mockData);

        mockMvc.perform(get("/pubversion/create")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("errors", hasSize(1)))
                .andExpect(content().string(containsString("Det finns inga ändringar, genomförda av inloggad användare, att publicera")));
    }

    @Test
    public void testPubVersionPreviewNoChangeForCurrentUser() throws Exception {
        PublishDataWrapper mockData = getEmptyPublishData();
        RivTaProfil rp = new RivTaProfil();
        rp.setUpdatedBy("OTHER_USER");
        rp.setUpdatedTime(new Date());
        mockData.rivTaProfilList.add(rp);
        mockData.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_ALL_USERS;
        when(pubVersionMock.ScanForPrePublishedEntries()).thenReturn(mockData);

        mockMvc.perform(get("/pubversion/create")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("errors", hasSize(1)))
                .andExpect(content().string(containsString("Det finns inga ändringar, genomförda av inloggad användare, att publicera")));
    }

    @Test
    public void testPubVersionPreviewChangesForCurrentUser() throws Exception {
        PublishDataWrapper mockData = getEmptyPublishData();
        RivTaProfil rp = new RivTaProfil();
        rp.setUpdatedBy("TEST_USER");
        rp.setUpdatedTime(new Date());
        mockData.rivTaProfilList.add(rp);
        mockData.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_ALL_USERS;
        when(pubVersionMock.ScanForPrePublishedEntries()).thenReturn(mockData);

        mockMvc.perform(get("/pubversion/create")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(content().string(containsString("Granska publicering")));
    }

    private PublishDataWrapper getEmptyPublishData() {
        PublishDataWrapper data = new PublishDataWrapper();
        data.anropsAdressList = new ArrayList<>();
        data.anropsbehorighetList = new ArrayList<>();
        data.filtercategorizationList = new ArrayList<>();
        data.filterList = new ArrayList<>();
        data.logiskAdressList = new ArrayList<>();
        data.rivTaProfilList = new ArrayList<>();
        data.tjanstekomponentList = new ArrayList<>();
        data.tjanstekontraktList = new ArrayList<>();
        data.vagvalList = new ArrayList<>();
        return data;
    }
}
