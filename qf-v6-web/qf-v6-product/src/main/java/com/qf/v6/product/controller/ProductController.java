package com.qf.v6.product.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qf.dto.ResultBean;
import com.qf.dto.TProductAddDTO;
import com.qf.entity.TProduct;
import com.qf.product.api.IProductService;
import com.qf.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("product")
public class ProductController {

    @Reference
    private IProductService productService;

    //注入fastdfs 客户端
    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    //图片服务器地址
    @Value("${imageServer}")
    private String imageServer;


    //带分页的
    @RequestMapping("page/{pageNum}/{pageSize}")//1 2  3  4    10 20
    public String showIndexByPage(
            @PathVariable int pageNum,
            @PathVariable int pageSize,
            Model model){
        //该服务需要提供封装了前端所需的所有分页信息的对象
        PageInfo<TProduct> pageInfo= productService.getPageInfo(pageNum,pageSize);


        model.addAttribute("pageInfo",pageInfo);

        model.addAttribute("products",pageInfo.getList());

        model.addAttribute("imageServer",imageServer);

        return "index";
    }



    /**
     * 展示后台所有商品列表页面(不带分页)
     * @param model
     * @return
     */
    @RequestMapping({"","index"})
    public String showIndex(Model model){

        //去调用service获得所有商品
        List<TProduct> products = productService.selectAll();
        model.addAttribute("products",products);
        return "index";
    }


    /**
     * 添加商品
     * @param product
     * @return
     */
    @RequestMapping("addProduct")
    public String addProduct(TProductAddDTO product){
//        System.out.println(product);

        ResultBean resultBean = productService.addProduct(product);

        //===============添加商品到solr库==================
        // 获得商品id，传递给搜索模块
        //        Long pid = ((TProduct) resultBean.getData()).getPid();
//        //httpclient发送请求
////        HttpClientUtils.doGet("http://localhost:9084/search/addProduct?pid="+pid);
//        System.out.println(resultBean);
        //改造 使用mq的通讯方式
        return "redirect:page/1/3";
    }


    /**
     * 删除商品
     * @param pid
     * @return
     */
    @RequestMapping("delProduct")
    @ResponseBody
    public ResultBean delProduct(Long pid){

        //pid
        productService.deleteByPrimaryKey(pid);

        return ResultBean.success("删除成功");
    }


    /**
     * 根据pid获得完整的商品信息
     * @param pid
     * @return
     */
    @RequestMapping("{pid}")
    @ResponseBody
    public ResultBean getProductByPid(@PathVariable Long pid){

        //获取商品所有信息
        ResultBean resultBean = productService.selectTProductInfo(pid);

        return resultBean;
    }

    /**
     * 编辑商品
     * @param product
     * @return
     */
    @RequestMapping("editProduct")
    public String editProduct(TProductAddDTO product){

        ResultBean resultBean = productService.editProduct(product);


        return "redirect:index";
    }


    /**
     * 图片上传
     * @param dropzFile
     * @return 图片路径
     */
    @RequestMapping("uploadImage")
    @ResponseBody
    public ResultBean uploadImage(MultipartFile dropzFile){
        //要拿到前端提交的文件的输入流，再用fastdfs进行上传
        String extName = StringUtil.getFileExtName(dropzFile.getOriginalFilename());

        String filePath = "";

        //1.获得输入流
        try {
            InputStream is = dropzFile.getInputStream();

            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(is, dropzFile.getSize(), extName, null);

//            System.out.println(storePath);

            filePath = storePath.getFullPath();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResultBean.success(filePath,"图片上传成功");

    }


    @RequestMapping("uploadImageByEditor")
    @ResponseBody
    public ResultBean uploadImageByEditor(MultipartFile file){
        try {
            InputStream is = file.getInputStream();

            long size = file.getSize();

            String extName = StringUtil.getFileExtName(file.getOriginalFilename());

            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(is, size, extName, null);

            String[] paths = new String[]{imageServer+"/"+storePath.getFullPath()};

            return ResultBean.success(paths);//errno:0  data:[path]
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBean.error();
        }


    }



}
