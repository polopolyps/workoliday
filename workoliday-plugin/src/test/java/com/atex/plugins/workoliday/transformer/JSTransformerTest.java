package com.atex.plugins.workoliday.transformer;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import org.apache.velocity.tools.generic.EscapeTool;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.testbase.ImportTestContent;

/**
 * JS Transformer test that checks if the footer html is wrapped in javascript document write
 * together with the additional javascript that was configured in the transformer content.
 */
@ImportTestContent
public class JSTransformerTest extends AbstractTransformerTest {

    private static final String TRANSFORMER_ID = "test.workoliday.jstransformer";
    private static final String[] HTML_CONTENT = new String[] {
        "<div class=\"footer\">",
        "<ul>",
        "<li><a href=\"http://localhost:8080/news\">News</a></li>",
        "<li><a href=\"http://localhost:8080/sport\">Sport</a></li>",
        "<li><a href=\"http://localhost:8080/about\">About</a></li>",
        "</ul>",
        "</div>"
    };
    private static final String JAVASCRIPT = "alert(\"Test of js transformer\");"; 
    
    @Inject
    private PolicyCMServer cmServer;
    
    private EscapeTool esc = new EscapeTool();
    
    @Test
    public void testTransformer() throws Exception {
        JSTransformer transformer = (JSTransformer) cmServer.getPolicy(new ExternalContentId(TRANSFORMER_ID));
        // Read sample html from test resources
        String result = transformer.transform(getResource("JSTransformerTestHTML.html"), null);
        Scanner scanner = new Scanner(result);
        int index = 0;
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          if (scanner.hasNextLine()) {
              assertEquals(getDocumentWriteLine(index++), line);
          } else {
              assertEquals(JAVASCRIPT, line);
          }
        }
    }

    private String getDocumentWriteLine(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("document.write('");
        sb.append(esc.javascript(HTML_CONTENT[i]));
        sb.append("');");
        return sb.toString();
    }
}
