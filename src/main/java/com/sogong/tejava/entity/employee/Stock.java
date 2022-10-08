package com.sogong.tejava.entity.employee;

import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "stock")
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "stock")
    private List<StockItem> stockItem = new ArrayList<>();
}
