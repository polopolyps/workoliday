package com.atex.plugins.workoliday.transformer;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis.utils.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.atex.plugins.workoliday.util.PolicyUtils;
import com.polopoly.util.StringUtil;

/**
 * Transformer used to parse XML and create Velocity context based on comma
 * separated XML tags provided by input template.
 * 
 * @author sarasprang
 */
public class VMTransformer extends VelocityTransformer {

    private static final String COMPONENT_TAGS = "tags";
    private static final String COMPONENT_VELOCITY_CODE = "velocityCode";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public String transform(byte[] resource, Map<String, Object> params)
            throws TransformationException {
        List<String> elementsToGetValuesOf = getTagNames();
        Document document;
        String xml = new String(resource);
        if (StringUtil.isEmpty(xml)) {
            throw new TransformationException(
                    "No XML was supplied, aborting transformation", null);
        }
        try {
            document = DocumentHelper.parseText(xml);

            HashMap<String, List> tagLists = new HashMap<String, List>();
            for (String tag : elementsToGetValuesOf) {
                if (!StringUtils.isEmpty(tag)) {
                    List<Node> elements = document.selectNodes("//" + tag);
                    ArrayList<HashMap> childValues = new ArrayList<HashMap>();
                    for (Node element : elements) {
                        HashMap<String, String> valueMap = new HashMap<String, String>();
                        List<Node> childElements = (element == null ? Collections
                                .emptyList()
                                : element.selectNodes(".//*"));
                        if (childElements.size() > 0) {
                            for (Node childElement : childElements) {
                                valueMap.put(childElement.getName(),
                                        childElement.getText());
                            }
                        } else {
                            valueMap.put("value", element.getText());
                        }
                        childValues.add(valueMap);
                    }
                    tagLists.put(tag + "s", childValues);
                }
            }
            Velocity.init();
            VelocityContext ctx = new VelocityContext();
            for (Entry<String, List> contextValue : tagLists.entrySet()) {
                ctx.put(contextValue.getKey(), contextValue.getValue());
            }

            String idString = getContentId().getContentId()
                    .getContentIdString();
            ctx.put("contentIdStr", idString);
            addParamsToContext(ctx, params);
            addParamsToContext(ctx, VELOCITY_TOOLS);
            Writer writer = new StringWriter();
            boolean evaluate = Velocity.evaluate(ctx, writer,
                    "VM Transformer - " + idString, getVelocityCode());

            if (!evaluate) {
                throw new TransformationException(
                        "Failed to transform the XML with the supplied velocity code");
            }

            return writer.toString();

        } catch (DocumentException e) {
            throw new TransformationException("Failed to get document", e);
        } catch (ResourceNotFoundException e) {
            throw new TransformationException("Failed to found resouce", e);
        } catch (ParseErrorException e) {
            throw new TransformationException("Failed to parse xml", e);
        } catch (Exception e) {
            throw new TransformationException(
                    "Exception when transforming document", e);
        }
    }

    private List<String> getTagNames() {
        String tagsAsString = PolicyUtils.getSingleValue("", this,
                COMPONENT_TAGS);
        tagsAsString = tagsAsString.replaceAll(" ", "");
        return Arrays.asList(tagsAsString.split(","));
    }

    public String getVelocityCode() {
        return PolicyUtils.getSingleValue("", this, COMPONENT_VELOCITY_CODE);
    }
}