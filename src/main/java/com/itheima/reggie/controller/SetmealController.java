package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;
    @Resource
    private CategoryService categoryService;

    //添加套餐
    @PostMapping
    private R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("添加成功");
    }

    // 分页显示
    @GetMapping("/page")
    private R<Page<SetmealDto>> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> dtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    //启用，停用套餐
    @PostMapping("/status/{status}")
    private R<String> status(@PathVariable Integer status,Long[] ids){
        for (Long id : ids) {
            //获取套餐
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            //更新状态
            setmealService.updateById(setmeal);
        }

        return R.success("修改成功");
    }
    //删除套餐
    @DeleteMapping
    private R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    private R<List<Setmeal>> list(Long categoryId,int status){
        //在表setmeal，根据categoryId 获取 setmealid
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,status);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    //返回套餐信息
    @GetMapping("/dish/{id}")
    private R<List<SetmealDishDto>> dish(@PathVariable Long id ){

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<SetmealDishDto> setmealDishDtoList = list.stream().map((item)->{
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            //根据dishid查询图片
            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Dish::getId,item.getDishId());
            Dish dish = dishService.getOne(lambdaQueryWrapper);

            //插入图片
            setmealDishDto.setImage(dish.getImage());
            //把item数据保存到setmealDishDto
            BeanUtils.copyProperties(item,setmealDishDto);
            return setmealDishDto;
        }).collect(Collectors.toList());

        //TODO 没有图片

        return R.success(setmealDishDtoList);
    }
}
