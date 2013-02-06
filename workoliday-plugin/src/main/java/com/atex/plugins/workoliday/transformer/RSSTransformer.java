package com.atex.plugins.workoliday.transformer;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.atex.plugins.workoliday.util.PolicyUtils;

/**
 * Grabs a RSS Feed and transforms it to HTML.
 * 
 */
public class RSSTransformer extends VelocityTransformer {

    private static final String COMPONENT_SHOW_SUMMARY = "showSummary";
    private static final String COMPONENT_SHOW_DATE_LINE = "showDateline";
    private static final String COMPONENT_LIST_LENGTH = "listLength";
    private static final String COMPONENT_VELOCITY_CODE = "velocityCode";

    private final static String RSS_ITEM_TITLE = "title";
    private final static String RSS_ITEM_LINK = "link";
    private final static String RSS_ITEM_DESCRIPTION = "description";
    private final static String RSS_ITEM_PUB_DATE = "pubDate";

    @Override
    public String transform(byte[] resource, Map<String, Object> params)
            throws TransformationException {
        int listLength = Integer.parseInt(PolicyUtils.getSingleValue("10",
                this, COMPONENT_LIST_LENGTH));
        List<Map<String, String>> rssObjects = new LinkedList<Map<String, String>>();
        Document document;
        try {
            document = DocumentHelper.parseText(new String(resource));
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> elements = root.element("channel").elements();
            int listIndex = 0;
            for (Element e : elements) {
                String title = null;
                String link = null;
                String description = null;

                Element titleElement = e.element(RSS_ITEM_TITLE);
                Element linkElement = e.element(RSS_ITEM_LINK);
                Element descriptionElement = e.element(RSS_ITEM_DESCRIPTION);
                Element pubDateElement = e.element(RSS_ITEM_PUB_DATE);

                if (titleElement != null) {
                    title = titleElement.getText();
                }

                if (linkElement != null) {
                    link = linkElement.getText();
                }

                if (descriptionElement != null) {
                    description = descriptionElement.getText();
                }

                Map<String, String> rssObject = new HashMap<String, String>();
                if (title != null && title.length() > 0 && link != null
                        && link.length() > 0) {
                    rssObject.put(RSS_ITEM_TITLE, title);
                    rssObject.put(RSS_ITEM_LINK, link);
                    rssObject.put(RSS_ITEM_DESCRIPTION, description);
                    if (pubDateElement != null)
                        rssObject.put(RSS_ITEM_PUB_DATE, pubDateElement
                                .getText());
                    rssObjects.add(rssObject);
                    listIndex++;
                }
                if (listIndex >= listLength)
                    break;
            }

            Velocity.init();
            VelocityContext ctx = new VelocityContext();
            ctx.put(COMPONENT_SHOW_SUMMARY, PolicyUtils.isChecked(this,
                    COMPONENT_SHOW_SUMMARY));
            ctx.put(COMPONENT_SHOW_DATE_LINE, PolicyUtils.isChecked(this,
                    COMPONENT_SHOW_DATE_LINE));
            addParamsToContext(ctx, params);
            addParamsToContext(ctx, VELOCITY_TOOLS);
            ctx.put("list", rssObjects);

            Writer writer = new StringWriter();
            boolean evaluate = Velocity.evaluate(ctx, writer,
                    "RSS Transformer - "
                            + getContentId().getContentId()
                                    .getContentIdString(), getVelocityCode());

            if (!evaluate) {
                throw new TransformationException(
                        "Failed to parse the RSS with the supplied velocity code");
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

    public String getVelocityCode() {
        return PolicyUtils.getSingleValue("", this, COMPONENT_VELOCITY_CODE);
    }
}
