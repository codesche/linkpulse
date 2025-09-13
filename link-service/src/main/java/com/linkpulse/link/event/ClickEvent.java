package com.linkpulse.link.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickEvent {

    private UUID linkId;
    private String shortCode;
    private UUID ownerId;
    private Instant occurredAt;
    private String referer;
    private String userAgent;
    private String clientIp;

}
