package com.sogong.tejava.entity.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.Data;
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
@Table(name = "stock")
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "stock")
    private List<StockItem> stockItem = new ArrayList<>();
}
