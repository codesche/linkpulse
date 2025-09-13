package com.linkpulse.link.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLinkRequest {

    @NotBlank
    @Size(max = 2048)
    private String originalUrl;

    // 선택: 커스텀 별칭
    @Size(min = 5, max = 20, message = "별칭은 5~20자")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "별칭은 영문/숫자/-/_ 만 허용")
    private String alias;

}
