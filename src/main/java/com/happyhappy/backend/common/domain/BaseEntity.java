package com.happyhappy.backend.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Comment("생성일시")
    @Column(name = "CREATE_AT")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Comment("수정일시")
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
