package com.atex.plugins.workoliday.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.workoliday.transformer.Transformer;
import com.atex.plugins.workoliday.util.PolicyUtils;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.Policy;
import com.polopoly.model.ModelTypeDescription;

/**
 * Policy for the ExternalDataCache that is used for getting pass-through data
 * and display it on the site.
 * 
 * Note, it needs to implement ModelTypeDescription to expose its values for the
 * solr mapping.
 * 
 */
public class ExternalDataCachePolicy extends ContentPolicy implements
        ModelTypeDescription {

    private static final String COMPONENT_URL = "url";
    private static final String COMPONENT_TRANSFORMER = "transformer";
    private static final String COMPONENT_UPDATE_INTERVAL = "updateInterval";
    private static final String COMPONENT_ENABLED = "enabled";
    private static final String COMPONENT_CACHE = "cache";
    private static final String COMPONENT_HASH_GROUP = "hashValue";
    private static final String COMPONENT_HASH_VALUE = "value";

    private static Logger logger = Logger
            .getLogger(ExternalDataCachePolicy.class.getName());

    @Override
    public void preCommitSelf() throws CMException {
        super.preCommitSelf();

        long oldHash = getHashValue();
        StringBuilder value = new StringBuilder();
        value.append(getName()).append(getUrl()).append(getUpdateInterval());
        long hashCode = value.toString().hashCode();

        if (oldHash != hashCode) {
            setComponent(COMPONENT_HASH_GROUP, COMPONENT_HASH_VALUE, String
                    .valueOf(hashCode));
        }
    }

    public long getHashValue() {
        try {
            return getComponentAsLong(COMPONENT_HASH_GROUP,
                    COMPONENT_HASH_VALUE, 0L);
        } catch (CMException e) {
            logger.log(Level.WARNING,
                    "Failed to get the hashvalue from the content "
                            + getContentId(), e);
        }
        return 0L;
    }

    public String getUrl() throws CMException {
        return PolicyUtils.getSingleValue(this, COMPONENT_URL);
    }

    public Transformer getTransformer() throws CMException {
        Transformer ret = null;
        Policy transformerPolicy = PolicyUtils.getSinglePolicyFromList(this,
                COMPONENT_TRANSFORMER);

        if (transformerPolicy instanceof Transformer) {
            ret = (Transformer) transformerPolicy;
        }

        return ret;
    }

    /**
     * @return Update interval in minutes.
     * @throws CMException
     */
    public int getUpdateInterval() throws CMException {
        return PolicyUtils.getSingleIntValue(this, 180,
                COMPONENT_UPDATE_INTERVAL);
    }

    public String getCachedValue() throws CMException {
        return PolicyUtils.getSingleValue(this, COMPONENT_CACHE);
    }

    public void setCachedValue(String newValue) throws CMException {
        SingleValuePolicy value = (SingleValuePolicy) this
                .getChildPolicy(COMPONENT_CACHE);
        value.setValue(newValue);
    }

    public boolean isEnabled() {
        return PolicyUtils.isChecked(this, COMPONENT_ENABLED);
    }

    public Map<String, Object> getTransformerParams() {
        Map<String, Object> params = new HashMap<String, Object>();

        /*
         * SitePolicy sitePolicy = PolicyUtilExtended.getSitePolicy(this); if
         * (sitePolicy instanceof StaticResourceProviderModelTypeDescription) {
         * StaticResourceProviderPolicy staticResourceProvider =
         * ((StaticResourceProviderModelTypeDescription) sitePolicy)
         * .getStaticResourceProvider(); if (staticResourceProvider != null) {
         * params.put(Transformer.STATIC_RESOURCE_DOMAIN,
         * staticResourceProvider.getStaticResourceDomain());
         * params.put(Transformer.POLOPOLY_FILE_URL_PREFIX,
         * staticResourceProvider.getPolopolyFileUrlPrefix()); } }
         */
        return params;
    }

}
