package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.util.PublishDataWrapper;

import java.time.LocalDate;
import java.util.List;

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
    public void testFindAllUsersBetweenDates() throws Exception {
        int offset = 0;
        int max = 10;
        String utforare = "all";
        LocalDate startDate = LocalDate.parse("2000-01-01");
        LocalDate endDate = LocalDate.parse("2024-01-01");

        PagedEntityList<PubVersion> list = service.getEntityList(offset, max, startDate, endDate, utforare);

        assertNotNull(list);
        assertEquals(3, list.getSize());
    }

    @Test
    public void testFindAllUsersBetweenDatesNoMatchingDates() throws Exception {
        int offset = 0;
        int max = 10;
        String utforare = "all";
        LocalDate startDate = LocalDate.parse("2000-01-01");
        LocalDate endDate = LocalDate.parse("2010-01-01");

        PagedEntityList<PubVersion> list = service.getEntityList(offset, max, startDate, endDate, utforare);

        assertNotNull(list);
        assertEquals(0, list.getSize());
    }

    @Test
    public void testFindUserBetweenDatesTwoMatches() throws Exception {
        int offset = 0;
        int max = 10;
        String utforare = "admin";
        LocalDate startDate = LocalDate.parse("2015-05-24");
        LocalDate endDate = LocalDate.parse("2015-07-04");

        PagedEntityList<PubVersion> list = service.getEntityList(offset, max, startDate, endDate, utforare);

        assertNotNull(list);
        assertEquals(2, list.getSize());
    }

    @Test
    public void testFindUserBetweenDatesNoMatchingUser() throws Exception {
        int offset = 0;
        int max = 10;
        String utforare = "TEST_USER";
        LocalDate startDate = LocalDate.parse("2015-05-01");
        LocalDate endDate = LocalDate.parse("2016-01-01");

        PagedEntityList<PubVersion> list = service.getEntityList(offset, max, startDate, endDate, utforare);

        assertNotNull(list);
        assertEquals(0, list.getSize());
    }

    @Test
    public void testScanForPrePublishedEntries() throws Exception {
        PublishDataWrapper data = service.scanForPrePublishedEntries();

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
        PublishDataWrapper data = service.scanForPendingEntriesByUsername("admin");

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
        PublishDataWrapper data = service.scanForPendingEntriesByUsername("other");

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
        PublishDataWrapper data = service.scanForEntriesAffectedByPubVer(1L);

        assertNotNull(data);
        assertEquals(4, data.rivTaProfilList.size());
        assertEquals(7, data.tjanstekontraktList.size());
        assertEquals(7, data.tjanstekomponentList.size());
        assertEquals(5, data.logiskAdressList.size());
        assertEquals(7, data.anropsAdressList.size());
        assertEquals(10, data.vagvalList.size());
        assertEquals(8, data.anropsbehorighetList.size());
        assertEquals(4, data.filterList.size());
        assertEquals(2, data.filtercategorizationList.size());
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

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testRollbackWrongVersion() throws Exception {
        assertThrows(OptimisticLockingFailureException.class, () ->
                service.rollback(2L, "TEST_USER")
        );
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testRollbackRemovesLatestVersion() throws Exception {
        assertNotNull(service.findById(3L));
        service.rollback(3L, "TEST_USER");

        assertThrows(IllegalArgumentException.class, () -> service.findById(3L));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testRollbackBringsBackRemovedObject() throws Exception {
        PublishDataWrapper pendingBefore = service.scanForPrePublishedEntries();
        assertEquals(1, pendingBefore.filtercategorizationList.size());

        service.rollback(3L, "");

        PublishDataWrapper pendingAfter = service.scanForPrePublishedEntries();
        assertEquals(2, pendingAfter.filtercategorizationList.size());
    }
}
