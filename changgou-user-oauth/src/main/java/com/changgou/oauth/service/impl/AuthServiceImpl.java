package com.changgou.oauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 16:23
 * @Description: com.changgou.oauth.service.impl
 ****/
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    /***
     * 授权认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //申请令牌
        AuthToken authToken = applyToken(username,password,clientId, clientSecret);
        if(authToken == null){
            throw new RuntimeException("申请令牌失败");
        }
        return authToken;
    }


    /****
     * 认证方法
     * @param username:用户登录名字
     * @param password：用户密码
     * @param clientId：配置文件中的客户端ID
     * @param clientSecret：配置文件中的秘钥
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //获取认证服务得地址
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        //获取令牌得url
        String url = serviceInstance.getUri().toString()+ "/oauth/token";
        //定义body:授权方式，账号，密码
        LinkedMultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("grant_type","password");
        bodyMap.add("username",username);
        bodyMap.add("password",password);
        //定义头header
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("Authorization",httpbasic(clientId,clientSecret));
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        //http请求spring securiity的申请令牌接口
        Map body = new HashMap<>();
        try {
            log.error("HTTP请求开始：new HttpEntity<MultiValueMap<String, String>>(bodyMap, headerMap):{}",JSON.toJSONString(new HttpEntity<MultiValueMap<String, String>>(bodyMap, headerMap)));
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(bodyMap, headerMap), Map.class);
            log.error("HTTP请求结束");
            body = response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (body == null || body.get("access_token") == null || body.get("refresh_token") == null || body.get("jti") == null) {
            //jti是jwt令牌的唯一标识作为用户身份令牌
            throw new RuntimeException("令牌生成失败");
        }
        //将响应数据包装为AuthToken对象
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken((String)body.get("access_token"));
        authToken.setJti((String)body.get("jti"));
        authToken.setRefreshToken((String)body.get("refresh_token"));
        return authToken;
    }


    /***
     * base64编码
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
