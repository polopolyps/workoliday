package com.atex.ps.workoliday.servlet;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.polopoly.application.Application;
import com.polopoly.application.ApplicationStatusReporter;
import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.ConnectionPropertiesException;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.LegacyDaemonThreadsStopper;
import com.polopoly.application.StandardApplication;
import com.polopoly.application.config.ConfigurationRuntimeException;
import com.polopoly.application.config.ResourceConfig;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cache.LRUSynchronizedUpdateCache;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.client.DiskCacheSettings;
import com.polopoly.cm.client.HttpCmClientHelper;
import com.polopoly.search.solr.PostFilteredSolrSearchClient;
import com.polopoly.search.solr.SolrIndexName;

/**
 * Creates, inits and destroys the Application used in the workoliday webapp.
 * The Application is also inserted in the ServletContext under the name
 * specified in web.xml.
 */
public class WorkolidayServletContextListener implements ServletContextListener {
    private static Logger LOG = Logger
            .getLogger(WorkolidayServletContextListener.class.getName());

    private Application _application;

    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext sc = sce.getServletContext();

            LOG.info("Starting application "
                    + ApplicationServletUtil.getApplicationName(sc));
            // Application with local JMX registry.
            _application = new StandardApplication(ApplicationServletUtil
                    .getApplicationName(sc));
            _application.setManagedBeanRegistry(ApplicationServletUtil
                    .getManagedBeanRegistry());

            // CM Client.
            ConnectionProperties connectionProperties = ApplicationServletUtil
                    .getConnectionProperties(sc);
            CmClientBase cmClient = HttpCmClientHelper
                    .createAndAddToApplication(_application,
                            connectionProperties);

            // Reports status back to the cm server
            _application.addApplicationComponent(new ApplicationStatusReporter(
                    cmClient));

            PostFilteredSolrSearchClient solrSearchClientInternal = new PostFilteredSolrSearchClient(
                    "search", "solrClientInternal", cmClient);
            solrSearchClientInternal
                    .setIndexName(new SolrIndexName("internal"));
            _application.addApplicationComponent(solrSearchClientInternal);

            // Sync cache.
            LRUSynchronizedUpdateCache syncCache = new LRUSynchronizedUpdateCache();
            _application.addApplicationComponent(syncCache);

            // Read connection properties.
            _application.readConnectionProperties(connectionProperties);

            // Read and apply config in xml resources. Since this
            // webapp uses the same source for preview and front we
            // use the application name to distinguish between preview
            // and front config.
            ResourceConfig config = new ResourceConfig(_application.getName());
            config.apply(_application);

            // If no base directory for caches was set in connection
            // properties, persistent caches will be set up relative
            // to webapp root. If no cache directory names were set
            // from config XML, we set them to reasonable defaults.
            // Configure disk cache settings
            DiskCacheSettings settings = cmClient.getDiskCacheSettings();
            ApplicationServletUtil.configureDiskCacheBaseDir(sce
                    .getServletContext(), settings, _application.getName(),
                    cmClient.getModuleName());
            cmClient.setDiskCacheSettings(settings);

            // Init.
            _application.init();

            // Put in global scope.
            ApplicationServletUtil.setApplication(sc, _application);
        } catch (IllegalApplicationStateException e) {
            throw new RuntimeException(
                    "This is a programming error, should never happend.", e);
        } catch (ConnectionPropertiesException e) {
            LOG.log(Level.SEVERE,
                    "Could not get, read or apply connection properties.", e);
        } catch (ConfigurationRuntimeException e) {
            LOG.log(Level.SEVERE, "Could not read or apply configuration.", e);
        }

    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();

        // Remove from global scope.
        ApplicationServletUtil.setApplication(sc, null);

        // Destroy.
        _application.destroy();

        LegacyDaemonThreadsStopper.stopStaticDaemons();
    }

}
