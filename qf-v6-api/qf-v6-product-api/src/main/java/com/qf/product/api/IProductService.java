package com.qf.product.api;

import com.github.pagehelper.PageInfo;
import com.qf.base.IBaseService;
import com.qf.dto.ResultBean;
import com.qf.dto.TProductAddDTO;
import com.qf.entity.TProduct;

import java.util.List;

public interface IProductService extends IBaseService<TProduct> {
    List<TProduct> selectAll();

    ResultBean addProduct(TProductAddDTO product);

    ResultBean selectTProductInfo(Long pid);

    ResultBean editProduct(TProductAddDTO product);

    PageInfo<TProduct> getPageInfo(int pageNum, int pageSize);
}
