package com.sogong.tejava.entity.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name="stock_item")
public class StockItem  extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stock_item_nm;
    private String stock_item_pic;
    private int quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
