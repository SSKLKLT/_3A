package com.shop._3a.web;

import com.shop._3a.store.DataStore;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 应用启动时初始化演示数据。
 */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        DataStore.getInstance().seedIfEmpty();
        event.getServletContext().log("3A 小游戏社区已启动，演示数据初始化完成");
    }
}
