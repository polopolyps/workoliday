package com.atex.plugins.workoliday.element;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

@RunWith(TestBaseRunner.class)
@ImportTestContent
public class TransformerElementTest {

    private static final String TRANSFORMER_ELEMENT = TransformerElementTest.class.getSimpleName() + ".element";
    @Inject
    private PolicyCMServer cmServer;
    
    @Test
    public void testElementOutput() throws CMException {
        TransformerElementPolicy policy = (TransformerElementPolicy) cmServer.getPolicy(new ExternalContentId(TRANSFORMER_ELEMENT));
        assertEquals("<p>Hello Transformer Element</p>", policy.getCachedValue());
    }
}
