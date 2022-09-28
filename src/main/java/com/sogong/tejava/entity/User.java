package com.sogong.tejava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String pwd;
    private String name;
    private String address;
    private Role role;
    private Boolean phoneCheck;

    @OneToMany(targetEntity = OrderHistory.class) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name="uo_fk", referencedColumnName = "id") // foreign key (userId) references User (id)
    private List<OrderHistory> orderHistories;
}
