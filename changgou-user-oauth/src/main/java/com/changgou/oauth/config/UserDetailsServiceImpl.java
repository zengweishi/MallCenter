package com.changgou.oauth.config;
import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.oauth.util.UserJwt;
import com.changgou.user.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*****
 * 自定义授权认证类
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;
    @Autowired
    private UserFeign userFeign;

    /****
     * 自定义授权认证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.error("loadUserByUsername:username用户名：{}",username);
        //取出身份，如果身份为空说明没有认证
        //=======================客户端信息认证 start================================
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        log.error("authentication:{}",JSON.toJSONString(authentication));
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            log.error("clientDetails:{}",JSON.toJSONString(clientDetails));
            if(clientDetails!=null){
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                return new User(username,//客户端ID
                        new BCryptPasswordEncoder().encode(clientSecret),//客户端密钥
                        AuthorityUtils.commaSeparatedStringToAuthorityList(""));//权限
                //数据库查找方式（clientSecret不需加密，数据库是加密好的）
                //return new User(username,clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        //=======================客户端信息认证 end================================

        //=======================用户信息认证 start================================
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        //根据用户名查询用户信息
        //String pwd = new BCryptPasswordEncoder().encode("szitheima");
        //创建User对象
        //根据用户名查询用户信息
        Result<com.changgou.user.pojo.User> userResult = userFeign.loadById(username);
        String permissions = "goods_list,seckill_list";
        UserJwt userDetails = new UserJwt(username,userResult.getData().getPassword(),AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        log.error("userDetails:{}", JSON.toJSONString(userDetails));
        return userDetails;
        //=======================用户信息认证 end================================
    }
}
