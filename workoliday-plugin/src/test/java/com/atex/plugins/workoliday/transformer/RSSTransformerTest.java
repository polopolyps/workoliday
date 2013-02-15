package com.atex.plugins.workoliday.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.testbase.ImportTestContent;

/**
 * Test of the RSS Transformer.
 *
 */
@ImportTestContent
public class RSSTransformerTest extends AbstractTransformerTest {

    @Inject
    private PolicyCMServer cmServer;
    
    private static final String TRANSFORMER_ID = "test.workoliday.rsstransformer";
    private static final String[] TITLES = {
        "Massive manhunt continues for ex-cop suspected in L.A. shooting rampage",
        "21,900 jobs lost in January, unemployment rate falls to 7 per cent",
        "De Beers says blockade of diamond mine near Attawapiskat is over",
        "Alberta clipper and Texas low combining forces over Ontario, Quebec",
        "Baird heads to Washington for first meeting with Kerry"
    };
    
    @Test
    public void testTransformer() throws Exception {
        RSSTransformer transformer = (RSSTransformer) cmServer.getPolicy(new ExternalContentId(TRANSFORMER_ID));
        // Read sample news feed from test resources
        String result = transformer.transform(getResource("RSSTransformerTestFeed.xml"), null);
        
        Document doc = Jsoup.parseBodyFragment(result);
        Elements elements = doc.select("li"); 
        
        // The RSS Transformer is configured to only show 5 elements (though the feed has 6 elements in total)
        assertEquals("Wrong number of items found in the HTML", 5, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Elements children = element.children();
            Element pubDate = children.get(0);
            String expectedPubDate = "Fri, 8 Feb 2013 09:" + i + i;
            
            assertTrue(String.format("Pub date must start with %s", expectedPubDate), pubDate.text().startsWith(expectedPubDate));
            
            Element link = children.get(1);
            assertEquals("http://atex.com/" + i, link.attr("href"));
            assertEquals(TITLES[i], link.text());
            
            Element description = children.get(2);
            assertTrue(description.text().startsWith("Article " + i));
        }
    }
}
