package com.sogong.tejava.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.validate.config.converter.RoleConverter;
import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(exclude = {"id", "pwd"})
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String pwd;
    private String name;
    private String address;
    private String phoneNo;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private Boolean phone_check; // default : false -> TINYINT(1)
    private Boolean agreement; // default : true -> TINYINT(1)

    private int order_cnt; // 주문 횟수

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private OrderHistory orderHistory;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private ShoppingCart shoppingCart;
}