package com.happyhappy.backend.common.response;

import lombok.Getter;

@Getter
public class ApiResponseMessage {

    private final int status;
    private final String message;
    private final Object data;

    public ApiResponseMessage(ApiResponseCode code, Object data) {
        this.status = code.getHttpStatus();
        this.message = code.getMessageKo();
        this.data = data;
    }
}
