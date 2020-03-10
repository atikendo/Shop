package com.qf.v6.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.base.BaseServiceImpl;
import com.qf.base.IBaseDao;
import com.qf.constant.RabbitConstant;
import com.qf.constant.RedisConstant;
import com.qf.dto.EmailMessageDTO;
import com.qf.dto.ResultBean;
import com.qf.entity.TUser;
import com.qf.mapper.TUserMapper;
import com.qf.user.api.IUserService;
import com.qf.util.SpringSecurityUtil;
import com.qf.util.StringUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class UserServiceImpl extends BaseServiceImpl<TUser> implements IUserService {
    @Autowired
    private TUserMapper mapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${activeAccountServer}")
    String activeAccountServer;
    @Override
    public IBaseDao<TUser> getBaseDao() {
        return mapper;
    }


    //验证用户名和密码
    @Override
    public ResultBean checkLogin(String username, String password) {
        // select * from t_user where username =  and password =
        // select * from t_user where username=
        TUser user = mapper.selectByUsername(username);
        if(user!=null){
            if(password!=null&&!"".equals(password)&&encoder.matches(password,user.getPassword())){
                //这里使用spring security
                return ResultBean.success(user,"登录成功");

            }
        }
        return ResultBean.error("用户名或密码错误");
    }

    /**
     * 根据uuid组织键，去redis中查有没有相应的登录信息
     * @param uuid
     * @return
     */
    @Override
    public ResultBean checkIsLogin(String uuid) {
        if(uuid!=null&&!"".equals(uuid)){
            //1.组织键
            String redisKey = StringUtil.getRedisKey(RedisConstant.USER_LOGIN_PRE, uuid);
            //2.去redis中查
            Object o = redisTemplate.opsForValue().get(redisKey);
            if(o!=null){
                TUser user  = (TUser) o;
                user.setPassword("");//数据传递时不带密码
                return ResultBean.success(user,"用户已登录");
            }
        }
        return ResultBean.error("用户未登录");
    }

    //注册
    @Override
    public ResultBean regist(String phone, String code, String password) {
        //1.校验验证码
        String redisKey = StringUtil.getRedisKey(RedisConstant.REGISTER_PHONE, phone);
        String redisCode = (String) redisTemplate.opsForValue().get(redisKey);
        if(code.equals(redisCode)){//验证码正确
            TUser user = new TUser();
            user.setPassword(SpringSecurityUtil.getEncodePassword(password));
            user.setPhone(phone);
            //插入到数据库中
            mapper.insertSelective(user);
            return ResultBean.success("注册成功");
        }
        //
        return ResultBean.error("验证码错误");
    }

    //邮箱注册
    @Override
    public ResultBean registByEmail(String email, String password) {

        try {
            //1.发邮件
            EmailMessageDTO message = new EmailMessageDTO();//里面有两样东西： username   url
            message.setEmail(email);
            //生成uuid
            String uuid = UUID.randomUUID().toString();
            //创建url
            String url = activeAccountServer+uuid;
            message.setUrl(url);
            rabbitTemplate.convertAndSend(RabbitConstant.EMAIL_TOPIC_EXCHANGE,"email.regist",message);
            //2.将数据插入到数据库中
            TUser user = new TUser();
            user.setEmail(email);
            user.setPassword(SpringSecurityUtil.getEncodePassword(password));
            mapper.insertSelective(user);
            //3.存入到redis中
            //组织键
            String redisKey = StringUtil.getRedisKey(RedisConstant.REGISTER_EMAIL, uuid);
            redisTemplate.opsForValue().set(redisKey,email,10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.error("注册失败");

        }


        return ResultBean.success("注册成功");
    }
}
