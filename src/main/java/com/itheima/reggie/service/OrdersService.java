package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;

public interface OrdersService extends IService<Orders> {


    void submit(Orders orders);
}
