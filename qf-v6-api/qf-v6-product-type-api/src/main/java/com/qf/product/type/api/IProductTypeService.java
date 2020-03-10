package com.qf.product.type.api;

import com.qf.base.IBaseService;
import com.qf.entity.TProductType;

import java.util.List;

public interface IProductTypeService extends IBaseService<TProductType> {
    List<TProductType> selectAll();
}
