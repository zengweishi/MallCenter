import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/9 13:54
 * @Description:测试JWT生成
 */
public class CreateJwtTest {

    @Test
    public void testJwt() {
        //证书文件路径
        String key_location = "changgou.jks";
        //密钥库密码
        String key_password = "changgou";
        //密钥密码
        String kewPwd = "changgou";
        //密钥别名
        String alias = "changgou";
        //访问证书路径
        ClassPathResource classPathResource = new ClassPathResource(key_location);
        //创建密钥工厂
        KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(classPathResource, key_password.toCharArray());
        //读取密钥对（公钥，私钥）
        KeyPair keyPair = keyFactory.getKeyPair(alias, kewPwd.toCharArray());
        //从密钥对中获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义载荷 payload
        Map<String, Object> map = new HashMap<>();
        map.put("id","1");
        map.put("name","itemName");
        map.put("role","admin");
        //生成JWT令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(aPrivate));
        //取出令牌
        System.out.println(jwt.getEncoded());
    }

    @Test
    public void testDecode() {
        //生成的token
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4iLCJuYW1lIjoiaXRlbU5hbWUiLCJpZCI6IjEifQ.eW9oLsXfqeg05IPGqNS6KwDPaIT3EMGqSwpinOGp9mq-pun6idZRYPZuPu5pB1H6Bw5FYbpAcADU3mvHqL_efthhMTOm3QgDXy70E7Y8Q_EpfVjwhE8uh7UT6YVbpAblzQvq6b5fNJGUN4VjnwTnouGF-XeqLwyLXiRN1P-twlpEYLkXGnZylQOBjeL95R3fep5uVXZ7sBXZXpymAO-WEhO6UUTvibEjWPmKcSybL5FZEpxcNvgznPOFE1PNibC5v3j1Rl8dhEJ17JQuisBz3Uz2eskaO2E6X29qXxT-CGqE6v_MNYMc9uOf2at-RuzBRPzE9htLp279tpHPC2saig";
        //生成的公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjMxuEugew9gzdce9o3Ve/XxDD6WLU8fDQ9Dnvu5vV21LrJK+O/UWQ9EQERzDGhxILR5h1Zf0y21FzTQN9AGc+uN5XYwn3T7+vhiNiBbm+maSW06h0npVAdys19wGrVnGCFoP+VxLbaiF5/suLDLt0pBdsT1le0eb1BQMNR8t4QU3h/C4V8VTAnSoj/RS7Ssy/sSAVYZ+AwjMuekOviqbTK1HYTJwR1vK1LbWe4+A5O7uBH582TgI0g/BVpOGpe189LdUHgvQ0oE2fmocwMJIJdDuNAaPU2sb9vzQ92H8MPXbEhwZo/KoJQ9GoiXnlEzEUmBDksBp9b4rRIwkE46YSwIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取jwt原始内容ResourceServerConfig
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);

    }
}
