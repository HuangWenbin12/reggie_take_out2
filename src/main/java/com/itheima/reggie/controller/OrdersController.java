package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.*;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Resource
    private OrdersService ordersService;
    @Resource
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders ){

        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize ){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        //获得用户id 查询订单
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        List<Orders> list = ordersService.list(queryWrapper);
        //执行查询
        ordersService.page(ordersPage,queryWrapper);
        //page复制
        BeanUtils.copyProperties(ordersPage,orderDtoPage,"records");
        List<OrderDto> orderDtoList = list.stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            //得到ordertail set到orderDto
            wrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);
            //对象复制
            BeanUtils.copyProperties(item,orderDto);
            orderDto.setOrderDetailList(orderDetails);
            return orderDto;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(ordersPage,orderDtoPage);
        orderDtoPage.setRecords(orderDtoList);
        return R.success(orderDtoPage);
    }



}
