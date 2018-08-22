package com.jiushig.springutil;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkzmz on 2017/11/4.
 */
public class HttpUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static RestTemplate restTemplate = new RestTemplate();

    private static String token;

    /**
     * 获取请求头
     *
     * @return
     */
    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Accpet-Encoding", "gzip");
        headers.add("Content-Encoding", "UTF-8");
        headers.add("Content-Type", "application/json; charset=UTF-8");
        if (token != null) headers.add("Authorization", token);
        return headers;
    }

    public static void setToken(String token) {
        HttpUtil.token = token;
        logger.debug(LoggerBuilder.get("token更新成功").setDescribe(token).build());
    }

    private static String request(Builder builder, HttpMethod method) {
        assert builder != null;
        assert builder.url != null;
        assert method != null;

        logger.info(LoggerBuilder.get("请求地址").setDescribe(builder.url).build());
        logger.debug(LoggerBuilder.get("请求详情").setDescribe(builder).build());
        StringBuilder url = new StringBuilder(builder.url);
        if (builder.params != null) {
            url.append("?");
            builder.params.keySet().forEach(s -> url.append(s).append("=").append(builder.params.get(s)).append("&"));
        }
        ResponseEntity<String> response = restTemplate.exchange(url.toString(), method,
                new HttpEntity<>(builder.body, builder.headers), String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new HttpException(String.valueOf(response.getStatusCode().value()) + "  " + builder.toString());
        }

        logger.info(LoggerBuilder.get("请求成功").setDescribe(builder.url).build());
        String result = response.getBody();
        logger.debug(LoggerBuilder.get("请求结果").setDescribe(result).build());
        return result;
    }

    /**
     * get请求
     * 请求失败时 抛出{@link HttpException}
     *
     * @param builder
     * @return
     */
    public static String doGet(Builder builder) {
        return request(builder, HttpMethod.GET);
    }

    public static <T> T get(Builder builder, Class<T> c) {
        return result(doGet(builder), c);
    }

    public static <T> T get(Builder builder, TypeToken<T> typeToken) {
        return result(doGet(builder), typeToken);
    }

    public static Result get(Builder builder) {
        return get(builder, Result.class);
    }

    /**
     * post请求
     *
     * @param builder
     * @return
     */
    public static String doPost(Builder builder) {
        return request(builder, HttpMethod.POST);
    }

    public static <T> T post(Builder builder, Class<T> c) {
        return result(doPost(builder), c);
    }

    public static <T> T post(Builder builder, TypeToken<T> typeToken) {
        return result(doPost(builder), typeToken);
    }

    public static Result post(Builder builder) {
        return post(builder, Result.class);
    }

    private static <T> T result(String result, Class<T> c) {
        assert result != null;
        return GsonUtil.get().fromJson(result, c);
    }

    private static <T> T result(String result, TypeToken<T> typeToken) {
        assert result != null;
        assert typeToken != null;
        return GsonUtil.get().fromJson(result, typeToken.getType());
    }

    public static class Builder {
        // 请求头
        private HttpHeaders headers = getHttpHeaders();
        // 请求参数
        private Map<String, String> params;

        private String url;

        private String body;

        public Builder setHeaders(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder setParams(String name, Object value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(name, value.toString());
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url.startsWith("http") ? url : "http://localhost:" + ServerInfo.getPort() + url;
            return this;
        }

        public Builder setBody(Object body) {
            if (!(body instanceof String)) {
                this.body = GsonUtil.get().toJson(body);
            } else {
                this.body = (String) body;
            }
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "url='" + url + '\'' +
                    ", params=" + params +
                    ", body='" + body + '\'' +
                    ", headers=" + headers +
                    '}';
        }
    }

    /**
     * 请求异常
     */
    public static class HttpException extends RuntimeException {
        HttpException(String msg) {
            super(msg);
        }
    }
}
