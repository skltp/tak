package se.skltp.tak.web.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;
import se.skltp.tak.web.repository.PubVersionRepository;

/**
 * Unit test for {@link PubVersionService}.
 */
@ExtendWith(MockitoExtension.class)
class PubVersionServiceTest {

    @Mock private PubVersionRepository repository;

    // Collaborating services invoked from addUpdateAndDeleteAllTypesToCache
    @Mock private RivTaProfilService rivTaProfilService;
    @Mock private TjanstekontraktService tjanstekontraktService;
    @Mock private TjanstekomponentService tjanstekomponentService;
    @Mock private LogiskAdressService logiskAdressService;
    @Mock private AnropsAdressService anropsAdressService;
    @Mock private VagvalService vagvalService;
    @Mock private AnropsBehorighetService anropsBehorighetService;
    @Mock private FilterService filterService;
    @Mock private FilterCategorizationService filterCategorizationService;

    private PubVersionService service;

    @BeforeEach
    void setUp() {
        // Create raw instance and inject mocks via reflection (we are outside Spring context)
        PubVersionService raw = new PubVersionService(repository);
        ReflectionTestUtils.setField(raw, "rivTaProfilService", rivTaProfilService);
        ReflectionTestUtils.setField(raw, "tjanstekontraktService", tjanstekontraktService);
        ReflectionTestUtils.setField(raw, "tjanstekomponentService", tjanstekomponentService);
        ReflectionTestUtils.setField(raw, "logiskAdressService", logiskAdressService);
        ReflectionTestUtils.setField(raw, "anropsAdressService", anropsAdressService);
        ReflectionTestUtils.setField(raw, "vagvalService", vagvalService);
        ReflectionTestUtils.setField(raw, "anropsBehorighetService", anropsBehorighetService);
        ReflectionTestUtils.setField(raw, "filterService", filterService);
        ReflectionTestUtils.setField(raw, "filterCategorizationService", filterCategorizationService);

        // Turn into a spy so we can verify internal helper invocation
        service = spy(raw);
    }

    /**
     * This test is run manually to be able to profile the JVM during the run. It tests the merger of a previous and
     * a new PubVersion, to see if memory usage is in a reasonable range.
     * @throws Exception
     */
    @Test
    @Disabled
    void mergeNewPubverDataOnOldPubverSnapshot() throws Exception {
        // ─── Arrange ────────────────────────────────────────────────────────
        // Add the path to your own pubversion gzip here.
        Blob oldBlob = getBlob("<PATH-TO-MY-PUBVERSION-GZIP>/PubVersion-nnnn.gzip");
        PubVersion oldPv = new PubVersion();
        oldPv.setId(1L);
        oldPv.setData(oldBlob);
        oldPv.setStorlek(oldBlob.length());

        // Add the path to your own pubversion gzip here.
        Blob newBlob = getBlob("<PATH-TO-MY-PUBVERSION-GZIP>/PubVersion-nnnn.gzip");
        PubVersion newPv = new PubVersion();
        newPv.setId(2L);
        newPv.setTime(new Date());
        newPv.setUtforare("tester");
        newPv.setFormatVersion(1);
        newPv.setKommentar("integration-test");
        newPv.setData(newBlob);
        newPv.setStorlek(newBlob.length());

        // ─── Act ────────────────────────────────────────────────────────────
        PubVersion merged = service.mergeNewPubverDataOnOldPubverSnapshot(newPv, oldPv, "tester");

        // ─── Assert ────────────────────────────────────────────────────────
        assertSame(newPv, merged);
        assertNotNull(merged.getData());
        assertTrue(merged.getStorlek() > 0);

        // None of the save calls should throw; optionally verify zero or any interactions
        verify(rivTaProfilService, atMostOnce()).save(any());
        verify(tjanstekontraktService, atMostOnce()).save(any());
        verify(tjanstekomponentService, atMostOnce()).save(any());
        verify(logiskAdressService, atMostOnce()).save(any());
        verify(anropsAdressService, atMostOnce()).save(any());
        verify(vagvalService, atMostOnce()).save(any());
        verify(anropsBehorighetService, atMostOnce()).save(any());
        verify(filterService, atMostOnce()).save(any());
        verify(filterCategorizationService, atMostOnce()).save(any());
    }

    private Blob getBlob(String blobPath)
    {
        Blob snapshotBlob = null;
        try {
            // --- 1. Read the raw bytes from disk ------------------------------
            byte[] compressedBytes = Files.readAllBytes(Paths.get(blobPath));

            // --- 2. Wrap them in a Blob ---------------------------------------
            snapshotBlob = new SerialBlob(compressedBytes);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return snapshotBlob;
    }
}
