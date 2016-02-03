package org.diekema.camelpoc.processors;

import org.apache.camel.Processor;

import java.util.List;

/**
 * Created by rdiekema on 1/31/16.
 */
public interface FileSplitterProcessor extends Processor {

    List<String> getEntityTokens();

    void setEntityTokens(List<String> entityTokens);
}
