package com.atex.plugins.workoliday.field;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.policy.InputValidator;
import com.polopoly.cm.policy.PolicyUtil;

/**
 * Extension of the single value policy. The parameter "validationClass" can be
 * specified as an input template parameter if you need custom validation.
 * Otherwise this policy will work exactly as the regular SingleValuePolicy.
 * 
 */
public class SingleValuePolicyWithValidation extends SingleValuePolicy {

    private static final String CLASS = SingleValuePolicyWithValidation.class
            .getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final String PARAMETER_VALIDATION_CLASS = "validationClass";

    @Override
    public void initSelf() {
        super.initSelf();

        String validationClass = PolicyUtil.getParameter(
                PARAMETER_VALIDATION_CLASS, this);
        if (validationClass != null) {
            Class<?> validatorClass;
            try {
                validatorClass = Class.forName(validationClass);
                inputValidator = (InputValidator) validatorClass.newInstance();
            } catch (ClassCastException e) {
                LOG
                        .log(
                                Level.WARNING,
                                "Supplied validator class does not extend InputValidator",
                                e);
                throw new IllegalArgumentException(
                        "Error while getting validator class");
            } catch (ClassNotFoundException e) {
                LOG.log(Level.WARNING,
                        "Supplied validator class does not exist", e);
                throw new IllegalArgumentException(
                        "Error while getting validator class");
            } catch (InstantiationException e) {
                LOG.log(Level.WARNING,
                        "Supplied validtor class could not be instantiated", e);
                throw new IllegalArgumentException(
                        "Error while getting validator class");
            } catch (IllegalAccessException e) {
                LOG.log(Level.WARNING,
                        "Supplied validator class could not be accessed", e);
                throw new IllegalArgumentException(
                        "Error while getting validator");
            }

        }
    }

}
