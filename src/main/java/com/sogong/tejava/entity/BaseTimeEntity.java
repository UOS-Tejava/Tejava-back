package com.sogong.tejava.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass // 추후에 엔티티가 상속받아 편하게 사용할 수 있게 하기 위함
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private String createdDate;

    @LastModifiedDate
    private String lastModifiedDate;

    @PrePersist
    public void onPrePersist() {
        this.createdDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.lastModifiedDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
    }
}