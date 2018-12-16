package com.shuyan.gracefulshutdown.common.config;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Date;
import java.util.concurrent.Executor;

import static java.lang.Thread.sleep;

/**
 * @author will
 */
@Configuration
public class ShutdownConfig {
    @Bean
    public GracefulShutdown gracefulShutdown(){
        return new GracefulShutdown();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(gracefulShutdown());
        return tomcat;
    }

    /**
     * 优雅关闭 Spring Boot。容器必须是 tomcat
     */
    private class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
        private final Logger log = LoggerFactory.getLogger(GracefulShutdown.class);
        private volatile Connector connector;

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
            if(this.connector == null){
                return;
            }
            this.connector.pause();
            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                log.warn("Tomcat 正在 shutdown，如果长时间无法结束您可以强制结束");
                threadPoolExecutor.shutdown();
                Long startTime = System.currentTimeMillis();
                Long lastTime = startTime;
                Long time2Sec = 2 * 1000L;
                while(threadPoolExecutor.isTerminating()){
                    Long currentTime = System.currentTimeMillis();
                    if(currentTime >= (lastTime + time2Sec)){
                        lastTime = currentTime;
                        log.warn("Tomcat shutdown 已执行" + (currentTime - startTime) / 1000.0 + "秒");
                    }
                }
                Long currentTime = System.currentTimeMillis();
                log.warn("Tomcat shutdown 完成，用时" + (currentTime - startTime) / 1000.0 + "秒");
            }
        }
    }
}
