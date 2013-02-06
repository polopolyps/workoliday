package com.atex.plugins.workoliday.field;

import org.apache.axis.utils.StringUtils;

import com.polopoly.cm.app.policy.NumberInputPolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyUtil;
import com.polopoly.cm.policy.PrepareResult;

/**
 * Integer Input Policy to use when you want to set a max/min value for the
 * integer. <param name="max">30</param> Defaults to 50 <param
 * name="min">10</param> Defaults to 0
 */
public class BoundedIntegerInputPolicy extends NumberInputPolicy {

    private static final String TYPE_INT = "int";
    private static final String PARAMETER_MAX = "max";
    private static final String PARAMETER_MIN = "min";
    private static final String PARAMETER_EMPTY_ALLOWED = "emptyAllowed";
    private static final String DEFAULT_MAX = "50";
    private static final String DEFAULT_MIN = "0";

    /**
     * Returns the type this policy is.
     */
    public String getType() {
        return TYPE_INT;
    }

    /**
     * Returns the max value this policy allows.
     */
    public String getMax() {
        return PolicyUtil.getParameter(PARAMETER_MAX, DEFAULT_MAX, this);
    }

    /**
     * Returns the min value this policy allows.
     */
    public String getMin() {
        return PolicyUtil.getParameter(PARAMETER_MIN, DEFAULT_MIN, this);
    }
    
    private boolean isAllowedToBeEmpty() {
        return Boolean.parseBoolean(PolicyUtil.getParameter(PARAMETER_EMPTY_ALLOWED, "false", this));
    }

    public PrepareResult prepareSelf() throws CMException {
        PrepareResult prepareResult = super.prepareSelf();
        if (!isAllowedToBeEmpty() && !StringUtils.isEmpty(getValue())) {
            try {
                int size = getIntValue();
                if (size < Integer.parseInt(getMin())) {
                    prepareResult.setError(true);
                    prepareResult
                            .setLocalizeMessage("com.atex.plugins.workoliday.BoundedIntegerInputPolicy.NumberTooSmall");
                    return prepareResult;
                }
                if (size > Integer.parseInt(getMax())) {
                    prepareResult.setError(true);
                    prepareResult
                            .setLocalizeMessage("com.atex.plugins.workoliday.BoundedIntegerInputPolicy.NumberTooBig");
                    return prepareResult;
                }
            } catch (NumberFormatException nfe) {
                prepareResult.setError(true);
                prepareResult.setLocalizeMessage("cm.field.numberinput.NotANumber");
                return prepareResult;
            }
    
            if ((getValue() == null || getValue().length() == 0)) {
                prepareResult.setError(true);
                prepareResult.setLocalizeMessage("cm.policy.ValueRequired");
                return prepareResult;
            }
        }
        return prepareResult;
    }
}

