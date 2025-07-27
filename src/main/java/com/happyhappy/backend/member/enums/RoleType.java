//package com.happyhappy.backend.member.enums;
//
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//
//@Getter
//@RequiredArgsConstructor
//public enum RoleType {
//
//    USER("ROLE_USER", "일반 사용자"),
//    ADMIN("ROLE_ADMIN", "관리자"),
//    SUPER_ADMIN("ROLE_SUPER_ADMIN", "최고 관리자");
//
//    private final String Key;
//    private final String description;
//
//    /**
//     * Spring Security에서 사용할 권한 문자열 반환
//     *
//     * @return "ROLE_" 접두사가 포함된 권한 문자열
//     */
//    public String getAuthority() {
//        return this.getKey();
//    }
//
//    public static RoleType fromKey(String key) {
//        // 접두사가 없는 경우 처리
//        String searchKey = key.startsWith("ROLE_") ? key : "ROLE_" + key;
//
//        for (RoleType roleType : values()) {
//            if (roleType.getAuthority().equals(searchKey)) {
//                return roleType;
//            }
//        }
//        throw new IllegalArgumentException("키를 가진 열거형 ㅅ앙수가 없습니다.:" + key);
//    }
//}
