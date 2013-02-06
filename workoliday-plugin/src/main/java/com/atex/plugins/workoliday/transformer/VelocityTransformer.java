package com.atex.plugins.workoliday.transformer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.MathTool;

import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.util.StringUtil;

public abstract class VelocityTransformer extends ContentPolicy implements Transformer {

	protected final static Map<String, Object> VELOCITY_TOOLS;
	static {
		Map<String, Object> tmpConstants = new HashMap<String, Object>();
		tmpConstants.put("date", new DateTool());
		tmpConstants.put("esc", new EscapeTool());
		tmpConstants.put("math", new MathTool());
		VELOCITY_TOOLS = Collections.unmodifiableMap(tmpConstants);
	}

	protected void addParamsToContext(VelocityContext context, Map<String, Object> params) {
		if (params != null) {
			Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
			if (iterator != null) {
				while (iterator.hasNext()) {
					Entry<String, Object> param = iterator.next();
					String key = param.getKey();
					Object value = param.getValue();

					if (!StringUtil.isEmpty(key) && null != value) {
						context.put(key, value);
					}
				}
			}
		}
	}
}
