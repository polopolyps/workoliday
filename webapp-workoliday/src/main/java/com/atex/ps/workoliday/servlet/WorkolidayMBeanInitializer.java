package com.atex.ps.workoliday.servlet;

import javax.servlet.ServletContextEvent;

import com.atex.ps.workoliday.util.WorkolidayMBeanUtil;

/**
 * ContextListener for registering the MBeans that track all statistics of the
 * update process of the ExternalDataCacheContent.
 * 
 */
public class WorkolidayMBeanInitializer extends MBeanInitializerListener {

    protected String COMPONENT_EXTERNAL_CACHE_UPDATE = "ExternalCacheUpdate";

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        super.contextDestroyed(evt);
        WorkolidayMBeanUtil.unregisterMBean(hostName, applicationName,
                LEVEL_FINE, COMPONENT_EXTERNAL_CACHE_UPDATE, STATISTICS);
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        // Initialize the HttpResourceManager for this application
        super.contextInitialized(evt);
        initExternalCacheUpdateStatisticsMBean();
    }

    private void initExternalCacheUpdateStatisticsMBean() {
        if (CacheUpdateManager.statisticsMBean == null) {
            CacheUpdateManager.statisticsMBean = new ExternalCacheUpdateStatistics();
        }
        WorkolidayMBeanUtil.registerMBeanIfNotAlreadyRegistered(
                CacheUpdateManager.statisticsMBean, hostName, applicationName,
                LEVEL_FINE, COMPONENT_EXTERNAL_CACHE_UPDATE, STATISTICS);
    }

}
