package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {

    // 用户名
    private String userName;

    // 手机号
    private String phone;

    // 地址
    private String address;

    private String consignee;

    private List<OrderDetail> orderDetailList;
}
