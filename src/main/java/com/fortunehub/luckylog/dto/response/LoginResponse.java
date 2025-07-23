package com.fortunehub.luckylog.dto.response;

import com.fortunehub.luckylog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 시 반환되는 정보를 담는 응답")
public record LoginResponse(
    @Schema(description = "인증에 사용되는 Access Token (JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    @Schema(description = "Token 유형", example = "Bearer")
    String tokenType,
    @Schema(description = "로그인한 사용자 정보")
    UserResponse userResponse
) {

    public static LoginResponse of(String accessToken, User user){
        return new LoginResponse(accessToken, "Bearer", UserResponse.from(user));
    }
}
