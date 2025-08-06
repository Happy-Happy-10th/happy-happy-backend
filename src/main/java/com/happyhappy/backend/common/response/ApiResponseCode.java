package com.happyhappy.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiResponseCode {
    BAD_REQUEST(400, "COMMON-ERR-400", "Request 오류", "BAD REQUEST"),
    UNAUTHORIZED(401, "COMMON-ERR-401", "인증실패", "Authentication Failed"),
    NOT_FOUND(404, "COMMON-ERR-404", "페이지를 찾을 수 없습니다.", "PAGE NOT FOUND"),
    INTER_SERVER_ERROR(500, "COMMON-ERR-500", "내부서버오류", "INTER SERVER ERROR"),
    COMMON_SUCCESS_000001(200, null, "정상처리되었습니다.", "Request success"),
    COMMON_ERROR_000001(200, "COMMON-ERR-000001", "필수값이 누락되었습니다.", "Missing required value"),
    COMMON_ERROR_000002(401, "COMMON-ERR-000002", "인증실패", "Authentication Failed"),
    COMMON_ERROR_000003(200, "COMMON-ERR-000003", "해당 정보를 찾을수 없음", "Authentication Failed"),
    MEMBER_SUCCESS_000001(200, null, "인증번호가 확인되었습니다.",
            "The authentication number has been verified."),
    MEMBER_SUCCESS_000002(200, null, "인증번호가 발송 되었습니다.<br/>인증번호를 입력해 주세요.",
            "The verification number has been sent.<br/>Please enter the verification number."),
    MEMBER_SUCCESS_000003(200, null, "입력한 이메일 주소로<br/>임시 비밀번호가 발송 되었습니다.",
            "A temporary password has been sent to the email address you entered."),
    MEMBER_ERR_000001(200, "MEMBER-ERR-000001", "등록되어 있지 않은 아이디 입니다.",
            "This ID is not registered."),
    MEMBER_ERR_000002(200, "MEMBER-ERR-000002", "비밀번호가 올바르지 않습니다.", "Your password is incorrect."),
    MEMBER_ERR_000003(200, "MEMBER-ERR-000003", "입력된 정보와 일치하는 아이디를 찾을 수 없습니다.",
            "Could not find an ID that matches the information entered."),
    MEMBER_ERR_000004(200, "MEMBER-ERR-000004", "발급된 인증번호를 찾을 수 없습니다.",
            "Could not find issued authentication code."),
    MEMBER_ERR_000005(200, "MEMBER-ERR-000005", "인증번호가 일치하지 않습니다.",
            "The verification code does not match."),
    MEMBER_ERR_000006(200, "MEMBER-ERR-000006", "이미 등록되어 있는 아이디 입니다.",
            "This ID is already registered."),
    ROLES_ERR_000001(200, "MEMBER-ERR-000006", "해당 권한을 가진 유저가 존재합니다.",
            "A user with that privilege exists."),
    ROLES_ERR_000002(200, "MEMBER-ERR-000006", "해당 권한 이름이 존재합니다.", "The permission name exists."),
    REQUEST_SUCCESS_000001(200, null, "저장되었습니다.", "Successfully saved."),
    REQUEST_SUCCESS_000002(200, null, "삭제되었습니다.", "It has been deleted."),
    CODECREATE_ERR_000001(200, "CODECREATE_ERR_000001", "유효성검사 실패",
            "This action is validation check failed."),
    CALENDAR_SUCCESS_000001(200, null, "캘린더 설정이 변경되었습니다.", "Calendar settings updated"),
    CALENDAR_ERR_000001(200, "CALENDAR-ERR-000001", "캘린더를 찾을 수 없습니다.", "Calendar not found"),
    CALENDAR_ERR_000002(200, "CALENDAR-ERR-000002", "유효하지 않은 지역 코드입니다.", "Invalid region code"),
    ;

    private final int httpStatus;
    private final String errorCode;
    private final String messageKo;
    private final String messageEn;
}
