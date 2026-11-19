/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.skltp.tak.web.dto.bestallning.BestallningsData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Opt-in load test for large orders (NTU-1641). Run with:
 * <pre>mvn test -pl tak-web -Dtest=BestallningLoadTest -Dtak.loadtest=true</pre>
 * Optional: -Dtak.loadtest.la=1000 -Dtak.loadtest.kontrakt=8 -Dtak.loadtest.konsumenter=0
 */
@EnabledIfSystemProperty(named = "tak.loadtest", matches = "true")
@SpringBootTest(properties = {
        // Separate in-memory DB so the generated data never leaks into other test contexts
        "spring.datasource.url=jdbc:h2:mem:loadtest;DB_CLOSE_DELAY=-1",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
class BestallningLoadTest {

    private static final Logger log = LoggerFactory.getLogger(BestallningLoadTest.class);
    private static final String USER = "LOADTEST_USER";

    @MockitoBean ConfigurationService configurationMock;
    @Autowired BestallningService service;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test
    void largeOrder() throws Exception {
        int la = Integer.getInteger("tak.loadtest.la", 1000);
        int kontrakt = Integer.getInteger("tak.loadtest.kontrakt", 8);
        int konsumenter = Integer.getInteger("tak.loadtest.konsumenter", 0);

        String json = LargeOrderGenerator.generate(la, kontrakt, konsumenter);
        int rawBytes = json.getBytes(StandardCharsets.UTF_8).length;
        int formBytes = URLEncoder.encode(json, StandardCharsets.UTF_8).length();

        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        long t0 = System.nanoTime();
        String formatted = service.parseAndFormatJson(json);
        long parseMs = (System.nanoTime() - t0) / 1_000_000;

        stats.clear();
        t0 = System.nanoTime();
        BestallningsData data = service.buildBestallningsData(formatted, USER);
        long buildMs = (System.nanoTime() - t0) / 1_000_000;
        long buildQueries = stats.getPrepareStatementCount();
        assertFalse(data.hasErrors(), () -> String.join("\n", data.getBestallningErrors()));

        // save() rebuilds the data from the session JSON before executing, so measure both
        stats.clear();
        t0 = System.nanoTime();
        BestallningsData saveData = service.buildBestallningsData(formatted, USER);
        service.execute(saveData, USER);
        long saveMs = (System.nanoTime() - t0) / 1_000_000;
        long saveStatements = stats.getPrepareStatementCount();

        Runtime rt = Runtime.getRuntime();
        long usedHeapMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);

        log.info("""
                        LOADTEST la={} kontrakt={} konsumenter={} -> vagval={} anropsbehorigheter={}
                          json raw={} KB, form-urlencoded={} KB
                          parseAndFormat={} ms
                          confirm (build)={} ms, statements={}
                          save (rebuild+execute)={} ms, statements={}
                          heap used after={} MB (max {} MB)""",
                la, kontrakt, konsumenter, la * kontrakt, la * kontrakt * konsumenter,
                rawBytes / 1024, formBytes / 1024, parseMs, buildMs, buildQueries, saveMs, saveStatements,
                usedHeapMb, rt.maxMemory() / (1024 * 1024));
    }
}
