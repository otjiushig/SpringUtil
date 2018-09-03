package com.jiushig.springutil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zkzmz on 2017/8/17.
 */
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    private static long intervalTime;  // 间隔时间
    private static String issuer;
    private static String[] tokenHeaders;

    private static Algorithm algorithm;

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_TIME = "time";
    public static final String KEY_IP = "ip";

    /**
     * 初始化Token
     *
     * @param secret
     * @param time   过期时间 单位m
     * @param issuer
     */
    public static void init(String secret, int time, String issuer, String[] tokenHeaders) {
        TokenUtil.issuer = issuer;
        TokenUtil.tokenHeaders = tokenHeaders;
        TokenUtil.intervalTime = time * 1000 * 60;
        try {
            algorithm = Algorithm.HMAC256(secret);
        } catch (Exception e) {
            throw new RuntimeException("Token init fail");
        }
    }

    public static void init(String secret, int time, String issuer) {
        TokenUtil.init(secret, time, issuer, null);
    }

    /**
     * 根据id生成token
     *
     * @param userId
     * @return
     */
    public static String create(int userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(KEY_USER_ID, userId);
        return create(hashMap);
    }

    /**
     * 根据map生成token
     *
     * @param stringMap
     * @return
     */
    public static String create(Map<String, Object> stringMap) {
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(issuer);
        builder.withClaim(KEY_TIME, System.currentTimeMillis() + intervalTime);
        if (stringMap != null)
            stringMap.forEach((k, v) -> {
                if (v instanceof Integer) builder.withClaim(k, (Integer) v);
                else if (v instanceof Double) builder.withClaim(k, (Double) v);
                else if (v instanceof Long) builder.withClaim(k, (Long) v);
                else if (v instanceof String) builder.withClaim(k, (String) v);
                else if (v instanceof Boolean) builder.withClaim(k, (Boolean) v);
                else throw new RuntimeException("Value type error, not support " + Object.class);
            });
        return builder.sign(algorithm);
    }

    /**
     * 获取并解析token
     *
     * @return 可能为null
     */
    public static DecodedJWT getToken() {
        DecodedJWT jwt = (DecodedJWT) SessionUtil.get("tokenInfo");
        if (jwt != null) return jwt;
        return getToken(getTokenFromRequest(Objects.requireNonNull(SessionUtil.getRequestAttribute()).getRequest()));
    }

    private static String getTokenFromRequest(HttpServletRequest request) {
        if (tokenHeaders == null || tokenHeaders.length == 0)
            return request.getHeader("Authorization");
        for (String string : tokenHeaders) {
            if (!CommonUtil.isEmpty(request.getHeader(string)))
                return request.getHeader(string);
        }
        return null;
    }

    public static DecodedJWT getToken(String token) {
        if (CommonUtil.isEmpty(token))
            return null;

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        DecodedJWT verify = verifier.verify(token);
        if (verify.getClaim(KEY_TIME).asLong() < System.currentTimeMillis()) {
            logger.warn("Token expired :" + token);
            return null;
        }
        SessionUtil.set("tokenInfo", verify);
        return verify;
    }

    /**
     * 通过Key从token中获取值
     *
     * @param jwt
     * @param key
     * @return
     */
    public static <T> T getByKey(DecodedJWT jwt, String key, Class<T> tClazz) {
        if (jwt == null) return null;
        Claim claim = jwt.getClaim(key);
        if (claim.isNull()) return null;
        return claim.as(tClazz);
    }

    public static String getByKey(DecodedJWT jwt, String key) {
        return getByKey(jwt, key, String.class);
    }


    /**
     * 通过Key从token中获取值
     *
     * @param key
     * @return
     */
    public static String getByKey(String key) {
        return getByKey(key, String.class);
    }

    public static <T> T getByKey(String key, Class<T> tClazz) {
        return getByKey(getToken(), key, tClazz);
    }

    /**
     * 通过token获取用户id
     *
     * @param unLogin 未登录时将被调用
     * @return 如果token无效/失效  返回0
     */
    public static int getUserId(VoidCallback unLogin) {
        Integer userId = getByKey(KEY_USER_ID, Integer.class);
        if (CommonUtil.isEmpty(userId)) {
            if (unLogin != null) unLogin.done();
            return 0;
        }
        return userId;
    }


    /**
     * 获取用户id
     * 从token中获取
     *
     * @return
     */
    public static int getUserId() {
        return getUserId(null);
    }
}
