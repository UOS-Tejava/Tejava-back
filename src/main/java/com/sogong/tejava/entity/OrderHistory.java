package com.sogong.tejava.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column // 생성된 이후, 수정 가능할 수 있어야 하므로 updatable 옵션은 true 로 둠
    private LocalDateTime timeStamp;

    private String option;

    @ManyToOne(fetch = FetchType.LAZY) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name = "user_id")
    private User user;
}
