package com.qf.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.GoodsImagesMapper;
import com.qf.dao.GoodsKillMapper;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.entity.GoodsImages;
import com.qf.entity.GoodsSecondkill;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author 张是非
 * @Date 2019/12/30
 */
@Service
@CacheConfig(cacheNames = "goods")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsImagesMapper goodsImagesMapper;

    @Autowired
    private GoodsKillMapper goodsKillMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加商品信息
     * @param goods
     * @return
     */
    @Override
    @Transactional
    @CacheEvict(key = "'kill_' + #goods.goodsKill.startTime.time" , condition = "#goods.type == 2")
    public int insertGoods(Goods goods) {

        //添加商品
        goodsMapper.insert(goods);
        //添加封面
        GoodsImages goodsImages = new GoodsImages()
                                .setIsfengmian(1)
                                .setUrl(goods.getFmUrl())
                                .setGid(goods.getId());
        goodsImagesMapper.insert(goodsImages);
        //添加其他图片
        for (String url : goods.getOtherUrl()) {
            GoodsImages goodsImages1 = new GoodsImages()
                                    .setUrl(url)
                                    .setGid(goods.getId())
                                    .setIsfengmian(0);
            goodsImagesMapper.insert(goodsImages1);
        }

        //TODO 添加秒杀信息
        if (goods.getType() == 2){
            //说明是秒杀商品
            GoodsSecondkill goodsSecondkill = goods.getGoodsKill();
            goodsSecondkill.setGid(goods.getId());
            goodsKillMapper.insert(goodsSecondkill);

            //将秒杀商品id放入redis集合中
            String timeSuffix = DateUtil.date2String(goodsSecondkill.getStartTime(), "yyMMddHH");
            stringRedisTemplate.opsForSet().add("killgoods_" + timeSuffix, goods.getId() + "");
        }

        //TODO 将商品信息放入rabbitmq,同步到索引库,以及生成静态页面
        rabbitTemplate.convertAndSend("kill_Exchange", "", goods);
        return 1;
    }

    /**
     * 后台查询商品列表
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Goods> goodsList() {

        //查询所有商品
        List<Goods> goods = goodsMapper.selectList(null);

        //查询商品图片
        for (Goods good : goods) {
            //查询相关图片
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("gid", good.getId());
            List<GoodsImages> images = goodsImagesMapper.selectList(queryWrapper);

            for (GoodsImages image : images) {
                if (image.getIsfengmian()==1){
                    //是封面
                    good.setFmUrl(image.getUrl());
                }else {
                    //非封面
                    good.addOtherUrl(image.getUrl());
                }
            }
            //处理秒杀信息
            if (good.getType()==2){
                GoodsSecondkill goodsSecondkill = goodsKillMapper.selectOne(queryWrapper);
                good.setGoodsKill(goodsSecondkill);
            }

        }

        return goods;
    }

    /**
     * 秒杀商品列表
     * @param time
     * @return
     */
    @Override
    @Cacheable(key = "'kill_'+ #time.time")
    public List<Goods> queryKillList(Date time) {

        System.out.println("查询了数据库！！！");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("start_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
        List<GoodsSecondkill> killList = goodsKillMapper.selectList(queryWrapper);

        List<Goods> goodsList = new ArrayList<>();

        for (GoodsSecondkill goodsSecondkill : killList) {

            Goods goods = goodsMapper.selectById(goodsSecondkill.getGid());
            goods.setGoodsKill(goodsSecondkill);

            //查询相关图片
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("gid",goods.getId());
            List<GoodsImages> images = goodsImagesMapper.selectList(queryWrapper1);

            for (GoodsImages image : images) {
                if (image.getIsfengmian() == 1){
                    //是封面
                    goods.setFmUrl(image.getUrl());
                }else {
                    //非封面
                    goods.addOtherUrl(image.getUrl());
                }
            }
            goodsList.add(goods);
        }
        return goodsList;
    }
}
