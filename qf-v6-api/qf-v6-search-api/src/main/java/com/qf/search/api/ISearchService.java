package com.qf.search.api;

import com.qf.dto.ResultBean;

public interface ISearchService {

    ResultBean searchByKeyword(String keyword);

    ResultBean addProduct(Long pid);
}
