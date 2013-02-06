package com.atex.ps.workoliday.servlet;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.atex.ps.workoliday.http.HttpResourceManager;
import com.atex.ps.workoliday.http.HttpResourceManagerSettings;
import com.atex.ps.workoliday.http.HttpResourceManagerStatistics;
import com.atex.ps.workoliday.util.WorkolidayMBeanUtil;
import com.polopoly.application.Application;
import com.polopoly.application.servlet.ApplicationServletUtil;

public class MBeanInitializerListener implements ServletContextListener{

    public static final Logger logger = Logger.getLogger(MBeanInitializerListener.class.getName());
    public static final String LEVEL_CONFIG = "CONFIG";
    public static final String LEVEL_FINE = "FINE";

    protected String COMPONENT_HTTP_RESOURCE_MANAGER = "HttpResourceManager";

    protected String SETTINGS = "Settings";
    protected String STATISTICS = "Statistics";

    protected String hostName = null;
    protected String applicationName = null;
    
    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        WorkolidayMBeanUtil.unregisterMBean(hostName, applicationName,
                LEVEL_CONFIG,
                COMPONENT_HTTP_RESOURCE_MANAGER, SETTINGS);

        WorkolidayMBeanUtil.unregisterMBean(hostName, applicationName,
                LEVEL_FINE,
                COMPONENT_HTTP_RESOURCE_MANAGER, STATISTICS);
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        Application application = ApplicationServletUtil.getApplication(evt
                .getServletContext());
        applicationName = application.getName();
        hostName = application.getHostname().getDomainlessHostname();
        initHttpResourceManagerStatisticsMBean();
        initHttpResourceManagerSettingsMBean();
    }
    
    protected void initHttpResourceManagerSettingsMBean() {
        
        //TODO Do we want a config content to read properties like this?
        /*
        RemoteIncludeConfigPolicy configContentPolicy;
        Integer connectionTimeout = null;
        Integer socketTimeout = null;
        Integer maxConnections = null;
        try {
            configContentPolicy = (RemoteIncludeConfigPolicy) policyCmServer.getPolicy(new ExternalContentId(RemoteIncludeConfigPolicy.EXT_ID));
            connectionTimeout = configContentPolicy.getHttpResourceManagerConnectionTimeout();
            socketTimeout = configContentPolicy.getHttpResourceManagerSocketTimeout();
            maxConnections = configContentPolicy.getHttpResourceManagerMaxConnections();
        } catch (CMException e) {
            logger.log(Level.SEVERE, "Failed to retrieve RemoteIncludeConfigContent. Initialization of MBean HttpResourceManagerSettings will use default values.");
        }
        //Initialize MBean with value from config content
        synchronized (HttpResourceManager.mBeanMutex) {
            if(HttpResourceManager.settingsMBean == null) {
                HttpResourceManager.settingsMBean = new HttpResourceManagerSettings();
                if(connectionTimeout == null)
                    HttpResourceManager.settingsMBean.setConnectionTimeout(HttpResourceManager.CONNECTION_TIMEOUT_DEFAULT);
                else
                    HttpResourceManager.settingsMBean.setConnectionTimeout(connectionTimeout);

                if(socketTimeout == null)
                    HttpResourceManager.settingsMBean.setSocketTimeout(HttpResourceManager.SOCKET_TIMEOUT_DEFAULT);
                else
                    HttpResourceManager.settingsMBean.setConnectionTimeout(socketTimeout);
                
                if(maxConnections == null)
                    HttpResourceManager.settingsMBean.setMaxConnections(HttpResourceManager.MAX_CONNECTIONS_DEFAULT);
                else
                    HttpResourceManager.settingsMBean.setMaxConnections(maxConnections);

            }
        }*/
        if (HttpResourceManager.settingsMBean == null) {
            HttpResourceManager.settingsMBean = new HttpResourceManagerSettings();
        }

        WorkolidayMBeanUtil.registerMBeanIfNotAlreadyRegistered(
                HttpResourceManager.settingsMBean, hostName, applicationName,
 LEVEL_CONFIG,
                COMPONENT_HTTP_RESOURCE_MANAGER, SETTINGS);
    }
    
    protected void initHttpResourceManagerStatisticsMBean() {
        if (HttpResourceManager.statisticsMBean == null) {
            HttpResourceManager.statisticsMBean = new HttpResourceManagerStatistics();
        }
        WorkolidayMBeanUtil
                .registerMBeanIfNotAlreadyRegistered(
                HttpResourceManager.statisticsMBean, hostName, applicationName,
 LEVEL_FINE,
                COMPONENT_HTTP_RESOURCE_MANAGER, STATISTICS);
    }

}