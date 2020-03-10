package com.qf.v6.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.dto.ResultBean;
import com.qf.search.api.ISearchService;
import com.qf.search.api.entity.TProductSearchDTO;
import com.qf.search.api.mapper.TProductSearchDTOMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private SolrClient solrClient;

    @Value("${imageServer}")
    private String imageServer;

    @Autowired
    private TProductSearchDTOMapper mapper;

    @Override
    public ResultBean searchByKeyword(String keyword) {

        //1.创建查询对象
        SolrQuery query = new SolrQuery();
        query.set("df","t_product_keywords");
        query.setQuery(keyword);
        //分页
        query.setStart(0);
        query.setRows(12);
        //高亮
        query.setHighlight(true);
        query.addHighlightField("t_product_name");
        query.setHighlightSimplePre("<span style='color:red'>");
        query.setHighlightSimplePost("</span>");

        try {
            QueryResponse response = solrClient.query(query);

            List<TProductSearchDTO> products = new ArrayList<>();

            //获得数据结果集
            SolrDocumentList results = response.getResults();
            //获得高亮结果集
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

            for (SolrDocument document : results) {
                TProductSearchDTO product = new TProductSearchDTO();
                String stringId = (String) document.getFieldValue("id");
                Long id = Long.parseLong(stringId );//89983
                product.setId(id);

                //==========从高亮结果集中那带高亮效果的t_product_name=============
                Map<String, List<String>> stringListMap = highlighting.get(stringId);
                List<String> t_product_name1 = stringListMap.get("t_product_name");
                String t_product_name = t_product_name1.get(0);
                product.settProductName(t_product_name);

                Float t_product_sale_price = (Float) document.getFieldValue("t_product_sale_price");
                product.settProductSalePrice(new BigDecimal(t_product_sale_price));
                String t_product_pimage = (String) document.getFieldValue("t_product_pimage");
                product.settProductPimage(imageServer+"/"+t_product_pimage);
                String t_product_pdesc = (String) document.getFieldValue("t_product_pdesc");
                product.settProductPdesc(t_product_pdesc);

                products.add(product);

            }

            return ResultBean.success(products);




        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResultBean.error("查询出现异常");
    }

    /**
     *  //1.根据pid从数据库中获取该商品
     *         //2.封装成SolrInputDocument
     *         //3.插入到solr库里
     * @param id
     * @return
     */
    @Override
    public ResultBean addProduct(Long id) {
        //1.根据pid从数据库中获取该商品
       TProductSearchDTO product =  mapper.selectById(id);
        //2.封装成SolrInputDocument
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id",product.getId().toString());
        document.setField("t_product_name",product.gettProductName());
        document.setField("t_product_sale_price",product.gettProductSalePrice().floatValue());
        document.setField("t_product_pimage",product.gettProductPimage());
        document.setField("t_product_pdesc",product.gettProductPdesc());
        //3.插入到solr库里
        try {
            solrClient.add(document);
            solrClient.commit();
            return ResultBean.success("插入搜索库成功！");
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultBean.error("插入搜索库失败");
    }
}
