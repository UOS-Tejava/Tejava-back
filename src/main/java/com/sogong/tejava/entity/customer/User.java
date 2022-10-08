package com.sogong.tejava.entity.customer;

import com.sogong.tejava.converter.RoleConverter;
import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.OrderHistory;
import com.sogong.tejava.entity.customer.ShoppingCart;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString(exclude = "pwd")
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

    private Boolean phoneCheck; // default : false -> TINYINT(1)
    private Boolean agreement; // default : true -> TINYINT(1)

    private Integer orderCnt = 0; // 주문 횟수

    @OneToOne(mappedBy = "user")
    private OrderHistory orderHistory;

    @OneToOne(mappedBy = "user")
    private ShoppingCart shoppingCart;
}
