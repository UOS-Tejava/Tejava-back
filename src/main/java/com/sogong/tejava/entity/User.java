package com.sogong.tejava.entity;

import com.sogong.tejava.converter.RoleConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = "pwd")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String pwd;
    private String name;
    private String address;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private Boolean phoneCheck; // default : false -> TINYINT(1)
    private Boolean agreement; // default : true -> TINYINT(1)

    @OneToMany(mappedBy = "User")
    private List<OrderHistory> orderHistories  = new ArrayList<>();
}
