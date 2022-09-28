package com.sogong.tejava.entity.employee;

import com.sogong.tejava.entity.Option;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(targetEntity = StockItem.class) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name="ss_fk", referencedColumnName = "id") // foreign key (userId) references User (id)
    private List<StockItem> stockItems;
}
