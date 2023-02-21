package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.util.PublishDataWrapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PubVersionServiceTests {

    @Autowired PublicationVersionService service;

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
}
