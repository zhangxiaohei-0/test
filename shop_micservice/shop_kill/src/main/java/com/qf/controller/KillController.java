package com.qf.controller;

import com.qf.aop.IsLogin;
import com.qf.entity.Goods;
import com.qf.entity.ResultData;
import com.qf.feign.GoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author 张是非
 * @Date 2020/1/3
 */
@Controller
@RequestMapping("/kill")
public class KillController {

    @Autowired
    private GoodsFeign goodsFeign;


    /**
     * 秒杀时间
     * @return
     */
    @RequestMapping("/queryKillTimes")
    @ResponseBody
    public ResultData<List<Date>> queryKillTimes(){

        System.out.println("ajax发送了信息");
        List<Date> dates=new ArrayList<>();
        //获得当前时间
        Date now = DateUtil.getNextNDate(0);
        //获得下个小时的时间
        Date next1 = DateUtil.getNextNDate(1);
        //获得下下个小时的时间
        Date next2 = DateUtil.getNextNDate(2);

        dates.add(now);
        dates.add(next1);
        dates.add(next2);
        return new ResultData<List<Date>>().setCode(ResultData.ResultCodeList.OK).setData(dates);
    }


    /**
     * 查询秒杀商品列表
     * @param n
     * @return
     */
    @RequestMapping("/queryKillList")
    @ResponseBody
    public ResultData<List<Goods>> queryKillList(Integer n){

        //获得对应的时间
        Date time = DateUtil.getNextNDate(n);
        //根据时间去查询相对应的秒杀商品信息
        List<Goods> goodsList = goodsFeign.queryKillList(time);
        System.out.println("对应场次的秒杀商品信息:" + goodsList);

        return new ResultData<List<Goods>>().setCode(ResultData.ResultCodeList.OK)
                .setData(goodsList);
    }


    /**
     * 查询服务器当前时间
     * @return
     */
    @RequestMapping("/queryNow")
    @ResponseBody
    public ResultData<Date> queryNow(){
        return new ResultData<Date>().setCode(ResultData.ResultCodeList.OK).setData(new Date());
    }


    /**
     * 抢购下单
     * @return
     */
    @IsLogin(mustLogin = true)
    @RequestMapping("/qiangGou")
    public String qiangGou(){


        return "";
    }
}
