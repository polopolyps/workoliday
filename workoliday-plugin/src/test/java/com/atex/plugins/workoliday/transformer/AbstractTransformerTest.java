package com.atex.plugins.workoliday.transformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.runner.RunWith;

import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
public abstract class AbstractTransformerTest {
   
    protected byte[] getResource(String resource) throws FileNotFoundException, URISyntaxException, UnsupportedEncodingException {
        java.net.URL url = AbstractTransformerTest.class.getResource("/" + resource);
        if (url == null) {
            Assert.fail("The requested resource " + resource + " was not found");
        }
        //Z means: "The end of the input but for the final terminator, if any"
        String xml = new java.util.Scanner(new File(url.toURI()),"UTF8").useDelimiter("\\Z").next();
        return xml.getBytes("UTF-8");
    }
}
