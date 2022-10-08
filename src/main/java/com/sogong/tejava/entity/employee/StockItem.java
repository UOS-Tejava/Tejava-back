package com.sogong.tejava.entity.employee;

import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="stock_item")
public class StockItem  extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockItemNm;
    private String stockItemPic;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
