package com.sogong.tejava.entity.style;

import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="style_item")
public class StyleItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String style_nm;
    private String style_config;
    private String style_pic;
    private int price;
}
