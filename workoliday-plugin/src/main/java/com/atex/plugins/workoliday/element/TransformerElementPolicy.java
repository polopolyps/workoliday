package com.atex.plugins.workoliday.element;

import com.atex.plugins.workoliday.cache.ExternalDataCachePolicy;
import com.atex.plugins.workoliday.util.PolicyUtils;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.common.lang.StringUtil;
import com.polopoly.model.ModelTypeDescription;
import com.polopoly.siteengine.layout.Element;

import com.atex.plugins.baseline.policy.BaselinePolicy;

public class TransformerElementPolicy extends BaselinePolicy implements
        Element, ModelTypeDescription {

    public String getCachedValue() throws CMException {
        ContentId sourceContentId = getContentReference("source", "reference");
        PolicyCMServer cmServer = getCMServer();

        if (cmServer.contentExists(sourceContentId)) {
            ExternalDataCachePolicy policy = (ExternalDataCachePolicy) getCMServer()
                    .getPolicy(sourceContentId);

            return policy.getCachedValue();
        }

        return "";
    }

    public long getCacheTime() {
        long ret = -1L;
        String selectedOption = PolicyUtils.getSingleValue(this, "cacheTime");
        if (!StringUtil.isEmpty(selectedOption)) {
            ret = Long.parseLong(selectedOption);
        }
        return ret;
    }
}
