package com.sogong.tejava.entity;

import com.sogong.tejava.converter.RoleConverter;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = "pwd")
@Table(name="user")
public class User {

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

    @OneToMany(mappedBy = "user")
    private List<OrderHistory> orderHistory  = new ArrayList<>();
}
