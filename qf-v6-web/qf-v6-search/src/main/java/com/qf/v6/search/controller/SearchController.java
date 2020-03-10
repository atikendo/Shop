package com.qf.v6.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.dto.ResultBean;
import com.qf.search.api.ISearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("search")
public class SearchController {

    @Reference
    private ISearchService searchService;


    /**
     * 根据关键字，进行搜索，返回的是搜索页面，页面上带着这一次搜索的记过
     * @param keyword
     * @param model
     * @return
     */
    @RequestMapping("")
    public String searchByKeyword(String keyword, Model model){

        ResultBean resultBean = searchService.searchByKeyword(keyword);

        //List<TProductSearchDTO>
        model.addAttribute("products",resultBean.getData());


        return "search";
    }


    @RequestMapping("addProduct")
    public ResultBean addProduct(Long pid){

        return searchService.addProduct(pid);


    }

}
