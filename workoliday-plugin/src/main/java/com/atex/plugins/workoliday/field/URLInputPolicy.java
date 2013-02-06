package com.atex.plugins.workoliday.field;

import java.net.MalformedURLException;
import java.net.URL;

import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyUtil;
import com.polopoly.cm.policy.PrepareResult;
import com.polopoly.util.StringUtil;

/**
 * Use this policy with a p.TextInput if you want to check the URL. <field
 * name="url" input-template="p.TextInput" label="URL">
 * <policy>com.atex.plugins.workoliday.field.URLInputPolicy</policy> </field>
 * Optional parameter to the field is: <param
 * name="validateWithDefaultProtocol">http://</param> Which means that the field
 * will validate the URL input value with the default protocol but not store it
 * on the content. So the editor can enter www.ctv.ca and the field will
 * automatically validate with the default protocol but the protocol will not be
 * stored on the content. The URL input value (including 'http://') must be at
 * least 10 chars long.
 */
public class URLInputPolicy extends SingleValuePolicy implements SingleValued {

    public PrepareResult prepareSelf() throws CMException {
        PrepareResult prepareResult = null;
        try {
            prepareResult = super.prepareSelf();

            String validateWithDefaultProtocol = PolicyUtil.getParameter(
                    "validateWithDefaultProtocol", null, this);

            String value = getValue();

            if (!prepareResult.isError() && !StringUtil.isEmpty(value)) {

                if (!StringUtil.isEmpty(validateWithDefaultProtocol)
                        && !StringUtil.isEmpty(value)
                        && !value.startsWith(validateWithDefaultProtocol)) {
                    value = validateWithDefaultProtocol + value;

                }

                try {
                    new URL(value);
                    if (value.length() < 10) {
                        throw new MalformedURLException();

                    }
                    if (!StringUtil.isEmpty(validateWithDefaultProtocol)) {
                        // setValue(value.substring(validateWithDefaultProtocol.length()));
                        String theURL = value
                                .substring(validateWithDefaultProtocol.length());
                        theURL = theURL.replace("http://", "");
                        setValue(theURL);
                    }

                } catch (MalformedURLException e) {
                    prepareResult.setError(true);
                    prepareResult
                            .setLocalizeMessage("com.atex.plugins.workoliday.URLInputPolicy.MalformedURL");
                }

            }
        } finally {
        }
        return prepareResult;
    }

}
