package com.atex.plugins.workoliday.field;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.polopoly.cm.policy.InputValidator;
import com.polopoly.cm.policy.PrepareResult;

/**
 * Checks if the incoming string is a well-formed XML document.
 * 
 */
public class XMLInputValidator implements InputValidator, ErrorHandler {

    private static final String CLASS = XMLInputValidator.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    @Override
    public void validate(String input, PrepareResult prepareResult) {
        final String METHOD = "validate";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(this);
            if (input != null) {
                input = input.trim();
            }
            StringReader xslStream = new StringReader(input);
            reader.parse(new InputSource(xslStream));
        } catch (ParserConfigurationException e) {
            LOG.logp(Level.WARNING, CLASS, METHOD,
                    "Failed to configure validator", e);
        } catch (SAXException e) {
            prepareResult.setError(true);
            prepareResult
                    .setLocalizeMessage("com.atex.plugins.workoliday.XMLInputValidator.InvalidCode");
            LOG.logp(Level.FINE, CLASS, METHOD, "Failed to parse XML", e);
        } catch (IOException e) {
            LOG.logp(Level.WARNING, CLASS, METHOD,
                    "Failed to configure XML Parser", e);
        }
    }

    @Override
    public void error(SAXParseException saxparseexception) throws SAXException {
        // Pass the error
        throw saxparseexception;
    }

    @Override
    public void fatalError(SAXParseException saxparseexception)
            throws SAXException {
        // Pass the error
        throw saxparseexception;
    }

    @Override
    public void warning(SAXParseException saxparseexception)
            throws SAXException {
        // Pass the error
        throw saxparseexception;
    }

}
