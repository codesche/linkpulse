package com.linkpulse.link.controller;

import com.linkpulse.api.ApiResponse;
import com.linkpulse.link.dto.request.CreateLinkRequest;
import com.linkpulse.link.dto.response.CreateLinkResponse;
import com.linkpulse.link.service.LinkService;
import com.linkpulse.trace.TraceIdFilter;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/links")
public class LinkCommandController {

    private final LinkService linkService;

    // NOTE: 추후 JWT 연동 시 SecurityContext에서 memberId 추출로 대체
    @PostMapping
    public ApiResponse<CreateLinkResponse> create(
        @RequestHeader("X-Member-Id") String memberIdHeader,
        @RequestBody @Valid CreateLinkRequest req
    ) {
        UUID ownerId = UUID.fromString(memberIdHeader);
        CreateLinkResponse res = linkService.create(ownerId, req);
        return ApiResponse.ok(res, MDC.get(TraceIdFilter.TRACE_ID));
    }

}
