package com.sogong.tejava.domain.dto;

import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema
public class UserDTO {

    private Long id;
    private String uid;
    private String name;
    private String address;
    private Role role;

    private int order_cnt;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .uid(user.getUid())
                .name(user.getName())
                .address(user.getAddress())
                .role(user.getRole())
                .order_cnt(user.getOrder_cnt())
                .build();
    }
}