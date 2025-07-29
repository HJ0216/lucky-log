package com.fortunehub.luckylog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyProfileImageUpdateRequest(
    @Schema(description = "사용자 프로필 이미지 url", example = "https://mybucket.s3.amazonaws.com/profiles/user-1-profile.jpg")
    String url) {

}
