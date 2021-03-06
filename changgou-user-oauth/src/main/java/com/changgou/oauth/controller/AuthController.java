package com.changgou.oauth.controller;

import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 16:42
 * @Description: com.changgou.oauth.controller
 ****/
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class AuthController {
    //客户端ID
    @Value("${auth.clientId}")
    private String clientId;

    //秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public Result login(String username, String password) {
        log.error("oauth的login方法调用：username:{},password:{}",username,password);
        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不允许为空");
        }
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不允许为空");
        }
        //申请令牌
        log.error("oauth的令牌申请开始");
        AuthToken authToken =  authService.login(username,password,clientId,clientSecret);
        log.error("oauth的令牌申请结束");
        //用户身份令牌
        String access_token = authToken.getAccessToken();
        //将令牌存储到cookie
        saveCookie(access_token);
        log.error("oauth的login方法调用结束");
        return new Result(true, StatusCode.OK,"登录成功！");
    }

    /***
     * 将令牌存储到cookie
     * @param token
     */
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
