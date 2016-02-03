package org.diekema.camelpoc.processors.impl;

import org.apache.camel.Exchange;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.diekema.camelpoc.processors.Headers;
import org.diekema.camelpoc.processors.FileSplitterProcessor;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rdiekema on 2/1/16.
 */
@Component
public class XmlReferenceProcessorImpl implements FileSplitterProcessor {

    List<String> entityTokens;

    @Override
    public List<String> getEntityTokens() {
        return entityTokens;
    }

    @Override
    public void setEntityTokens(List<String> entityTokens) {
        this.entityTokens = entityTokens;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // With the reference tables, we're going to identify a cache region by file hash.
        // That way all other processors handling the exchange for a given file can look up the relevant cache region.
        CacheAccess<String, Map<String, String>> referenceCache = JCS.getInstance(exchange.getIn().getHeader(Headers.FILE_HASH, String.class));

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLEventReader xer = factory.createXMLEventReader(new FileReader(exchange.getIn().getBody(File.class).getAbsolutePath()));

        while (xer.hasNext()) {
            XMLEvent xmlEvent = xer.nextEvent();

            if (xmlEvent.isStartElement()) {
                // If we're here, we have a start element. Check to see if it's one of the elements we care about.
                String tokenName = xmlEvent.asStartElement().getName().getLocalPart();
                if (entityTokens.contains(tokenName)) {

                    // Create a map to hold all of the reference values found.
                    Map<String, String> referenceValues = new HashMap<>();

                    // Loop over all elements that match the current token name.
                    while(xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals(tokenName)){

                        // Hard coded attribute names to extract the reference values.
                        Attribute code = xmlEvent.asStartElement().getAttributeByName(new QName("code"));
                        Attribute name = xmlEvent.asStartElement().getAttributeByName(new QName("name"));

                        // Add the reference to the map using the code as the map key and the name as the value.
                        referenceValues.put(code.getValue(), name.getValue());
                        xmlEvent = xer.nextTag();

                        // If the next element is the closing tag to the current, skip it.
                        while(xmlEvent.isCharacters() || xmlEvent.isEndElement()){
                            xmlEvent = xer.nextEvent();
                        }
                    }

                    // Put all the reference values found into the cache keyed off of the tokenName.
                    referenceCache.put(tokenName, referenceValues);
                }
            }
        }
    }
}
