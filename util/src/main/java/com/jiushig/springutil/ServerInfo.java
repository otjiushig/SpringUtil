package com.jiushig.springutil;

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zk on 2018/8/4.
 */
@Configuration
public class ServerInfo implements ApplicationListener<ServletWebServerInitializedEvent> {
    private static ServletWebServerInitializedEvent event;

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        ServerInfo.event = event;
    }

    /**
     * 获取服务当前运行端口
     *
     * @return
     */
    public static int getPort() {
        if (event == null) {
            throw new RuntimeException("ServerInfo 未初始化");
        }
        return event.getWebServer().getPort();
    }
}

