package com.atex.plugins.workoliday.element;

import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;
import com.polopoly.siteengine.mvc.Renderer;
import com.polopoly.siteengine.mvc.controller.RendererForXmlContent;

/**
 * Controller to set the correct Content-type response header specified in the
 * TransformerElement.
 * 
 * @author sarasprang
 */
public class TransformerElementController extends RenderControllerBase {

    @Override
    public Renderer getRenderer(RenderRequest request, TopModel m,
            Renderer defaultRenderer, ControllerContext context) {
        String contentType = (String) ModelPathUtil.get(context
                .getContentModel(), "contenttype/value");
        if ("text/xml".equals(contentType)) {
            return new RendererForXmlContent(defaultRenderer);
        } else if ("text/javascript".equals(contentType)) {
            return new RendererForJavaScriptContent(defaultRenderer);
        }
        return super.getRenderer(request, m, defaultRenderer, context);
    }

    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        super.populateModelAfterCacheKey(request, m, cacheInfo, context);
        Long cacheTime = (Long) context.getContentModel().getAttribute(
                "cacheTime");
        if (cacheTime > 0) {
            cacheInfo.setCacheTime(cacheTime);
        }
    }
}
