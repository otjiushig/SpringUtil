package com.jiushig.springutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * Created by zk on 2018/8/15.
 */

public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * 获取当前项目运行路径
     *
     * @return
     */
    public static String getSystemPath() {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        int index = path.indexOf("target") - 1;
        return index > 0 ? path.substring(0, index) : System.getProperty("user.dir");
    }

    /**
     * 检查对象是不是为null
     * 如果为字符串 则不能为空字符串
     *
     * @param objects
     * @return
     */
    public static boolean isEmpty(Object... objects) {
        if (objects == null) return true;
        for (Object obj : objects) {
            if (obj == null) return true;
            if (obj instanceof String) {
                if (((String) obj).isEmpty())
                    return true;
            }
        }
        return false;
    }

    /**
     * 通过反射，简单复制对象
     *
     * @param object     被复制的对象
     * @param copyObject 将对象复制到此对象
     */
    public static void copyObject(Object object, Object copyObject) {
        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            try {
                copyObject.getClass().getField(field.getName()).set(copyObject, field.get(object));
            } catch (Exception e) {
                logger.warn("The object copy failed : " + object.toString(), e);
            }
        }
    }

    /**
     * 判断是否少于几
     *
     * @param less
     * @param integers
     * @return
     */
    public static boolean isLess(int less, Integer... integers) {
        if (integers == null) return true;
        for (Integer integer : integers) {
            if (integer == null || integer < less)
                return true;
        }
        return false;
    }

    public static boolean isLessZero(Integer... integers) {
        return isLess(0, integers);
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (logger.isInfoEnabled()) {
                logger.trace("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                    if (logger.isInfoEnabled()) {
                        logger.trace("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                    }
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                    if (logger.isInfoEnabled()) {
                        logger.trace("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                    }
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                    if (logger.isInfoEnabled()) {
                        logger.trace("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                    }
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                    if (logger.isInfoEnabled()) {
                        logger.trace("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                    }
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                    if (logger.isInfoEnabled()) {
                        logger.trace("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                    }
                }
            } else if (ip.length() > 15) {
                String[] ips = ip.split(",");
                for (String ip1 : ips) {
                    if (!("unknown".equalsIgnoreCase(ip1))) {
                        ip = ip1;
                        break;
                    }
                }
            }
            return ip;
        } catch (Exception e) {
            logger.warn("Ip information got failed", e.getMessage());
        }
        return null;
    }

    /**
     * 格式化文本
     *
     * @param text
     * @return
     */
    public static String formatText(String text) {
        if (isEmpty(text)) return text;
        // 删除多余换行符
        text = text.replaceAll("(\n){2,}", "\n");

        return text;
    }

    /**
     * 格式化markdow文本
     *
     * @param markdownText
     * @return
     */
    public static String formatMarkdownText(String markdownText) {
        markdownText = formatText(markdownText);
        if (isEmpty(markdownText)) return null;
        // markdown 替换
        markdownText = markdownText.replaceAll("[#*`]", "");
        markdownText = markdownText.replaceAll("!\\[.*?\\]\\(.*?\\)", "[图片]");
        markdownText = markdownText.replaceAll("\\[.*?\\]\\(.*?\\)", "[链接]");
        return markdownText;
    }
}
