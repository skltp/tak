package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.util.PublishDataWrapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PubVersionServiceTests {

    @Autowired
    PubVersionService service;

    @Test
    public void testGetEntityList() throws Exception {
        PagedEntityList<PubVersion> list = service.getEntityList(0,10);

        assertNotNull(list);
        assertEquals(3, list.getSize());
    }

    @Test
    public void testScanForPrePublishedEntries() throws Exception {
        PublishDataWrapper data = service.ScanForPrePublishedEntries();

        assertNotNull(data);
        assertEquals(5, data.rivTaProfilList.size());
        assertEquals(2, data.tjanstekontraktList.size());
        assertEquals(2, data.tjanstekomponentList.size());
        assertEquals(2, data.logiskAdressList.size());
        assertEquals(3, data.anropsAdressList.size());
        assertEquals(3, data.vagvalList.size());
        assertEquals(1, data.anropsbehorighetList.size());
        assertEquals(1, data.filterList.size());
        assertEquals(1, data.filtercategorizationList.size());
    }

    @Test
    public void testScanForPendingEntriesByUsername() throws Exception {
        PublishDataWrapper data = service.ScanForPendingEntriesByUsername("admin");

        assertNotNull(data);
        assertEquals(4, data.rivTaProfilList.size());
        assertEquals(1, data.tjanstekontraktList.size());
        assertEquals(1, data.tjanstekomponentList.size());
        assertEquals(1, data.logiskAdressList.size());
        assertEquals(2, data.anropsAdressList.size());
        assertEquals(3, data.vagvalList.size());
        assertEquals(1, data.anropsbehorighetList.size());
        assertEquals(1, data.filterList.size());
        assertEquals(1, data.filtercategorizationList.size());
        assertEquals(0, data.getPublishErrors("admin").size());
    }

    @Test
    public void testScanForPendingEntriesByUsernameWithNoChanges() throws Exception {
        PublishDataWrapper data = service.ScanForPendingEntriesByUsername("other");

        assertNotNull(data);
        assertEquals(0, data.rivTaProfilList.size());
        assertEquals(0, data.tjanstekontraktList.size());
        assertEquals(0, data.tjanstekomponentList.size());
        assertEquals(0, data.logiskAdressList.size());
        assertEquals(0, data.anropsAdressList.size());
        assertEquals(0, data.vagvalList.size());
        assertEquals(0, data.anropsbehorighetList.size());
        assertEquals(0, data.filterList.size());
        assertEquals(0, data.filtercategorizationList.size());
        assertEquals(1, data.getPublishErrors("other").size());
    }

    @Test
    public void testScanForEntriesAffectedByPubVer() throws Exception {
        PublishDataWrapper data = service.ScanForEntriesAffectedByPubVer(1L);

        assertNotNull(data);
        assertEquals(4, data.rivTaProfilList.size());
        assertEquals(7, data.tjanstekontraktList.size());
        assertEquals(7, data.tjanstekomponentList.size());
        assertEquals(5, data.logiskAdressList.size());
        assertEquals(7, data.anropsAdressList.size());
        assertEquals(10, data.vagvalList.size());
        assertEquals(8, data.anropsbehorighetList.size());
        assertEquals(4, data.filterList.size());
        assertEquals(3, data.filtercategorizationList.size());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAdd() throws Exception {
        PubVersion pv = new PubVersion();
        pv.setKommentar("TESTING");
        PubVersion pvAfter = service.add(pv, "admin");

        assertNotNull(pvAfter);
        assertEquals("TESTING", pvAfter.getKommentar());
        assertEquals("admin", pvAfter.getUtforare());
        assertEquals(1, pvAfter.getFormatVersion());
        assertEquals(4L, pvAfter.getId());
    }
}
