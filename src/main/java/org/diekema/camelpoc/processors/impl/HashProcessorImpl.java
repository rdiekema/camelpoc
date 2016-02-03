package org.diekema.camelpoc.processors.impl;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.camel.Exchange;
import org.apache.commons.codec.digest.DigestUtils;
import org.diekema.camelpoc.processors.HashProcessor;
import org.diekema.camelpoc.processors.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by rdiekema on 1/29/16.
 */

@Component
public class HashProcessorImpl implements HashProcessor {

    @Override
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    @Override
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    private String hashAlgorithm;



    private static final Logger logger = LoggerFactory.getLogger(HashProcessorImpl.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        // Get the body of the exchange as a in InputStream (implicit type conversion provided by Camel).
        InputStream inputStream = exchange.getIn().getBody(InputStream.class);

        MessageDigest digest = DigestUtils.getDigest(hashAlgorithm);

        byte[] hash = DigestUtils.updateDigest(digest, inputStream).digest();

        // Add the hash as a Base64 encoded string to the exchange headers.
        exchange.getIn().setHeader(Headers.FILE_HASH, Base64.encode(hash));

        // Log the hash
        logger.info(exchange.getIn().getHeader(Headers.FILE_HASH, String.class));

        // Close the input stream.
        inputStream.close();
    }
}
