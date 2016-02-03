package org.diekema.camelpoc.processors.impl;

import org.apache.camel.Exchange;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.diekema.camelpoc.processors.EntityLoaderProcessor;
import org.diekema.camelpoc.processors.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rdiekema on 1/29/16.
 */

@Component
public class EntityLoaderProcessorImpl implements EntityLoaderProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EntityLoaderProcessorImpl.class);

    Map<String, String> referenceMap = new HashMap<>();

    {
        // This tells us what entry in the cache to lookup values from and the pattern used to find and replace.
        referenceMap.put("CountryName", "\"([A-Z]{1,10})\"");
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        CacheAccess<String, Map<String, String>> referenceCache = JCS.getInstance(exchange.getIn().getHeader(Headers.FILE_HASH, String.class));

        String body = exchange.getIn().getBody(String.class);

        // Create a string buffer to build the new string with the replacement values.
        StringBuffer stringBuffer = new StringBuffer(body.length());

        // The reference map contains the cache key from which we want to retrieve values and the pattern to be used to find values that need to be replaced.
        for (Map.Entry<String, String> entry : referenceMap.entrySet()) {
            // Retrieve the references from the cache.
            Map<String, String> references = referenceCache.get(entry.getKey());

            // Compile the pattern to extract and replace the lookup value.
            Pattern pattern = Pattern.compile(entry.getValue());

            // Create a matcher to iterate all instances of the value to be replaced.
            Matcher matcher = pattern.matcher(body);
            while (matcher.find()) {
                // Retrieve the replacement value from the map based on the extracted value from the pattern match.
                String replacement = references.get(matcher.group(1));
                if (replacement != null) {
                    // Append the modified text to the buffer.
                    matcher.appendReplacement(stringBuffer, "\"" + replacement + "\"");
                }
            }

            // Appends the remaining unmodified chunk of text to the buffer.
            matcher.appendTail(stringBuffer);
        }

        // Print the buffer if you want to see the result.
        // System.out.println(stringBuffer.toString());
    }
}
