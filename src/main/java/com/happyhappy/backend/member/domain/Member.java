package com.happyhappy.backend.member.domain;


import com.happyhappy.backend.calendar.domain.Calendar;
import com.happyhappy.backend.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
public class Member extends BaseEntity implements UserDetails {

    @Id
    @UuidGenerator
    @Column(name = "MEMBER_ID", columnDefinition = "BINARY(16)")
    @Comment("회원 고유 식별자")
    private UUID memberId;

    @Email
    @NotNull
    @Column(name = "USERNAME", unique = true)
    @Comment("사용자 이메일")
    private String username;

    @Column(name = "USERID", unique = true)
    @Comment("사용자 로그인 아이디")
    private String userId;

    @Column(name = "NICKNAME")
    @Comment("사용자 이름")
    private String nickname;

    @Setter
    @NotNull
    @Column(name = "PASSWORD")
    @Comment("사용자 로그인 PW")
    private String password;

    @Column(name = "IMAGE_URL")
    @Comment("프로필 이미지 URL")
    private String imageUrl;

    @NotNull
    @Column(name = "IS_ACTIVE")
    @Comment("활성화 여부")
    private Boolean isActive;

    @Column(name = "MARKETING_AGREED_AT")
    @Comment("마케팅 수신 동의 시각")
    private LocalDateTime marketingAgreedAt;

    @Column(name = "WITHDRAWN_AT")
    @Comment("탈퇴 시각")
    private LocalDateTime withdrawnAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    public void createCalendar() {
        if (this.calendar == null) {
            this.calendar = Calendar.builder().build();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
