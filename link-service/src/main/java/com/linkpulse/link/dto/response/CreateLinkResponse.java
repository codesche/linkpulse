package com.linkpulse.link.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLinkResponse {

    private String id;
    private String shortCode;
    private String originalUrl;

}
