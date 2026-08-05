/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.configuration.AaaEnabledCondition;

/**
 * Feign client for the AAA service. The bean is only created when {@code aaa.url} is set to a
 * non-blank value; a missing or empty/blank value disables the AAA integration.
 * <p>
 * One URL is analyzed per request (GET), so that a failing analysis only affects that single
 * connection instead of an entire batch.
 */
@Conditional(AaaEnabledCondition.class)
@FeignClient(name = "aaaClient", url = "${aaa.url}")
public interface AaaClient {
    String SUCCESS = "SUCCESS";

    @GetMapping("/api/v1/aaa/analyze")
    AnalysisResultV1 analyze(@RequestParam("url") String url, @RequestParam("method") String method);
}
