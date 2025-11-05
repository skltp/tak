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
