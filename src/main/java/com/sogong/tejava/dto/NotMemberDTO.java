package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotMemberDTO {

    private Long id;
    private String uid;
    private String name;
    private Role role;

    public static NotMemberDTO fromNotMember(User user) {
        return NotMemberDTO.builder()
                .id(user.getId())
                .uid("비회원")
                .name("비회원")
                .role(user.getRole())
                .build();
    }
}