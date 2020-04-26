package com.qf.listener;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author 张是非
 * @Date 2020/1/3
 */
@Component
public class MyRabbitListener {

    @Autowired
    private Configuration configuration;

    @RabbitListener(queues = "kill_queue")
    public void msgHandler(Goods goods){

        //获得classpath路径
        String path = MyRabbitListener.class.getResource("/").getPath() + "static/html";
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(file, goods.getId() + ".html");
        try(
                //准备一个静态页面的输出路劲
                Writer out = new FileWriter(file)
           ) {
            //获得对应的模板
            Template template = configuration.getTemplate("kill.ftlh");
            //准备数据
            Map<String,Object> map = new HashMap<>();
            map.put("goods", goods);

            //生成静态页面
            template.process(map,out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
