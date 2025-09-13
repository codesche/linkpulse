package com.linkpulse.auth.dto.request;

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
public class SignUpRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "유저명은 알파벳 포함 가능함")
    private String username;

    @NotBlank
    @Size(min = 8, max = 72, message = "비밀번호는 최소 8자리에서 최대 72자리까지")
    private String password;

    @NotBlank
    @Size(min = 2, max = 30)
    private String nickName;

}
