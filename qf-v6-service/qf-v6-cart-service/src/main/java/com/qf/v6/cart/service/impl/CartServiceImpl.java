package com.qf.v6.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.cart.api.ICartService;
import com.qf.constant.RedisConstant;
import com.qf.dto.ResultBean;
import com.qf.dto.TProductCartDTO;
import com.qf.entity.TProduct;
import com.qf.mapper.TProductMapper;
import com.qf.util.StringUtil;
import com.qf.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TProductMapper productMapper;

    @Override
    public ResultBean addProduct(String uuid, Long productId, int count) {
        /*
         *     * 	1）当前用户没有购物车
         *      * 		新建购物车，把商品添加到购物车中，再把购物车存到redis中
         *      * 	2）当前用户有购物车，但是购物车中没有该商品
         *      * 		先从redis中获取该购物车，再把商品添加都购物车中，再存入到redis中。
         *      * 	3）当前用户有购物车，且购物车中有该商品
         *      * 		先从redis中获取该购物车，再获取该商品的数量，再让新的数量和老的数量相加，更新回购物车中，再更新回redis中。
         */

        String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);

        Object o = redisTemplate.opsForValue().get(redisKey);
        if(o==null){
            //当前用户没有购物车
            //封装购物车商品对象
            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setCount(count);
            cartItem.setUpdateTime(new Date());

            //存入到购物车中
            List<CartItem> carts = new ArrayList<>();
            carts.add(cartItem);
            //存入到redis中
            redisTemplate.opsForValue().set(redisKey,carts);
            return ResultBean.success(carts,"添加购物车成功");
        }

        //第2 或第3中情况
        List<CartItem> carts = (List<CartItem>) o;
        for (CartItem cartItem : carts) {

            if(cartItem.getProductId().longValue()==productId.longValue()){
                //当前用户有购物车，且购物车中有该商品
                cartItem.setCount(cartItem.getCount()+count);
                //更新商品的时间
                cartItem.setUpdateTime(new Date());
                //购物车中的商品已更新，得把购物车存回到redis中
                redisTemplate.opsForValue().set(redisKey,carts);
                return ResultBean.success(carts,"添加购物车成功");
            }
        }

        //当前用户有购物车，但购物车中没有该商品
        //封装购物车商品对象
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setCount(count);
        cartItem.setUpdateTime(new Date());
        carts.add(cartItem);
        //存到redis里面
        redisTemplate.opsForValue().set(redisKey,carts);
        return ResultBean.success(carts,"添加购物车成功");


    }
    //清空购物车
    @Override
    public ResultBean clean(String uuid) {

        String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
        redisTemplate.delete(redisKey);


        return ResultBean.success("清空购物车成功");
    }

    //更新购物车
    @Override
    public ResultBean update(String uuid, Long productId, int count) {

        if(uuid!=null&&!"".equals(uuid)) {
            //组织redis中的键
            String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
            Object o = redisTemplate.opsForValue().get(redisKey);
            if (o != null) {
                List<CartItem> carts = (List<CartItem>) o;
                for (CartItem cartItem : carts) {
                    if (cartItem.getProductId().longValue() == productId.longValue()) {
                        cartItem.setCount(count);
                        cartItem.setUpdateTime(new Date());
                        //把集合直接存回到redis中
                        redisTemplate.opsForValue().set(redisKey, carts);
                        return ResultBean.success(carts, "更新购物车成功");
                    }

                }
            }
        }

        return ResultBean.error("当前用户没有购物车");
    }

    //查看购物车
    @Override
    public ResultBean showCart(String uuid) {

        if(uuid!=null&&!"".equals(uuid)){
            String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
            Object o = redisTemplate.opsForValue().get(redisKey);
            if(o!=null){
                List<CartItem> carts = (List<CartItem>) o;
//                List<TProduct> products = new ArrayList<>();
                List<TProductCartDTO> products = new ArrayList<>();
                for (CartItem cartItem : carts) {
                   //去reids中取
                    // product:10
                    String productKey = StringUtil.getRedisKey(RedisConstant.PRODUCT_PRE, cartItem.getProductId().toString());
                    TProduct pro = (TProduct) redisTemplate.opsForValue().get(productKey);
                    if(pro==null){
                        //去数据库拿。再存redis
                        pro = productMapper.selectByPrimaryKey(cartItem.getProductId());
                        //存redis
                        redisTemplate.opsForValue().set(productKey,pro);
                    }
                    //pro肯定是有的
                    TProductCartDTO cartDTO = new TProductCartDTO();

                    //封装
                    cartDTO.setProduct(pro);
                    cartDTO.setCount(cartItem.getCount());
                    cartDTO.setUpdateTime(cartItem.getUpdateTime());




                    //存到product集合中
                    products.add(cartDTO);
                }

                return ResultBean.success(products);
            }
        }

        return ResultBean.error("当前用户没有购物车");
    }
}
