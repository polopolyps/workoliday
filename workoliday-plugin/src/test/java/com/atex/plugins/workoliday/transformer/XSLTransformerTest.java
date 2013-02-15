package com.atex.plugins.workoliday.transformer;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.testbase.ImportTestContent;

@ImportTestContent
public class XSLTransformerTest extends AbstractTransformerTest {

    private static final String TRANSFORMER_ID = "test.workoliday.xsltransformer";
    
    private static final String[] HIGHWAY = new String[] {
        "Route",
        "GDNR WESTBOUND (DON VALLEY PKWY TO JAMESON AVE.)",
        "DVP NORTHBOUND (GARDINER TO HWY. 401)",
        "LAKE SHORE BLVD W FROM COXWELL TO KINGSWAY",
        "GARDINER WESTBOUND (DVP TO HWY. 427)",
        "404 NORTHBOUND (GARDINER TO BLOOMINGTON RD.)",
        "ALLEN SOUTHBOUND (HWY 401 TO EGLINTON)",
        "401 EASTBOUND (KINGSTON RD. TO SALEM RD.)"
    };
    
    private static final String[] CURRENT_DELAY = new String[] {
        "Current", "23", "25", "22", "23", "39", "5", "10"
    };
    
    private static final String[] IDEAL_DELAY = new String[] {
        "Ideal", "7", "12", "11", "13", "27", "4", "9"
    };
    
    private static final String[] DELAY = new String[] {
        "Delay", "16", "13", "11", "10", "12", "1", "1"
    };
    
    private static final String[] PERCENTAGE = new String[] {
        "Percentage", "+229", "+108", "+100", "+77", "+44", "+25", "+11"
    };
    
    private static final String MIN = " MIN";
    private static final String PERCENT = "%";
    
    @Inject
    private PolicyCMServer cmServer;
    
    @Test
    public void testTransformer() throws Exception {
        XSLTransformer transformer = (XSLTransformer) cmServer.getPolicy(new ExternalContentId(TRANSFORMER_ID));
        // Read sample feed from test resources
        String result = transformer.transform(getResource("XSLTransformerTestFeed.xml"), null);
        
        Document doc = Jsoup.parseBodyFragment(result);
        Elements elements = doc.select("tr"); 
        
        // We expect 8 table rows since we include the header
        assertEquals("Wrong number of items found in the HTML", 8, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Elements children = element.children();
            Element highway = children.get(0);
            String expectedHighway = HIGHWAY[i];
            
            assertEquals(String.format("Wrong title found %s", highway.text()), expectedHighway, highway.text());
            
            Element currentDelay = children.get(1);
            String expectedCurrentDelay = CURRENT_DELAY[i];
            if (i > 0) expectedCurrentDelay += MIN;
            assertEquals(String.format("Wrong current delay found %s", currentDelay.text()), expectedCurrentDelay, currentDelay.text());
            
            Element idealDelay = children.get(2);
            String expectedIdealDelay = IDEAL_DELAY[i];
            if (i > 0) expectedIdealDelay += MIN;
            assertEquals(String.format("Wrong ideal delay found %s", idealDelay.text()), expectedIdealDelay, idealDelay.text());
            
            Element delay = children.get(3);
            String expectedDelay = DELAY[i];
            if (i > 0) expectedDelay += MIN;
            assertEquals(String.format("Wrong delay found %s", delay.text()), expectedDelay, delay.text());
            
            Element percentage = children.get(4);
            String expectedPercentage = PERCENTAGE[i];
            if (i > 0) expectedPercentage += PERCENT;
            assertEquals(String.format("Wrong delay found %s", percentage.text()), expectedPercentage, percentage.text());
            
        }
    }
}
