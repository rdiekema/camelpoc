package org.diekema.camelpoc.processors.impl;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.diekema.camelpoc.processors.Headers;
import org.diekema.camelpoc.processors.FileSplitterProcessor;
import org.diekema.camelpoc.routes.Endpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by rdiekema on 1/29/16.
 */

@Component
public class XmlSplitterProcessorImpl implements FileSplitterProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XmlSplitterProcessorImpl.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Override
    public List<String> getEntityTokens() {
        return entityTokens;
    }

    @Override
    public void setEntityTokens(List<String> entityTokens) {
        this.entityTokens = entityTokens;
    }

    List<String> entityTokens;
    Map<String, String> dictionaryMapping;
    List<String> dictionaryTokens;

    @Override
    public void process(Exchange exchange) throws Exception {
        if (exchange.getIn().getHeader(Headers.FILE_HASH) != null) {
            logger.info(exchange.getIn().getHeader(Headers.FILE_HASH, String.class));

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLOutputFactory xof = XMLOutputFactory.newFactory();
            XMLEventReader xer = factory.createXMLEventReader(new FileReader(exchange.getIn().getBody(File.class).getAbsolutePath()));

            while (xer.hasNext()) {
                XMLEvent xmlEvent = xer.nextEvent();

                if (xmlEvent.isStartElement()) {
                    // If we're here, we have a start element. Check to see if it's one of the elements we care about.
                    String tokenName = xmlEvent.asStartElement().getName().getLocalPart();
                    if (entityTokens.contains(tokenName)) {
                        // Create an event stack to collect all the XMLEvents.
                        Stack<XMLEvent> eventStack = new Stack<>();

                        // Loop and add events to the stack until we reach the end element of a token we care about.
                        while (!(xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart().equals(tokenName))) {
                            eventStack.push(xmlEvent);
                            xmlEvent = xer.nextEvent();
                        }
                        // Push the last event, the end element, onto the stack.
                        eventStack.push(xmlEvent);

                        // Here we can do whatever we want with the output, it's a string containing all of the XML
                        // elements and character data collected in the previous step. We could submit it to a JMS Queue
                        // or another processing endpoint to carry out an additional processing step.
                        String result = writeAllEvents(eventStack, xof);

                        producerTemplate.requestBodyAndHeaders(Endpoints.MessageQueue.ENTITY_LOADER_PROCESSOR, result, exchange.getIn().getHeaders());
                    }
                }
            }
        }
    }

    private String writeAllEvents(Stack<XMLEvent> events, XMLOutputFactory xof) throws XMLStreamException, IOException {
        StringWriter stringWriter = new StringWriter();
        XMLEventWriter xew = xof.createXMLEventWriter(stringWriter);

        for (XMLEvent cachedEvent : events) {
            xew.add(cachedEvent);
        }

        String retval = stringWriter.toString();
        stringWriter.close();

        return retval;
    }
}