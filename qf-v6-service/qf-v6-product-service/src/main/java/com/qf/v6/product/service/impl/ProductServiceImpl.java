package com.qf.v6.product.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.base.BaseServiceImpl;
import com.qf.base.IBaseDao;
import com.qf.constant.RabbitConstant;
import com.qf.dto.ResultBean;
import com.qf.dto.TProductAddDTO;
import com.qf.entity.TProduct;
import com.qf.entity.TProductDesc;
import com.qf.mapper.TProductDescMapper;
import com.qf.mapper.TProductMapper;
import com.qf.product.api.IProductService;
import freemarker.template.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
public class ProductServiceImpl extends BaseServiceImpl<TProduct> implements IProductService {

    @Autowired
    private TProductMapper dao;

    @Autowired
    private TProductDescMapper descMapper;


    @Autowired
    private Configuration configuration;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public IBaseDao<TProduct> getBaseDao() {
        return dao;
    }

    @Override
    public List<TProduct> selectAll() {
        return dao.selectAll();
    }

    @Override
    public ResultBean addProduct(TProductAddDTO product) {

        try {
            //插两张表
            //插入第一张表：商品基本信息表
            //要实现id回填
            dao.insertSelective(product);
//        System.out.println(product);//开启主键回填后，id主键会回填到对象中
            //插入第二张表：商品描述表
            //手动封装TproductDesc对象
            TProductDesc tProductDesc = new TProductDesc();
            tProductDesc.setPid(product.getPid());
            tProductDesc.setPdesc(product.getPdesc());
            descMapper.insertSelective(tProductDesc);


            //=======添加商品到solr库\生成静态页面========
            //消息的生产者
            rabbitTemplate.convertAndSend(RabbitConstant.PRODUCT_ADD_TOPIC_EXCHANGE,"product.add",product);

            //实现的静态页面的生成
//            ResultBean resultBean = resourcesService.createPage(product);
//



        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.error("添加商品失败");
        }


        return ResultBean.success(product,"添加商品成功");
    }

    //通过pid获得完整的商品信息
    @Override
    public ResultBean selectTProductInfo(Long pid) {


        TProductAddDTO productDTO = new TProductAddDTO();

        TProduct product = dao.selectByPrimaryKey(pid);
        TProductDesc productDesc = descMapper.selectByPid(pid);

        //封装
        productDTO.setPid(product.getPid());
        productDTO.setCreateTime(product.getCreateTime());
        productDTO.setCreateUser(product.getCreateUser());
        productDTO.setFlag(product.getFlag());
        productDTO.setPimage(product.getPimage());
        productDTO.setPname(product.getPname());
        productDTO.setPrice(product.getPrice());
        productDTO.setSalePrice(product.getSalePrice());
        productDTO.setStatus(product.getStatus());
        productDTO.setTypeId(product.getTypeId());
        productDTO.setUpdateTime(product.getUpdateTime());
        productDTO.setUpdateUser(product.getUpdateUser());
        productDTO.setPdesc(productDesc.getPdesc());


        return ResultBean.success(productDTO);
    }

    //编辑商品
    @Override
    public ResultBean editProduct(TProductAddDTO product) {

//        System.out.println(product);//

        try {
            //更新到基本信息表
            dao.updateByPrimaryKey(product);

            //更新到描述表
            Map map = new HashMap();
            map.put("pid",product.getPid());
            map.put("pdesc",product.getPdesc());

            descMapper.updateDescByPid(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.error("更新失败");
        }

        return ResultBean.success("更新成功");
    }

    //提供分页功能
    @Override
    public PageInfo<TProduct> getPageInfo(int pageNum, int pageSize) {

        //使用pageHelper开始分页
        PageHelper.startPage(pageNum,pageSize);

        List<TProduct> list = selectAll();
        PageInfo<TProduct> pageInfo = new PageInfo<TProduct>(list,5);


        return pageInfo;
    }
}
