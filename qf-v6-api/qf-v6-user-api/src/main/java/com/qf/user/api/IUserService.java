package com.qf.user.api;

import com.qf.base.IBaseService;
import com.qf.dto.ResultBean;
import com.qf.entity.TUser;

public interface IUserService extends IBaseService<TUser> {
    ResultBean checkLogin(String username, String password);

    ResultBean checkIsLogin(String uuid);

    ResultBean regist(String phone, String code, String password);

    ResultBean registByEmail(String email, String password);
}
