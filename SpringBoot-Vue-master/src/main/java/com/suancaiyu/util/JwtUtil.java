package com.suancaiyu.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.suancaiyu.entity.Person;

public class JwtUtil {
    /**
     * 使用固定的解密秘钥
     */
    private static final String SECRET = "wulawula_iii";

    /**
     * @version: V1.0
     * @description: 生成token并验证token并解密token中的信息
     * @param:  userInfo 用户手机号和用户Id
     * @return: java.lang.String 返回token
     **/
    public static String getToken(Person person) {
        try{
            //用秘钥生成签名
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //默认头部+载荷（手机号/id）+签名=jwt
            String jwtToken= JWT.create()
                    .withClaim("username", person.getUsername())
                    .withClaim("id",person.getId())
                    .sign(algorithm);
            return jwtToken;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @version: V1.0
     * @description: 校验token是否正确
     * @param:  token
     * @param: userPhone
     * @return: UserInfoEntity token中的用户信息(姓名/id)
     **/
    public static Person verify(String token) {
        try {
            // 根据用户信息userInfo生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            // 效验TOKEN
            verifier.verify(token);
            //返回token内容
            return getTokenInfo(token);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * @version: V1.0
     * @Title: getUsername
     * @description: 从Token中解密获得Token中的用户信息
     * @param:  token
     * @return: UserInfoEntity token中的用户信息(姓名/id)
     **/
    private static Person getTokenInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Person userInfo=new Person();
            userInfo.setUsername(jwt.getClaim("username").asString());
            userInfo.setId(jwt.getClaim("id").asInt());
            return userInfo;
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
