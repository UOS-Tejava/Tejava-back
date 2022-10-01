package com.sogong.tejava.entity.employee;

import com.sogong.tejava.entity.Menu;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="stock_item")
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemNm;
    private String imagePath;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
