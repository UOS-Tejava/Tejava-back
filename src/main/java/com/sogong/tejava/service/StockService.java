package com.sogong.tejava.service;

import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    /*
    1. 재고 현황 보여주기
     */

    private final StockRepository stockRepository;
    public List<StockItem> showStockInfo() {
        return stockRepository.findAll();
    }
}
