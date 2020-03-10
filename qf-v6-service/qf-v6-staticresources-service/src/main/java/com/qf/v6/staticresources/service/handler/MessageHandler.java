package com.qf.v6.staticresources.service.handler;

import com.qf.constant.RabbitConstant;
import com.qf.dto.TProductAddDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandler {

    @Autowired
    private Configuration configuration;

    @Value("${imageServer}")
    private String imageServer;

    @Value("${resourcesPath}")
    private String resourcesPath;

    @RabbitListener(queues = RabbitConstant.PRODUCT_ADD_TO_RESOURCES_QUEUE)
    public void proccess(TProductAddDTO product){

        try {
//            //生成静态页面
            Template template = configuration.getTemplate("introduction.ftl");
//            //数据
            Map<String,Object> data = new HashMap<>();

            product.setPimage(imageServer+product.getPimage());

            data.put("pro",product);


            String fileName = product.getPid().toString()+".html";
            Writer out = new FileWriter(resourcesPath+fileName);
            template.process(data,out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }


    }


}
