package com.linkpulse.auth;

import com.linkpulse.api.ApiResponse;
import com.linkpulse.trace.TraceIdFilter;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("auth-ok", MDC.get(TraceIdFilter.TRACE_ID));
    }

}
