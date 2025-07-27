//package com.happyhappy.backend.member.domain;
//
//import com.happyhappy.backend.member.enums.RoleType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.Id;
//import jakarta.persistence.IdClass;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import java.util.UUID;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.Comment;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "MEMBER_ROLE")
//@IdClass(RoleId.class)
//public class Role {
//
//    @Id
//    @Column(name = "MEMBER_ID")
//    private UUID memberId;
//
//    @Id
//    @NotNull
//    @Enumerated(EnumType.STRING)
//    @Column(name = "ROLE_TYPE")
//    @Comment("권한 유형")
//    private RoleType roleType;
//
//    @ManyToOne
//    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
//    private Member member;
//
//    /**
//     * Spring Security에서 사용할 권한 문자열 반환
//     *
//     * @return 권한 문자열
//     */
//    public String getAuthority() {
//        return this.roleType.getAuthority();
//    }
//}
