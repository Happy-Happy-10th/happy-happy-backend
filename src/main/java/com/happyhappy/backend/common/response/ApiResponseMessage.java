package com.happyhappy.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseMessage {

    private final int status;
    private final String message;
    private final Object data;
    private final String code;
    private final LocalDateTime timeStamp;

    public ApiResponseMessage(ApiResponseCode code, Object data) {
        this.status = code.getHttpStatus();
        this.message = code.getMessageKo();
        this.data = data;
        this.code = code.getErrorCode();
        this.timeStamp = LocalDateTime.now();
    }

    // 성공 응답용
    public static ApiResponseMessage success(Object data, String message) {
        return new ApiResponseMessage(200, message, data, null);
    }

    public static ApiResponseMessage success(Object data) {
        return success(data, "정상처리되었습니다.");
    }

    // 에러 응답용
    public static ApiResponseMessage error(int status, String message, String code) {
        return new ApiResponseMessage(status, message, null, code);
    }

    private ApiResponseMessage(int status, String message, Object data, String code) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;
        this.timeStamp = LocalDateTime.now();
    }
}
