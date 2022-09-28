package com.sogong.tejava.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String menuNm;

    private String menuConfig; // 메뉴 구성요소
    private String imagePath;

    @OneToMany(targetEntity = Style.class) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name="ms_fk", referencedColumnName = "id") // foreign key (userId) references User (id)
    private List<Style> styles;

    @OneToMany(targetEntity = Option.class) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name="mo_fk", referencedColumnName = "id") // foreign key (userId) references User (id)
    private List<Option> options;

    private String price;
}
