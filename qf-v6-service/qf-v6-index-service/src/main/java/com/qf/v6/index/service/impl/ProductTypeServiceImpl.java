package com.qf.v6.index.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.base.BaseServiceImpl;
import com.qf.base.IBaseDao;
import com.qf.constant.StringConstant;
import com.qf.entity.TProductType;
import com.qf.mapper.TProductTypeMapper;
import com.qf.product.type.api.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class ProductTypeServiceImpl extends BaseServiceImpl<TProductType> implements IProductTypeService {

    @Autowired
    private TProductTypeMapper mapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public IBaseDao<TProductType> getBaseDao() {
        return mapper;
    }

    @Override
    public List<TProductType> selectAll() {
        //从redis拿
        String key = "producttypes";
        List<TProductType> types = (List<TProductType>) redisTemplate.opsForValue().get(key);

        if(types==null){
            //上分布式锁
            String uuid = UUID.randomUUID().toString();
            //上锁
//            Boolean absent = redisTemplate.opsForValue().setIfAbsent(StringConstant.redis_types_lock, uuid);
            //原子操作 防线程安全问题和事务问题。
            Boolean absent = redisTemplate.opsForValue().setIfAbsent(StringConstant.redis_types_lock, uuid, 5, TimeUnit.MINUTES);
            if(absent){
                try {
                    //设置超时时间
//                    redisTemplate.expire(StringConstant.redis_types_lock,5, TimeUnit.MINUTES);
                    //查数据
                    types = mapper.selectAll();
                    //存到redis中
                    redisTemplate.opsForValue().set(key,types);
                    //释放锁
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    String currentUUID = (String) redisTemplate.opsForValue().get(StringConstant.redis_types_lock);
                    if(uuid.equals(currentUUID)){
                        redisTemplate.delete(StringConstant.redis_types_lock);
                    }

                }

            }else{
                try {
                    Thread.sleep(10);
                    return selectAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        return types;
    }
}
