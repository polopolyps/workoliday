package com.atex.plugins.workoliday.transformer;

import java.util.Map;

/**
 * Interface to use for different transformers.
 */
public interface Transformer {

    public static final String STATIC_RESOURCE_DOMAIN = "staticResourceDomain";
    public static final String POLOPOLY_FILE_URL_PREFIX = "polopolyFileURLPrefix";

    /**
     * Method to transform source to string.
     * 
     * @param resource
     *            Input to transform (parse through).
     * @return Transformed input.
     * @throws TransformationException
     *             If exception occurs while working the data.
     */
    public String transform(byte[] resource, Map<String, Object> params)
            throws TransformationException;
}
