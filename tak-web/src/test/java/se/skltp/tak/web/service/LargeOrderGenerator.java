/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a TakOrder JSON shaped like a mass-TAKning order: one producer, N logical addresses,
 * M contracts, a vagval for every LA x contract and optionally anropsbehorigheter for K consumers.
 */
final class LargeOrderGenerator {

    private LargeOrderGenerator() {
    }

    static String generate(int logiskaAdresser, int kontrakt, int konsumenter) throws JsonProcessingException {
        String producer = "LOADTEST-PROD-001";

        List<Map<String, Object>> komponenter = new ArrayList<>();
        komponenter.add(Map.of("hsaId", producer, "beskrivning", "Loadtest producent"));
        for (int k = 1; k <= konsumenter; k++) {
            komponenter.add(Map.of("hsaId", String.format("LOADTEST-KONS-%03d", k), "beskrivning", "Loadtest konsument " + k));
        }

        List<Map<String, Object>> kontraktList = new ArrayList<>();
        for (int c = 1; c <= kontrakt; c++) {
            kontraktList.add(Map.of(
                    "namnrymd", namnrymd(c),
                    "majorVersion", 1,
                    "beskrivning", "Loadtest kontrakt " + c));
        }

        List<Map<String, Object>> laList = new ArrayList<>();
        List<Map<String, Object>> vagval = new ArrayList<>();
        List<Map<String, Object>> behorigheter = new ArrayList<>();
        for (int l = 1; l <= logiskaAdresser; l++) {
            String la = String.format("SE2321000000-LT%05d", l);
            laList.add(Map.of("hsaId", la, "beskrivning", "Loadtest avdelning " + l));
            for (int c = 1; c <= kontrakt; c++) {
                vagval.add(Map.of(
                        "tjanstekomponent", producer,
                        "logiskAdress", la,
                        "tjanstekontrakt", namnrymd(c),
                        "rivtaprofil", "RIVTABP21",
                        "adress", "https://rtjp.loadtest.sjunet.org/csp/rtp/loadtest/contract" + c + "/1/rivtabp21"));
                for (int k = 1; k <= konsumenter; k++) {
                    behorigheter.add(Map.of(
                            "tjanstekonsument", String.format("LOADTEST-KONS-%03d", k),
                            "logiskAdress", la,
                            "tjanstekontrakt", namnrymd(c)));
                }
            }
        }

        Map<String, Object> inkludera = new LinkedHashMap<>();
        inkludera.put("tjanstekomponenter", komponenter);
        inkludera.put("tjanstekontrakt", kontraktList);
        inkludera.put("logiskadresser", laList);
        inkludera.put("anropsbehorigheter", behorigheter);
        inkludera.put("vagval", vagval);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("plattform", "SKLTP-TEST");
        order.put("formatVersion", "1.0");
        order.put("version", "1");
        order.put("bestallningsTidpunkt", "2026-11-10T10:16:04+0100");
        order.put("utforare", "Loadtest");
        order.put("genomforandeTidpunkt", "2026-11-10T10:16:04+0100");
        order.put("inkludera", inkludera);
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order);
    }

    private static String namnrymd(int c) {
        return "urn:riv:loadtest:contract" + c + ":LoadtestResponder:1";
    }
}
