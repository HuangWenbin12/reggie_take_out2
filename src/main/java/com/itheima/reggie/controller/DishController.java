package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;

    //添加菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    //菜品信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        //拷贝数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");


        List<DishDto> list = pageInfo.getRecords().stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    //返回菜品信息
    @GetMapping({"/{id}"})
    public R<DishDto> get(@PathVariable Long id){
//        long ids = id;
        DishDto dishDto =  dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //修改菜品
    @PutMapping
    private R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    //起售，停售商品
    @PostMapping("/status/{status}")
    private R<String> status(@PathVariable Integer status, Long[] ids){
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
//            Integer status = dish.getStatus();
//            if(status == 1){
//                dish.setStatus(0);
//            }else{
//                dish.setStatus(1);
//            }
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    @DeleteMapping
    private R<String> delete(@RequestParam List<Long> ids){

//        List<Long> list = Arrays.stream(ids).collect(Collectors.toList());
        dishService.removeByIds(ids);
//        for (Long id : ids) {
//
//        }
//        boolean flag = dishService.removeById(ids);
//        if(!flag){
//            return R.success("删除失败");
//        }
        return R.success("删除成功");
    }
}
