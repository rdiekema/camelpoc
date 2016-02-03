package org.diekema.camelpoc.processors;

import org.apache.camel.Processor;

/**
 * Created by rdiekema on 1/31/16.
 */
public interface HashProcessor extends Processor {
    String getHashAlgorithm();

    void setHashAlgorithm(String hashAlgorithm);
}
