package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.transaction.annotation.Transactional;


public interface DishService extends IService<Dish> {
    @Transactional
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);
    @Transactional
    void updateWithFlavor(DishDto dishDto);
}
