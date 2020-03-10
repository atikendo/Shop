package com.qf.v6.staticresources.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.dto.ResultBean;
import com.qf.dto.TProductAddDTO;
import com.qf.resuorces.api.IResourcesService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
@Service
public class ResourcesServiceImpl implements IResourcesService {

    @Autowired
    private Configuration configuration;

    @Value("${resourcesPath}")
    private String resourcesPath;


    @Value("${imageServer}")
    private String imageServer;


    @Override
    public ResultBean createPage(TProductAddDTO product) {


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


        return null;
    }
}
