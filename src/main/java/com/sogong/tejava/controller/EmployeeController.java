package com.sogong.tejava.controller;

import com.sogong.tejava.dto.ChangeOrderStatusDTO;
import com.sogong.tejava.dto.UserIdDTO;
import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 주문 현황 보여주기
    @PostMapping("/employee/orders")
    @ApiOperation(value = "주문 현황 조회하기", notes = "직원 인터페이스의 홈화면에서 접수된 주문 목록을 확인할 수 있습니다.")
    public ResponseEntity<List<Order>> showOrders(HttpServletRequest request, @RequestBody UserIdDTO userIdDTO) {

        List<Order> orderList = employeeService.getOrderList(request, userIdDTO);
        return ResponseEntity.ok().body(orderList);
    }

    // 주문 상태 바꾸기 (pending, cooking, delivering, done)
    @PatchMapping("/employee/orderStatus")
    @ApiOperation(value = "주문 상태 바꾸기", notes = "pending(접수 대기 중), cooking(조리 중), delivering(배달 중), completed(배달 완료)로 총 4가지의 주문 상태가 있습니다.")
    public ResponseEntity<?> updateOrderStatus(HttpServletRequest request, @RequestBody UserIdDTO userIdDTO, @RequestBody ChangeOrderStatusDTO changeOrderStatusDTO) {

        employeeService.updateOrderStatus(request, userIdDTO, changeOrderStatusDTO);
        return ResponseEntity.ok().build();
    }

    // 재고 현환 보여주기
    @PostMapping("/employee/stock-info")
    @ApiOperation(value = "재고 현황 보여주기", notes = "재료와 마실 것(커피, 와인, 샴페인의 재고를 인분을 기준으로 보여줍니다.")
    public ResponseEntity<List<StockItem>> showStockInfo(HttpServletRequest request, @RequestBody UserIdDTO userIdDTO) {

        List<StockItem> stockItemList = employeeService.showStockInfo(request, userIdDTO);
        return ResponseEntity.ok().body(stockItemList);
    }
}
