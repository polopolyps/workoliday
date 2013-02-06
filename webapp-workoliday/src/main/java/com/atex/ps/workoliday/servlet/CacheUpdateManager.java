package com.atex.ps.workoliday.servlet;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.workoliday.cache.ExternalDataCachePolicy;
import com.atex.plugins.workoliday.transformer.Transformer;
import com.atex.ps.workoliday.http.HttpResourceManager;
import com.atex.ps.workoliday.http.HttpResourceManager.HttpResourceManagerException;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.StringUtil;

public final class CacheUpdateManager {

    public static ExternalCacheUpdateStatisticsMBean statisticsMBean = new ExternalCacheUpdateStatistics();
    private static final CacheUpdateManager instance = new CacheUpdateManager();
    private static final Logger logger = Logger
            .getLogger(CacheUpdateManager.class.getName());

    private CacheUpdateManager() {
    }

    public static CacheUpdateManager getInstance() {
        return instance;
    }

    public void registerIndexSize(long size) {
        statisticsMBean.registerTotalNumberOfContentsInIndex(size);
    }

    public void updateExternalCache(CacheUpdateEvent event,
            PolicyCMServer cmServer) {
        String url = event.getUrl();
        if (StringUtil.isEmpty(url)) {
            statisticsMBean.registerFailedUpdate(event);
            logger.log(Level.WARNING, "No url was found for content "
                    + event.getContentId());
            return;
        }

        // Fetch the resource from the given URL
        byte[] resource = null;
        try {
            resource = HttpResourceManager.getInstance().getResource(url);
        } catch (HttpResourceManagerException hrme) {
            statisticsMBean.registerFailedUpdate(event);
            // Don't need to log the exception here since it is already logged
            logger.log(Level.WARNING, "Failed to get the url " + url
                    + " for content " + event.getContentId());
            return;
        }

        if (resource == null) {
            statisticsMBean.registerFailedUpdate(event);
            logger.log(Level.WARNING, "The response was null from the url "
                    + url + " on content " + event.getContentId());
            return;
        }
        ExternalDataCachePolicy policy = null;
        try {
            policy = (ExternalDataCachePolicy) cmServer
                    .createContentVersion(new VersionedContentId(
                            ContentIdFactory.createContentId(event
                                    .getContentId()),
                            VersionedContentId.LATEST_COMMITTED_VERSION));
            logger.fine("Updating " + event
                    + " and applying tranformer to resource ");
            Transformer transformer = policy.getTransformer();
            String result = null;
            if (transformer != null) {
                result = transformer.transform(resource, policy
                        .getTransformerParams());
                logger.fine("Transformer applied to " + event);
            } else {
                // Use default charset, might need to add this as a conf
                result = new String(resource);
                logger.fine("No transformer found for " + event
                        + " using result directly");
            }

            if (!StringUtil.isEmpty(result)) {
                policy.setCachedValue(result);
                cmServer.commitContent(policy);
                logger.fine(event + " committed with an updated cached value");
                statisticsMBean.registerSuccessfulUpdate(event);
            } else {
                logger.log(Level.WARNING,
                        "The transformed result was empty for "
                                + event.getContentId() + " using the url "
                                + event.getUrl());
                statisticsMBean.registerFailedUpdate(event);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to update policy " + event, ex);
            statisticsMBean.registerFailedUpdate(event);
        } finally {
            if (policy != null) {
                try {
                    cmServer.abortContent(policy);
                } catch (CMException e) {
                    logger.log(Level.WARNING,
                            "Failed to abort newly created policy for event "
                                    + event, e);
                }
            }
        }

    }
}
