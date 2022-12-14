package com.sogong.tejava.controller;

import com.sogong.tejava.domain.dto.ChangeOrderStatusDTO;
import com.sogong.tejava.domain.dto.ChangeStockInfoDTO;
import com.sogong.tejava.domain.dto.GetOrderListResponseDTO;
import com.sogong.tejava.domain.dto.StockItemDTO;
import com.sogong.tejava.domain.dto.service.EmployeeService;
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
    @GetMapping("/employee/orders")
    @ApiOperation(value = "주문 현황 조회하기", notes = "직원 인터페이스의 홈화면에서 접수된 주문 목록을 확인할 수 있습니다.")
    public ResponseEntity<GetOrderListResponseDTO> showOrders(HttpServletRequest request) {

        return ResponseEntity.ok().body(employeeService.getOrderList(request));
    }

    // 주문 상태 바꾸기 (pending, cooking, delivering, completed)
    @PatchMapping("/employee/order-status")
    @ApiOperation(value = "주문 상태 바꾸기", notes = "pending(접수 대기 중), cooking(조리 중), delivering(배달 중), completed(배달 완료)로 총 4가지의 주문 상태가 있습니다.")
    public ResponseEntity<?> updateOrderStatus(HttpServletRequest request, @RequestBody ChangeOrderStatusDTO changeOrderStatusDTO) {

        employeeService.updateOrderStatus(request, changeOrderStatusDTO);
        return ResponseEntity.ok().build();
    }

    // 재고 현황 보여주기
    @GetMapping("/employee/stock-info")
    @ApiOperation(value = "재고 현황 보여주기", notes = "재료와 마실 것(커피, 와인, 샴페인의 재고를 인분을 기준으로 보여줍니다.")
    public ResponseEntity<List<StockItemDTO>> showStockInfo(HttpServletRequest request) {

        List<StockItemDTO> stockItemList = employeeService.showStockInfo(request);
        return ResponseEntity.ok().body(stockItemList);
    }

    // 재고 수량 수정하기
    @PatchMapping("employee/stock-info")
    @ApiOperation(value = "재고 수량 수정하기", notes = "요청자(직원)의 id, 재고의 id, 그리고 수량을 입력받아 갱신합니다.")
    public ResponseEntity<?> changeStockInfo(HttpServletRequest request, @RequestBody ChangeStockInfoDTO changeStockInfoDTO) {

        employeeService.changeStockInfo(request, changeStockInfoDTO);
        return ResponseEntity.ok().build();
    }
}