package com.qf.resuorces.api;

import com.qf.dto.ResultBean;
import com.qf.dto.TProductAddDTO;

public interface IResourcesService {

    ResultBean createPage(TProductAddDTO product);

}
