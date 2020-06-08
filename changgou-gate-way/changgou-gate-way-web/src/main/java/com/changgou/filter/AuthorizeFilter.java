package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/8 10:06
 * @Description:全局过滤器 Spring Cloud Gatewayu全局过滤器GlobalFilters
 * GlobalFilter ： 全局过滤器，对所有的路由均起作用
 * GatewayFilter ： 只对指定的路由起作用
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求的URI
        String path = request.getURI().getPath();
        if (path.startsWith("/api/user/login") || path.startsWith("/api/brand/search/")) {
            //登录接口以及一些商品查询接口放行（可以设计权限系统）
            return chain.filter(exchange);
        }
        //1.请求头获取令牌数据
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //2.请求参数中获取令牌数据
        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        //3.从cookie中获取令牌数据
        HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
        if (token == null && cookie != null) {
            token = cookie.getValue();
        }
        //token为空
        if (StringUtils.isEmpty(token)) {
            //设置方法不允许被访问，405错误
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //解析token
        try {
            Claims claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败，401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
