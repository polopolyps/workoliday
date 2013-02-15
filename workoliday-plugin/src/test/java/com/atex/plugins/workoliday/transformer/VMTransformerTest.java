package com.atex.plugins.workoliday.transformer;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

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
 * Test of the VM Transformer
 *
 */
@ImportTestContent
public class VMTransformerTest extends AbstractTransformerTest {

    private static final String TRANSFORMER_ID = "test.workoliday.vmtransformer";
    @Inject
    private PolicyCMServer cmServer;
    
    private String[][] SHOWS = new String[][]{
            new String[] {"09:00 Schcooby Doo", "10:00 Hulk Hogan Show"},
            new String[] {"14:00 Live! Lady Gaga", "16:00 The Marilyn Manson Show"},
            new String[] {"10:00 True Blood", "14:00 The Doors"},
            new String[] {"09:00 Guns &amp; Ammo", "18:00 News"},
            new String[] {"08:00 The smurfs", "19:00 Game of Thrones"},
            new String[] {"15:00 Good News Friday", "20:00 Friday the 13th"},
            new String[] {"13:00 The Great Escape", "16:00 The Big Blue"}
    };
    
    @Test
    public void testTransformer() throws Exception {
        VMTransformer transformer = (VMTransformer) cmServer.getPolicy(new ExternalContentId(TRANSFORMER_ID));
        // Read test xml file from test resources
        String result = transformer.transform(getResource("VMTransformerTestFeed.xml"), null);
        Document doc = Jsoup.parseBodyFragment(result);
        assertEquals("Wrong title found for tabbed element", "What's On", doc.select("h3").text());
        
        Elements titles = doc.select("div.flora > ul li");
        assertEquals(5, titles.size());
       
        // In this test we only test if it displays correctly for todays date
        int dayOfWeek = Calendar.getInstance().get(7);
        
        Elements elements = doc.select("div[id$=_tab1] div ul li"); 
        
        // Test that we got five days of tv back
        assertEquals("Wrong number of elements found for day " + dayOfWeek, 2, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            assertEquals("Wrong show found in todays' schedule", SHOWS[dayOfWeek - 1][i], element.text());
        }
    }
    
}
