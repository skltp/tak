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
import se.skltp.tak.core.entity.PubVersion;
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

    @MockBean PubVersionService pubVersionServiceMock;
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
        when(mockSubject.hasRole("Administrator")).thenReturn(true);
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
        when(pubVersionServiceMock.scanForPrePublishedEntries()).thenReturn(mockData);

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
        when(pubVersionServiceMock.scanForPrePublishedEntries()).thenReturn(mockData);

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
        when(pubVersionServiceMock.scanForPrePublishedEntries()).thenReturn(mockData);

        mockMvc.perform(get("/pubversion/create")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(content().string(containsString("Granska publicering")));
    }

    @Test
    public void testPublish() throws Exception {
        PubVersion mockPv = new PubVersion();
        mockPv.setId(42);
        when(pubVersionServiceMock.add(any(PubVersion.class), eq("TEST_USER"))).thenReturn(mockPv);
        PublishDataWrapper mockData = getEmptyPublishData();
        mockData.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_USERNAME;
        when(pubVersionServiceMock.scanForEntriesAffectedByPubVer(42L)).thenReturn(mockData);

        mockMvc.perform(post("/pubversion/create")
                        .param("kommentar", "TEST_COMMENT")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pubversion/42"))
                .andExpect(flash().attribute("message", "Publicerad version 42 skapad."));

        verify(pubVersionServiceMock, times(1)).add(any(PubVersion.class), eq("TEST_USER"));
        verify(alerterServiceMock, times(1)).alertOnPublicering(any(PubVersion.class), anyList());
    }

    @Test
    public void testRollback() throws Exception {
        PubVersion mockPv = new PubVersion();
        when(pubVersionServiceMock.findById(42L)).thenReturn(mockPv);

        mockMvc.perform(get("/pubversion/rollback/42")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pubversion"))
                .andExpect(flash().attribute("message", "Rollback av Publicerad version 42 genomförd."));

        verify(pubVersionServiceMock, times(1)).rollback(42L, "TEST_USER");
        verify(alerterServiceMock, times(1)).alertOnRollback(mockPv);
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
