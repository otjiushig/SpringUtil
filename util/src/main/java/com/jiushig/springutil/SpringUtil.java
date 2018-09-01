package com.jiushig.springutil;

/**
 * Created by Guowang on 2017/4/21.
 * 普通类调用Spring Bean 对象
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class SpringUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            context = applicationContext;
        }
        logger.debug("Initializing applicationContext finished :" + applicationContext);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 通过class获取bean对象
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> tClass) {
        if (getContext() != null) {
            return getContext().getBean(tClass);
        }
        return null;
    }
}