package com.jiushig.springutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * Created by zk on 2018/9/3.
 */
public class SessionUtil {
    private static final Logger logger = LoggerFactory.getLogger(SessionUtil.class);

    public static void set(String key, Object object) {
        try {
            Objects.requireNonNull(getRequestAttribute()).getRequest().setAttribute(key, object);
        } catch (NullPointerException e) {
            logger.warn(LoggerBuilder.get("Save fail")
                    .set("key", key)
                    .set("value", object).build(), e);
        }

    }


    public static Object get(String key) {
        try {
            return Objects.requireNonNull(getRequestAttribute()).getRequest().getAttribute(key);
        } catch (NullPointerException e) {
            logger.warn(LoggerBuilder.get("Get fail")
                    .set("key", key).build(), e);
        }
        return null;
    }

    /**
     * 获取当前请求属性
     *
     * @return
     */
    public static ServletRequestAttributes getRequestAttribute() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.warn("ServletRequestAttributes is null");
            return null;
        }
        return attributes;
    }
}
