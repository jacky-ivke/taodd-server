package com.esports.scheme.web;

import com.esports.scheme.service.CommissionSchemeManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【代理管理系统-代理中心相关接口】"})
@RestController
public class CommissionSchemeAction {

    @Autowired
    private CommissionSchemeManager commissionSchemeManager;


}
