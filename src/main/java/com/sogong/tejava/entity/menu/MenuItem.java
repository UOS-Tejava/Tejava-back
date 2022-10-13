package com.sogong.tejava.entity.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.options.OptionsItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="menu_item")
public class MenuItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem")
    private List<OptionsItem> optionsItem = new ArrayList<>();
}
