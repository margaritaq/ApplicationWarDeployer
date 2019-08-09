package com.example.WarDeployer.config;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletException;
import java.io.File;

@Configuration
public class TomcatConfiguration {

    @Autowired
    private Environment environment;
    private  static Logger log = LoggerFactory.getLogger(TomcatConfiguration.class);

    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        log.debug("Start servletContainerFactory");
        return new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {

                new File(tomcat.getServer().getCatalinaBase(), "webapps").mkdirs();

                try {
                    String warPath = environment.getProperty("war.path");
                    File wars = new File(warPath);
                    System.out.println(warPath);
                    String[] warList = wars.list();

                    for (int i = 0;i < warList.length;i++) {
                        String generalAppPath = "/app" + (i+1);
                        Context context = tomcat.addWebapp(generalAppPath, warPath + warList[i]);
                        context.setParentClassLoader(getClass().getClassLoader());
                        log.info("War -> " + warList[i]+ " -> deployed successfully!");
                        log.debug("War -> " + warList[i]+ " -> deployed successfully!");
                    }
                } catch (ServletException ex) {
                    throw new IllegalStateException("Failed to add a webapp!", ex);
                }
                return super.getTomcatEmbeddedServletContainer(tomcat);
            }
        };
    }
}