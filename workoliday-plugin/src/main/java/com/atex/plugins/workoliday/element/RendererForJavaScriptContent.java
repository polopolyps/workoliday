package com.atex.plugins.workoliday.element;

import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderException;
import com.polopoly.render.RenderRequest;
import com.polopoly.render.RenderResponse;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.Renderer;

/**
 * Renderer that sets JavaScript content type.
 */
public class RendererForJavaScriptContent implements Renderer {

    private final Renderer defaultRenderer;

    public RendererForJavaScriptContent(Renderer defaultRenderer)
    {
        this.defaultRenderer = defaultRenderer;
    }

    public void render(TopModel m, RenderRequest req, RenderResponse resp, CacheInfo cacheInfo,
            ControllerContext context) throws RenderException
    {
        resp.setContentType("text/javascript; charset=UTF-8");
        defaultRenderer.render(m, req, resp, cacheInfo, context);
    }

}