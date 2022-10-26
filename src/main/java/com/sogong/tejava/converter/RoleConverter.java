package com.sogong.tejava.converter;

import com.sogong.tejava.entity.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter // 해당 변환 클래스에 지정된 타입에 대해서는 모두 해당 변환 클래스의 메소드를 이용해 DB 와의 통신에서 값을 변환하려면 @Converter(autoApply = true) 옵션
public class RoleConverter implements AttributeConverter<Role, String> {


    @Override
    public String convertToDatabaseColumn(Role role) {
        switch (role) {
            case USER:
                return "user";
            case ADMINISTRATOR:
                return "administrator";
            case NOT_MEMBER:
                return "not_member";
            default:
                throw new IllegalArgumentException("UserRole [" + role + "] not supported");
        }
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        switch (dbData) {
            case "user":
                return Role.USER;
            case "administrator":
                return Role.ADMINISTRATOR;
            case "not_member":
                return Role.NOT_MEMBER;

            default:
                throw new IllegalArgumentException("UserRole [" + dbData + "] not supported");
        }
    }
}
