package org.diekema.camelpoc.processors.impl;

import org.apache.camel.Exchange;
import org.diekema.camelpoc.processors.MetaDataProcessor;

import java.io.*;
import java.util.Map;

/**
 * Created by rdiekema on 2/1/16.
 */
public class XmlMetaDataProcessor implements MetaDataProcessor {

    private Map<String, String> metaDataFields;

    public Map<String, String> getMetaDataFields() {
        return metaDataFields;
    }

    public void setMetaDataFields(Map<String, String> metaDataFields) {
        this.metaDataFields = metaDataFields;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // open stream
        Reader reader = new FileReader(exchange.getIn().getBody(File.class));


        // find metadata tags
        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        for(String key : metaDataFields.keySet()){

        }

        // extract data

        // persist metadata
    }
}
