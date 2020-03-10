package com.qf.cart.api;

import com.qf.dto.ResultBean;

public interface ICartService {


    ResultBean addProduct(String uuid, Long productId, int count);

    ResultBean clean(String uuid);

    ResultBean update(String uuid, Long productId, int count);

    ResultBean showCart(String uuid);
}
