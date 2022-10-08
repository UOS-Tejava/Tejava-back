package com.sogong.tejava.controller;

import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.service.StockService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    private final StockService stockService;

    public EmployeeController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/employee/showStockInfo")
    @ApiOperation(value = "재고 현황 보여주기", notes = "재료와 마실 것(커피, 와인, 샴페인의 재고를 인분을 기준으로 보여줍니다.")
    public ResponseEntity<List<StockItem>> showStockInfo() {

        List<StockItem> stockItemList = stockService.showStockInfo();
        return ResponseEntity.ok().body(stockItemList);
    }
}
