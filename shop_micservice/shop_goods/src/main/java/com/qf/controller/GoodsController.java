package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @Author 张是非
 * @Date 2019/12/30
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 添加商品
     * @return
     */
    @RequestMapping("/insert")
    public int insertGoods(@RequestBody Goods goods){
        System.out.println("商品服务：" + goods);
        return goodsService.insertGoods(goods);
    }

    /**
     * 商品列表
     * @return
     */
    @RequestMapping("/list")
    public List<Goods> goodsList(){
        return goodsService.goodsList();
    }


    /**
     * 秒杀商品列表
     * @param time
     * @return
     */
    @RequestMapping("/queryKillList")
    public List<Goods> queryKillList(@RequestBody Date time){

        System.out.println("商品服务获得事件对象:" + time);
        List<Goods> goodsList=goodsService.queryKillList(time);
        return goodsList;
    }
}
