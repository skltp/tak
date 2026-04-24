/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import se.skltp.tak.web.aaa.client.model.AnalysisRequestV1;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;

import java.util.List;

@ConditionalOnProperty(name = "aaa.url")
@FeignClient(name = "aaaClient", url = "${aaa.url}")
public interface AaaClient {
    String SUCCESS = "SUCCESS";

    @PostMapping("/api/v1/aaa/analyze")
    List<AnalysisResultV1> analyze(@RequestBody List<AnalysisRequestV1> requests);
}
