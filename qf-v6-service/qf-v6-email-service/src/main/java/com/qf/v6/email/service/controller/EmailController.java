package com.qf.v6.email.service.controller;

import com.qf.dto.ResultBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("email")
public class EmailController {


    @RequestMapping("active")
    @ResponseBody
    public ResultBean actionAccount(String uuid){
        return ResultBean.success(uuid);
    }

}
