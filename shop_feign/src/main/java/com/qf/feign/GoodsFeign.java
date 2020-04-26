package com.qf.feign;

import com.qf.entity.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * @Author 张是非
 * @Date 2020/1/1
 */
@FeignClient("MICSERVICE-GOODS")
public interface GoodsFeign {

    @RequestMapping("/goods/insert")
    int insertGoods(@RequestBody Goods goods);

    @RequestMapping("/goods/list")
    List<Goods> goodsList();

    @RequestMapping("/goods/queryKillList")
    List<Goods> queryKillList(@RequestBody Date time);
}
