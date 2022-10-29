package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserDTO {

    private String uid;
    private String name;
    private String address;

    private int order_cnt;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .uid(user.getUid())
                .name(user.getName())
                .address(user.getAddress())
                .order_cnt(user.getOrder_cnt())
                .build();
    }
}
