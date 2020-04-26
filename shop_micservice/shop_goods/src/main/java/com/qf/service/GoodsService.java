package com.qf.service;

import com.qf.entity.Goods;

import java.util.Date;
import java.util.List;

/**
 * @Author 张是非
 * @Date 2019/12/30
 */
public interface GoodsService {
    int insertGoods(Goods goods);

    List<Goods> goodsList();

    List<Goods> queryKillList(Date time);
}
